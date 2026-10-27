package jungleattack.cliente.vista;

import common.model.Especie;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import jungleattack.cliente.constantes.ConstantesCliente;
import jungleattack.cliente.recursos.CacheImagenes;

/**
 * Fábrica de componentes con el aspecto común de las pantallas (fuentes, colores y botones), para no repetir
 * el mismo estilo en cada pantalla. Todo es estático porque no guarda estado.
 */
final class EstiloVista {

    private EstiloVista() {
    }

    static Font fuente(int estilo, int tamano) {
        return new Font(ConstantesCliente.NOMBRE_FUENTE, estilo, tamano);
    }

    // Etiqueta centrada con el tamaño, grosor y color .
    static JLabel etiqueta(String texto, int tamano, boolean negrita, Color color) {
        JLabel etiqueta = new JLabel(texto, SwingConstants.CENTER);
        etiqueta.setFont(fuente(negrita ? Font.BOLD : Font.PLAIN, tamano));
        etiqueta.setForeground(color);
        return etiqueta;
    }

    //Botón plano y de color, con el cursor de mano.
    static JButton boton(String texto, Color fondo) {
        JButton boton = new JButton(texto);
        boton.setFont(fuente(Font.BOLD, ConstantesCliente.TAMANO_FUENTE_BOTON));
        boton.setBackground(fondo);
        boton.setForeground(ConstantesCliente.COLOR_TEXTO_BOTON);
        boton.setOpaque(true);
        boton.setContentAreaFilled(true);
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boton.setBorder(BorderFactory.createEmptyBorder(ConstantesCliente.RELLENO_BOTON_VERTICAL,
                ConstantesCliente.RELLENO_BOTON_HORIZONTAL, ConstantesCliente.RELLENO_BOTON_VERTICAL,
                ConstantesCliente.RELLENO_BOTON_HORIZONTAL));
        return boton;
    }

    // Da a un campo de texto la fuente común y lo devuelve.
    static JTextField campo(JTextField campo) {
        campo.setFont(fuente(Font.PLAIN, ConstantesCliente.TAMANO_FUENTE_BOTON));
        campo.setMargin(new Insets(ConstantesCliente.RELLENO_CAMPO, ConstantesCliente.RELLENO_CAMPO,
                ConstantesCliente.RELLENO_CAMPO, ConstantesCliente.RELLENO_CAMPO));
        return campo;
    }

    // Etiqueta con la imagen de una especie. Si la imagen no se pudo cargar, queda vacía.
    static JLabel imagenDeEspecie(Especie especie, int tamano) {
        JLabel etiqueta = new JLabel();
        etiqueta.setHorizontalAlignment(SwingConstants.CENTER);
        if (especie != null) {
            Image imagen = CacheImagenes.imagenDeEspecie(especie, tamano);
            if (imagen != null) {
                etiqueta.setIcon(new ImageIcon(imagen));
            }
        }
        return etiqueta;
    }
}
