package jungleattack.cliente.modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.StringReader;
import org.junit.jupiter.api.Test;

class MapaClienteTest {

    private MapaCliente mapa() {
        return MapaCliente.cargaDesdeRecurso();
    }

    @Test
    void elMapaRealTieneDoceZonasQuinceCaminosYSuTamano() {
        MapaCliente mapa = mapa();
        assertEquals(12, mapa.consultaZonas().size());
        assertEquals(15, mapa.consultaCaminos().size());
        assertEquals(950, mapa.consultaAncho());
        assertEquals(550, mapa.consultaAlto());
    }

    @Test
    void cadaZonaTieneNombreContornoYEtiqueta() {
        for (ZonaVisible zona : mapa().consultaZonas()) {
            assertNotNull(zona.consultaNombre());
            assertTrue(zona.consultaPoligono().size() >= 3);
            assertNotNull(zona.consultaPosicionEtiqueta());
            assertTrue(zona.consultaAnclajeEtiqueta().equals(ZonaVisible.ANCLAJE_INICIO)
                    || zona.consultaAnclajeEtiqueta().equals(ZonaVisible.ANCLAJE_FIN)
                    || zona.consultaAnclajeEtiqueta().equals(ZonaVisible.ANCLAJE_CENTRO));
        }
    }

    @Test
    void cadaCaminoQuedaEnLasConexionesDeSusDosZonas() {
        int conexiones = 0;
        for (ZonaVisible zona : mapa().consultaZonas()) {
            assertTrue(zona.consultaConexiones().size() >= 1);
            conexiones += zona.consultaConexiones().size();
        }
        assertEquals(30, conexiones); // 15 caminos contados en sus dos extremos
    }

    @Test
    void sePuedeBuscarUnaZonaPorSuNumero() {
        MapaCliente mapa = mapa();
        assertEquals("Monteverde", mapa.zonaPorIdentificador(4).consultaNombre());
        assertNull(mapa.zonaPorIdentificador(0));
        assertNull(mapa.zonaPorIdentificador(13));
    }

    @Test
    void elCentroDeCadaZonaEstaDentroDeSuContorno() {
        MapaCliente mapa = mapa();
        for (ZonaVisible zona : mapa.consultaZonas()) {
            assertTrue(zona.contienePunto(zona.consultaPosicion()));
            assertSame(zona, mapa.zonaPorPosicion(zona.consultaPosicion()));
        }
    }

    @Test
    void unPuntoFueraDeTodasLasZonasSeAsignaALaMasCercana() {
        MapaCliente mapa = mapa();
        PuntoVisible lejos = new PuntoVisible(0, 0);
        for (ZonaVisible zona : mapa.consultaZonas()) {
            assertFalse(zona.contienePunto(lejos));
        }
        assertEquals(1, mapa.zonaPorPosicion(lejos).consultaIdentificador());
    }

    @Test
    void sePuedeCargarUnMapaPropioDesdeUnTexto() {
        String json = "{\"ancho\":100,\"alto\":50,\"zonas\":["
                + "{\"id\":1,\"nombre\":\"A\",\"x\":10,\"y\":10,\"etiqueta\":{\"x\":5,\"y\":5,\"anclaje\":\"inicio\"},"
                + "\"poligono\":[[0,0],[20,0],[20,20],[0,20]]},"
                + "{\"id\":2,\"nombre\":\"B\",\"x\":60,\"y\":10,\"etiqueta\":{\"x\":55,\"y\":5,\"anclaje\":\"fin\"},"
                + "\"poligono\":[[40,0],[80,0],[80,20],[40,20]]}],"
                + "\"caminos\":[{\"id\":1,\"desde\":1,\"hasta\":2,\"longitud\":50}]}";
        MapaCliente mapa = MapaCliente.cargaDesdeLector(new StringReader(json));
        assertEquals(2, mapa.consultaZonas().size());
        assertEquals(1, mapa.consultaCaminos().size());
        assertEquals(50.0, mapa.consultaCaminos().get(0).consultaLongitud(), 0.001);
        assertEquals("fin", mapa.zonaPorIdentificador(2).consultaAnclajeEtiqueta());
    }

    @Test
    void unCaminoQueUneUnaZonaInexistenteEsUnError() {
        String json = "{\"ancho\":100,\"alto\":50,\"zonas\":["
                + "{\"id\":1,\"nombre\":\"A\",\"x\":10,\"y\":10,\"etiqueta\":{\"x\":5,\"y\":5,\"anclaje\":\"inicio\"},"
                + "\"poligono\":[[0,0],[20,0],[20,20],[0,20]]}],"
                + "\"caminos\":[{\"id\":1,\"desde\":1,\"hasta\":9,\"longitud\":50}]}";
        assertThrows(IllegalArgumentException.class, () -> MapaCliente.cargaDesdeLector(new StringReader(json)));
    }
}
