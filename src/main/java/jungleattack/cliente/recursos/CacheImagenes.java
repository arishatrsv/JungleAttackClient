package jungleattack.cliente.recursos;

import com.github.weisj.jsvg.SVGDocument;
import com.github.weisj.jsvg.parser.SVGLoader;
import com.github.weisj.jsvg.view.ViewBox;
import common.model.Especie;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.JComponent;
import jungleattack.cliente.constantes.ConstantesCliente;

/**
 * Guarda las imágenes ya cargadas para no cargarlas otra vez cada vez que se dibujan.
 * Las imágenes del juego son SVG. Java no sabe dibujar SVG solo, así que uso
 * la librería JSVG: cada SVG se convierte una sola vez en una imagen normal (BufferedImage) y esa
 * imagen se guarda aquí. Nunca interpreto el SVG en cada cuadro de la animación.
 *
 * Todo es estático porque la caché es una sola y se comparte en todo el cliente.
 */
public class CacheImagenes {

    // Imágenes ya convertidas, por nombre. ConcurrentHashMap por si se piden desde hilos distintos.
    private static final Map<String, Image> imagenes = new ConcurrentHashMap<>();

    // Constructor privado: la clase solo tiene métodos estáticos.
    private CacheImagenes() {
    }

    // Carga un SVG que está en los recursos del proyecto, lo convierte a una imagen del tamaño
    // pedido y la guarda con ese nombre. Si ya estaba guardada, no hace nada.
    // Si el archivo no existe o no se puede leer, no se guarda nada y imagenPorNombre devolverá null.
    public static void cargaImagen(String nombre, String ruta, int ancho, int alto) {
        if (imagenes.containsKey(nombre)) {
            return;
        }
        URL url = CacheImagenes.class.getClassLoader().getResource(ruta);
        if (url == null) {
            return; // el archivo no está en los recursos
        }
        try {
            SVGDocument documento = new SVGLoader().load(url);
            if (documento == null) {
                return;
            }
            BufferedImage imagen = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = imagen.createGraphics();
            try {
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Dibujo el SVG dentro de un rectángulo del tamaño pedido (sin componente asociado).
                documento.render((JComponent) null, g, new ViewBox(0, 0, ancho, alto));
            } finally {
                g.dispose(); // siempre libero el Graphics2D
            }
            imagenes.put(nombre, imagen);
        } catch (RuntimeException e) {
            // Un SVG dañado no debe tumbar el juego: simplemente esa imagen no se muestra.
        }
    }

    // Devuelve una imagen ya guardada, o null si no está.
    public static Image imagenPorNombre(String nombre) {
        return imagenes.get(nombre);
    }

    // Devuelve la imagen de una especie. La primera vez la carga (con el tamaño de la pantalla de
    // selección) y después sale de la caché. La ruta del archivo la trae la propia Especie.
    public static Image imagenDeEspecie(Especie especie) {
        String nombre = especie.consultaIdentificador();
        cargaImagen(nombre, especie.consultaImagen(),
                ConstantesCliente.TAMANO_IMAGEN_ESPECIE, ConstantesCliente.TAMANO_IMAGEN_ESPECIE);
        return imagenPorNombre(nombre);
    }

    // Devuelve la imagen de una especie con el tamaño pedido (por ejemplo, más pequeña para dibujarla en el mapa).
    // Cada tamaño se guarda por separado en la caché.
    public static Image imagenDeEspecie(Especie especie, int tamano) {
        String nombre = especie.consultaIdentificador() + "@" + tamano;
        cargaImagen(nombre, especie.consultaImagen(), tamano, tamano);
        return imagenPorNombre(nombre);
    }

    // Carga (si hace falta) y devuelve una imagen cualquiera de los recursos. Devuelve null si el archivo no existe.
    public static Image imagenDeRecurso(String nombre, String ruta, int ancho, int alto) {
        cargaImagen(nombre, ruta, ancho, alto);
        return imagenPorNombre(nombre);
    }
}
