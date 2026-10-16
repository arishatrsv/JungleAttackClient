package jungleattack.cliente.vista;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import jungleattack.cliente.constantes.ConstantesCliente;
import jungleattack.cliente.controlador.ControladorJuego;

/**
 * Primera pantalla: el jugador escribe su apodo y crea una partida, o escribe el código de la
 * partida de otro jugador y se une.
 *
 * La pantalla no tiene lógica: cuando se pulsa un botón solo le pasa los datos al controlador.
 */
public class PantallaInicio extends PantallaBase {

    private final ControladorJuego controlador;

    // Campos de texto de la pantalla. Los dejo como atributos para no perder lo escrito si la pantalla se vuelve a dibujar.
    private final JTextField campoApodo = new JTextField(ConstantesCliente.COLUMNAS_CAMPO_APODO);
    private final JTextField campoCodigo = new JTextField(ConstantesCliente.COLUMNAS_CAMPO_CODIGO);

    public PantallaInicio(ControladorJuego controlador) {
        this.controlador = controlador;
    }

    @Override
    public void dibujo() {
        removeAll(); // limpio por si la pantalla se dibuja más de una vez
        setLayout(new GridBagLayout());

        GridBagConstraints posicion = new GridBagConstraints();
        posicion.insets = new Insets(8, 8, 8, 8);

        // Fila 0: apodo
        posicion.gridx = 0;
        posicion.gridy = 0;
        add(new JLabel(ConstantesCliente.TEXTO_APODO), posicion);
        posicion.gridx = 1;
        add(campoApodo, posicion);

        // Fila 1: crear partida (ocupa las dos columnas)
        JButton botonCrear = new JButton(ConstantesCliente.TEXTO_BOTON_CREAR);
        botonCrear.addActionListener(evento -> controlador.creacionPartida(campoApodo.getText()));
        posicion.gridx = 0;
        posicion.gridy = 1;
        posicion.gridwidth = 2;
        add(botonCrear, posicion);

        // Fila 2: código de la partida a la que me quiero unir
        posicion.gridwidth = 1;
        posicion.gridy = 2;
        add(new JLabel(ConstantesCliente.TEXTO_CODIGO_UNIRSE), posicion);
        posicion.gridx = 1;
        add(campoCodigo, posicion);

        // Fila 3: unirse
        JButton botonUnirse = new JButton(ConstantesCliente.TEXTO_BOTON_UNIRSE);
        botonUnirse.addActionListener(evento ->
                controlador.unionPartida(campoApodo.getText(), campoCodigo.getText()));
        posicion.gridx = 0;
        posicion.gridy = 3;
        posicion.gridwidth = 2;
        add(botonUnirse, posicion);

        revalidate();
        repaint();
    }
}
