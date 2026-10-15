package jungleattack.cliente.vista;

import javax.swing.JPanel;

/**
 * Clase base de todas las pantallas. Hereda de JPanel para que cada pantalla sea un panel que la
 * ventana principal pueda mostrar.
 *
 * Cada pantalla hija implementa dibujo() a su manera:
 * esto es polimorfismo, la ventana trabaja con PantallaBase sin saber cuál pantalla es.
 */
public abstract class PantallaBase extends JPanel {

    /** Arma o actualiza lo que se ve en la pantalla. La llama la ventana al mostrarla. */
    public abstract void dibujo();
}
