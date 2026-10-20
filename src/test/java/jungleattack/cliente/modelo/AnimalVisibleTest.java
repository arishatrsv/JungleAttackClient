package jungleattack.cliente.modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import common.model.Especie;
import org.junit.jupiter.api.Test;

class AnimalVisibleTest {

    private AnimalVisible animal() {
        return new AnimalVisible("jaguar-1", Especie.JAGUAR, 20, 20, new PuntoVisible(100, 200), true, false);
    }

    @Test
    void alCrearseSuPosicionDibujadaEsLaReal() {
        AnimalVisible animal = animal();
        assertEquals(100.0, animal.consultaVisualX(), 0.0001);
        assertEquals(200.0, animal.consultaVisualY(), 0.0001);
    }

    @Test
    void elServidorCambiaLaPosicionRealPeroLaDibujadaNoSalta() {
        AnimalVisible animal = animal();
        animal.actualizacionDesdeServidor(18, new PuntoVisible(160, 200));
        assertEquals(160, animal.consultaPosicion().consultaPosicionX());
        assertEquals(18, animal.consultaEnergia());
        assertEquals(100.0, animal.consultaVisualX(), 0.0001); // todavía no se movió
    }

    @Test
    void cadaCuadroAcercaLaPosicionDibujadaALaReal() {
        AnimalVisible animal = animal();
        animal.actualizacionDesdeServidor(20, new PuntoVisible(160, 200));
        animal.actualizacionVisual(33);
        double primera = animal.consultaVisualX();
        assertTrue(primera > 100 && primera < 160);
        animal.actualizacionVisual(33);
        assertTrue(animal.consultaVisualX() > primera && animal.consultaVisualX() < 160);
    }

    @Test
    void conMuchoTiempoLlegaExactamenteALaPosicionReal() {
        AnimalVisible animal = animal();
        animal.actualizacionDesdeServidor(20, new PuntoVisible(160, 230));
        animal.actualizacionVisual(2000);
        assertEquals(160.0, animal.consultaVisualX(), 0.0001);
        assertEquals(230.0, animal.consultaVisualY(), 0.0001);
    }

    @Test
    void sinTiempoTranscurridoNoSeMueve() {
        AnimalVisible animal = animal();
        animal.actualizacionDesdeServidor(20, new PuntoVisible(160, 200));
        animal.actualizacionVisual(0);
        assertEquals(100.0, animal.consultaVisualX(), 0.0001);
    }

    @Test
    void sePuedeColocarLaPosicionDibujadaDeInmediato() {
        AnimalVisible animal = animal();
        animal.actualizacionDesdeServidor(20, new PuntoVisible(160, 200));
        animal.reinicioPosicionVisual();
        assertEquals(160.0, animal.consultaVisualX(), 0.0001);
    }
}
