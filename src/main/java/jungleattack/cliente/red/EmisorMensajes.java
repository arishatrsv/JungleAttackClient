package jungleattack.cliente.red;

import jungleattack.cliente.protocolo.ConversorMensajes;
import jungleattack.cliente.protocolo.Mensaje;
import java.io.PrintWriter;

/**
 * Envía mensajes al servidor por el socket: un mensaje por línea, en formato JSON.
 */
public class EmisorMensajes {

    // Salida del socket. Se crea con autoflush, así cada println se envía de inmediato.
    private final PrintWriter salida;

    public EmisorMensajes(PrintWriter salida) {
        this.salida = salida;
    }

    /**
     * Envía un mensaje al servidor. Es synchronized porque puede que el hilo de la interfaz
     * y otro hilo quieran enviar al mismo tiempo, y no quiero que se mezclen dos mensajes en una línea.
     */
    public synchronized void envioMensaje(Mensaje mensaje) {
        salida.println(ConversorMensajes.conversionAJson(mensaje));
    }
}
