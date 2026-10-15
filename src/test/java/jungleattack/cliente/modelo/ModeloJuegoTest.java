package jungleattack.cliente.modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class ModeloJuegoTest {

    @Test
    void empiezaEnLaFaseDeInicio() {
        assertEquals(FaseCliente.INICIO, new ModeloJuego().consultaFase());
    }

    @Test
    void cambiarLaFaseAvisaALosObservadores() {
        ModeloJuego modelo = new ModeloJuego();
        AtomicInteger avisos = new AtomicInteger();
        modelo.registroObservador(avisos::incrementAndGet); // el observador solo cuenta los avisos

        modelo.actualizacionEstado(FaseCliente.ESPERA);

        assertEquals(FaseCliente.ESPERA, modelo.consultaFase());
        assertEquals(1, avisos.get());
    }

    @Test
    void elErrorSeGuardaYSePuedeLimpiar() {
        ModeloJuego modelo = new ModeloJuego();
        AtomicInteger avisos = new AtomicInteger();
        modelo.registroObservador(avisos::incrementAndGet);

        modelo.actualizacionError("SIN_CONEXION");
        assertEquals("SIN_CONEXION", modelo.consultaError());
        assertEquals(1, avisos.get());

        modelo.limpiezaError(); // limpiar no debe avisar de nuevo
        assertNull(modelo.consultaError());
        assertEquals(1, avisos.get());
    }

    @Test
    void elCodigoDePartidaSeGuardaEnElJugadorLocal() {
        ModeloJuego modelo = new ModeloJuego();
        modelo.consultaJugadorLocal().actualizacionCodigoPartida("A7K2");
        assertEquals("A7K2", modelo.consultaJugadorLocal().consultaCodigoPartida());
    }
}
