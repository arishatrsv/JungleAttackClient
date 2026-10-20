package jungleattack.cliente.modelo;

import common.model.Especie;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    // Identificadores de los animales propios que el jugador tiene seleccionados con el ratón.
    private final List<String> animalesSeleccionados = new ArrayList<>();

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
        this.animalesVisibles.clear();
        this.animalesSeleccionados.clear();
        aplicacionEstadoVisible(danio, idsZonasVisibles, animales, tazas, banderas);
        this.fase = FaseCliente.JUEGO;
        notificacionObservadores();
    }

    /**
     * Reemplaza lo que el jugador ve por la última foto que mandó el servidor (STATE_UPDATE): las zonas visibles
     * y todo lo que hay en ellas. Los animales que ya se conocían conservan su posición dibujada, para que el
     * movimiento se vea fluido. Si el animal ya no se ve (o murió) deja de estar seleccionado, y si la zona
     * que se miraba dejó de ser visible se pasa a mirar otra. Si la partida no está en curso se ignora.
     */
    public void actualizacionEstadoVisible(int danio, List<Integer> idsZonasVisibles, List<AnimalVisible> animales,
            List<TazaVisible> tazas, List<BanderaVisible> banderas) {
        if (mapa == null || fase != FaseCliente.JUEGO) {
            return;
        }
        aplicacionEstadoVisible(danio, idsZonasVisibles, animales, tazas, banderas);
        notificacionObservadores();
    }

    // Lo común de GAME_STARTED y STATE_UPDATE. No avisa a los observadores: lo hace quien lo llama.
    private void aplicacionEstadoVisible(int danio, List<Integer> idsZonasVisibles, List<AnimalVisible> animales,
            List<TazaVisible> tazas, List<BanderaVisible> banderas) {
        this.danio = danio;
        this.zonasVisibles.clear();
        for (int identificador : idsZonasVisibles) {
            ZonaVisible zona = mapa.zonaPorIdentificador(identificador);
            if (zona != null) {
                this.zonasVisibles.add(zona);
            }
        }

        // Los animales que ya estaban se actualizan en lugar de reemplazarse, para no perder su posición dibujada.
        Map<String, AnimalVisible> conocidos = new HashMap<>();
        for (AnimalVisible animal : animalesVisibles) {
            conocidos.put(animal.consultaIdentificador(), animal);
        }
        List<AnimalVisible> actualizados = new ArrayList<>();
        for (AnimalVisible recibido : animales) {
            AnimalVisible conocido = conocidos.get(recibido.consultaIdentificador());
            if (conocido != null && conocido.consultaPropio() == recibido.consultaPropio()) {
                conocido.actualizacionDesdeServidor(recibido.consultaEnergia(), recibido.consultaPosicion());
                actualizados.add(conocido);
            } else {
                actualizados.add(recibido);
            }
        }
        this.animalesVisibles.clear();
        this.animalesVisibles.addAll(actualizados);
        this.tazasVisibles.clear();
        this.tazasVisibles.addAll(tazas);
        this.banderasVisibles.clear();
        this.banderasVisibles.addAll(banderas);

        // Solo se pueden tener seleccionados animales propios que se sigan viendo.
        animalesSeleccionados.removeIf(id -> {
            for (AnimalVisible animal : animalesVisibles) {
                if (animal.consultaIdentificador().equals(id) && animal.consultaPropio()) {
                    return false;
                }
            }
            return true;
        });

        // Si la zona que se miraba ya no es visible, se pasa a mirar la primera zona visible.
        boolean observadaVisible = false;
        for (ZonaVisible zona : zonasVisibles) {
            observadaVisible |= zona.consultaIdentificador() == zonaObservada;
        }
        if (!observadaVisible && !zonasVisibles.isEmpty()) {
            zonaObservada = zonasVisibles.get(0).consultaIdentificador();
        }
    }

    /**
     * Cambia los animales seleccionados. Se ignoran los identificadores que no sean de un animal propio visible.
     */
    public void actualizacionAnimalesSeleccionados(List<String> identificadores) {
        animalesSeleccionados.clear();
        for (String identificador : identificadores) {
            for (AnimalVisible animal : animalesVisibles) {
                if (animal.consultaIdentificador().equals(identificador) && animal.consultaPropio()
                        && !animalesSeleccionados.contains(identificador)) {
                    animalesSeleccionados.add(identificador);
                }
            }
        }
        notificacionObservadores();
    }

    // Los animales vivos que están en la zona que se está mirando (son los que se dibujan y se pueden tomar con el ratón).
    public List<AnimalVisible> consultaAnimalesEnZonaObservada() {
        List<AnimalVisible> enZona = new ArrayList<>();
        if (mapa == null) {
            return enZona;
        }
        for (AnimalVisible animal : animalesVisibles) {
            ZonaVisible zona = mapa.zonaPorPosicion(animal.consultaPosicion());
            if (animal.consultaVida() && zona != null && zona.consultaIdentificador() == zonaObservada) {
                enZona.add(animal);
            }
        }
        return enZona;
    }

    public boolean consultaSeleccionado(AnimalVisible animal) {
        return animalesSeleccionados.contains(animal.consultaIdentificador());
    }

    // Los animales que el jugador tiene seleccionados ahora.
    public List<AnimalVisible> consultaAnimalesSeleccionados() {
        List<AnimalVisible> seleccionados = new ArrayList<>();
        for (AnimalVisible animal : animalesVisibles) {
            if (animalesSeleccionados.contains(animal.consultaIdentificador())) {
                seleccionados.add(animal);
            }
        }
        return seleccionados;
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
        this.animalesSeleccionados.clear();
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
