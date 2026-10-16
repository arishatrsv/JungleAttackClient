package jungleattack.cliente.controlador;

import common.constantes.ConstantesProtocolo;
import common.model.Especie;
import java.util.List;
import javax.swing.SwingUtilities;
import jungleattack.cliente.modelo.AnimalVisible;
import jungleattack.cliente.modelo.FaseCliente;
import jungleattack.cliente.modelo.ModeloJuego;
import jungleattack.cliente.modelo.PuntoVisible;
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
            ConstantesProtocolo.UNIR_PARTIDA,
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
    public void seleccionAnimales(List<AnimalVisible> animales) {
        // TODO: implementar
    }

    public void ordenMovimiento(List<AnimalVisible> animales, PuntoVisible destino) {
        // TODO: implementar
    }

    public void ordenAtaque(List<AnimalVisible> animales, AnimalVisible objetivo) {
        // TODO: implementar
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
            case ConstantesProtocolo.ERROR -> {
                String motivo = ConversorMensajes.campoDelContenido(mensaje, ConstantesProtocolo.CAMPO_MOTIVO);
                modelo.actualizacionError(motivo);
            }
            case ConstantesProtocolo.DESCONEXION -> {
                // Se perdió la conexión: aviso y vuelvo a la pantalla de inicio.
                modelo.actualizacionError(ConstantesProtocolo.MOTIVO_SIN_CONEXION);
                modelo.actualizacionEstado(FaseCliente.INICIO);
            }
            default -> {
                // TODO: los demás tipos (unirse, selección, estado de la partida...) se agregan en los siguientes pasos.
            }
        }
    }
}
