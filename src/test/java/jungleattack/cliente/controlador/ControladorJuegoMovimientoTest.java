package jungleattack.cliente.controlador;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import common.constantes.ConstantesProtocolo;
import common.model.Especie;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import javax.swing.SwingUtilities;
import jungleattack.cliente.modelo.AnimalVisible;
import jungleattack.cliente.modelo.MapaCliente;
import jungleattack.cliente.modelo.ModeloJuego;
import jungleattack.cliente.modelo.PuntoVisible;
import jungleattack.cliente.protocolo.ConversorMensajes;
import jungleattack.cliente.protocolo.Mensaje;
import jungleattack.cliente.red.ConexionServidor;
import org.junit.jupiter.api.Test;

/**
 * Pruebas del movimiento en el controlador, sin servidor: la conexión es falsa y guarda los mensajes
 * que el cliente "enviaría".
 */
class ControladorJuegoMovimientoTest {

    // Conexión falsa: siempre "conectada" y guarda lo que se envía.
    private static class ConexionFalsa extends ConexionServidor {
        private final BlockingQueue<Mensaje> enviados = new LinkedBlockingQueue<>();

        @Override
        public synchronized boolean conexion(Consumer<Mensaje> destino) {
            return true;
        }

        @Override
        public void envioMensaje(Mensaje mensaje) {
            enviados.add(mensaje);
        }
    }

    private final ModeloJuego modelo = new ModeloJuego();
    private final ConexionFalsa conexion = new ConexionFalsa();
    private final ControladorJuego controlador = new ControladorJuego(modelo, conexion);

    private void recibir(Mensaje mensaje) throws Exception {
        controlador.procesamientoActualizacion(mensaje);
        SwingUtilities.invokeAndWait(() -> { });
    }

    private JsonObject animal(String id, int x, int y, boolean propio) {
        JsonObject animal = new JsonObject();
        animal.addProperty(ConstantesProtocolo.CAMPO_ID, id);
        animal.addProperty(ConstantesProtocolo.CAMPO_ESPECIE, propio ? "jaguar" : "tucan");
        animal.addProperty(ConstantesProtocolo.CAMPO_ENERGIA, 20);
        animal.addProperty(ConstantesProtocolo.CAMPO_ENERGIA_MAXIMA, 20);
        animal.addProperty(ConstantesProtocolo.CAMPO_X, x);
        animal.addProperty(ConstantesProtocolo.CAMPO_Y, y);
        animal.addProperty(ConstantesProtocolo.CAMPO_ES_PROPIO, propio);
        animal.addProperty(ConstantesProtocolo.CAMPO_ES_BOT, false);
        return animal;
    }

    // Arma el contenido de un GAME_STARTED o STATE_UPDATE con los animales dados.
    private JsonObject foto(List<Integer> zonas, JsonObject... animales) {
        JsonObject contenido = new JsonObject();
        contenido.addProperty(ConstantesProtocolo.CAMPO_DANIO, 2);
        JsonArray zonasVisibles = new JsonArray();
        zonas.forEach(zonasVisibles::add);
        contenido.add(ConstantesProtocolo.CAMPO_ZONAS_VISIBLES, zonasVisibles);
        JsonArray lista = new JsonArray();
        for (JsonObject animal : animales) {
            lista.add(animal);
        }
        contenido.add(ConstantesProtocolo.CAMPO_ANIMALES, lista);
        contenido.add(ConstantesProtocolo.CAMPO_TAZAS, new JsonArray());
        contenido.add(ConstantesProtocolo.CAMPO_BANDERAS, new JsonArray());
        return contenido;
    }

    @Test
    void unStateUpdateTraeElDanioNuevoYLasTazasConsumidas() throws Exception {
        empezarPartida();
        assertEquals(0, modelo.consultaTazasConsumidas());
        assertEquals(2, modelo.consultaDanio());

        JsonObject contenido = foto(List.of(12), animal("jaguar-1", 725, 420, true), animal("jaguar-2", 665, 420, true));
        contenido.addProperty(ConstantesProtocolo.CAMPO_DANIO, 3);
        contenido.addProperty(ConstantesProtocolo.CAMPO_TAZAS_CONSUMIDAS, 1);
        recibir(new Mensaje(ConstantesProtocolo.ACTUALIZACION_ESTADO, contenido.toString()));

        assertEquals(3, modelo.consultaDanio());
        assertEquals(1, modelo.consultaTazasConsumidas());
    }

    @Test
    void siElMensajeNoTraeLasTazasConsumidasSeTomanComoCero() throws Exception {
        empezarPartida();
        recibir(new Mensaje(ConstantesProtocolo.ACTUALIZACION_ESTADO,
                foto(List.of(12), animal("jaguar-1", 725, 420, true)).toString()));
        assertEquals(0, modelo.consultaTazasConsumidas());
    }

    private void empezarPartida() throws Exception {
        JsonObject contenido = foto(List.of(12), animal("jaguar-1", 725, 420, true), animal("jaguar-2", 665, 420, true));
        contenido.addProperty(ConstantesProtocolo.CAMPO_ZONA_INICIAL, 12);
        recibir(new Mensaje(ConstantesProtocolo.PARTIDA_INICIADA, contenido.toString()));
    }

    @Test
    void unStateUpdateActualizaLasPosicionesYLasZonasVisibles() throws Exception {
        empezarPartida();
        AnimalVisible primero = modelo.consultaAnimalesVisibles().get(0);

        recibir(new Mensaje(ConstantesProtocolo.ACTUALIZACION_ESTADO,
                foto(List.of(11, 12), animal("jaguar-1", 700, 420, true), animal("jaguar-2", 535, 420, true)).toString()));

        assertEquals(2, modelo.consultaZonasVisibles().size());
        assertEquals(700, modelo.consultaAnimalesVisibles().get(0).consultaPosicion().consultaPosicionX());
        assertTrue(modelo.consultaAnimalesVisibles().get(0) == primero, "el animal conocido conserva su objeto");
    }

    @Test
    void unStateUpdateConUnAnimalAjenoLoMarcaComoNoPropio() throws Exception {
        empezarPartida();
        recibir(new Mensaje(ConstantesProtocolo.ACTUALIZACION_ESTADO,
                foto(List.of(12), animal("jaguar-1", 725, 420, true), animal("tucan-1", 695, 430, false)).toString()));
        assertEquals(2, modelo.consultaAnimalesVisibles().size());
        assertFalse(modelo.consultaAnimalesVisibles().get(1).consultaPropio());
    }

    @Test
    void unStateUpdateMalFormadoSeIgnoraYLaPartidaSigue() throws Exception {
        empezarPartida();
        recibir(new Mensaje(ConstantesProtocolo.ACTUALIZACION_ESTADO, "{\"danio\":2}"));
        recibir(new Mensaje(ConstantesProtocolo.ACTUALIZACION_ESTADO, "esto no es json"));
        assertEquals(2, modelo.consultaAnimalesVisibles().size());
        assertEquals(null, modelo.consultaError());
    }

    @Test
    void unStateUpdateAntesDeEmpezarLaPartidaSeIgnora() throws Exception {
        recibir(new Mensaje(ConstantesProtocolo.ACTUALIZACION_ESTADO,
                foto(List.of(12), animal("jaguar-1", 725, 420, true)).toString()));
        assertTrue(modelo.consultaAnimalesVisibles().isEmpty());
    }

    @Test
    void seleccionarAnimalesSoloCambiaElModeloYNoEnviaNada() throws Exception {
        empezarPartida();
        controlador.seleccionAnimales(List.of(modelo.consultaAnimalesVisibles().get(1)));
        assertEquals(1, modelo.consultaAnimalesSeleccionados().size());
        assertEquals("jaguar-2", modelo.consultaAnimalesSeleccionados().get(0).consultaIdentificador());
        assertTrue(conexion.enviados.isEmpty());
    }

    @Test
    void unaOrdenDeMovimientoEnviaLosIdentificadoresYElDestino() throws Exception {
        empezarPartida();
        controlador.ordenMovimiento(modelo.consultaAnimalesVisibles(), new PuntoVisible(535, 420));

        Mensaje enviado = conexion.enviados.poll(3, TimeUnit.SECONDS);
        assertNotNull(enviado, "debió enviarse MOVE_ANIMALS");
        assertEquals(ConstantesProtocolo.MOVER_ANIMALES, enviado.consultaTipo());
        assertEquals("jaguar-1,jaguar-2", ConversorMensajes.campoDelContenido(enviado, ConstantesProtocolo.CAMPO_IDS_ANIMALES));
        assertEquals("535", ConversorMensajes.campoDelContenido(enviado, ConstantesProtocolo.CAMPO_X));
        assertEquals("420", ConversorMensajes.campoDelContenido(enviado, ConstantesProtocolo.CAMPO_Y));
    }

    @Test
    void unaOrdenSinAnimalesONiDestinoNoEnviaNada() throws Exception {
        empezarPartida();
        controlador.ordenMovimiento(List.of(), new PuntoVisible(1, 1));
        controlador.ordenMovimiento(modelo.consultaAnimalesVisibles(), null);
        controlador.ordenMovimiento(null, new PuntoVisible(1, 1));
        assertNotNull(MapaCliente.cargaDesdeRecurso());
        assertEquals(null, conexion.enviados.poll(300, TimeUnit.MILLISECONDS));
    }

    @Test
    void laEspecieDeLosAnimalesDelInicioSeLeeBien() throws Exception {
        empezarPartida();
        assertEquals(Especie.JAGUAR, modelo.consultaAnimalesVisibles().get(0).consultaEspecie());
    }

    private JsonObject evento(String tipo, String atacante, String objetivo, String especie, int x, int y) {
        JsonObject evento = new JsonObject();
        evento.addProperty(ConstantesProtocolo.CAMPO_TIPO_EVENTO, tipo);
        if (atacante != null) {
            evento.addProperty(ConstantesProtocolo.CAMPO_ATACANTE, atacante);
        }
        evento.addProperty(ConstantesProtocolo.CAMPO_OBJETIVO, objetivo);
        evento.addProperty(ConstantesProtocolo.CAMPO_ESPECIE, especie);
        evento.addProperty(ConstantesProtocolo.CAMPO_DANIO, 2);
        evento.addProperty(ConstantesProtocolo.CAMPO_X, x);
        evento.addProperty(ConstantesProtocolo.CAMPO_Y, y);
        return evento;
    }

    @Test
    void unStateUpdateConEventosCreaLosEfectosYLosBotsEliminados() throws Exception {
        empezarPartida();
        JsonObject contenido = foto(List.of(12), animal("jaguar-1", 725, 420, true));
        contenido.addProperty(ConstantesProtocolo.CAMPO_BOTS_ELIMINADOS, 2);
        JsonArray eventos = new JsonArray();
        eventos.add(evento(ConstantesProtocolo.EVENTO_ATAQUE, "jaguar-1", "cocodrilo-1", "jaguar", 700, 410));
        eventos.add(evento(ConstantesProtocolo.EVENTO_MUERTE, null, "cocodrilo-1", "cocodrilo", 700, 410));
        contenido.add(ConstantesProtocolo.CAMPO_EVENTOS, eventos);
        recibir(new Mensaje(ConstantesProtocolo.ACTUALIZACION_ESTADO, contenido.toString()));

        assertEquals(2, modelo.consultaBotsEliminados());
        List<jungleattack.cliente.modelo.EfectoCombate> efectos = modelo.consultaEfectosActivos(System.currentTimeMillis());
        assertEquals(2, efectos.size());
        assertFalse(efectos.get(0).consultaMuerte());
        assertEquals(Especie.JAGUAR, efectos.get(0).consultaEspecie());
        assertEquals(700, efectos.get(0).consultaX());
        assertTrue(efectos.get(1).consultaMuerte());
    }

    @Test
    void unStateUpdateSinEventosNoCreaEfectos() throws Exception {
        empezarPartida();
        recibir(new Mensaje(ConstantesProtocolo.ACTUALIZACION_ESTADO,
                foto(List.of(12), animal("jaguar-1", 725, 420, true)).toString()));
        assertEquals(0, modelo.consultaEfectosActivos(System.currentTimeMillis()).size());
        assertEquals(0, modelo.consultaBotsEliminados());
    }

    @Test
    void unaOrdenDeAtaqueEnviaLosAtacantesYElObjetivo() throws Exception {
        empezarPartida();
        AnimalVisible objetivo = new AnimalVisible("cocodrilo-1", Especie.COCODRILO, 10, 10, new PuntoVisible(700, 410), false, true);
        controlador.ordenAtaque(modelo.consultaAnimalesVisibles(), objetivo);

        Mensaje enviado = conexion.enviados.poll(3, TimeUnit.SECONDS);
        assertNotNull(enviado, "debió enviarse ATTACK");
        assertEquals(ConstantesProtocolo.ATACAR, enviado.consultaTipo());
        assertEquals("jaguar-1,jaguar-2", ConversorMensajes.campoDelContenido(enviado, ConstantesProtocolo.CAMPO_IDS_ANIMALES));
        assertEquals("cocodrilo-1", ConversorMensajes.campoDelContenido(enviado, ConstantesProtocolo.CAMPO_OBJETIVO));
    }

    @Test
    void unaOrdenDeAtaqueSinAtacantesONiObjetivoNoEnviaNada() throws Exception {
        empezarPartida();
        AnimalVisible objetivo = new AnimalVisible("cocodrilo-1", Especie.COCODRILO, 10, 10, new PuntoVisible(700, 410), false, true);
        controlador.ordenAtaque(List.of(), objetivo);
        controlador.ordenAtaque(modelo.consultaAnimalesVisibles(), null);
        controlador.ordenAtaque(null, objetivo);
        assertEquals(null, conexion.enviados.poll(300, TimeUnit.MILLISECONDS));
    }

    // ----- Fin de la partida -----

    private JsonObject estadisticas(String apodo, String especie, int bots, int perdidos, int tazas) {
        JsonObject datos = new JsonObject();
        datos.addProperty(ConstantesProtocolo.CAMPO_APODO, apodo);
        datos.addProperty(ConstantesProtocolo.CAMPO_ESPECIE, especie);
        datos.addProperty(ConstantesProtocolo.CAMPO_BOTS_ELIMINADOS, bots);
        datos.addProperty(ConstantesProtocolo.CAMPO_ANIMALES_PERDIDOS, perdidos);
        datos.addProperty(ConstantesProtocolo.CAMPO_TAZAS_CONSUMIDAS, tazas);
        return datos;
    }

    private Mensaje mensajeFin(String resultado, String razon) {
        JsonObject contenido = new JsonObject();
        contenido.addProperty(ConstantesProtocolo.CAMPO_RESULTADO, resultado);
        contenido.addProperty(ConstantesProtocolo.CAMPO_GANADOR, "Ana");
        contenido.addProperty(ConstantesProtocolo.CAMPO_RAZON, razon);
        contenido.addProperty(ConstantesProtocolo.CAMPO_DURACION, 125);
        contenido.add(ConstantesProtocolo.CAMPO_RESUMEN_PROPIO, estadisticas("Ana", "jaguar", 3, 2, 4));
        contenido.add(ConstantesProtocolo.CAMPO_RESUMEN_OPONENTE, estadisticas("Luis", "tucan", 1, 5, 0));
        return new Mensaje(ConstantesProtocolo.FIN_PARTIDA, contenido.toString());
    }

    @Test
    void alLlegarGameOverElModeloPasaAResumenConTodosLosDatos() throws Exception {
        empezarPartida();
        recibir(mensajeFin(ConstantesProtocolo.RESULTADO_VICTORIA, ConstantesProtocolo.RAZON_BANDERA));

        assertEquals(jungleattack.cliente.modelo.FaseCliente.RESUMEN, modelo.consultaFase());
        jungleattack.cliente.modelo.ResumenVisible resumen = modelo.consultaResumen();
        assertTrue(resumen.consultaVictoria());
        assertEquals("Ana", resumen.consultaGanador());
        assertEquals(ConstantesProtocolo.RAZON_BANDERA, resumen.consultaRazon());
        assertEquals(125, resumen.consultaDuracion());
        assertEquals("Ana", resumen.consultaPropio().consultaApodo());
        assertEquals(Especie.JAGUAR, resumen.consultaPropio().consultaEspecie());
        assertEquals(3, resumen.consultaPropio().consultaBotsEliminados());
        assertEquals(2, resumen.consultaPropio().consultaAnimalesPerdidos());
        assertEquals(4, resumen.consultaPropio().consultaTazasConsumidas());
        assertEquals("Luis", resumen.consultaOponente().consultaApodo());
        assertEquals(5, resumen.consultaOponente().consultaAnimalesPerdidos());
    }

    @Test
    void unaDerrotaSeLeeComoDerrota() throws Exception {
        empezarPartida();
        recibir(mensajeFin(ConstantesProtocolo.RESULTADO_DERROTA, ConstantesProtocolo.RAZON_BANDERA));
        assertFalse(modelo.consultaResumen().consultaVictoria());
    }

    @Test
    void unGameOverMalFormadoMuestraUnErrorYVuelveAlInicio() throws Exception {
        empezarPartida();
        recibir(new Mensaje(ConstantesProtocolo.FIN_PARTIDA, "{\"resultado\":\"VICTORIA\"}"));
        assertEquals(ConstantesProtocolo.MOTIVO_MENSAJE_INVALIDO, modelo.consultaError());
        assertEquals(jungleattack.cliente.modelo.FaseCliente.INICIO, modelo.consultaFase());
    }

    @Test
    void despuesDelGameOverLosEstadosYLosErroresTardiosSeIgnoran() throws Exception {
        empezarPartida();
        recibir(mensajeFin(ConstantesProtocolo.RESULTADO_VICTORIA, ConstantesProtocolo.RAZON_BANDERA));
        recibir(new Mensaje(ConstantesProtocolo.ACTUALIZACION_ESTADO,
                foto(List.of(12), animal("jaguar-1", 1, 1, true)).toString()));
        recibir(ConversorMensajes.creacionMensaje(ConstantesProtocolo.ERROR,
                ConstantesProtocolo.CAMPO_MOTIVO, ConstantesProtocolo.MOTIVO_FASE_INCORRECTA));
        assertEquals(jungleattack.cliente.modelo.FaseCliente.RESUMEN, modelo.consultaFase());
        assertEquals(null, modelo.consultaError());
    }

    @Test
    void volverAlInicioLimpiaLaPartidaYVuelveALaPantallaDeInicio() throws Exception {
        empezarPartida();
        recibir(mensajeFin(ConstantesProtocolo.RESULTADO_VICTORIA, ConstantesProtocolo.RAZON_ABANDONO));
        controlador.volverAlInicio();
        assertEquals(jungleattack.cliente.modelo.FaseCliente.INICIO, modelo.consultaFase());
        assertEquals(null, modelo.consultaResumen());
        assertTrue(modelo.consultaAnimalesVisibles().isEmpty());
    }
}

