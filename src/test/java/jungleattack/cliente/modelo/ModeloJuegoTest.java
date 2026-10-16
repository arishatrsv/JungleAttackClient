package jungleattack.cliente.modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import common.model.Especie;
import java.util.List;
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

    @Test
    void laSeleccionActualizaLosDatosYPasaALaFaseSeleccionAvisandoUnaVez() {
        ModeloJuego modelo = new ModeloJuego();
        AtomicInteger avisos = new AtomicInteger();
        modelo.registroObservador(avisos::incrementAndGet);

        modelo.actualizacionSeleccion(List.of(Especie.TUCAN, Especie.COCODRILO), Especie.JAGUAR, null, false);

        assertEquals(FaseCliente.SELECCION, modelo.consultaFase());
        assertEquals(2, modelo.consultaEspeciesDisponibles().size());
        assertEquals(Especie.JAGUAR, modelo.consultaJugadorLocal().consultaEspecie());
        assertNull(modelo.consultaEspecieOponente());
        assertFalse(modelo.consultaSeleccionCompleta());
        assertEquals(1, avisos.get()); // un solo aviso, no uno por cada dato
    }

    @Test
    void reiniciarLaPartidaLimpiaLosDatosYVuelveAlInicio() {
        ModeloJuego modelo = new ModeloJuego();
        modelo.consultaJugadorLocal().actualizacionApodo("Ana");
        modelo.consultaJugadorLocal().actualizacionCodigoPartida("A7K2");
        modelo.actualizacionSeleccion(List.of(Especie.TUCAN), Especie.JAGUAR, Especie.TUCAN, true);

        modelo.reinicioPartida();

        assertEquals(FaseCliente.INICIO, modelo.consultaFase());
        assertNull(modelo.consultaJugadorLocal().consultaCodigoPartida());
        assertNull(modelo.consultaJugadorLocal().consultaEspecie());
        assertEquals("Ana", modelo.consultaJugadorLocal().consultaApodo()); // el apodo se conserva
        assertTrue(modelo.consultaEspeciesDisponibles().isEmpty());
        assertFalse(modelo.consultaSeleccionCompleta());
    }
}