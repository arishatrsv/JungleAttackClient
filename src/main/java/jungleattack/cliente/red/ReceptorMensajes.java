package jungleattack.cliente.red;

import common.constantes.ConstantesProtocolo;
import jungleattack.cliente.protocolo.ConversorMensajes;
import jungleattack.cliente.protocolo.Mensaje;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.function.Consumer;

/**
 * Lee los mensajes que manda el servidor (una línea JSON por mensaje) y entrega cada uno
 * a quien lo va a interpretar (el ControladorJuego).
 *
 * Esto corre en un hilo aparte, porque readLine() se queda esperando y bloquearía la pantalla.
 * Por eso este hilo NUNCA toca componentes de Swing: quien recibe el mensaje se encarga de eso.
 */
public class ReceptorMensajes {

    private final BufferedReader entrada;

    // A quién le entrego cada mensaje recibido.
    private final Consumer<Mensaje> destino;

    // volatile porque cierre() lo cambia desde otro hilo (el de la interfaz).
    private volatile boolean activo;

    public ReceptorMensajes(BufferedReader entrada, Consumer<Mensaje> destino) {
        this.entrada = entrada;
        this.destino = destino;
    }

    /**
     * Ciclo de lectura: espera líneas del servidor hasta que se cierra la conexión.
     * Si la conexión se cae sin que yo la haya cerrado, aviso con un mensaje de DESCONEXION
     * para que la interfaz le muestre el problema al jugador.
     */
    public void recepcionMensajes() {
        activo = true;
        try {
            String linea;
            while (activo && (linea = entrada.readLine()) != null) {
                if (linea.isBlank()) {
                    continue;
                }
                try {
                    destino.accept(ConversorMensajes.conversionDesdeJson(linea));
                } catch (IllegalArgumentException e) {
                    // El servidor mandó algo que no es un mensaje válido: lo ignoro y sigo leyendo.
                }
            }
        } catch (IOException e) {
            // se cerró el socket mientras esperaba
        }
        if (activo) {
            // Salí del ciclo y nadie llamó a cierre(), la conexión se perdió.
            activo = false;
            destino.accept(new Mensaje(ConstantesProtocolo.DESCONEXION, "{}"));
        }
    }

    /**
     * Detiene la lectura: solo apaga la bandera.
     * NO cierro el BufferedReader aquí a propósito: si otro hilo está esperando dentro de readLine(),
     * close() se queda bloqueado esperándolo (los dos usan el mismo candado interno) y el programa se cuelga.
     * Lo correcto es cerrar el socket: eso hace que readLine() termine y las entradas se cierren solas.
     */
    public void cierre() {
        activo = false;
    }
}
