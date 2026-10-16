package jungleattack.cliente.modelo;

import common.model.Especie;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * El modelo del cliente: guarda lo que el jugador conoce de la partida y avisa
 * a quienes lo observan cuando algo cambia. Utiliza el patrón Observer para
 * mantener separadas la lógica del modelo y la interfaz.
 *
 * El estado verdadero de la partida lo tiene el servidor. Este modelo guarda
 * únicamente la información que el cliente necesita mostrar.
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
    
    // Especies disponibles y elecciones de los jugadores.
    private List<Especie> especiesDisponibles = new ArrayList<>();
    private Especie especieOponente;
    private boolean seleccionCompleta;
    
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

    // Guarda la información de selección recibida del servidor.
    public void actualizacionSeleccion(List<Especie> disponibles, Especie propia,
            Especie oponente, boolean completa) {
        this.especiesDisponibles = new ArrayList<>(disponibles);
        this.jugadorLocal.actualizacionEspecie(propia);
        this.especieOponente = oponente;
        this.seleccionCompleta = completa;
        this.fase = FaseCliente.SELECCION;
        notificacionObservadores();
    }

    // Reinicia los datos de selección y vuelve al inicio.
    public void reinicioPartida() {
        this.fase = FaseCliente.INICIO;
        this.jugadorLocal.reinicio();
        this.especiesDisponibles = new ArrayList<>();
        this.especieOponente = null;
        this.seleccionCompleta = false;
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

    public List<Especie> consultaEspeciesDisponibles() {
        return List.copyOf(especiesDisponibles);
    }

    public Especie consultaEspecieOponente() {
        return especieOponente;
    }

    public boolean consultaSeleccionCompleta() {
        return seleccionCompleta;
    }
}
