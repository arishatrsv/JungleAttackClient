package jungleattack.cliente.vista;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.LineBorder;
import jungleattack.cliente.constantes.ConstantesCliente;
import jungleattack.cliente.controlador.ControladorJuego;
import jungleattack.cliente.modelo.ModeloJuego;

/**
 * Sala de espera: muestra el código de la partida para que el otro jugador se una.
 */
public class PantallaEspera extends PantallaBase {

    // Lee del modelo el código de la partida, no lo guarda ella misma.
    private final ModeloJuego modelo;
    private final ControladorJuego controlador;
    private JLabel etiquetaEspera;
    private int puntos = ConstantesCliente.PUNTOS_ESPERA_MAXIMO;
    private Timer temporizadorPuntos;

    /** @param controlador a quien se le avisa si el jugador cancela (puede ser null en pruebas) */
    public PantallaEspera(ModeloJuego modelo, ControladorJuego controlador) {
        super(true);
        this.modelo = modelo;
        this.controlador = controlador;
    }

    @Override
    public void dibujo() {
        removeAll();
        setLayout(new GridBagLayout());

        PanelTarjeta tarjeta = new PanelTarjeta(new GridBagLayout());
        int espacio = ConstantesCliente.ESPACIO_PANTALLA;
        GridBagConstraints posicion = new GridBagConstraints();
        posicion.insets = new Insets(espacio, espacio, espacio, espacio);
        posicion.gridx = 0;

        posicion.gridy = 0;
        tarjeta.add(EstiloVista.etiqueta(ConstantesCliente.TEXTO_ESPERA_TITULO, ConstantesCliente.TAMANO_FUENTE_TITULO,
                true, ConstantesCliente.COLOR_ACENTO), posicion);
        posicion.gridy++;
        tarjeta.add(EstiloVista.etiqueta(ConstantesCliente.TEXTO_ESPERA_AYUDA, ConstantesCliente.TAMANO_FUENTE_SUBTITULO,
                false, ConstantesCliente.COLOR_TEXTO_SECUNDARIO), posicion);
        posicion.gridy++;
        tarjeta.add(EstiloVista.etiqueta(ConstantesCliente.TEXTO_CODIGO, ConstantesCliente.TAMANO_FUENTE_ETIQUETA,
                true, ConstantesCliente.COLOR_TEXTO_PANTALLA), posicion);

        // El código en fichas grandes para que se pueda leer y compartir fácil.
        String codigo = modelo.consultaJugadorLocal().consultaCodigoPartida();
        posicion.gridy++;
        tarjeta.add(panelDelCodigo(codigo == null ? "" : codigo), posicion);

        JButton botonCopiar = EstiloVista.boton(ConstantesCliente.TEXTO_BOTON_COPIAR, ConstantesCliente.COLOR_BOTON_SECUNDARIO);
        botonCopiar.addActionListener(evento -> copiarCodigo(botonCopiar, codigo == null ? "" : codigo));
        posicion.gridy++;
        tarjeta.add(botonCopiar, posicion);

        etiquetaEspera = EstiloVista.etiqueta(ConstantesCliente.TEXTO_ESPERA, ConstantesCliente.TAMANO_FUENTE_SUBTITULO,
                false, ConstantesCliente.COLOR_TEXTO_PANTALLA);
        posicion.gridy++;
        tarjeta.add(etiquetaEspera, posicion);

        JButton botonCancelar = EstiloVista.boton(ConstantesCliente.TEXTO_BOTON_CANCELAR, ConstantesCliente.COLOR_BOTON_CANCELAR);
        botonCancelar.addActionListener(evento -> {
            if (controlador != null) {
                controlador.cancelarPartida();
            }
        });
        posicion.gridy++;
        tarjeta.add(botonCancelar, posicion);

        add(tarjeta, new GridBagConstraints());
        revalidate();
        repaint();
    }

    // Una ficha por cada carácter del código.
    private JPanel panelDelCodigo(String codigo) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, ConstantesCliente.ESPACIO_PANTALLA, 0));
        panel.setOpaque(false);
        for (char caracter : codigo.toCharArray()) {
            JLabel ficha = new JLabel(String.valueOf(caracter), SwingConstants.CENTER);
            ficha.setFont(new Font(ConstantesCliente.NOMBRE_FUENTE_CODIGO, Font.BOLD, ConstantesCliente.TAMANO_FUENTE_CODIGO));
            ficha.setForeground(ConstantesCliente.COLOR_ACENTO);
            ficha.setOpaque(true);
            ficha.setBackground(ConstantesCliente.COLOR_FICHA_CODIGO);
            ficha.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(ConstantesCliente.COLOR_ACENTO, ConstantesCliente.GROSOR_FICHA_CODIGO, true),
                    BorderFactory.createEmptyBorder(ConstantesCliente.RELLENO_FICHA_VERTICAL,
                            ConstantesCliente.RELLENO_FICHA_HORIZONTAL, ConstantesCliente.RELLENO_FICHA_VERTICAL,
                            ConstantesCliente.RELLENO_FICHA_HORIZONTAL)));
            panel.add(ficha);
        }
        return panel;
    }

    // Copia el código al portapapeles. Si el sistema no tiene portapapeles (por ejemplo sin pantalla), no pasa nada.
    private void copiarCodigo(JButton boton, String codigo) {
        try {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(codigo), null);
            boton.setText(ConstantesCliente.TEXTO_BOTON_COPIADO);
        } catch (HeadlessException | IllegalStateException e) {
            // sin portapapeles disponible: el jugador puede copiar el código a mano
        }
    }

    // Un paso de la animación: aparece un punto más y, al llegar al máximo, vuelve a empezar.
    void avanceDePuntos() {
        puntos = (puntos + 1) % (ConstantesCliente.PUNTOS_ESPERA_MAXIMO + 1);
        if (etiquetaEspera != null) {
            // Los espacios al final mantienen el ancho del texto igual para que no se mueva al centrarse.
            etiquetaEspera.setText(ConstantesCliente.TEXTO_ESPERA_BASE + ".".repeat(puntos)
                    + " ".repeat(ConstantesCliente.PUNTOS_ESPERA_MAXIMO - puntos));
        }
    }

    // El temporizador solo corre mientras la pantalla está dentro de la ventana.
    @Override
    public void addNotify() {
        super.addNotify();
        temporizadorPuntos = new Timer(ConstantesCliente.INTERVALO_PUNTOS_ESPERA_MS, evento -> avanceDePuntos());
        temporizadorPuntos.start();
    }

    @Override
    public void removeNotify() {
        if (temporizadorPuntos != null) {
            temporizadorPuntos.stop();
        }
        super.removeNotify();
    }
}
