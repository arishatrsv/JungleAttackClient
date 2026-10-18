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

/**
 * Controlador principal. Recibe lo que quiere hacer el usuario y lo manda al
 * servidor, y recibe lo que responde el servidor y actualiza el modelo.
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

    // Crea una partida y envía el apodo al servidor.
    public void creacionPartida(String apodo) {
        String apodoLimpio = apodo == null ? "" : apodo.trim();

        if (apodoLimpio.isEmpty()) {
            modelo.actualizacionError(ConstantesProtocolo.MOTIVO_APODO_INVALIDO);
            return;
        }
        modelo.consultaJugadorLocal().actualizacionApodo(apodoLimpio);
        Mensaje mensaje = ConversorMensajes.creacionMensaje(
                ConstantesProtocolo.CREAR_PARTIDA,
                ConstantesProtocolo.CAMPO_APODO, apodoLimpio);
        conexionYEnvio(mensaje);
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
                SwingUtilities.invokeLater(() -> modelo.actualizacionError(ConstantesProtocolo.MOTIVO_SIN_CONEXION));
                return;
            }
            conexion.envioMensaje(mensaje);
        }, "conexion-envio");

        hilo.setDaemon(true);
        hilo.start();
    }

    /**
     * Recibe un mensaje del servidor. Esto lo llama el hilo de red, y ese hilo no
     * puede tocar Swing,
     * así que paso el trabajo al hilo de Swing con invokeLater. Ahí sí se puede
     * cambiar el modelo
     * y, con eso, la pantalla.
     */
    public void procesamientoActualizacion(Mensaje mensaje) {
        SwingUtilities.invokeLater(() -> aplicacionActualizacion(mensaje));
    }

    // Aplica al modelo lo que dijo el servidor, según el tipo de mensaje.
    private void aplicacionActualizacion(Mensaje mensaje) {
        switch (mensaje.consultaTipo()) {
            case ConstantesProtocolo.PARTIDA_CREADA -> {
                String codigo = ConversorMensajes.campoDelContenido(
                        mensaje, ConstantesProtocolo.CAMPO_CODIGO);

                modelo.consultaJugadorLocal().actualizacionCodigoPartida(codigo);
                modelo.actualizacionEstado(FaseCliente.ESPERA);
            }
            case ConstantesProtocolo.PARTIDA_UNIDA -> {
                String codigo = ConversorMensajes.campoDelContenido(
                        mensaje, ConstantesProtocolo.CAMPO_CODIGO);
                modelo.consultaJugadorLocal().actualizacionCodigoPartida(codigo);
            }
            case ConstantesProtocolo.SELECCION_ANIMALES -> aplicacionSeleccion(mensaje);
            case ConstantesProtocolo.ERROR -> {
                String motivo = ConversorMensajes.campoDelContenido(
                        mensaje, ConstantesProtocolo.CAMPO_MOTIVO);
                if (ConstantesProtocolo.MOTIVO_OPONENTE_DESCONECTADO.equals(motivo)) {
                    modelo.reinicioPartida();
                }
                modelo.actualizacionError(motivo);
            }
            case ConstantesProtocolo.DESCONEXION -> {
                conexion.cierre();
                modelo.reinicioPartida();
                modelo.actualizacionError(ConstantesProtocolo.MOTIVO_SIN_CONEXION);
            }
            default -> {
                // Los demás mensajes se implementarán en los siguientes cortes.
            }
        }
    }

    // Actualiza las especies disponibles y las elecciones de ambos jugadores.
    private void aplicacionSeleccion(Mensaje mensaje) {
        String disponiblesTexto = ConversorMensajes.campoDelContenido(
                mensaje, ConstantesProtocolo.CAMPO_DISPONIBLES);
        String propiaTexto = ConversorMensajes.campoDelContenido(
                mensaje, ConstantesProtocolo.CAMPO_PROPIA);
        String oponenteTexto = ConversorMensajes.campoDelContenido(
                mensaje, ConstantesProtocolo.CAMPO_OPONENTE);
        String completaTexto = ConversorMensajes.campoDelContenido(
                mensaje, ConstantesProtocolo.CAMPO_COMPLETA);
        List<Especie> disponibles = new java.util.ArrayList<>();
        if (disponiblesTexto != null && !disponiblesTexto.isBlank()) {
            for (String identificador : disponiblesTexto.split(
                    ConstantesProtocolo.SEPARADOR_LISTA)) {
                Especie especie = Especie.consultaPorIdentificador(identificador.trim());
                if (especie != null) {
                    disponibles.add(especie);
                }
            }
        }
        Especie propia = propiaTexto == null || propiaTexto.isBlank()
                ? null
                : Especie.consultaPorIdentificador(propiaTexto);
        Especie oponente = oponenteTexto == null || oponenteTexto.isBlank()
                ? null
                : Especie.consultaPorIdentificador(oponenteTexto);
        boolean completa = Boolean.parseBoolean(completaTexto);
        modelo.actualizacionSeleccion(disponibles, propia, oponente, completa);
    }
}
