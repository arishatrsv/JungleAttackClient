package jungleattack.cliente.modelo;

import java.util.List;

/**
 * Zona del mapa tal como la conoce el cliente. Hereda de common.model.Zona (número, centro y conexiones)
 * y agrega lo necesario para dibujarla: su nombre, el contorno (polígono) y dónde va el texto del nombre.
 */
public class ZonaVisible extends common.model.Zona {

    // Valores posibles de anclaje del nombre, tal como vienen en mapa.json.
    public static final String ANCLAJE_INICIO = "inicio"; // el texto empieza en el punto
    public static final String ANCLAJE_FIN = "fin"; // el texto termina en el punto
    public static final String ANCLAJE_CENTRO = "centro";

    private final String nombre;
    private final List<PuntoVisible> poligono;
    private final PuntoVisible posicionEtiqueta;
    private final String anclajeEtiqueta;

    public ZonaVisible(int identificador, String nombre, PuntoVisible centro, List<PuntoVisible> poligono,
            PuntoVisible posicionEtiqueta, String anclajeEtiqueta) {
        super(identificador, centro);
        this.nombre = nombre;
        this.poligono = List.copyOf(poligono);
        this.posicionEtiqueta = posicionEtiqueta;
        this.anclajeEtiqueta = anclajeEtiqueta;
    }

    public String consultaNombre() {
        return nombre;
    }

    public List<PuntoVisible> consultaPoligono() {
        return poligono;
    }

    public PuntoVisible consultaPosicionEtiqueta() {
        return posicionEtiqueta;
    }

    public String consultaAnclajeEtiqueta() {
        return anclajeEtiqueta;
    }

    /**
     * Dice si un punto del mapa está dentro del contorno de la zona.
     * Se traza una línea horizontal desde el punto hacia la derecha y se cuentan los lados que cruza:
     * si son impares, el punto está adentro.
     */
    public boolean contienePunto(common.model.Punto punto) {
        double x = punto.consultaPosicionX();
        double y = punto.consultaPosicionY();
        boolean dentro = false;
        for (int i = 0, anterior = poligono.size() - 1; i < poligono.size(); anterior = i++) {
            double xi = poligono.get(i).consultaPosicionX();
            double yi = poligono.get(i).consultaPosicionY();
            double xa = poligono.get(anterior).consultaPosicionX();
            double ya = poligono.get(anterior).consultaPosicionY();
            boolean cruzaLaAltura = (yi > y) != (ya > y);
            if (cruzaLaAltura && x < (xa - xi) * (y - yi) / (ya - yi) + xi) {
                dentro = !dentro;
            }
        }
        return dentro;
    }
}
