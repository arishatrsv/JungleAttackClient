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
 * Primera pantalla: el jugador escribe su apodo y crea una partida.
 * (Unirse a una partida con código se agrega en el siguiente paso.)
 *
 * La pantalla no tiene lógica: cuando se pulsa el botón solo le pasa el apodo al controlador.
 */
public class PantallaInicio extends PantallaBase {

    private final ControladorJuego controlador;

    // Campo donde se escribe el apodo.
    private final JTextField campoApodo = new JTextField(ConstantesCliente.COLUMNAS_CAMPO_APODO);

    public PantallaInicio(ControladorJuego controlador) {
        this.controlador = controlador;
    }

    @Override
    public void dibujo() {
        removeAll(); // limpio por si la pantalla se dibuja más de una vez
        setLayout(new GridBagLayout());

        GridBagConstraints posicion = new GridBagConstraints();
        posicion.insets = new Insets(8, 8, 8, 8);

        posicion.gridx = 0;
        posicion.gridy = 0;
        add(new JLabel(ConstantesCliente.TEXTO_APODO), posicion);

        posicion.gridx = 1;
        add(campoApodo, posicion);

        JButton botonCrear = new JButton(ConstantesCliente.TEXTO_BOTON_CREAR);
        // Al pulsar el botón, el controlador se encarga de lo demás.
        botonCrear.addActionListener(evento -> controlador.creacionPartida(campoApodo.getText()));
        posicion.gridx = 0;
        posicion.gridy = 1;
        posicion.gridwidth = 2; // el botón ocupa las dos columnas
        add(botonCrear, posicion);

        revalidate();
        repaint();
    }
}