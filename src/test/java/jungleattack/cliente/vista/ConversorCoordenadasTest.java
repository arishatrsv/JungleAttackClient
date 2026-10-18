package jungleattack.cliente.vista;

import static org.junit.jupiter.api.Assertions.assertEquals;

import jungleattack.cliente.modelo.PuntoVisible;
import org.junit.jupiter.api.Test;

class ConversorCoordenadasTest {

    @Test
    void conElPanelDelTamanoDelMapaNoCambiaNada() {
        ConversorCoordenadas conversor = new ConversorCoordenadas();
        conversor.actualizacionVentana(950, 550, 950, 550);
        PuntoVisible pantalla = conversor.conversionMapaAPantalla(new PuntoVisible(100, 200));
        assertEquals(100, pantalla.consultaPosicionX());
        assertEquals(200, pantalla.consultaPosicionY());
        assertEquals(1.0, conversor.consultaEscala(), 0.0001);
    }

    @Test
    void conUnPanelAlTodoElMapaSeAgrandaYQuedaCentrado() {
        ConversorCoordenadas conversor = new ConversorCoordenadas();
        conversor.actualizacionVentana(1900, 1500, 950, 550); // el ancho manda: escala 2, sobra alto
        assertEquals(2.0, conversor.consultaEscala(), 0.0001);
        assertEquals(0.0, conversor.consultaDesplazamientoX(), 0.0001);
        assertEquals(200.0, conversor.consultaDesplazamientoY(), 0.0001);
        PuntoVisible origen = conversor.conversionMapaAPantalla(new PuntoVisible(0, 0));
        assertEquals(0, origen.consultaPosicionX());
        assertEquals(200, origen.consultaPosicionY());
    }

    @Test
    void convertirDeIdaYVueltaDevuelveElMismoPunto() {
        ConversorCoordenadas conversor = new ConversorCoordenadas();
        conversor.actualizacionVentana(1000, 700, 950, 550);
        PuntoVisible original = new PuntoVisible(375, 240);
        PuntoVisible vuelta = conversor.conversionPantallaAMapa(conversor.conversionMapaAPantalla(original));
        assertEquals(375, vuelta.consultaPosicionX());
        assertEquals(240, vuelta.consultaPosicionY());
    }
}
