package jungleattack.cliente.protocolo;

import com.google.gson.Gson; //para cinverstit entre json y objetos
import com.google.gson.JsonElement; //leer un camop del contenido de un mensaje
import com.google.gson.JsonObject; //armar el contenido de un mensaje
import com.google.gson.JsonParser; //leer el campo del contenido

/**
 * Convierte los mensajes entre texto JSON y objetos Mensaje.
 *
 * Todos los métodos son estáticos porque la clase no guarda datos propios que cambien,
 * solo hace conversiones. Gson se puede compartir entre hilos sin problema.
 */
public class ConversorMensajes {

    // Un solo objeto Gson para toda la clase.
    private static final Gson GSON = new Gson();

    // Constructor privado: no hace falta crear objetos de esta clase.
    private ConversorMensajes() {
    }

    /**
     * Convierte un texto JSON (una línea que llegó del servidor) en un Mensaje.
     *
     * @throws IllegalArgumentException si el texto no es un JSON válido o no trae el tipo
     */
    public static Mensaje conversionDesdeJson(String json) {
        try {
            Mensaje leido = GSON.fromJson(json, Mensaje.class);
            if (leido == null || leido.consultaTipo() == null) {
                throw new IllegalArgumentException("El mensaje no tiene tipo");
            }
            // Si no trae contenido queda como un objeto vacío para que después no haya null.
            String contenido = leido.consultaContenido() == null ? "{}" : leido.consultaContenido();
            return new Mensaje(leido.consultaTipo(), contenido);
        } catch (RuntimeException e) {
            // JsonSyntaxException y cualquier otro error de formato los convierto en uno solo.
            throw new IllegalArgumentException("Mensaje con formato inválido", e);
        }
    }

    //Convierte un Mensaje en una línea de texto JSON lista para enviar al servidor por el socket.
    public static String conversionAJson(Mensaje mensaje) {
        return GSON.toJson(mensaje);
    }

    /**
     * Crea un mensaje armando el contenido a partir de pares campo-valor.
     * Ejemplo: creacionMensaje(PARTIDA_CREADA, CAMPO_CODIGO, "A7K2")
     * produce un mensaje con el contenido {"codigo":"A7K2"}.
     *
     * @param camposYValores lista de pares: campo1, valor1, campo2, valor2...
     */
    public static Mensaje creacionMensaje(String tipo, String... camposYValores) {
        if (camposYValores.length % 2 != 0) {
            throw new IllegalArgumentException("Cada campo necesita su valor");
        }
        JsonObject contenido = new JsonObject();
        for (int i = 0; i < camposYValores.length; i += 2) {
            contenido.addProperty(camposYValores[i], camposYValores[i + 1]);
        }
        return new Mensaje(tipo, contenido.toString());
    }

    /** //javadoc
     * Lee un campo del contenido de un mensaje.
     *
     * @return el valor del campo, o null si el campo no existe o el contenido no es válido
     */
    public static String campoDelContenido(Mensaje mensaje, String campo) {
        try {
            JsonObject contenido = JsonParser.parseString(mensaje.consultaContenido()).getAsJsonObject();
            JsonElement valor = contenido.get(campo);
            return (valor == null || valor.isJsonNull()) ? null : valor.getAsString();
        } catch (RuntimeException e) {
            return null; // contenido mal formado
        }
    }

    /**
     * Devuelve el contenido del mensaje como objeto JSON, para leer sus listas y objetos anidados
     * (por ejemplo los animales de GAME_STARTED). campoDelContenido solo sirve para valores simples.
     *
     * @throws IllegalArgumentException si el contenido no es un objeto JSON válido
     */
    public static JsonObject contenidoComoObjeto(Mensaje mensaje) {
        try {
            return JsonParser.parseString(mensaje.consultaContenido()).getAsJsonObject();
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("El contenido del mensaje no es un objeto JSON", e);
        }
    }
}
