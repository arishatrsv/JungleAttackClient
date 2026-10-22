package jungleattack.cliente.controlador;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import common.constantes.ConstantesProtocolo;
import common.model.Especie;
import java.util.List;
import javax.swing.SwingUtilities;
import jungleattack.cliente.modelo.AnimalVisible;
import jungleattack.cliente.modelo.BanderaVisible;
import jungleattack.cliente.modelo.EfectoCombate;
import jungleattack.cliente.modelo.FaseCliente;
import jungleattack.cliente.modelo.MapaCliente;
import jungleattack.cliente.modelo.ModeloJuego;
import jungleattack.cliente.modelo.PuntoVisible;
import jungleattack.cliente.modelo.TazaVisible;
import jungleattack.cliente.modelo.ZonaVisible;
import jungleattack.cliente.protocolo.ConversorMensajes;
import jungleattack.cliente.protocolo.Mensaje;
import jungleattack.cliente.red.ConexionServidor;
import java.util.ArrayList;

/**
 * Controlador principal. Recibe lo que quiere hacer el usuario y lo manda al
 *         servidor, y recibe lo que responde el servidor y actualiza el modelo.
 * El controlador no decide si algo es válido, eso lo decide el servidor.
 * Prepara los mensajes y aplica las respuestas.
 */
public class ControladorJuego {

    private final ModeloJuego modelo;
    private final ConexionServidor conexion;

    public ControladorJuego(ModeloJuego modelo, ConexionServidor conexion) {
        this.modelo = modelo;
        this.conexion = conexion;
    }

    //El jugador quiere crear una partida. Conecta con el servidor y envía CREATE_MATCH.
    public void creacionPartida(String apodo) {
        String apodoLimpio = apodo == null ? "" : apodo.trim();
        if (apodoLimpio.isEmpty()) {
            modelo.actualizacionError(ConstantesProtocolo.MOTIVO_APODO_INVALIDO);
            return;
        }
        modelo.consultaJugadorLocal().actualizacionApodo(apodoLimpio);

        // Conectarse puede tardar, así que lo hago en otro hilo para que la ventana no se congele.
        Thread hilo = new Thread(() -> {
            if (!conexion.conexion(this::procesamientoActualizacion)) {
                // Los cambios al modelo siempre en el hilo de Swing.
                SwingUtilities.invokeLater(() ->
                        modelo.actualizacionError(ConstantesProtocolo.MOTIVO_SIN_CONEXION));
                return;
            }
            conexion.envioMensaje(ConversorMensajes.creacionMensaje(
                    ConstantesProtocolo.CREAR_PARTIDA, ConstantesProtocolo.CAMPO_APODO, apodoLimpio));
        }, "conexion-crear-partida");
        hilo.setDaemon(true); //daemon para que no bloquee el cierre de la aplicacion
        hilo.start();
    }

public void unionPartida(String apodo, String codigoPartida) {
    String apodoLimpio = apodo == null ? "" : apodo.trim();
    String codigoLimpio = codigoPartida == null ? "" : codigoPartida.trim();
    if (apodoLimpio.isEmpty()) {
        modelo.actualizacionError(ConstantesProtocolo.MOTIVO_APODO_INVALIDO);
        return;
    }
    if (codigoLimpio.isEmpty()) {
        modelo.actualizacionError(ConstantesProtocolo.MOTIVO_PARTIDA_NO_ENCONTRADA);
        return;
    }
    modelo.consultaJugadorLocal().actualizacionApodo(apodoLimpio);
    Mensaje mensaje = ConversorMensajes.creacionMensaje(
            ConstantesProtocolo.UNIRSE_PARTIDA,
            ConstantesProtocolo.CAMPO_APODO, apodoLimpio,
            ConstantesProtocolo.CAMPO_CODIGO, codigoLimpio);
    conexionYEnvio(mensaje);
}

public void seleccionEspecie(Especie especie) {
    if (especie == null) {
        return;
    }
    Mensaje mensaje = ConversorMensajes.creacionMensaje(
            ConstantesProtocolo.SELECCIONAR_ANIMAL,
            ConstantesProtocolo.CAMPO_ESPECIE,
            especie.consultaIdentificador());
    conexionYEnvio(mensaje);
}
    // El jugador seleccionó animales con el ratón. Es solo del cliente: no se le avisa al servidor.
    public void seleccionAnimales(List<AnimalVisible> animales) {
        List<String> identificadores = new ArrayList<>();
        for (AnimalVisible animal : animales) {
            identificadores.add(animal.consultaIdentificador());
        }
        modelo.actualizacionAnimalesSeleccionados(identificadores);
    }

    /**
     * El jugador mandó mover animales a un punto del mapa (en las coordenadas de mapa.json).
     * Se envía MOVE_ANIMALS con los identificadores separados por comas y el destino. El cliente no mueve nada:
     * el servidor decide la ruta y las posiciones, y el movimiento llega en los STATE_UPDATE.
     */
    public void ordenMovimiento(List<AnimalVisible> animales, PuntoVisible destino) {
        if (animales == null || animales.isEmpty() || destino == null) {
            return;
        }
        List<String> identificadores = new ArrayList<>();
        for (AnimalVisible animal : animales) {
            identificadores.add(animal.consultaIdentificador());
        }
        conexionYEnvio(ConversorMensajes.creacionMensaje(
                ConstantesProtocolo.MOVER_ANIMALES,
                ConstantesProtocolo.CAMPO_IDS_ANIMALES, String.join(ConstantesProtocolo.SEPARADOR_LISTA, identificadores),
                ConstantesProtocolo.CAMPO_X, String.valueOf(destino.consultaPosicionX()),
                ConstantesProtocolo.CAMPO_Y, String.valueOf(destino.consultaPosicionY())));
    }

    /**
     * El jugador mandó atacar a un animal con los animales seleccionados. Se envía ATTACK con los identificadores
     * de los atacantes (separados por comas) y el identificador del objetivo. Quién está al alcance y cuánto daño se
     * hace lo decide el servidor: el cliente solo muestra el resultado en los STATE_UPDATE.
     */
    public void ordenAtaque(List<AnimalVisible> animales, AnimalVisible objetivo) {
        if (animales == null || animales.isEmpty() || objetivo == null) {
            return;
        }
        List<String> identificadores = new ArrayList<>();
        for (AnimalVisible animal : animales) {
            identificadores.add(animal.consultaIdentificador());
        }
        conexionYEnvio(ConversorMensajes.creacionMensaje(
                ConstantesProtocolo.ATACAR,
                ConstantesProtocolo.CAMPO_IDS_ANIMALES, String.join(ConstantesProtocolo.SEPARADOR_LISTA, identificadores),
                ConstantesProtocolo.CAMPO_OBJETIVO, objetivo.consultaIdentificador()));
    }

    public void consultaZona(ZonaVisible zona) {
        // TODO: implementar
    }
    private void conexionYEnvio(Mensaje mensaje) {
        Thread hilo = new Thread(() -> {
            if (!conexion.conexion(this::procesamientoActualizacion)) {
                SwingUtilities.invokeLater(() ->
                        modelo.actualizacionError(ConstantesProtocolo.MOTIVO_SIN_CONEXION));
                return;
            }
            conexion.envioMensaje(mensaje);
        }, "conexion-envio");

        hilo.setDaemon(true);
        hilo.start();
    }
    /**
     * Recibe un mensaje del servidor. Esto lo llama el hilo de red, y ese hilo no puede tocar Swing,
     * así que paso el trabajo al hilo de Swing con invokeLater. Ahí sí se puede cambiar el modelo
     * y, con eso, la pantalla.
     */
    public void procesamientoActualizacion(Mensaje mensaje) {
        SwingUtilities.invokeLater(() -> aplicacionActualizacion(mensaje));
    }

    // Aplica al modelo lo que dijo el servidor, según el tipo de mensaje.
    private void aplicacionActualizacion(Mensaje mensaje) {
        switch (mensaje.consultaTipo()) {
            case ConstantesProtocolo.PARTIDA_CREADA -> {
                // La partida se creó: guardo el código y paso a la sala de espera.
                String codigo = ConversorMensajes.campoDelContenido(mensaje, ConstantesProtocolo.CAMPO_CODIGO);
                modelo.consultaJugadorLocal().actualizacionCodigoPartida(codigo);
                modelo.actualizacionEstado(FaseCliente.ESPERA);
            }
            case ConstantesProtocolo.PARTIDA_UNIDA -> {
                // Me uní a una partida: guardo el código. La pantalla cambia cuando llega la selección de animal.
                String codigo = ConversorMensajes.campoDelContenido(mensaje, ConstantesProtocolo.CAMPO_CODIGO);
                modelo.consultaJugadorLocal().actualizacionCodigoPartida(codigo);
            }
            case ConstantesProtocolo.SELECCION_ANIMALES -> aplicacionSeleccion(mensaje);
            case ConstantesProtocolo.PARTIDA_INICIADA -> aplicacionInicio(mensaje);
            case ConstantesProtocolo.ACTUALIZACION_ESTADO -> aplicacionEstado(mensaje);
            case ConstantesProtocolo.ERROR -> {
                String motivo = ConversorMensajes.campoDelContenido(mensaje, ConstantesProtocolo.CAMPO_MOTIVO);
                if (ConstantesProtocolo.MOTIVO_OPONENTE_DESCONECTADO.equals(motivo)) {
                    // El servidor canceló la partida: vuelvo al inicio y aviso del motivo.
                    modelo.reinicioPartida();
                }
                modelo.actualizacionError(motivo);
            }
            case ConstantesProtocolo.DESCONEXION -> {
                // Se perdió la conexión: aviso y vuelvo a la pantalla de inicio.
                modelo.reinicioPartida();
                modelo.actualizacionError(ConstantesProtocolo.MOTIVO_SIN_CONEXION);
                modelo.actualizacionEstado(FaseCliente.INICIO);
            }
            default -> {
                // TODO: los demás tipos (fin de la partida...) se agregan en los siguientes pasos.
            }
        }
    }

    // ANIMAL_SELECTION: especies disponibles, lo que eligió cada uno y si la selección está completa.
    private void aplicacionSeleccion(Mensaje mensaje) {
        String disponibles = ConversorMensajes.campoDelContenido(mensaje, ConstantesProtocolo.CAMPO_DISPONIBLES);
        List<Especie> especiesDisponibles = new ArrayList<>();
        if (disponibles != null && !disponibles.isEmpty()) {
            for (String identificador : disponibles.split(ConstantesProtocolo.SEPARADOR_LISTA)) {
                Especie especie = Especie.consultaPorIdentificador(identificador);
                if (especie != null) {
                    especiesDisponibles.add(especie);
                }
            }
        }
        Especie propia = Especie.consultaPorIdentificador(
                ConversorMensajes.campoDelContenido(mensaje, ConstantesProtocolo.CAMPO_PROPIA)); // texto vacío = null
        Especie oponente = Especie.consultaPorIdentificador(
                ConversorMensajes.campoDelContenido(mensaje, ConstantesProtocolo.CAMPO_OPONENTE));
        boolean completa = Boolean.parseBoolean(
                ConversorMensajes.campoDelContenido(mensaje, ConstantesProtocolo.CAMPO_COMPLETA));
        modelo.actualizacionSeleccion(especiesDisponibles, propia, oponente, completa);
    }

    /**
     * GAME_STARTED: la zona donde empieza el jugador, el daño de su manada, las zonas que ve y lo que hay
     * en ellas. El mapa no viene en el mensaje: se carga de mapa.json. Si el mensaje o el mapa están mal,
     * el jugador ve un error en lugar de quedarse con una pantalla vacía.
     */
    private void aplicacionInicio(Mensaje mensaje) {
        try {
            JsonObject contenido = ConversorMensajes.contenidoComoObjeto(mensaje);
            MapaCliente mapa = MapaCliente.cargaDesdeRecurso();
            FotoVisible foto = lecturaFoto(contenido);
            modelo.actualizacionTazasConsumidas(foto.tazasConsumidas);
            modelo.actualizacionBotsEliminados(foto.botsEliminados);
            modelo.actualizacionInicio(mapa, contenido.get(ConstantesProtocolo.CAMPO_ZONA_INICIAL).getAsInt(),
                    foto.danio, foto.zonasVisibles, foto.animales, foto.tazas, foto.banderas);
        } catch (RuntimeException e) {
            // Mensaje mal formado o mapa que no se pudo cargar.
            modelo.actualizacionError(ConstantesProtocolo.MOTIVO_MENSAJE_INVALIDO);
        }
    }

    /**
     * STATE_UPDATE: una foto de todo lo que el jugador ve ahora. El modelo reemplaza lo que tenía por ella.
     * Un mensaje mal formado se ignora: llegará otro en menos de un segundo, y no vale la pena
     * interrumpir la partida por una sola foto.
     */
    private void aplicacionEstado(Mensaje mensaje) {
        try {
            FotoVisible foto = lecturaFoto(ConversorMensajes.contenidoComoObjeto(mensaje));
            modelo.actualizacionTazasConsumidas(foto.tazasConsumidas);
            modelo.actualizacionBotsEliminados(foto.botsEliminados);
            modelo.actualizacionEfectos(foto.efectos);
            modelo.actualizacionEstadoVisible(foto.danio, foto.zonasVisibles, foto.animales, foto.tazas, foto.banderas);
        } catch (RuntimeException e) {
            // Se ignora esta foto.
        }
    }

    // Lo que tienen en común GAME_STARTED y STATE_UPDATE, ya leído del JSON.
    private static class FotoVisible {
        private int danio;
        private int tazasConsumidas;
        private int botsEliminados;
        private final List<EfectoCombate> efectos = new ArrayList<>();
        private final List<Integer> zonasVisibles = new ArrayList<>();
        private final List<AnimalVisible> animales = new ArrayList<>();
        private final List<TazaVisible> tazas = new ArrayList<>();
        private final List<BanderaVisible> banderas = new ArrayList<>();
    }

    private FotoVisible lecturaFoto(JsonObject contenido) {
        FotoVisible foto = new FotoVisible();
        foto.danio = contenido.get(ConstantesProtocolo.CAMPO_DANIO).getAsInt();
        // Si el servidor no lo manda se toma como 0.
        foto.tazasConsumidas = contenido.has(ConstantesProtocolo.CAMPO_TAZAS_CONSUMIDAS)
                ? contenido.get(ConstantesProtocolo.CAMPO_TAZAS_CONSUMIDAS).getAsInt() : 0;
        foto.botsEliminados = contenido.has(ConstantesProtocolo.CAMPO_BOTS_ELIMINADOS)
                ? contenido.get(ConstantesProtocolo.CAMPO_BOTS_ELIMINADOS).getAsInt() : 0;
        // Los eventos (ataques y muertes) solo vienen en algunos mensajes: si no están, no hay efectos.
        if (contenido.has(ConstantesProtocolo.CAMPO_EVENTOS)) {
            long ahora = System.currentTimeMillis();
            for (JsonElement elemento : contenido.getAsJsonArray(ConstantesProtocolo.CAMPO_EVENTOS)) {
                JsonObject datos = elemento.getAsJsonObject();
                foto.efectos.add(new EfectoCombate(
                        ConstantesProtocolo.EVENTO_MUERTE.equals(datos.get(ConstantesProtocolo.CAMPO_TIPO_EVENTO).getAsString()),
                        Especie.consultaPorIdentificador(datos.get(ConstantesProtocolo.CAMPO_ESPECIE).getAsString()),
                        datos.get(ConstantesProtocolo.CAMPO_OBJETIVO).getAsString(),
                        datos.get(ConstantesProtocolo.CAMPO_X).getAsInt(), datos.get(ConstantesProtocolo.CAMPO_Y).getAsInt(), ahora));
            }
        }
        for (JsonElement zona : contenido.getAsJsonArray(ConstantesProtocolo.CAMPO_ZONAS_VISIBLES)) {
            foto.zonasVisibles.add(zona.getAsInt());
        }
        for (JsonElement elemento : contenido.getAsJsonArray(ConstantesProtocolo.CAMPO_ANIMALES)) {
            foto.animales.add(lecturaAnimal(elemento.getAsJsonObject()));
        }
        for (JsonElement elemento : contenido.getAsJsonArray(ConstantesProtocolo.CAMPO_TAZAS)) {
            JsonObject datos = elemento.getAsJsonObject();
            foto.tazas.add(new TazaVisible(datos.get(ConstantesProtocolo.CAMPO_ID).getAsString(), lecturaPunto(datos),
                    datos.get(ConstantesProtocolo.CAMPO_ZONA).getAsInt()));
        }
        for (JsonElement elemento : contenido.getAsJsonArray(ConstantesProtocolo.CAMPO_BANDERAS)) {
            JsonObject datos = elemento.getAsJsonObject();
            foto.banderas.add(new BanderaVisible(lecturaPunto(datos), datos.get(ConstantesProtocolo.CAMPO_ZONA).getAsInt(),
                    datos.get(ConstantesProtocolo.CAMPO_ES_PROPIO).getAsBoolean()));
        }
        return foto;
    }

    private AnimalVisible lecturaAnimal(JsonObject datos) {
        return new AnimalVisible(
                datos.get(ConstantesProtocolo.CAMPO_ID).getAsString(),
                Especie.consultaPorIdentificador(datos.get(ConstantesProtocolo.CAMPO_ESPECIE).getAsString()),
                datos.get(ConstantesProtocolo.CAMPO_ENERGIA).getAsInt(),
                datos.get(ConstantesProtocolo.CAMPO_ENERGIA_MAXIMA).getAsInt(),
                lecturaPunto(datos),
                datos.get(ConstantesProtocolo.CAMPO_ES_PROPIO).getAsBoolean(),
                datos.get(ConstantesProtocolo.CAMPO_ES_BOT).getAsBoolean());
    }

    private PuntoVisible lecturaPunto(JsonObject datos) {
        return new PuntoVisible(datos.get(ConstantesProtocolo.CAMPO_X).getAsInt(),
                datos.get(ConstantesProtocolo.CAMPO_Y).getAsInt());
    }
}
