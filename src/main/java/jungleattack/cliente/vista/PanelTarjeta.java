package jungleattack.cliente.vista;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import jungleattack.cliente.constantes.ConstantesCliente;


//Panel con fondo oscuro semitransparente, para agrupar el contenido de una pantalla
//sobre la imagen de fondo. Solo se encarga de cómo se ve: no tiene lógica.
public class PanelTarjeta extends JPanel {

    public PanelTarjeta(LayoutManager distribucion) {
        super(distribucion);
        setOpaque(false); // el fondo lo pinto yo, con esquinas redondeadas
        int relleno = ConstantesCliente.RELLENO_TARJETA;
        setBorder(BorderFactory.createEmptyBorder(relleno, relleno, relleno, relleno));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(ConstantesCliente.COLOR_TARJETA);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), ConstantesCliente.ARCO_TARJETA, ConstantesCliente.ARCO_TARJETA);
            g2.setColor(ConstantesCliente.COLOR_BORDE_TARJETA);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, ConstantesCliente.ARCO_TARJETA, ConstantesCliente.ARCO_TARJETA);
        } finally {
            g2.dispose();
        }
        super.paintComponent(g);
    }
}
