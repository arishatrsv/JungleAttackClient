package jungleattack.cliente.vista;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import javax.swing.JPanel;
import jungleattack.cliente.constantes.ConstantesCliente;
import jungleattack.cliente.recursos.CacheImagenes;

//Cada pantalla hija implementa dibujo() a su manera:
//esto es polimorfismo, la ventana trabaja con PantallaBase sin saber cuál pantalla es.
//Pantalla base, comparten el mismo fondo
public abstract class PantallaBase extends JPanel {

    private final boolean conFondo;
    private Color capaFondo = ConstantesCliente.COLOR_CAPA_FONDO;

    //Pantalla sin fondo propio 
    protected PantallaBase() {
        this(false);
    }

    // @param conFondo si es true, la pantalla pinta el fondo común de las pantallas de menú 
    protected PantallaBase(boolean conFondo) {
        this.conFondo = conFondo;
    }

    //Arma o actualiza lo que se ve en la pantalla. La llama la ventana al mostrarla. 
    public abstract void dibujo();

    //Cambia el color de la capa que se pinta sobre el fondo (por ejemplo, verde si ganó y rojo si perdió). 
    protected void actualizacionCapaFondo(Color capa) {
        this.capaFondo = capa;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (!conFondo) {
            return;
        }
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            Image fondo = CacheImagenes.imagenDeRecurso(ConstantesCliente.NOMBRE_FONDO_INICIO,
                    ConstantesCliente.RUTA_FONDO_INICIO, ConstantesCliente.ANCHO_FONDO_INICIO, ConstantesCliente.ALTO_FONDO_INICIO);
            if (fondo == null) {
                // Si el SVG no se pudo cargar, la pantalla se ve con un color liso en vez de quedar en blanco.
                g2.setColor(ConstantesCliente.COLOR_FONDO_PANTALLA);
                g2.fillRect(0, 0, getWidth(), getHeight());
            } else {
                dibujoFondoCubriendo(g2, fondo);
            }
            g2.setColor(capaFondo);
            g2.fillRect(0, 0, getWidth(), getHeight());
        } finally {
            g2.dispose();
        }
    }

    // Escala la imagen para que cubra toda la pantalla sin deformarse (se recorta lo que sobra) y la centra.
    private void dibujoFondoCubriendo(Graphics2D g2, Image fondo) {
        int anchoImagen = fondo.getWidth(null);
        int altoImagen = fondo.getHeight(null);
        if (anchoImagen <= 0 || altoImagen <= 0) {
            return;
        }
        double escala = Math.max((double) getWidth() / anchoImagen, (double) getHeight() / altoImagen);
        int ancho = (int) Math.ceil(anchoImagen * escala);
        int alto = (int) Math.ceil(altoImagen * escala);
        g2.drawImage(fondo, (getWidth() - ancho) / 2, (getHeight() - alto) / 2, ancho, alto, null);
    }
}
