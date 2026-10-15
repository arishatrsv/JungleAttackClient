package jungleattack.cliente.vista;

import common.constantes.ConstantesProtocolo;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import jungleattack.cliente.constantes.ConstantesCliente;
import jungleattack.cliente.controlador.ControladorJuego;
import jungleattack.cliente.modelo.FaseCliente;
import jungleattack.cliente.modelo.ModeloJuego;
import jungleattack.cliente.modelo.ObservadorModelo;

/**
 * La ventana principal. Muestra una pantalla a la vez y cambia de pantalla según la fase del modelo.
 *
 * Implementa ObservadorModelo (agregué esto al README): se registra en el modelo y cada vez que el
 * modelo cambia, la ventana revisa la fase y muestra la pantalla que corresponde. También muestra los errores.
 */
public class VentanaPrincipal extends JFrame implements ObservadorModelo {

    private final ModeloJuego modelo;
    private final ControladorJuego controlador;

    // Pantalla que se está mostrando ahora.
    private PantallaBase pantallaActual;

    // Fase de la pantalla mostrada, para no volver a crearla si la fase no cambió.
    private FaseCliente faseMostrada;

    public VentanaPrincipal(ModeloJuego modelo, ControladorJuego controlador) {
        this.modelo = modelo;
        this.controlador = controlador;

        setTitle(ConstantesCliente.TITULO_VENTANA);
        setSize(ConstantesCliente.ANCHO_VENTANA, ConstantesCliente.ALTO_VENTANA);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // centrada en la pantalla

        modelo.registroObservador(this);
        mostrarPantallaDeLaFase(); // la pantalla inicial según la fase inicial del modelo
    }

    /** Cambia la pantalla que se ve: la dibuja y la pone dentro de la ventana. */
    public void cambioPantalla(PantallaBase pantalla) {
        pantallaActual = pantalla;
        pantalla.dibujo();
        setContentPane(pantalla);
        revalidate();
        repaint();
    }

    /** Muestra la ventana. */
    public void visualizacion() {
        setVisible(true);
    }

    /** El modelo cambió: muestro el error si hay y reviso si toca cambiar de pantalla. */
    @Override
    public void actualizacionModelo() {
        String error = modelo.consultaError();
        if (error != null) {
            modelo.limpiezaError(); // lo borro antes de mostrarlo para que no se muestre dos veces
            JOptionPane.showMessageDialog(this, textoDeError(error),
                    ConstantesCliente.TITULO_ERROR, JOptionPane.ERROR_MESSAGE);
        }
        mostrarPantallaDeLaFase();
    }

    // Muestra la pantalla que corresponde a la fase actual, solo si la fase cambió.
    private void mostrarPantallaDeLaFase() {
        FaseCliente fase = modelo.consultaFase();
        if (fase == faseMostrada) {
            return;
        }
        switch (fase) {
            case INICIO -> cambioPantalla(new PantallaInicio(controlador));
            case ESPERA -> cambioPantalla(new PantallaEspera(modelo));
            default -> {
                // TODO: pantallas de selección, juego y resumen (siguientes pasos)
            }
        }
        faseMostrada = fase;
    }

    // Convierte el código de un error en un texto para el jugador.
    private String textoDeError(String motivo) {
        return switch (motivo) {
            case ConstantesProtocolo.MOTIVO_APODO_INVALIDO -> ConstantesCliente.TEXTO_ERROR_APODO;
            case ConstantesProtocolo.MOTIVO_SIN_CONEXION -> ConstantesCliente.TEXTO_ERROR_SIN_CONEXION;
            case ConstantesProtocolo.MOTIVO_SERVIDOR_LLENO -> ConstantesCliente.TEXTO_ERROR_SERVIDOR_LLENO;
            case ConstantesProtocolo.MOTIVO_YA_EN_PARTIDA -> ConstantesCliente.TEXTO_ERROR_YA_EN_PARTIDA;
            default -> ConstantesCliente.TEXTO_ERROR_GENERICO;
        };
    }
}
