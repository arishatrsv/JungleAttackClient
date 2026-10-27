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

    // Cambia la pantalla que se ve: la dibuja y la pone dentro de la ventana.
    public void cambioPantalla(PantallaBase pantalla) {
        pantallaActual = pantalla;
        pantalla.dibujo();
        setContentPane(pantalla);
        revalidate();
        repaint();
    }

    // Muestra la ventana.
    public void visualizacion() {
        setVisible(true);
    }

    // El modelo cambió: muestro el error si hay y reviso si toca cambiar de pantalla.
    @Override
    public void actualizacionModelo() {
        String error = modelo.consultaError();
        if (error != null) {
            modelo.limpiezaError(); // lo borro antes de mostrarlo para que no se muestre dos veces
            mostrarError(error);
        }
        FaseCliente fase = modelo.consultaFase();
        if (fase != faseMostrada) {
            mostrarPantallaDeLaFase();
        } else if (pantallaActual != null) {
            pantallaActual.dibujo(); // misma fase, pero los datos cambiaron: vuelvo a dibujar la pantalla
        }
    }

    // Los errores de las órdenes durante la partida se avisan sobre el mapa para no interrumpir el juego;
    // todos los demás errores se muestran en una ventana.
    private void mostrarError(String motivo) {
        String texto = textoDeError(motivo);
        if (modelo.consultaFase() == FaseCliente.JUEGO && faseMostrada == FaseCliente.JUEGO
                && pantallaActual instanceof PantallaJuego juego && esErrorDeOrden(motivo)) {
            juego.mostrarAviso(texto);
            return;
        }
        JOptionPane.showMessageDialog(this, texto, ConstantesCliente.TITULO_ERROR, JOptionPane.ERROR_MESSAGE);
    }

    // Errores que puede devolver el servidor a una orden de mover o atacar (el jugador solo debe corregir la orden).
    static boolean esErrorDeOrden(String motivo) {
        return switch (motivo) {
            case ConstantesProtocolo.MOTIVO_ANIMAL_INVALIDO,
                    ConstantesProtocolo.MOTIVO_DESTINO_INVALIDO,
                    ConstantesProtocolo.MOTIVO_OBJETIVO_INVALIDO,
                    ConstantesProtocolo.MOTIVO_FUERA_DE_ALCANCE,
                    ConstantesProtocolo.MOTIVO_FASE_INCORRECTA,
                    ConstantesProtocolo.MOTIVO_ERROR_INTERNO -> true;
            default -> false;
        };
    }

    // Muestra la pantalla que corresponde a la fase actual, solo si la fase cambió.
    private void mostrarPantallaDeLaFase() {
        FaseCliente fase = modelo.consultaFase();
        if (fase == faseMostrada) {
            return;
        }
        switch (fase) {
            case INICIO -> cambioPantalla(new PantallaInicio(controlador));
            case ESPERA -> cambioPantalla(new PantallaEspera(modelo, controlador));
            case SELECCION -> cambioPantalla(new PantallaSeleccion(modelo, controlador));
            case JUEGO -> cambioPantalla(new PantallaJuego(modelo, controlador));
            case RESUMEN -> cambioPantalla(new PantallaResumen(modelo, controlador));
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
            case ConstantesProtocolo.MOTIVO_PARTIDA_NO_ENCONTRADA -> ConstantesCliente.TEXTO_ERROR_PARTIDA_NO_ENCONTRADA;
            case ConstantesProtocolo.MOTIVO_PARTIDA_LLENA -> ConstantesCliente.TEXTO_ERROR_PARTIDA_LLENA;
            case ConstantesProtocolo.MOTIVO_ESPECIE_NO_DISPONIBLE -> ConstantesCliente.TEXTO_ERROR_ESPECIE_NO_DISPONIBLE;
            case ConstantesProtocolo.MOTIVO_ESPECIE_YA_ELEGIDA -> ConstantesCliente.TEXTO_ERROR_ESPECIE_YA_ELEGIDA;
            case ConstantesProtocolo.MOTIVO_OPONENTE_DESCONECTADO -> ConstantesCliente.TEXTO_ERROR_OPONENTE_DESCONECTADO;
            case ConstantesProtocolo.MOTIVO_ANIMAL_INVALIDO -> ConstantesCliente.TEXTO_ERROR_ANIMAL_INVALIDO;
            case ConstantesProtocolo.MOTIVO_DESTINO_INVALIDO -> ConstantesCliente.TEXTO_ERROR_DESTINO_INVALIDO;
            case ConstantesProtocolo.MOTIVO_OBJETIVO_INVALIDO -> ConstantesCliente.TEXTO_ERROR_OBJETIVO_INVALIDO;
            case ConstantesProtocolo.MOTIVO_FUERA_DE_ALCANCE -> ConstantesCliente.TEXTO_ERROR_FUERA_DE_ALCANCE;
            default -> ConstantesCliente.TEXTO_ERROR_GENERICO;
        };
    }
}