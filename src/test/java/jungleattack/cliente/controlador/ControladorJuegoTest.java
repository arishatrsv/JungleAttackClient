package jungleattack.cliente.controlador;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import common.constantes.ConstantesProtocolo;
import common.model.Especie;
import javax.swing.SwingUtilities;
import jungleattack.cliente.modelo.FaseCliente;
import jungleattack.cliente.modelo.ModeloJuego;
import jungleattack.cliente.protocolo.ConversorMensajes;
import jungleattack.cliente.protocolo.Mensaje;
import jungleattack.cliente.red.ConexionServidor;
import org.junit.jupiter.api.Test;

/**
 * Pruebas del controlador SIN servidor: le "llegan" mensajes como si los mandara el servidor
 * y reviso que el modelo quede bien. procesamientoActualizacion trabaja en el hilo de Swing,
 * por eso después de cada mensaje espero a que ese hilo termine (esperarSwing).
 */
class ControladorJuegoTest {

    private final ModeloJuego modelo = new ModeloJuego();
    private final ControladorJuego controlador = new ControladorJuego(modelo, new ConexionServidor());

    // Espera a que el hilo de Swing termine lo que tenía pendiente.
    private void esperarSwing() throws Exception {
        SwingUtilities.invokeAndWait(() -> { });
    }

    private void recibir(Mensaje mensaje) throws Exception {
        controlador.procesamientoActualizacion(mensaje);
        esperarSwing();
    }

    @Test
    void alCrearLaPartidaGuardaElCodigoYPasaALaEspera() throws Exception {
        recibir(ConversorMensajes.creacionMensaje(
                ConstantesProtocolo.PARTIDA_CREADA, ConstantesProtocolo.CAMPO_CODIGO, "A7K2"));
        assertEquals("A7K2", modelo.consultaJugadorLocal().consultaCodigoPartida());
        assertEquals(FaseCliente.ESPERA, modelo.consultaFase());
    }

    @Test
    void alUnirseGuardaElCodigoPeroEsperaLaSeleccionParaCambiarDePantalla() throws Exception {
        recibir(ConversorMensajes.creacionMensaje(
                ConstantesProtocolo.PARTIDA_UNIDA, ConstantesProtocolo.CAMPO_CODIGO, "A7K2"));
        assertEquals("A7K2", modelo.consultaJugadorLocal().consultaCodigoPartida());
        assertEquals(FaseCliente.INICIO, modelo.consultaFase());
    }

    @Test
    void laSeleccionDelServidorActualizaElModelo() throws Exception {
        recibir(ConversorMensajes.creacionMensaje(ConstantesProtocolo.SELECCION_ANIMALES,
                ConstantesProtocolo.CAMPO_DISPONIBLES, "tucan,cocodrilo,perezoso",
                ConstantesProtocolo.CAMPO_PROPIA, "jaguar",
                ConstantesProtocolo.CAMPO_OPONENTE, "",
                ConstantesProtocolo.CAMPO_COMPLETA, "false"));

        assertEquals(FaseCliente.SELECCION, modelo.consultaFase());
        assertEquals(3, modelo.consultaEspeciesDisponibles().size());
        assertEquals(Especie.JAGUAR, modelo.consultaJugadorLocal().consultaEspecie());
        assertNull(modelo.consultaEspecieOponente()); // el texto vacío significa "todavía no eligió"
    }

    @Test
    void laSeleccionCompletaSeReconoce() throws Exception {
        recibir(ConversorMensajes.creacionMensaje(ConstantesProtocolo.SELECCION_ANIMALES,
                ConstantesProtocolo.CAMPO_DISPONIBLES, "cocodrilo",
                ConstantesProtocolo.CAMPO_PROPIA, "jaguar",
                ConstantesProtocolo.CAMPO_OPONENTE, "tucan",
                ConstantesProtocolo.CAMPO_COMPLETA, "true"));
        assertEquals(Especie.TUCAN, modelo.consultaEspecieOponente());
        assertTrue(modelo.consultaSeleccionCompleta());
    }

    @Test
    void siElOponenteSeDesconectaVuelveAlInicioConElError() throws Exception {
        modelo.actualizacionSeleccion(java.util.List.of(Especie.TUCAN), null, null, false);

        recibir(ConversorMensajes.creacionMensaje(ConstantesProtocolo.ERROR,
                ConstantesProtocolo.CAMPO_MOTIVO, ConstantesProtocolo.MOTIVO_OPONENTE_DESCONECTADO));

        assertEquals(FaseCliente.INICIO, modelo.consultaFase());
        assertEquals(ConstantesProtocolo.MOTIVO_OPONENTE_DESCONECTADO, modelo.consultaError());
    }

    @Test
    void unErrorCualquieraSoloSeGuarda() throws Exception {
        recibir(ConversorMensajes.creacionMensaje(ConstantesProtocolo.ERROR,
                ConstantesProtocolo.CAMPO_MOTIVO, ConstantesProtocolo.MOTIVO_PARTIDA_LLENA));
        assertEquals(ConstantesProtocolo.MOTIVO_PARTIDA_LLENA, modelo.consultaError());
        assertEquals(FaseCliente.INICIO, modelo.consultaFase());
    }

    @Test
    void siSePierdeLaConexionVuelveAlInicioConElErrorDeConexion() throws Exception {
        modelo.actualizacionEstado(FaseCliente.ESPERA);

        recibir(new Mensaje(ConstantesProtocolo.DESCONEXION, "{}"));

        assertEquals(FaseCliente.INICIO, modelo.consultaFase());
        assertEquals(ConstantesProtocolo.MOTIVO_SIN_CONEXION, modelo.consultaError());
    }

    // Arma un GAME_STARTED como lo manda el servidor: el jugador empieza en la zona 12 con 3 animales,
    // ve esa zona, hay una taza y la bandera propia.
    private Mensaje mensajeInicio() {
        JsonObject contenido = new JsonObject();
        contenido.addProperty(ConstantesProtocolo.CAMPO_ZONA_INICIAL, 12);
        contenido.addProperty(ConstantesProtocolo.CAMPO_DANIO, 2);
        JsonArray zonas = new JsonArray();
        zonas.add(12);
        contenido.add(ConstantesProtocolo.CAMPO_ZONAS_VISIBLES, zonas);
        JsonArray animales = new JsonArray();
        for (int i = 1; i <= 3; i++) {
            JsonObject animal = new JsonObject();
            animal.addProperty(ConstantesProtocolo.CAMPO_ID, "jaguar-" + i);
            animal.addProperty(ConstantesProtocolo.CAMPO_ESPECIE, "jaguar");
            animal.addProperty(ConstantesProtocolo.CAMPO_ENERGIA, 20);
            animal.addProperty(ConstantesProtocolo.CAMPO_ENERGIA_MAXIMA, 20);
            animal.addProperty(ConstantesProtocolo.CAMPO_X, 700 + i);
            animal.addProperty(ConstantesProtocolo.CAMPO_Y, 480);
            animal.addProperty(ConstantesProtocolo.CAMPO_ES_PROPIO, true);
            animal.addProperty(ConstantesProtocolo.CAMPO_ES_BOT, false);
            animales.add(animal);
        }
        contenido.add(ConstantesProtocolo.CAMPO_ANIMALES, animales);
        JsonArray tazas = new JsonArray();
        JsonObject taza = new JsonObject();
        taza.addProperty(ConstantesProtocolo.CAMPO_ID, "taza-3");
        taza.addProperty(ConstantesProtocolo.CAMPO_X, 730);
        taza.addProperty(ConstantesProtocolo.CAMPO_Y, 420);
        taza.addProperty(ConstantesProtocolo.CAMPO_ZONA, 12);
        tazas.add(taza);
        contenido.add(ConstantesProtocolo.CAMPO_TAZAS, tazas);
        JsonArray banderas = new JsonArray();
        JsonObject bandera = new JsonObject();
        bandera.addProperty(ConstantesProtocolo.CAMPO_X, 730);
        bandera.addProperty(ConstantesProtocolo.CAMPO_Y, 480);
        bandera.addProperty(ConstantesProtocolo.CAMPO_ZONA, 12);
        bandera.addProperty(ConstantesProtocolo.CAMPO_ES_PROPIO, true);
        banderas.add(bandera);
        contenido.add(ConstantesProtocolo.CAMPO_BANDERAS, banderas);
        return new Mensaje(ConstantesProtocolo.PARTIDA_INICIADA, contenido.toString());
    }

    @Test
    void alEmpezarLaPartidaElModeloPasaAJuegoConLoQueVeElJugador() throws Exception {
        recibir(mensajeInicio());

        assertEquals(FaseCliente.JUEGO, modelo.consultaFase());
        assertEquals(12, modelo.consultaZonaInicial());
        assertEquals(12, modelo.consultaZonaObservada());
        assertEquals(2, modelo.consultaDanio());
        assertEquals(12, modelo.consultaMapa().consultaZonas().size());
        assertEquals(1, modelo.consultaZonasVisibles().size());
        assertEquals(12, modelo.consultaZonasVisibles().get(0).consultaIdentificador());
        assertEquals(3, modelo.consultaAnimalesVisibles().size());
        assertEquals(1, modelo.consultaTazasVisibles().size());
        assertEquals(1, modelo.consultaBanderasVisibles().size());
    }

    @Test
    void losAnimalesDelInicioSeLeenConTodosSusDatos() throws Exception {
        recibir(mensajeInicio());
        jungleattack.cliente.modelo.AnimalVisible animal = modelo.consultaAnimalesVisibles().get(0);
        assertEquals("jaguar-1", animal.consultaIdentificador());
        assertEquals(Especie.JAGUAR, animal.consultaEspecie());
        assertEquals(20, animal.consultaEnergia());
        assertEquals(20, animal.consultaEnergiaMaxima());
        assertEquals(701, animal.consultaPosicion().consultaPosicionX());
        assertEquals(480, animal.consultaPosicion().consultaPosicionY());
        assertTrue(animal.consultaPropio());
        assertFalse(animal.consultaBot());
    }

    @Test
    void laTazaYLaBanderaDelInicioSeLeenConSuZona() throws Exception {
        recibir(mensajeInicio());
        assertEquals("taza-3", modelo.consultaTazasVisibles().get(0).consultaIdentificador());
        assertEquals(12, modelo.consultaTazasVisibles().get(0).consultaZona());
        assertTrue(modelo.consultaBanderasVisibles().get(0).consultaPropia());
        assertEquals(730, modelo.consultaBanderasVisibles().get(0).consultaPosicion().consultaPosicionX());
    }

    @Test
    void unInicioMalFormadoMuestraUnErrorYNoCambiaDeFase() throws Exception {
        recibir(new Mensaje(ConstantesProtocolo.PARTIDA_INICIADA, "{\"zonaInicial\":12}"));
        assertEquals(ConstantesProtocolo.MOTIVO_MENSAJE_INVALIDO, modelo.consultaError());
        assertEquals(FaseCliente.INICIO, modelo.consultaFase());
    }

    @Test
    void siElOponenteSeVaConLaPartidaEnCursoVuelveAlInicioYLimpiaLaPartida() throws Exception {
        recibir(mensajeInicio());
        recibir(ConversorMensajes.creacionMensaje(ConstantesProtocolo.ERROR,
                ConstantesProtocolo.CAMPO_MOTIVO, ConstantesProtocolo.MOTIVO_OPONENTE_DESCONECTADO));
        assertEquals(FaseCliente.INICIO, modelo.consultaFase());
        assertEquals(ConstantesProtocolo.MOTIVO_OPONENTE_DESCONECTADO, modelo.consultaError());
        assertTrue(modelo.consultaAnimalesVisibles().isEmpty());
        assertNull(modelo.consultaMapa());
    }
}
