package jungleattack.cliente.modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import common.model.Especie;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class ModeloJuegoInicioTest {

    private final MapaCliente mapa = MapaCliente.cargaDesdeRecurso();

    private AnimalVisible animal(String id) {
        return new AnimalVisible(id, Especie.JAGUAR, 20, 20, new PuntoVisible(700, 480), true, false);
    }

    // Deja el modelo en JUEGO con dos zonas visibles (12 y 11) y la zona 12 como inicial.
    private ModeloJuego modeloEnJuego() {
        ModeloJuego modelo = new ModeloJuego();
        modelo.actualizacionInicio(mapa, 12, 2, List.of(12, 11), List.of(animal("jaguar-1"), animal("jaguar-2")),
                List.of(new TazaVisible("taza-1", new PuntoVisible(730, 420), 12)),
                List.of(new BanderaVisible(new PuntoVisible(730, 480), 12, true)));
        return modelo;
    }

    @Test
    void elInicioPasaAJuegoYGuardaLaPartidaAvisandoUnaSolaVez() {
        ModeloJuego modelo = new ModeloJuego();
        AtomicInteger avisos = new AtomicInteger();
        modelo.registroObservador(avisos::incrementAndGet);

        modelo.actualizacionInicio(mapa, 12, 2, List.of(12), List.of(animal("jaguar-1")), List.of(), List.of());

        assertEquals(FaseCliente.JUEGO, modelo.consultaFase());
        assertEquals(12, modelo.consultaZonaInicial());
        assertEquals(12, modelo.consultaZonaObservada());
        assertEquals(2, modelo.consultaDanio());
        assertEquals(1, modelo.consultaAnimalesVisibles().size());
        assertEquals(1, avisos.get());
    }

    @Test
    void soloSeGuardanLasZonasVisiblesQueExistenEnElMapa() {
        ModeloJuego modelo = new ModeloJuego();
        modelo.actualizacionInicio(mapa, 12, 2, List.of(12, 99), List.of(), List.of(), List.of());
        assertEquals(1, modelo.consultaZonasVisibles().size());
    }

    @Test
    void sePuedeMirarOtraZonaSiempreQueSeaVisible() {
        ModeloJuego modelo = modeloEnJuego();
        assertTrue(modelo.actualizacionZonaObservada(11));
        assertEquals(11, modelo.consultaZonaObservada());
        assertEquals(12, modelo.consultaZonaInicial());
    }

    @Test
    void noSePuedeMirarUnaZonaQueNoSeVe() {
        ModeloJuego modelo = modeloEnJuego();
        assertFalse(modelo.actualizacionZonaObservada(1));
        assertEquals(12, modelo.consultaZonaObservada());
    }

    @Test
    void cambiarLaZonaQueSeMiraAvisaALosObservadores() {
        ModeloJuego modelo = modeloEnJuego();
        AtomicInteger avisos = new AtomicInteger();
        modelo.registroObservador(avisos::incrementAndGet);
        modelo.actualizacionZonaObservada(11);
        assertEquals(1, avisos.get());
    }

    @Test
    void reiniciarLaPartidaBorraTodoLoDeLaPartidaEnCurso() {
        ModeloJuego modelo = modeloEnJuego();
        modelo.reinicioPartida();
        assertEquals(FaseCliente.INICIO, modelo.consultaFase());
        assertNull(modelo.consultaMapa());
        assertTrue(modelo.consultaZonasVisibles().isEmpty());
        assertTrue(modelo.consultaAnimalesVisibles().isEmpty());
        assertTrue(modelo.consultaTazasVisibles().isEmpty());
        assertTrue(modelo.consultaBanderasVisibles().isEmpty());
        assertEquals(0, modelo.consultaDanio());
    }
}