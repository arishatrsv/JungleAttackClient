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

    // Datos de la partida en curso (llegan en GAME_STARTED).
    private MapaCliente mapa;
    private int zonaInicial;
    // La zona que se está mirando ahora: el jugador ve una zona a la vez, entre las que tiene visibles.
    private int zonaObservada;
    private int danio;

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

    /**
     * Guarda lo que llegó en GAME_STARTED y pasa a la fase de juego, avisando una sola vez a los observadores.
     * Al empezar el jugador mira su zona inicial.
     *
     * @param idsZonasVisibles números de las zonas que el servidor dejó ver al jugador
     */
    public void actualizacionInicio(MapaCliente mapa, int zonaInicial, int danio, List<Integer> idsZonasVisibles,
            List<AnimalVisible> animales, List<TazaVisible> tazas, List<BanderaVisible> banderas) {
        this.mapa = mapa;
        this.zonaInicial = zonaInicial;
        this.zonaObservada = zonaInicial;
        this.danio = danio;
        this.zonasVisibles.clear();
        for (int identificador : idsZonasVisibles) {
            ZonaVisible zona = mapa.zonaPorIdentificador(identificador);
            if (zona != null) {
                this.zonasVisibles.add(zona);
            }
        }
        this.animalesVisibles.clear();
        this.animalesVisibles.addAll(animales);
        this.tazasVisibles.clear();
        this.tazasVisibles.addAll(tazas);
        this.banderasVisibles.clear();
        this.banderasVisibles.addAll(banderas);
        this.fase = FaseCliente.JUEGO;
        notificacionObservadores();
    }

    /**
     * Cambia la zona que se está mirando. Solo se puede mirar una zona que el jugador tenga visible.
     *
     * @return true si se cambió; false si esa zona no es visible
     */
    public boolean actualizacionZonaObservada(int identificadorZona) {
        for (ZonaVisible zona : zonasVisibles) {
            if (zona.consultaIdentificador() == identificadorZona) {
                this.zonaObservada = identificadorZona;
                notificacionObservadores();
                return true;
            }
        }
        return false;
    }

    // Reinicia los datos de selección y de la partida, y vuelve al inicio.
    public void reinicioPartida() {
        this.fase = FaseCliente.INICIO;
        this.jugadorLocal.reinicio();
        this.especiesDisponibles = new ArrayList<>();
        this.especieOponente = null;
        this.seleccionCompleta = false;
        this.mapa = null;
        this.zonaInicial = 0;
        this.zonaObservada = 0;
        this.danio = 0;
        this.zonasVisibles.clear();
        this.animalesVisibles.clear();
        this.tazasVisibles.clear();
        this.banderasVisibles.clear();
        this.resumen = null;
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

    // Null hasta que empieza la partida.
    public MapaCliente consultaMapa() {
        return mapa;
    }

    public int consultaZonaInicial() {
        return zonaInicial;
    }

    public int consultaZonaObservada() {
        return zonaObservada;
    }

    public int consultaDanio() {
        return danio;
    }

    // Zonas cuyo contenido el jugador puede ver ahora.
    public List<ZonaVisible> consultaZonasVisibles() {
        return List.copyOf(zonasVisibles);
    }

    public List<AnimalVisible> consultaAnimalesVisibles() {
        return List.copyOf(animalesVisibles);
    }

    public List<TazaVisible> consultaTazasVisibles() {
        return List.copyOf(tazasVisibles);
    }

    public List<BanderaVisible> consultaBanderasVisibles() {
        return List.copyOf(banderasVisibles);
    }
}