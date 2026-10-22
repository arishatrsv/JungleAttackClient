package jungleattack.cliente.modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import common.model.Especie;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class ModeloJuegoInicioTest {

    private final MapaCliente mapa = MapaCliente.cargaDesdeRecurso();

    private AnimalVisible propio(String id, int x, int y) {
        return new AnimalVisible(id, Especie.JAGUAR, 20, 20, new PuntoVisible(x, y), true, false);
    }

    private AnimalVisible ajeno(String id, int x, int y) {
        return new AnimalVisible(id, Especie.TUCAN, 20, 20, new PuntoVisible(x, y), false, false);
    }

    // Partida iniciada: dos animales propios en la zona 12 (Corcovado), que se mira.
    private ModeloJuego modeloEnJuego() {
        ModeloJuego modelo = new ModeloJuego();
        modelo.actualizacionInicio(mapa, 12, 2, List.of(12), List.of(propio("jaguar-1", 725, 420), propio("jaguar-2", 665, 420)),
                List.of(), List.of());
        return modelo;
    }

    @Test
    void sinPartidaEnCursoLasActualizacionesSeIgnoran() {
        ModeloJuego modelo = new ModeloJuego();
        AtomicInteger avisos = new AtomicInteger();
        modelo.registroObservador(avisos::incrementAndGet);
        modelo.actualizacionEstadoVisible(2, List.of(12), List.of(propio("jaguar-1", 1, 1)), List.of(), List.of());
        assertEquals(0, avisos.get());
        assertTrue(modelo.consultaAnimalesVisibles().isEmpty());
    }

    @Test
    void unaActualizacionReemplazaLoQueSeVeYAvisaUnaVez() {
        ModeloJuego modelo = modeloEnJuego();
        AtomicInteger avisos = new AtomicInteger();
        modelo.registroObservador(avisos::incrementAndGet);
        modelo.actualizacionEstadoVisible(3, List.of(12),
                List.of(propio("jaguar-1", 700, 420), propio("jaguar-2", 665, 420), ajeno("tucan-1", 695, 430)),
                List.of(new TazaVisible("taza-1", new PuntoVisible(695, 380), 12)), List.of());
        assertEquals(1, avisos.get());
        assertEquals(3, modelo.consultaDanio());
        assertEquals(3, modelo.consultaAnimalesVisibles().size());
        assertEquals(1, modelo.consultaTazasVisibles().size());
    }

    @Test
    void losAnimalesYaConocidosConservanSuObjetoYSuPosicionDibujada() {
        ModeloJuego modelo = modeloEnJuego();
        AnimalVisible original = modelo.consultaAnimalesVisibles().get(0);
        original.actualizacionVisual(0);
        modelo.actualizacionEstadoVisible(2, List.of(12), List.of(propio("jaguar-1", 700, 420), propio("jaguar-2", 665, 420)),
                List.of(), List.of());
        AnimalVisible despues = modelo.consultaAnimalesVisibles().get(0);
        assertSame(original, despues);
        assertEquals(700, despues.consultaPosicion().consultaPosicionX()); // la posición real se actualizó
        assertEquals(725.0, despues.consultaVisualX(), 0.0001); // la dibujada se acerca despacio
    }

    @Test
    void unAnimalQueDejaDeVerseSeQuita() {
        ModeloJuego modelo = modeloEnJuego();
        modelo.actualizacionEstadoVisible(2, List.of(12), List.of(propio("jaguar-1", 725, 420)), List.of(), List.of());
        assertEquals(1, modelo.consultaAnimalesVisibles().size());
    }

    @Test
    void laSeleccionSoloAdmiteAnimalesPropiosQueSeVen() {
        ModeloJuego modelo = modeloEnJuego();
        modelo.actualizacionEstadoVisible(2, List.of(12),
                List.of(propio("jaguar-1", 725, 420), propio("jaguar-2", 665, 420), ajeno("tucan-1", 695, 430)), List.of(), List.of());
        modelo.actualizacionAnimalesSeleccionados(List.of("jaguar-1", "tucan-1", "dragon-1", "jaguar-1"));
        assertEquals(1, modelo.consultaAnimalesSeleccionados().size());
        assertTrue(modelo.consultaSeleccionado(modelo.consultaAnimalesVisibles().get(0)));
    }

    @Test
    void unAnimalSeleccionadoQueDejaDeVerseSeQuitaDeLaSeleccion() {
        ModeloJuego modelo = modeloEnJuego();
        modelo.actualizacionAnimalesSeleccionados(List.of("jaguar-1", "jaguar-2"));
        modelo.actualizacionEstadoVisible(2, List.of(12), List.of(propio("jaguar-2", 665, 420)), List.of(), List.of());
        assertEquals(1, modelo.consultaAnimalesSeleccionados().size());
        assertEquals("jaguar-2", modelo.consultaAnimalesSeleccionados().get(0).consultaIdentificador());
    }

    @Test
    void siLaZonaObservadaDejaDeSerVisibleSePasaAOtra() {
        ModeloJuego modelo = modeloEnJuego();
        assertEquals(12, modelo.consultaZonaObservada());
        // Los animales se fueron a la zona 11 (535, 420): ahora solo se ve esa.
        modelo.actualizacionEstadoVisible(2, List.of(11), List.of(propio("jaguar-1", 535, 420)), List.of(), List.of());
        assertEquals(11, modelo.consultaZonaObservada());
    }

    @Test
    void siLaZonaObservadaSigueVisibleNoCambia() {
        ModeloJuego modelo = modeloEnJuego();
        modelo.actualizacionEstadoVisible(2, List.of(11, 12), List.of(propio("jaguar-1", 725, 420)), List.of(), List.of());
        assertEquals(12, modelo.consultaZonaObservada());
    }

    @Test
    void losAnimalesDeLaZonaObservadaSonSoloLosQueEstanEnEllaYVivos() {
        ModeloJuego modelo = new ModeloJuego();
        AnimalVisible enOtraZona = propio("jaguar-3", 535, 420);
        AnimalVisible muerto = propio("jaguar-4", 700, 420);
        muerto.actualizacionEnergia(0);
        modelo.actualizacionInicio(mapa, 12, 2, List.of(11, 12),
                List.of(propio("jaguar-1", 725, 420), propio("jaguar-2", 665, 420), enOtraZona, muerto), List.of(), List.of());
        List<AnimalVisible> enZona = modelo.consultaAnimalesEnZonaObservada();
        assertEquals(2, enZona.size());
        assertFalse(enZona.contains(enOtraZona));
        assertFalse(enZona.contains(muerto));
    }

    @Test
    void reiniciarLaPartidaBorraLaSeleccion() {
        ModeloJuego modelo = modeloEnJuego();
        modelo.actualizacionAnimalesSeleccionados(List.of("jaguar-1"));
        modelo.reinicioPartida();
        assertTrue(modelo.consultaAnimalesSeleccionados().isEmpty());
    }
}
