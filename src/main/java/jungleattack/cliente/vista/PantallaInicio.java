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
    private final JTextField campoApodo = EstiloVista.campo(new JTextField(ConstantesCliente.COLUMNAS_CAMPO_APODO));
    private final JTextField campoCodigo = EstiloVista.campo(new JTextField(ConstantesCliente.COLUMNAS_CAMPO_CODIGO));

    public PantallaInicio(ControladorJuego controlador) {
        super(true); // con el fondo común de las pantallas de menú
        this.controlador = controlador;
    }

    @Override
    public void dibujo() {
        removeAll(); // limpio por si la pantalla se dibuja más de una vez
        setLayout(new GridBagLayout());

        PanelTarjeta tarjeta = new PanelTarjeta(new GridBagLayout());
        int espacio = ConstantesCliente.ESPACIO_PANTALLA;
        GridBagConstraints posicion = new GridBagConstraints();
        posicion.insets = new Insets(espacio, espacio, espacio, espacio);
        posicion.fill = GridBagConstraints.HORIZONTAL;
        posicion.gridwidth = 2;
        posicion.gridx = 0;

        // Título y subtítulo
        posicion.gridy = 0;
        tarjeta.add(EstiloVista.etiqueta(ConstantesCliente.TITULO_VENTANA, ConstantesCliente.TAMANO_FUENTE_TITULO,
                true, ConstantesCliente.COLOR_ACENTO), posicion);
        posicion.gridy++;
        tarjeta.add(EstiloVista.etiqueta(ConstantesCliente.TEXTO_SUBTITULO_INICIO, ConstantesCliente.TAMANO_FUENTE_SUBTITULO,
                false, ConstantesCliente.COLOR_TEXTO_SECUNDARIO), posicion);

        // Apodo
        posicion.gridy++;
        posicion.gridwidth = 1;
        tarjeta.add(etiquetaDeCampo(ConstantesCliente.TEXTO_APODO), posicion);
        posicion.gridx = 1;
        tarjeta.add(campoApodo, posicion);

        // Crear partida (ocupa las dos columnas)
        JButton botonCrear = EstiloVista.boton(ConstantesCliente.TEXTO_BOTON_CREAR, ConstantesCliente.COLOR_BOTON_PRINCIPAL);
        botonCrear.addActionListener(evento -> controlador.creacionPartida(campoApodo.getText()));
        posicion.gridx = 0;
        posicion.gridy++;
        posicion.gridwidth = 2;
        tarjeta.add(botonCrear, posicion);

        // Separador
        posicion.gridy++;
        tarjeta.add(EstiloVista.etiqueta(ConstantesCliente.TEXTO_O_UNIRSE, ConstantesCliente.TAMANO_FUENTE_ETIQUETA,
                false, ConstantesCliente.COLOR_TEXTO_SECUNDARIO), posicion);

        // Código de la partida a la que me quiero unir
        posicion.gridy++;
        posicion.gridwidth = 1;
        tarjeta.add(etiquetaDeCampo(ConstantesCliente.TEXTO_CODIGO_UNIRSE), posicion);
        posicion.gridx = 1;
        tarjeta.add(campoCodigo, posicion);

        // Unirse
        JButton botonUnirse = EstiloVista.boton(ConstantesCliente.TEXTO_BOTON_UNIRSE, ConstantesCliente.COLOR_BOTON_SECUNDARIO);
        botonUnirse.addActionListener(evento ->
                controlador.unionPartida(campoApodo.getText(), campoCodigo.getText()));
        posicion.gridx = 0;
        posicion.gridy++;
        posicion.gridwidth = 2;
        tarjeta.add(botonUnirse, posicion);

        add(tarjeta, new GridBagConstraints());
        revalidate();
        repaint();
    }

    // Etiqueta de un campo de texto (apodo, código), en el color de la pantalla.
    private JLabel etiquetaDeCampo(String texto) {
        JLabel etiqueta = EstiloVista.etiqueta(texto, ConstantesCliente.TAMANO_FUENTE_ETIQUETA, true,
                ConstantesCliente.COLOR_TEXTO_PANTALLA);
        etiqueta.setHorizontalAlignment(SwingConstants.RIGHT);
        return etiqueta;
    }
}
