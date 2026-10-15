package jungleattack.cliente.protocolo;

/**
 * Un mensaje individual del protocolo entre el cliente y el servidor.
 *
 * los atributos son final y se asignan una sola vez en el constructor.
 * cada mensaje que llega crea su propio objeto,
 * así si llegan muchos mensajes muy rápido no se pisan unos a otros.
 */
public class Mensaje {

    // Los valores están en ConstantesProtocolo.
    private final String tipo;

    // Los datos del mensaje, escritos como un texto JSON.
    private final String contenido;

    /**
     * Crea un mensaje con su tipo y su contenido.
     *
     * @param tipo      tipo de mensaje
     * @param contenido datos del mensaje en formato JSON
     */
    public Mensaje(String tipo, String contenido) {
        this.tipo = tipo;
        this.contenido = contenido;
    }

    public String consultaTipo() {
        return tipo;
    }

    //Devuelve el contenido (JSON)
    public String consultaContenido() {
        return contenido;
    }
}