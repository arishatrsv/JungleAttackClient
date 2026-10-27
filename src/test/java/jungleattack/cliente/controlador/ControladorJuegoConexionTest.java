package jungleattack.cliente.controlador;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import common.constantes.ConstantesProtocolo;
import common.model.Especie;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import javax.swing.SwingUtilities;
import jungleattack.cliente.modelo.AnimalVisible;
import jungleattack.cliente.modelo.FaseCliente;
import jungleattack.cliente.modelo.ModeloJuego;
import jungleattack.cliente.modelo.PuntoVisible;
import jungleattack.cliente.protocolo.Mensaje;
import jungleattack.cliente.red.ConexionServidor;
import org.junit.jupiter.api.Test;

/** Pruebas de cómo el controlador maneja la conexión: cerrarla al perderla, reconectar y enviar en orden. */
class ControladorJuegoConexionTest {

    // Conexión falsa: cuenta los cierres, recuerda si está "conectada" y guarda lo que se envía.
    private static class ConexionFalsa extends ConexionServidor {
        private final BlockingQueue<Mensaje> enviados = new LinkedBlockingQueue<>();
        private volatile boolean conectada;
        private volatile int cierres;

        @Override
        public synchronized boolean conexion(Consumer<Mensaje> destino) {
            conectada = true;
            return true;
        }

        @Override
        public void envioMensaje(Mensaje mensaje) {
            enviados.add(mensaje);
        }

        @Override
        public boolean consultaConectado() {
            return conectada;
        }

        @Override
        public synchronized void cierre() {
            cierres++;
            conectada = false;
        }
    }

    private final ModeloJuego modelo = new ModeloJuego();
    private final ConexionFalsa conexion = new ConexionFalsa();
    private final ControladorJuego controlador = new ControladorJuego(modelo, conexion);

    private void recibir(Mensaje mensaje) throws Exception {
        controlador.procesamientoActualizacion(mensaje);
        SwingUtilities.invokeAndWait(() -> { });
    }

    private AnimalVisible animal(String id) {
        return new AnimalVisible(id, Especie.JAGUAR, 20, 20, new PuntoVisible(100, 100), true, false);
    }

    @Test
    void perderLaConexionLaCierraParaPoderVolverAConectar() throws Exception {
        conexion.conectada = true;
        recibir(new Mensaje(ConstantesProtocolo.DESCONEXION, "{}"));
        assertEquals(1, conexion.cierres);
        assertEquals(false, conexion.consultaConectado());
        assertEquals(FaseCliente.INICIO, modelo.consultaFase());
        assertEquals(ConstantesProtocolo.MOTIVO_SIN_CONEXION, modelo.consultaError());
    }

    @Test
    void despuesDePerderLaConexionSePuedeCrearOtraPartida() throws Exception {
        conexion.conectada = true;
        recibir(new Mensaje(ConstantesProtocolo.DESCONEXION, "{}"));

        controlador.creacionPartida("Ana"); // ya no está conectada: debe conectar de nuevo y enviar

        Mensaje enviado = conexion.enviados.poll(3, TimeUnit.SECONDS);
        assertNotNull(enviado, "debió enviarse CREATE_MATCH");
        assertEquals(ConstantesProtocolo.CREAR_PARTIDA, enviado.consultaTipo());
    }

    @Test
    void conConexionLasOrdenesSalenEnElActoYEnElMismoOrden() {
        conexion.conectada = true;
        List<AnimalVisible> animales = List.of(animal("jaguar-1"));

        controlador.ordenMovimiento(animales, new PuntoVisible(10, 10));
        controlador.ordenAtaque(animales, animal("tucan-1"));

        // Sin esperar: no se usan hilos aparte cuando ya hay conexión.
        assertEquals(2, conexion.enviados.size());
        assertEquals(ConstantesProtocolo.MOVER_ANIMALES, conexion.enviados.poll().consultaTipo());
        assertEquals(ConstantesProtocolo.ATACAR, conexion.enviados.poll().consultaTipo());
    }

    @Test
    void cancelarLaPartidaCierraLaConexionYVuelveAlInicio() {
        conexion.conectada = true;
        modelo.actualizacionEstado(FaseCliente.ESPERA);

        controlador.cancelarPartida();

        assertEquals(1, conexion.cierres);
        assertEquals(FaseCliente.INICIO, modelo.consultaFase());
    }
}
