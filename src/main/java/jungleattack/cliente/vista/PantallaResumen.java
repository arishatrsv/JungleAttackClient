package jungleattack.cliente.vista;

import common.constantes.ConstantesProtocolo;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import jungleattack.cliente.constantes.ConstantesCliente;
import jungleattack.cliente.controlador.ControladorJuego;
import jungleattack.cliente.modelo.ModeloJuego;
import jungleattack.cliente.modelo.ResumenVisible;


 // Solo muestra lo que hay en el resumen del modelo. El botón "Volver al inicio" le avisa al controlador.

public class PantallaResumen extends PantallaBase {

    private final ModeloJuego modelo;
    private final ControladorJuego controlador;

    public PantallaResumen(ModeloJuego modelo, ControladorJuego controlador) {
        this.modelo = modelo;
        this.controlador = controlador;
    }

    @Override
    public void dibujo() {
        removeAll();
        setLayout(new GridBagLayout());
        ResumenVisible resumen = modelo.consultaResumen();
        if (resumen == null) {
            revalidate();
            repaint();
            return;
        }
        GridBagConstraints posicion = new GridBagConstraints();
        posicion.insets = new Insets(6, 14, 6, 14);
        posicion.gridx = 0;
        posicion.gridwidth = 3;
        posicion.gridy = 0;

        // Título: ganó o perdió, en grande y con color.
        JLabel titulo = new JLabel(resumen.consultaVictoria() ? ConstantesCliente.TEXTO_RESUMEN_VICTORIA : ConstantesCliente.TEXTO_RESUMEN_DERROTA,
                SwingConstants.CENTER);
        titulo.setFont(titulo.getFont().deriveFont(44f));
        titulo.setForeground(resumen.consultaVictoria() ? ConstantesCliente.COLOR_RESUMEN_VICTORIA : ConstantesCliente.COLOR_RESUMEN_DERROTA);
        add(titulo, posicion);

        posicion.gridy++;
        add(new JLabel(textoDeLaRazon(resumen), SwingConstants.CENTER), posicion);
        posicion.gridy++;
        add(new JLabel(ConstantesCliente.TEXTO_RESUMEN_DURACION + formatoDuracion(resumen.consultaDuracion()), SwingConstants.CENTER), posicion);

        // Tabla: una fila por estadística y una columna por jugador (primero el propio).
        posicion.gridwidth = 1;
        posicion.gridy++;
        add(new JLabel(""), posicion);
        posicion.gridx = 1;
        add(encabezado(ConstantesCliente.TEXTO_RESUMEN_TU + " (" + resumen.consultaPropio().consultaApodo() + ")"), posicion);
        posicion.gridx = 2;
        add(encabezado(resumen.consultaOponente().consultaApodo()), posicion);
        fila(posicion, ConstantesCliente.TEXTO_RESUMEN_BOTS, resumen.consultaPropio().consultaBotsEliminados(),
                resumen.consultaOponente().consultaBotsEliminados());
        fila(posicion, ConstantesCliente.TEXTO_RESUMEN_PERDIDOS, resumen.consultaPropio().consultaAnimalesPerdidos(),
                resumen.consultaOponente().consultaAnimalesPerdidos());
        fila(posicion, ConstantesCliente.TEXTO_RESUMEN_TAZAS, resumen.consultaPropio().consultaTazasConsumidas(),
                resumen.consultaOponente().consultaTazasConsumidas());

        JButton volver = new JButton(ConstantesCliente.TEXTO_BOTON_VOLVER);
        volver.addActionListener(evento -> controlador.volverAlInicio());
        posicion.gridx = 0;
        posicion.gridy++;
        posicion.gridwidth = 3;
        posicion.insets = new Insets(24, 14, 6, 14);
        add(volver, posicion);

        revalidate();
        repaint();
    }

    private JLabel encabezado(String texto) {
        JLabel etiqueta = new JLabel(texto, SwingConstants.CENTER);
        etiqueta.setFont(etiqueta.getFont().deriveFont(java.awt.Font.BOLD, 16f));
        return etiqueta;
    }

    // Agrega una fila de la tabla: el nombre de la estadística y el valor de cada jugador.
    private void fila(GridBagConstraints posicion, String nombre, int valorPropio, int valorOponente) {
        posicion.gridy++;
        posicion.gridx = 0;
        add(new JLabel(nombre), posicion);
        posicion.gridx = 1;
        add(new JLabel(String.valueOf(valorPropio), SwingConstants.CENTER), posicion);
        posicion.gridx = 2;
        add(new JLabel(String.valueOf(valorOponente), SwingConstants.CENTER), posicion);
    }

    // Por qué terminó la partida, dicho desde el punto de vista de este jugador.
    private String textoDeLaRazon(ResumenVisible resumen) {
        if (ConstantesProtocolo.RAZON_ABANDONO.equals(resumen.consultaRazon())) {
            return ConstantesCliente.TEXTO_RESUMEN_RAZON_ABANDONO;
        }
        return resumen.consultaVictoria() ? ConstantesCliente.TEXTO_RESUMEN_RAZON_BANDERA : ConstantesCliente.TEXTO_RESUMEN_RAZON_BANDERA_PERDIDA;
    }

    // Segundos a minutos y segundos: 125 -> "2:05".
    static String formatoDuracion(int segundos) {
        return String.format("%d:%02d", segundos / 60, segundos % 60);
    }
}