package jungleattack.cliente.controlador;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;
import jungleattack.cliente.constantes.ConstantesCliente;
import jungleattack.cliente.modelo.AnimalVisible;
import jungleattack.cliente.modelo.MapaCliente;
import jungleattack.cliente.modelo.ModeloJuego;
import jungleattack.cliente.modelo.PuntoVisible;
import jungleattack.cliente.modelo.ZonaVisible;
import jungleattack.cliente.vista.PantallaJuego;

/**
 * Convierte lo que hace el jugador con el ratón en acciones del juego. No contiene reglas: solo decide
 * qué quiso hacer el jugador y se lo pasa al modelo (selección, cambio de zona) o al controlador (órdenes).
 *
 * <ul>
 *   <li>Clic izquierdo sobre un animal propio: lo selecciona (con Ctrl o Shift lo agrega o lo quita de la selección).</li>
 *   <li>Arrastrar con el botón izquierdo: selecciona los animales propios dentro del rectángulo.</li>
 *   <li>Clic izquierdo en otra zona que se ve: pasa a mirar esa zona.</li>
 *   <li>Clic derecho sobre un animal de otro jugador o un bot: manda atacarlo con los animales seleccionados.</li>
 *   <li>Clic derecho en cualquier otro punto: manda mover los animales seleccionados a ese punto.</li>
 * </ul>
 */
public class ControladorRaton extends MouseAdapter {

    private final ModeloJuego modelo;
    private final PantallaJuego pantallaJuego;
    // Puede ser null (en pruebas): entonces las órdenes no se envían.
    private final ControladorJuego controladorJuego;

    // Dónde se apretó el botón izquierdo (null si no está apretado) y si ya se arrastró lo suficiente.
    private Point inicioArrastre;
    private boolean arrastrando;

    public ControladorRaton(ModeloJuego modelo, PantallaJuego pantallaJuego, ControladorJuego controladorJuego) {
        this.modelo = modelo;
        this.pantallaJuego = pantallaJuego;
        this.controladorJuego = controladorJuego;
    }

    @Override
    public void mousePressed(MouseEvent evento) {
        if (SwingUtilities.isRightMouseButton(evento) || evento.isPopupTrigger()) {
            clicDerecho(evento.getPoint());
        } else if (SwingUtilities.isLeftMouseButton(evento)) {
            inicioArrastre = evento.getPoint();
            arrastrando = false;
        }
    }

    @Override
    public void mouseDragged(MouseEvent evento) {
        if (inicioArrastre == null) {
            return;
        }
        if (inicioArrastre.distance(evento.getPoint()) >= ConstantesCliente.DISTANCIA_MINIMA_ARRASTRE) {
            arrastrando = true;
        }
        if (arrastrando) {
            pantallaJuego.actualizacionRectanguloSeleccion(rectangulo(inicioArrastre, evento.getPoint()));
        }
    }

    @Override
    public void mouseReleased(MouseEvent evento) {
        if (inicioArrastre == null || SwingUtilities.isRightMouseButton(evento)) {
            return;
        }
        boolean agregar = evento.isControlDown() || evento.isShiftDown();
        if (arrastrando) {
            seleccion(rectangulo(inicioArrastre, evento.getPoint()), agregar);
        } else {
            clic(evento.getPoint(), agregar);
        }
        inicioArrastre = null;
        arrastrando = false;
        pantallaJuego.actualizacionRectanguloSeleccion(null);
    }

    /**
     * Selecciona los animales propios que quedaron dentro de un rectángulo (en píxeles de la ventana).
     * Solo cuentan los de la zona que se está mirando.
     */
    public void seleccion(Rectangle rectanguloPantalla, boolean agregar) {
        PuntoVisible esquina1 = pantallaJuego.consultaConversor().conversionPantallaAMapa(
                new PuntoVisible(rectanguloPantalla.x, rectanguloPantalla.y));
        PuntoVisible esquina2 = pantallaJuego.consultaConversor().conversionPantallaAMapa(
                new PuntoVisible(rectanguloPantalla.x + rectanguloPantalla.width, rectanguloPantalla.y + rectanguloPantalla.height));
        List<AnimalVisible> dentro = new ArrayList<>();
        for (AnimalVisible animal : modelo.consultaAnimalesEnZonaObservada()) {
            if (animal.consultaPropio()
                    && animal.consultaVisualX() >= esquina1.consultaPosicionX() && animal.consultaVisualX() <= esquina2.consultaPosicionX()
                    && animal.consultaVisualY() >= esquina1.consultaPosicionY() && animal.consultaVisualY() <= esquina2.consultaPosicionY()) {
                dentro.add(animal);
            }
        }
        if (agregar) {
            for (AnimalVisible ya : modelo.consultaAnimalesSeleccionados()) {
                if (!dentro.contains(ya)) {
                    dentro.add(ya);
                }
            }
        }
        controladorJuego.seleccionAnimales(dentro);
    }

    /**
     * Clic izquierdo en un punto de la ventana: si cae sobre un animal propio lo selecciona; si cae sobre otra zona
     * que el jugador ve, pasa a mirar esa zona; si cae en cualquier otro lado, quita la selección.
     */
    public void clic(Point puntoPantalla, boolean agregar) {
        PuntoVisible punto = pantallaJuego.consultaConversor().conversionPantallaAMapa(
                new PuntoVisible(puntoPantalla.x, puntoPantalla.y));
        AnimalVisible animal = animalPropioCerca(punto);
        if (animal != null) {
            List<AnimalVisible> seleccion = new ArrayList<>(agregar ? modelo.consultaAnimalesSeleccionados() : List.of());
            if (!seleccion.remove(animal)) {
                seleccion.add(animal); // si ya estaba (con Ctrl o Shift), se quita
            }
            controladorJuego.seleccionAnimales(seleccion);
            return;
        }
        ZonaVisible zona = zonaVisibleEn(punto);
        if (zona != null && zona.consultaIdentificador() != modelo.consultaZonaObservada()) {
            modelo.actualizacionZonaObservada(zona.consultaIdentificador());
        } else if (!agregar) {
            controladorJuego.seleccionAnimales(List.of());
        }
    }

    /**
     * Clic derecho: manda mover los animales seleccionados al punto donde se hizo clic.
     * Si el clic cae fuera del mapa (en el margen de la ventana) se lleva al borde del mapa.
     * Sin animales seleccionados no hace nada.
     */
    public void seleccionDestino(Point puntoPantalla) {
        List<AnimalVisible> seleccionados = modelo.consultaAnimalesSeleccionados();
        MapaCliente mapa = modelo.consultaMapa();
        if (seleccionados.isEmpty() || mapa == null || controladorJuego == null) {
            return;
        }
        PuntoVisible punto = pantallaJuego.consultaConversor().conversionPantallaAMapa(
                new PuntoVisible(puntoPantalla.x, puntoPantalla.y));
        int x = Math.max(0, Math.min(mapa.consultaAncho(), punto.consultaPosicionX()));
        int y = Math.max(0, Math.min(mapa.consultaAlto(), punto.consultaPosicionY()));
        controladorJuego.ordenMovimiento(seleccionados, new PuntoVisible(x, y));
    }

    /**
     * Clic derecho: si cayó sobre un animal que no es del jugador (de su oponente o un bot) manda atacarlo;
     * si cayó en cualquier otro lugar manda mover los animales seleccionados hasta ahí.
     */
    public void clicDerecho(Point puntoPantalla) {
        if (!seleccionObjetivo(puntoPantalla)) {
            seleccionDestino(puntoPantalla);
        }
    }

    /**
     * Si el punto de la ventana cayó sobre un animal ajeno de la zona que se mira, manda atacarlo con los animales
     * seleccionados. Sin animales seleccionados no envía nada, pero igual cuenta como clic sobre un animal ajeno.
     *
     * @return true si el clic cayó sobre un animal ajeno (se haya enviado la orden o no)
     */
    public boolean seleccionObjetivo(Point puntoPantalla) {
        PuntoVisible punto = pantallaJuego.consultaConversor().conversionPantallaAMapa(
                new PuntoVisible(puntoPantalla.x, puntoPantalla.y));
        AnimalVisible objetivo = animalAjenoCerca(punto);
        if (objetivo == null) {
            return false;
        }
        List<AnimalVisible> seleccionados = modelo.consultaAnimalesSeleccionados();
        if (!seleccionados.isEmpty() && controladorJuego != null) {
            controladorJuego.ordenAtaque(seleccionados, objetivo);
        }
        return true;
    }

    // El animal que no es del jugador, de la zona observada, más cercano al punto (si el clic cayó lo bastante cerca).
    private AnimalVisible animalAjenoCerca(PuntoVisible punto) {
        AnimalVisible mejor = null;
        double menorDistancia = ConstantesCliente.RADIO_CLIC_ANIMAL;
        for (AnimalVisible animal : modelo.consultaAnimalesEnZonaObservada()) {
            double distancia = Math.hypot(animal.consultaVisualX() - punto.consultaPosicionX(),
                    animal.consultaVisualY() - punto.consultaPosicionY());
            if (!animal.consultaPropio() && distancia <= menorDistancia) {
                menorDistancia = distancia;
                mejor = animal;
            }
        }
        return mejor;
    }

    // El animal propio de la zona observada más cercano al punto, si el clic cayó lo bastante cerca de él.
    private AnimalVisible animalPropioCerca(PuntoVisible punto) {
        AnimalVisible mejor = null;
        double menorDistancia = ConstantesCliente.RADIO_CLIC_ANIMAL;
        for (AnimalVisible animal : modelo.consultaAnimalesEnZonaObservada()) {
            double distancia = Math.hypot(animal.consultaVisualX() - punto.consultaPosicionX(),
                    animal.consultaVisualY() - punto.consultaPosicionY());
            if (animal.consultaPropio() && distancia <= menorDistancia) {
                menorDistancia = distancia;
                mejor = animal;
            }
        }
        return mejor;
    }

    // La zona visible que contiene el punto, o null si el punto cae en una zona que el jugador no ve.
    private ZonaVisible zonaVisibleEn(PuntoVisible punto) {
        for (ZonaVisible zona : modelo.consultaZonasVisibles()) {
            if (zona.contienePunto(punto)) {
                return zona;
            }
        }
        return null;
    }

    private Rectangle rectangulo(Point a, Point b) {
        return new Rectangle(Math.min(a.x, b.x), Math.min(a.y, b.y), Math.abs(a.x - b.x), Math.abs(a.y - b.y));
    }
}
