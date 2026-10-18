package jungleattack.cliente.vista;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import common.model.Especie;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;
import jungleattack.cliente.constantes.ConstantesCliente;
import jungleattack.cliente.modelo.AnimalVisible;
import jungleattack.cliente.modelo.BanderaVisible;
import jungleattack.cliente.modelo.MapaCliente;
import jungleattack.cliente.modelo.ModeloJuego;
import jungleattack.cliente.modelo.PuntoVisible;
import jungleattack.cliente.modelo.TazaVisible;
import jungleattack.cliente.modelo.ZonaVisible;
import org.junit.jupiter.api.Test;

class PantallaJuegoTest {

    private static final int ANCHO = 950;
    private static final int ALTO = 550;

    // Partida con el jugador en la zona 12 (Corcovado): su manada, una taza y su bandera.
    private ModeloJuego modeloEnJuego() {
        MapaCliente mapa = MapaCliente.cargaDesdeRecurso();
        ZonaVisible zona = mapa.zonaPorIdentificador(12);
        int x = zona.consultaPosicion().consultaPosicionX();
        int y = zona.consultaPosicion().consultaPosicionY();
        ModeloJuego modelo = new ModeloJuego();
        modelo.actualizacionInicio(mapa, 12, 2, List.of(12),
                List.of(new AnimalVisible("jaguar-1", Especie.JAGUAR, 20, 20, new PuntoVisible(x + 30, y), true, false)),
                List.of(new TazaVisible("taza-1", new PuntoVisible(x, y - 45), 12)),
                List.of(new BanderaVisible(new PuntoVisible(x, y), 12, true)));
        return modelo;
    }

    // Dibuja la pantalla en una imagen del tamaño del mapa (la escala queda en 1) y la devuelve.
    private BufferedImage dibujar(ModeloJuego modelo) {
        PantallaJuego pantalla = new PantallaJuego(modelo);
        pantalla.setSize(ANCHO, ALTO);
        BufferedImage imagen = new BufferedImage(ANCHO, ALTO, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = imagen.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, ANCHO, ALTO);
        pantalla.print(g);
        g.dispose();
        return imagen;
    }

    private int brillo(BufferedImage imagen, int x, int y) {
        Color color = new Color(imagen.getRGB(x, y));
        return color.getRed() + color.getGreen() + color.getBlue();
    }

    // Un punto dentro de la zona, cerca de un vértice de su contorno (lejos de los caminos y los nodos).
    private PuntoVisible puntoInterior(ZonaVisible zona) {
        PuntoVisible vertice = zona.consultaPoligono().get(0);
        int x = vertice.consultaPosicionX() + (zona.consultaPosicion().consultaPosicionX() - vertice.consultaPosicionX()) / 8;
        int y = vertice.consultaPosicionY() + (zona.consultaPosicion().consultaPosicionY() - vertice.consultaPosicionY()) / 8;
        return new PuntoVisible(x, y);
    }

    @Test
    void sinPartidaSoloPintaElColorDeFondoYNoFalla() {
        BufferedImage imagen = dibujar(new ModeloJuego());
        assertEquals(ConstantesCliente.COLOR_FONDO_JUEGO.getRGB(), imagen.getRGB(10, 10));
    }

    @Test
    void conPartidaDibujaElMapaSinErrores() {
        BufferedImage imagen = dibujar(modeloEnJuego());
        assertNotEquals(ConstantesCliente.COLOR_FONDO_JUEGO.getRGB(), imagen.getRGB(10, 10)); // el panel de información ocupa esa esquina
    }

    @Test
    void laNieblaOscureceLasZonasQueElJugadorNoVe() {
        ModeloJuego modelo = modeloEnJuego();
        BufferedImage imagen = dibujar(modelo);
        MapaCliente mapa = modelo.consultaMapa();
        PuntoVisible enZonaVisible = puntoInterior(mapa.zonaPorIdentificador(12));
        PuntoVisible enZonaOculta = puntoInterior(mapa.zonaPorIdentificador(1));
        assertTrue(brillo(imagen, enZonaVisible.consultaPosicionX(), enZonaVisible.consultaPosicionY())
                > brillo(imagen, enZonaOculta.consultaPosicionX(), enZonaOculta.consultaPosicionY()),
                "la zona sin niebla debe verse más clara que la zona con niebla");
    }

    @Test
    void unaZonaVisibleQueNoSeMiraTieneNieblaSuaveYLaMiradaNinguna() {
        MapaCliente mapa = MapaCliente.cargaDesdeRecurso();
        ModeloJuego modelo = new ModeloJuego();
        modelo.actualizacionInicio(mapa, 12, 2, List.of(12, 11), List.of(), List.of(), List.of());
        BufferedImage mirando12 = dibujar(modelo);
        PuntoVisible punto = puntoInterior(mapa.zonaPorIdentificador(11));
        int claroSinMirar = brillo(mirando12, punto.consultaPosicionX(), punto.consultaPosicionY());

        modelo.actualizacionZonaObservada(11);
        BufferedImage mirando11 = dibujar(modelo);
        int claroMirando = brillo(mirando11, punto.consultaPosicionX(), punto.consultaPosicionY());

        assertTrue(claroMirando > claroSinMirar, "al mirar la zona 11 su niebla suave debe desaparecer");
    }

    @Test
    void elTemporizadorDeLaAnimacionSoloCorreConLaPantallaEnLaVentana() {
        PantallaJuego pantalla = new PantallaJuego(modeloEnJuego());
        pantalla.actualizacionAnimacion(); // repintar sin estar en una ventana no debe fallar
        pantalla.removeNotify(); // quitarla sin haberla agregado tampoco
    }
}
