package jungleattack.cliente.constantes;

/**
 * Constantes de la interfaz y la representación visual
 * Las reglas definitivas de combate y alcance son del servidor.
 * El radio de encuentro está en common.constantes.ConstantesComunes
 */
public class ConstantesCliente {

    public static final int ANCHO_VENTANA = 1000;
    public static final int ALTO_VENTANA = 700;
    public static final int VELOCIDAD_MOVIMIENTO = 60;
    public static final int INTERVALO_ANIMACION = 33;

    //textos de las pantallas
    public static final String TITULO_VENTANA = "Jungle Attack";
    public static final String TEXTO_APODO = "Apodo:";
    public static final String TEXTO_BOTON_CREAR = "Crear partida";
    public static final String TEXTO_CODIGO = "Código de la partida:";
    public static final String TEXTO_ESPERA = "Esperando al otro jugador...";
    public static final String TITULO_ERROR = "Error";
    public static final String TEXTO_ERROR_APODO = "Escribe un apodo válido.";
    public static final String TEXTO_ERROR_SIN_CONEXION = "No se pudo conectar con el servidor.";
    public static final String TEXTO_ERROR_SERVIDOR_LLENO = "El servidor no puede crear más partidas ahora.";
    public static final String TEXTO_ERROR_YA_EN_PARTIDA = "Ya estás dentro de una partida.";
    public static final String TEXTO_ERROR_GENERICO = "Ocurrió un error. Intenta de nuevo.";

    //ancho (en columnas) del campo de texto del apodo.
    public static final int COLUMNAS_CAMPO_APODO = 16;
}
