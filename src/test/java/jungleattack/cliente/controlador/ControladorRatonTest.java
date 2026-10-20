package jungleattack.cliente.controlador;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import common.constantes.ConstantesProtocolo;
import common.model.Especie;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import jungleattack.cliente.modelo.AnimalVisible;
import jungleattack.cliente.modelo.MapaCliente;
import jungleattack.cliente.modelo.ModeloJuego;
import jungleattack.cliente.modelo.PuntoVisible;
import jungleattack.cliente.protocolo.ConversorMensajes;
import jungleattack.cliente.protocolo.Mensaje;
import jungleattack.cliente.red.ConexionServidor;
import jungleattack.cliente.vista.PantallaJuego;
import org.junit.jupiter.api.Test;

/**
 * Pruebas del ratón sin ventana ni servidor. La pantalla mide 950 x 550, igual que el mapa,
 * así que un píxel de la pantalla es una unidad del mapa.
 */
class ControladorRatonTest {

    private static class ConexionFalsa extends ConexionServidor {
        private final BlockingQueue<Mensaje> enviados = new LinkedBlockingQueue<>();

        @Override
        public synchronized boolean conexion(Consumer<Mensaje> destino) {
            return true;
        }

        @Override
        public void envioMensaje(Mensaje mensaje) {
            enviados.add(mensaje);
        }
    }

    private final ModeloJuego modelo = new ModeloJuego();
    private final ConexionFalsa conexion = new ConexionFalsa();
    private final ControladorJuego controlador = new ControladorJuego(modelo, conexion);
    private final PantallaJuego pantalla = new PantallaJuego(modelo, controlador);
    private final ControladorRaton raton = new ControladorRaton(modelo, pantalla, controlador);

    // Partida con tres animales propios en la zona 12 (se mira), y la zona 11 también visible.
    private void empezarPartida() {
        MapaCliente mapa = MapaCliente.cargaDesdeRecurso();
        modelo.actualizacionInicio(mapa, 12, 2, List.of(11, 12),
                List.of(propio("jaguar-1", 725, 420), propio("jaguar-2", 665, 420), propio("jaguar-3", 695, 450),
                        new AnimalVisible("tucan-1", Especie.TUCAN, 20, 20, new PuntoVisible(695, 380), false, false)),
                List.of(), List.of());
        pantalla.setSize(950, 550);
        pantalla.consultaConversor().actualizacionVentana(950, 550, 950, 550);
    }

    private AnimalVisible propio(String id, int x, int y) {
        return new AnimalVisible(id, Especie.JAGUAR, 20, 20, new PuntoVisible(x, y), true, false);
    }

    private void seleccionados(String... ids) {
        modelo.actualizacionAnimalesSeleccionados(List.of(ids));
    }

    @Test
    void unClicSobreUnAnimalPropioLoSelecciona() {
        empezarPartida();
        raton.clic(new Point(727, 421), false);
        assertEquals(1, modelo.consultaAnimalesSeleccionados().size());
        assertEquals("jaguar-1", modelo.consultaAnimalesSeleccionados().get(0).consultaIdentificador());
    }

    @Test
    void unClicEnOtroAnimalCambiaLaSeleccion() {
        empezarPartida();
        raton.clic(new Point(725, 420), false);
        raton.clic(new Point(665, 420), false);
        assertEquals(1, modelo.consultaAnimalesSeleccionados().size());
        assertEquals("jaguar-2", modelo.consultaAnimalesSeleccionados().get(0).consultaIdentificador());
    }

    @Test
    void conControlSeAgregaYSeQuitaDeLaSeleccion() {
        empezarPartida();
        raton.clic(new Point(725, 420), false);
        raton.clic(new Point(665, 420), true);
        assertEquals(2, modelo.consultaAnimalesSeleccionados().size());
        raton.clic(new Point(725, 420), true); // ya estaba: se quita
        assertEquals(1, modelo.consultaAnimalesSeleccionados().size());
        assertEquals("jaguar-2", modelo.consultaAnimalesSeleccionados().get(0).consultaIdentificador());
    }

    @Test
    void unClicSobreUnAnimalAjenoNoLoSelecciona() {
        empezarPartida();
        raton.clic(new Point(695, 380), false);
        assertTrue(modelo.consultaAnimalesSeleccionados().isEmpty());
    }

    @Test
    void unClicLejosDeLosAnimalesQuitaLaSeleccion() {
        empezarPartida();
        seleccionados("jaguar-1");
        raton.clic(new Point(640, 440), false); // dentro de la zona 12, sin animales cerca
        assertTrue(modelo.consultaAnimalesSeleccionados().isEmpty());
    }

    @Test
    void unClicEnOtraZonaVisiblePasaAMirarEsaZona() {
        empezarPartida();
        assertEquals(12, modelo.consultaZonaObservada());
        raton.clic(new Point(560, 400), false); // dentro de la zona 11 (Manuel Antonio)
        assertEquals(11, modelo.consultaZonaObservada());
    }

    @Test
    void unClicEnUnaZonaQueNoSeVeNoCambiaDeZona() {
        empezarPartida();
        raton.clic(new Point(100, 100), false); // zona que no está entre las visibles
        assertEquals(12, modelo.consultaZonaObservada());
    }

    @Test
    void unRectanguloSeleccionaLosAnimalesPropiosQueQuedanDentro() {
        empezarPartida();
        raton.seleccion(new Rectangle(640, 400, 100, 60), false); // incluye jaguar-1 (725,420) y jaguar-2 (665,420) y jaguar-3 (695,450)
        assertEquals(3, modelo.consultaAnimalesSeleccionados().size());
    }

    @Test
    void unRectanguloNoSeleccionaLosAnimalesAjenos() {
        empezarPartida();
        raton.seleccion(new Rectangle(600, 340, 200, 140), false); // incluye el tucán (695,380)
        assertEquals(3, modelo.consultaAnimalesSeleccionados().size());
        for (AnimalVisible animal : modelo.consultaAnimalesSeleccionados()) {
            assertTrue(animal.consultaPropio());
        }
    }

    @Test
    void unRectanguloConControlAgregaALaSeleccion() {
        empezarPartida();
        seleccionados("jaguar-3");
        raton.seleccion(new Rectangle(655, 410, 80, 20), true); // jaguar-1 y jaguar-2
        assertEquals(3, modelo.consultaAnimalesSeleccionados().size());
    }

    @Test
    void unRectanguloVacioDejaSinSeleccion() {
        empezarPartida();
        seleccionados("jaguar-1");
        raton.seleccion(new Rectangle(10, 10, 20, 20), false);
        assertTrue(modelo.consultaAnimalesSeleccionados().isEmpty());
    }

    @Test
    void elClicDerechoMandaMoverLosSeleccionadosAlPunto() throws Exception {
        empezarPartida();
        seleccionados("jaguar-1", "jaguar-2");
        raton.seleccionDestino(new Point(535, 420));

        Mensaje enviado = conexion.enviados.poll(3, TimeUnit.SECONDS);
        assertNotNull(enviado);
        assertEquals(ConstantesProtocolo.MOVER_ANIMALES, enviado.consultaTipo());
        assertEquals("jaguar-1,jaguar-2", ConversorMensajes.campoDelContenido(enviado, ConstantesProtocolo.CAMPO_IDS_ANIMALES));
        assertEquals("535", ConversorMensajes.campoDelContenido(enviado, ConstantesProtocolo.CAMPO_X));
        assertEquals("420", ConversorMensajes.campoDelContenido(enviado, ConstantesProtocolo.CAMPO_Y));
    }

    @Test
    void sinAnimalesSeleccionadosElClicDerechoNoHaceNada() throws Exception {
        empezarPartida();
        raton.seleccionDestino(new Point(535, 420));
        assertNull(conexion.enviados.poll(300, TimeUnit.MILLISECONDS));
    }

    @Test
    void unDestinoFueraDelMapaSeLlevaAlBorde() throws Exception {
        empezarPartida();
        seleccionados("jaguar-1");
        raton.seleccionDestino(new Point(2000, -50));
        Mensaje enviado = conexion.enviados.poll(3, TimeUnit.SECONDS);
        assertNotNull(enviado);
        assertEquals("950", ConversorMensajes.campoDelContenido(enviado, ConstantesProtocolo.CAMPO_X));
        assertEquals("0", ConversorMensajes.campoDelContenido(enviado, ConstantesProtocolo.CAMPO_Y));
    }

    @Test
    void conUnaVentanaMasGrandeElClicSeConvierteAlMapa() throws Exception {
        empezarPartida();
        pantalla.setSize(1900, 1100); // el mapa se ve al doble de tamaño
        pantalla.consultaConversor().actualizacionVentana(1900, 1100, 950, 550);
        seleccionados("jaguar-1");
        raton.seleccionDestino(new Point(1070, 840)); // (535, 420) en el mapa
        Mensaje enviado = conexion.enviados.poll(3, TimeUnit.SECONDS);
        assertNotNull(enviado);
        assertEquals("535", ConversorMensajes.campoDelContenido(enviado, ConstantesProtocolo.CAMPO_X));
        assertEquals("420", ConversorMensajes.campoDelContenido(enviado, ConstantesProtocolo.CAMPO_Y));
    }

    // ----- Eventos reales del ratón -----

    private MouseEvent evento(int tipo, int x, int y, int boton, int modificadores) {
        return new MouseEvent(pantalla, tipo, System.currentTimeMillis(), modificadores, x, y, 1, false, boton);
    }

    @Test
    void apretarYSoltarEnElMismoLugarEsUnClic() {
        empezarPartida();
        raton.mousePressed(evento(MouseEvent.MOUSE_PRESSED, 725, 420, MouseEvent.BUTTON1, MouseEvent.BUTTON1_DOWN_MASK));
        raton.mouseReleased(evento(MouseEvent.MOUSE_RELEASED, 725, 420, MouseEvent.BUTTON1, 0));
        assertEquals(1, modelo.consultaAnimalesSeleccionados().size());
    }

    @Test
    void apretarArrastrarYSoltarSeleccionaPorRectangulo() {
        empezarPartida();
        raton.mousePressed(evento(MouseEvent.MOUSE_PRESSED, 640, 400, MouseEvent.BUTTON1, MouseEvent.BUTTON1_DOWN_MASK));
        raton.mouseDragged(evento(MouseEvent.MOUSE_DRAGGED, 700, 440, MouseEvent.BUTTON1, MouseEvent.BUTTON1_DOWN_MASK));
        raton.mouseDragged(evento(MouseEvent.MOUSE_DRAGGED, 740, 460, MouseEvent.BUTTON1, MouseEvent.BUTTON1_DOWN_MASK));
        raton.mouseReleased(evento(MouseEvent.MOUSE_RELEASED, 740, 460, MouseEvent.BUTTON1, 0));
        assertEquals(3, modelo.consultaAnimalesSeleccionados().size());
    }

    @Test
    void unMovimientoMuyPequenoDuranteElClicNoCuentaComoArrastre() {
        empezarPartida();
        raton.mousePressed(evento(MouseEvent.MOUSE_PRESSED, 725, 420, MouseEvent.BUTTON1, MouseEvent.BUTTON1_DOWN_MASK));
        raton.mouseDragged(evento(MouseEvent.MOUSE_DRAGGED, 727, 421, MouseEvent.BUTTON1, MouseEvent.BUTTON1_DOWN_MASK));
        raton.mouseReleased(evento(MouseEvent.MOUSE_RELEASED, 727, 421, MouseEvent.BUTTON1, 0));
        assertEquals(1, modelo.consultaAnimalesSeleccionados().size());
    }

    @Test
    void elBotonDerechoMandaMoverLosSeleccionados() throws Exception {
        empezarPartida();
        seleccionados("jaguar-3");
        raton.mousePressed(evento(MouseEvent.MOUSE_PRESSED, 600, 430, MouseEvent.BUTTON3, MouseEvent.BUTTON3_DOWN_MASK));
        Mensaje enviado = conexion.enviados.poll(3, TimeUnit.SECONDS);
        assertNotNull(enviado);
        assertEquals("jaguar-3", ConversorMensajes.campoDelContenido(enviado, ConstantesProtocolo.CAMPO_IDS_ANIMALES));
    }
}