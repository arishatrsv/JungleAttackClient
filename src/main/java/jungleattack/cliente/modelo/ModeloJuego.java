package jungleattack.cliente.modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * El modelo del cliente: guarda lo que el jugador sabe de la partida y avisa a quienes lo observan
 * cuando algo cambia. Aquí uso el patrón Observer: ModeloJuego es el "sujeto" y las pantallas
 * (que implementan ObservadorModelo) son los "observadores". Así el modelo no sabe nada de la
 * interfaz y la vista no tiene lógica del juego.
 *
 * El estado verdadero de la partida lo tiene el servidor. Esto es solo lo que el cliente conoce.
 *
 * En este paso solo está implementado lo de la fase, el jugador y los errores
 */
public class ModeloJuego {

    // Fase actual de la aplicación (cada fase tiene su pantalla).
    private FaseCliente fase = FaseCliente.INICIO;

    private final JugadorLocal jugadorLocal = new JugadorLocal();

    private final List<ZonaVisible> zonasVisibles = new ArrayList<>();
    private final List<AnimalVisible> animalesVisibles = new ArrayList<>();
    private final List<TazaVisible> tazasVisibles = new ArrayList<>();
    // Puede haber más de una bandera visible a la vez (2).
    private final List<BanderaVisible> banderasVisibles = new ArrayList<>();
    private ResumenVisible resumen;

    // CopyOnWriteArrayList: así se puede registrar un observador mientras se está notificando a los demás.
    private final List<ObservadorModelo> observadores = new CopyOnWriteArrayList<>();

    // Último error para mostrarle al jugador (es un código de ConstantesProtocolo). Es null si no hay.
    private String error;


    //Cambia la fase de la aplicación y avisa a los observadores.
    public void actualizacionEstado(FaseCliente fase) {
        this.fase = fase;
        notificacionObservadores();
    }

    //Guarda un error para mostrarlo y avisa a los observadores.
    public void actualizacionError(String motivo) {
        this.error = motivo;
        notificacionObservadores();
    }

    //Borra el error ya mostrado
    public void limpiezaError() {
        this.error = null;
    }

    public void actualizacionZona(ZonaVisible zona) {
        // TODO: implementar
    }

    public void actualizacionResumen(ResumenVisible resumen) {
        // TODO: implementar
    }

    public void actualizacionMovimiento(AnimalVisible animal, PuntoVisible destino) {
        // TODO: implementar
    }

    public void actualizacionAnimal(AnimalVisible animal) {
        // TODO: implementar
    }

    //Registra un observador para que reciba los avisos de cambio.
    public void registroObservador(ObservadorModelo observador) {
        observadores.add(observador);
    }

    //Avisa a todos los observadores que el modelo cambió.
    public void notificacionObservadores() {
        for (ObservadorModelo observador : observadores) {
            observador.actualizacionModelo();
        }
    }

    public FaseCliente consultaFase() {
        return fase;
    }

    public JugadorLocal consultaJugadorLocal() {
        return jugadorLocal;
    }

    public String consultaError() {
        return error;
    }
}
