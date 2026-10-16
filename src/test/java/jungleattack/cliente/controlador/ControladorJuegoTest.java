package jungleattack.cliente.controlador;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
}
