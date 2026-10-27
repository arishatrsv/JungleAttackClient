package jungleattack.cliente.vista;

import common.model.Especie;
import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.GridLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import jungleattack.cliente.constantes.ConstantesCliente;
import jungleattack.cliente.controlador.ControladorJuego;
import jungleattack.cliente.modelo.ModeloJuego;
import jungleattack.cliente.recursos.CacheImagenes;

/**
 * Pantalla donde cada jugador elige su animal. Muestra todas las especies con su imagen: las que
 * ya eligió alguien aparecen desactivadas y marcadas ("Tu elección" u "Oponente").
 *
 * Es una pantalla que solo lee del modelo lo que tiene que mostrar, y cuando se pulsa un
 * animal se lo dice al controlador. El servidor es quien decide si la elección es válida.
 */
public class PantallaSeleccion extends PantallaBase {

    private final ModeloJuego modelo;
    private final ControladorJuego controlador;

    public PantallaSeleccion(ModeloJuego modelo, ControladorJuego controlador) {
        super(true); // con el fondo común de las pantallas de menú
        this.modelo = modelo;
        this.controlador = controlador;
    }

    // Se vuelve a dibujar cada vez que el modelo cambia (por ejemplo cuando el oponente elige).
    @Override
    public void dibujo() {
        removeAll();
        setLayout(new BorderLayout());

        // Arriba: el título y el estado de la selección.
        JPanel arriba = new JPanel(new GridLayout(2, 1));
        arriba.setOpaque(false); // para que se vea el fondo
        arriba.add(EstiloVista.etiqueta(ConstantesCliente.TEXTO_SELECCION_TITULO, ConstantesCliente.TAMANO_FUENTE_TITULO,
                true, ConstantesCliente.COLOR_ACENTO));
        arriba.add(EstiloVista.etiqueta(textoDeEstado(), ConstantesCliente.TAMANO_FUENTE_SUBTITULO, false,
                ConstantesCliente.COLOR_TEXTO_PANTALLA));
        add(arriba, BorderLayout.NORTH);

        // Centro: una tarjeta por cada especie.
        JPanel tarjetas = new JPanel(new GridLayout(0, ConstantesCliente.COLUMNAS_SELECCION, 8, 8));
        tarjetas.setOpaque(false); // para que se vea el fondo
        Especie propia = modelo.consultaJugadorLocal().consultaEspecie();
        for (Especie especie : Especie.values()) {
            tarjetas.add(crearTarjeta(especie, propia));
        }
        add(tarjetas, BorderLayout.CENTER);

        revalidate();
        repaint();
    }

    // Arma el botón de una especie con su imagen, su nombre y, si ya está elegida, quién la tiene.
    private JButton crearTarjeta(Especie especie, Especie propia) {
        JButton boton = new JButton(textoDeTarjeta(especie, propia), iconoDe(especie));
        boton.setHorizontalTextPosition(SwingConstants.CENTER);
        boton.setVerticalTextPosition(SwingConstants.BOTTOM);
        // Solo se puede pulsar si todavía no elegí yo y la especie sigue libre.
        boolean puedeElegir = propia == null && modelo.consultaEspeciesDisponibles().contains(especie);
        boton.setEnabled(puedeElegir);
        boton.addActionListener(evento -> controlador.seleccionEspecie(especie));
        return boton;
    }

    // El texto de la tarjeta: el nombre del animal y, si está elegido, quién lo eligió.
    private String textoDeTarjeta(Especie especie, Especie propia) {
        String texto = nombreVisible(especie);
        if (especie == propia) {
            texto += " (" + ConstantesCliente.TEXTO_SELECCION_TU_ELECCION + ")";
        } else if (especie == modelo.consultaEspecieOponente()) {
            texto += " (" + ConstantesCliente.TEXTO_SELECCION_OPONENTE + ")";
        }
        return texto;
    }

    // La imagen de la especie sale de la caché (CacheImagenes). Si no se pudo cargar, la tarjeta se queda solo con texto.
    private ImageIcon iconoDe(Especie especie) {
        Image imagen = CacheImagenes.imagenDeEspecie(especie);
        return imagen == null ? null : new ImageIcon(imagen);
    }

    // Mensaje de estado según cómo va la selección.
    private String textoDeEstado() {
        if (modelo.consultaSeleccionCompleta()) {
            return ConstantesCliente.TEXTO_SELECCION_COMPLETA;
        }
        if (modelo.consultaJugadorLocal().consultaEspecie() != null) {
            return ConstantesCliente.TEXTO_SELECCION_ESPERA_OPONENTE;
        }
        return ConstantesCliente.TEXTO_SELECCION_ELIGE;
    }

    // Convierte el identificador ("mono_carablanca") en un nombre para mostrar ("Mono carablanca").
    private String nombreVisible(Especie especie) {
        String nombre = especie.consultaIdentificador().replace('_', ' ');
        return Character.toUpperCase(nombre.charAt(0)) + nombre.substring(1);
    }
}

