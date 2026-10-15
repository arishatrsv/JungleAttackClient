package jungleattack.cliente.vista;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JLabel;
import jungleattack.cliente.constantes.ConstantesCliente;
import jungleattack.cliente.modelo.ModeloJuego;

/**
 * Sala de espera: muestra el código de la partida para que el otro jugador se una.
 */
public class PantallaEspera extends PantallaBase {

    // Lee del modelo el código de la partida, no lo guarda ella misma.
    private final ModeloJuego modelo;

    public PantallaEspera(ModeloJuego modelo) {
        this.modelo = modelo;
    }

    @Override
    public void dibujo() {
        removeAll();
        setLayout(new GridBagLayout());

        GridBagConstraints posicion = new GridBagConstraints();
        posicion.insets = new Insets(8, 8, 8, 8);
        posicion.gridx = 0;

        posicion.gridy = 0;
        add(new JLabel(ConstantesCliente.TEXTO_CODIGO), posicion);

        // El código en letra grande para que se pueda leer y compartir fácil.
        JLabel etiquetaCodigo = new JLabel(modelo.consultaJugadorLocal().consultaCodigoPartida());
        etiquetaCodigo.setFont(etiquetaCodigo.getFont().deriveFont(40f));
        posicion.gridy = 1;
        add(etiquetaCodigo, posicion);

        posicion.gridy = 2;
        add(new JLabel(ConstantesCliente.TEXTO_ESPERA), posicion);

        revalidate();
        repaint();
    }
}
