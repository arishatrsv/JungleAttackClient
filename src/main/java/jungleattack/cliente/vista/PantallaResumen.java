package jungleattack.cliente.vista;

import common.constantes.ConstantesProtocolo;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import jungleattack.cliente.constantes.ConstantesCliente;
import jungleattack.cliente.controlador.ControladorJuego;
import jungleattack.cliente.modelo.EstadisticasVisibles;
import jungleattack.cliente.modelo.ModeloJuego;
import jungleattack.cliente.modelo.ResumenVisible;


 // Solo muestra lo que hay en el resumen del modelo. El botón "Volver al inicio" le avisa al controlador.
//muestra el final de la partida
public class PantallaResumen extends PantallaBase {

    private final ModeloJuego modelo;
    private final ControladorJuego controlador;

    public PantallaResumen(ModeloJuego modelo, ControladorJuego controlador) {
        super(true);
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
        boolean victoria = resumen.consultaVictoria();
        actualizacionCapaFondo(victoria ? ConstantesCliente.COLOR_CAPA_VICTORIA : ConstantesCliente.COLOR_CAPA_DERROTA);

        int espacio = ConstantesCliente.ESPACIO_PANTALLA;
        GridBagConstraints posicion = new GridBagConstraints();
        posicion.insets = new Insets(espacio, espacio, espacio, espacio);
        posicion.gridx = 0;
        posicion.gridy = 0;

        // Título: ganó o perdió, en grande y con color, junto al animal ganador.
        EstadisticasVisibles ganador = victoria ? resumen.consultaPropio() : resumen.consultaOponente();
        JLabel titulo = EstiloVista.etiqueta(victoria ? ConstantesCliente.TEXTO_RESUMEN_VICTORIA : ConstantesCliente.TEXTO_RESUMEN_DERROTA,
                ConstantesCliente.TAMANO_FUENTE_TITULO_RESUMEN, true,
                victoria ? ConstantesCliente.COLOR_RESUMEN_VICTORIA : ConstantesCliente.COLOR_RESUMEN_DERROTA);
        titulo.setIcon(EstiloVista.imagenDeEspecie(ganador.consultaEspecie(), ConstantesCliente.TAMANO_ICONO_TITULO_RESUMEN).getIcon());
        titulo.setIconTextGap(ConstantesCliente.ESPACIO_TARJETAS_RESUMEN);
        add(titulo, posicion);

        posicion.gridy++;
        add(EstiloVista.etiqueta(textoDeLaRazon(resumen), ConstantesCliente.TAMANO_FUENTE_SUBTITULO, false,
                ConstantesCliente.COLOR_TEXTO_PANTALLA), posicion);
        posicion.gridy++;
        add(EstiloVista.etiqueta(ConstantesCliente.TEXTO_RESUMEN_DURACION + formatoDuracion(resumen.consultaDuracion()),
                ConstantesCliente.TAMANO_FUENTE_SUBTITULO, true, ConstantesCliente.COLOR_ACENTO), posicion);

        // Dos tarjetas lado a lado: primero el propio jugador y después el oponente.
        EstadisticasVisibles propio = resumen.consultaPropio();
        EstadisticasVisibles oponente = resumen.consultaOponente();
        JPanel tarjetas = new JPanel(new GridLayout(1, 2, ConstantesCliente.ESPACIO_TARJETAS_RESUMEN, 0));
        tarjetas.setOpaque(false);
        tarjetas.add(tarjetaDelJugador(ConstantesCliente.TEXTO_RESUMEN_TU + " (" + propio.consultaApodo() + ")", propio, oponente));
        tarjetas.add(tarjetaDelJugador(oponente.consultaApodo(), oponente, propio));
        posicion.gridy++;
        add(tarjetas, posicion);

        JButton volver = EstiloVista.boton(ConstantesCliente.TEXTO_BOTON_VOLVER, ConstantesCliente.COLOR_BOTON_PRINCIPAL);
        volver.addActionListener(evento -> controlador.volverAlInicio());
        posicion.gridy++;
        add(volver, posicion);

        revalidate();
        repaint();
    }

    // La tarjeta de un jugador: su animal, su nombre y sus tres estadísticas. "otro" sirve para saber cuál valor es el mejor.
    private PanelTarjeta tarjetaDelJugador(String nombre, EstadisticasVisibles jugador, EstadisticasVisibles otro) {
        PanelTarjeta tarjeta = new PanelTarjeta(new GridBagLayout());
        int espacio = ConstantesCliente.ESPACIO_PANTALLA;
        GridBagConstraints posicion = new GridBagConstraints();
        posicion.insets = new Insets(espacio / 2, espacio, espacio / 2, espacio);
        posicion.gridx = 0;
        posicion.gridy = 0;
        posicion.gridwidth = 2;
        tarjeta.add(EstiloVista.imagenDeEspecie(jugador.consultaEspecie(), ConstantesCliente.TAMANO_ICONO_TARJETA_RESUMEN), posicion);
        posicion.gridy++;
        tarjeta.add(EstiloVista.etiqueta(nombre, ConstantesCliente.TAMANO_FUENTE_ENCABEZADO, true,
                ConstantesCliente.COLOR_TEXTO_PANTALLA), posicion);

        posicion.gridwidth = 1;
        // Bots y tazas: gana quien tiene más. Animales perdidos: gana quien perdió menos.
        fila(tarjeta, posicion, ConstantesCliente.TEXTO_RESUMEN_BOTS, jugador.consultaBotsEliminados(),
                jugador.consultaBotsEliminados() > otro.consultaBotsEliminados());
        fila(tarjeta, posicion, ConstantesCliente.TEXTO_RESUMEN_PERDIDOS, jugador.consultaAnimalesPerdidos(),
                jugador.consultaAnimalesPerdidos() < otro.consultaAnimalesPerdidos());
        fila(tarjeta, posicion, ConstantesCliente.TEXTO_RESUMEN_TAZAS, jugador.consultaTazasConsumidas(),
                jugador.consultaTazasConsumidas() > otro.consultaTazasConsumidas());
        return tarjeta;
    }

    // Agrega una fila: el nombre de la estadística a la izquierda y su valor a la derecha (resaltado si es el mejor).
    private void fila(JPanel tarjeta, GridBagConstraints posicion, String nombre, int valor, boolean esElMejor) {
        posicion.gridy++;
        posicion.gridx = 0;
        posicion.anchor = GridBagConstraints.WEST;
        JLabel etiquetaNombre = EstiloVista.etiqueta(nombre, ConstantesCliente.TAMANO_FUENTE_ETIQUETA, false,
                ConstantesCliente.COLOR_TEXTO_SECUNDARIO);
        etiquetaNombre.setHorizontalAlignment(SwingConstants.LEFT);
        tarjeta.add(etiquetaNombre, posicion);

        posicion.gridx = 1;
        posicion.anchor = GridBagConstraints.EAST;
        Color color = esElMejor ? ConstantesCliente.COLOR_ACENTO : ConstantesCliente.COLOR_TEXTO_PANTALLA;
        JLabel etiquetaValor = EstiloVista.etiqueta(String.valueOf(valor), ConstantesCliente.TAMANO_FUENTE_ESTADISTICA,
                esElMejor, color);
        etiquetaValor.setHorizontalAlignment(SwingConstants.RIGHT);
        tarjeta.add(etiquetaValor, posicion);
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
