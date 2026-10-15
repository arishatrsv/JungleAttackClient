package jungleattack.cliente.red;

import jungleattack.cliente.aplicacion.ConfiguracionCliente;
import jungleattack.cliente.protocolo.Mensaje;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

/**
 * La conexión del cliente con el servidor: abre el socket, envía mensajes y recibe los que
 * manda el servidor en un hilo propio.
 */
public class ConexionServidor {

    private Socket socket;

    // volatile porque lo lee el hilo de la interfaz y lo cambian otros hilos.
    private volatile boolean conectado;

    private EmisorMensajes emisor;
    private ReceptorMensajes receptor;

    /**
     * Se conecta al servidor (host y puerto de ConfiguracionCliente) y empieza a escuchar.
     * Este método puede tardar si el servidor no responde, por eso NO se debe llamar desde el
     * hilo de la interfaz.
     *
     * @param destino a quién se le entrega cada mensaje que llegue del servidor
     * @return true si se conectó (o ya estaba conectado), false si no se pudo
     */
    public synchronized boolean conexion(Consumer<Mensaje> destino) {
        if (conectado) {
            return true;
        }
        try {
            socket = new Socket(ConfiguracionCliente.HOST_SERVIDOR, ConfiguracionCliente.PUERTO_SERVIDOR);
            emisor = new EmisorMensajes(new PrintWriter(
                    new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true));
            receptor = new ReceptorMensajes(new BufferedReader(
                    new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8)), destino);
            conectado = true;

            // El receptor se queda esperando mensajes, por eso va en su propio hilo.
            // Lo marco como daemon para que no impida cerrar el programa.
            Thread hilo = new Thread(receptor::recepcionMensajes, "receptor-servidor");
            hilo.setDaemon(true);
            hilo.start();
            return true;
        } catch (IOException e) {
            conectado = false;
            return false;
        }
    }

    /** Envía un mensaje al servidor (si no hay conexión, no hace nada). */
    public void envioMensaje(Mensaje mensaje) {
        if (conectado) {
            emisor.envioMensaje(mensaje);
        }
    }

    /** Dice si hay conexión con el servidor. */
    public boolean consultaConectado() {
        return conectado;
    }

    /** Cierra la conexión con el servidor. */
    public synchronized void cierre() {
        conectado = false;
        if (receptor != null) {
            receptor.cierre();
        }
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            // ya estaba cerrado
        }
    }
}