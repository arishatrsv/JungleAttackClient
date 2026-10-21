package jungleattack.cliente.constantes;

import java.awt.Color;

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
    public static final String TEXTO_CODIGO_UNIRSE = "Código:";
    public static final String TEXTO_BOTON_UNIRSE = "Unirse a una partida";
    public static final String TEXTO_ERROR_PARTIDA_NO_ENCONTRADA = "No existe una partida con ese código.";
    public static final String TEXTO_ERROR_PARTIDA_LLENA = "Esa partida ya está llena.";
    public static final String TEXTO_ERROR_ESPECIE_NO_DISPONIBLE = "Ese animal ya fue elegido.";
    public static final String TEXTO_ERROR_ESPECIE_YA_ELEGIDA = "Ya elegiste tu animal.";
    public static final String TEXTO_ERROR_OPONENTE_DESCONECTADO = "Tu oponente se desconectó. La partida se canceló.";
    
    // Textos de la pantalla de selección de animal
    public static final String TEXTO_SELECCION_TITULO = "Elige tu animal";
    public static final String TEXTO_SELECCION_ELIGE = "Elige un animal de la lista.";
    public static final String TEXTO_SELECCION_ESPERA_OPONENTE = "Esperando que tu oponente elija...";
    public static final String TEXTO_SELECCION_COMPLETA = "Los dos eligieron. La partida empezará pronto.";
    public static final String TEXTO_SELECCION_TU_ELECCION = "Tu elección";
    public static final String TEXTO_SELECCION_OPONENTE = "Oponente";

    //ancho (en columnas) del campo de texto del apodo.
    public static final int COLUMNAS_CAMPO_APODO = 16;
    // Ancho del campo de texto del código.
    public static final int COLUMNAS_CAMPO_CODIGO = 6;
    // Tamaño de la imagen de cada animal y cantidad de columnas.
    public static final int TAMANO_IMAGEN_ESPECIE = 96;
    public static final int COLUMNAS_SELECCION = 4;

    // Campos de mapa.json que solo usa el cliente (el servidor no dibuja los nombres de las zonas)
    public static final String CAMPO_ETIQUETA = "etiqueta";
    public static final String CAMPO_ETIQUETA_X = "x";
    public static final String CAMPO_ETIQUETA_Y = "y";
    public static final String CAMPO_ETIQUETA_ANCLAJE = "anclaje";

    // Imágenes de la pantalla de juego (ruta dentro de los recursos y nombre con el que se guarda en la caché)
    public static final String RUTA_FONDO_MAPA = "imagenes/mapa/mapa_fondo.svg";
    public static final String RUTA_BANDERA_PROPIA = "imagenes/objetos/bandera_jugador1.svg";
    public static final String RUTA_BANDERA_OPONENTE = "imagenes/objetos/bandera_jugador2.svg";
    public static final String RUTA_TAZA = "imagenes/objetos/taza_cafe.svg";
    public static final String NOMBRE_FONDO_MAPA = "fondo_mapa";
    public static final String NOMBRE_BANDERA_PROPIA = "bandera_propia";
    public static final String NOMBRE_BANDERA_OPONENTE = "bandera_oponente";
    public static final String NOMBRE_TAZA = "taza";

    // Tamaños en el mapa (en las unidades de mapa.json, antes de ajustar a la ventana)
    public static final int TAMANO_ANIMAL_MAPA = 30;
    public static final int ANCHO_BANDERA_MAPA = 44;
    public static final int ALTO_BANDERA_MAPA = 62; // misma proporción que el SVG (64 x 90)
    public static final int TAMANO_TAZA_MAPA = 28;
    public static final int RADIO_NODO = 8;
    public static final float GROSOR_CAMINO = 6f;
    public static final int ALTO_BARRA_ENERGIA = 4;
    public static final int ANCHO_BARRA_ENERGIA = 22;
    public static final float GROSOR_ANILLO = 2.5f;
    public static final int MARGEN_HUD = 10;
    public static final int RELLENO_HUD = 8;
    public static final int ARCO_HUD = 12;
    public static final int TAMANO_FUENTE_ZONA = 13;
    public static final int TAMANO_FUENTE_HUD = 15;
    // Las imágenes se rasterizan más grandes de lo que se muestran, para que se vean bien al agrandar la ventana
    public static final int FACTOR_CALIDAD_IMAGEN = 2;

    // Colores de la pantalla de juego
    public static final Color COLOR_FONDO_JUEGO = new Color(20, 30, 20);
    public static final Color COLOR_CAMINO = new Color(150, 110, 60);
    public static final Color COLOR_BORDE_CAMINO = new Color(60, 40, 20, 200);
    public static final Color COLOR_NODO = new Color(245, 235, 200);
    public static final Color COLOR_NODO_BORDE = new Color(70, 50, 25);
    public static final Color COLOR_TEXTO_ZONA = Color.WHITE;
    public static final Color COLOR_SOMBRA_TEXTO = new Color(0, 0, 0, 200);
    public static final Color COLOR_NIEBLA = new Color(10, 15, 20, 225); // zonas que el jugador no ve
    public static final Color COLOR_NIEBLA_SUAVE = new Color(10, 15, 20, 110); // zonas que ve pero no está mirando ahora
    public static final Color COLOR_ANILLO_PROPIO = new Color(80, 220, 120);
    public static final Color COLOR_ANILLO_OPONENTE = new Color(240, 90, 90);
    public static final Color COLOR_ANILLO_BOT = new Color(240, 190, 60);
    public static final Color COLOR_BARRA_FONDO = new Color(40, 40, 40, 200);
    public static final Color COLOR_BARRA_ENERGIA = new Color(90, 220, 90);
    public static final Color COLOR_PANEL_HUD = new Color(0, 0, 0, 150);
    public static final Color COLOR_TEXTO_HUD = Color.WHITE;

    // Textos de la pantalla de juego
    public static final String TEXTO_JUEGO_ZONA = "Zona: ";
    public static final String TEXTO_JUEGO_DANIO = "Daño: ";
    public static final String TEXTO_JUEGO_TAZAS = "Tazas: ";
    public static final String TEXTO_JUEGO_ANIMALES = "Animales: ";
    public static final String TEXTO_JUEGO_SEPARADOR = "  |  ";

    // Selección y movimiento con el ratón
    public static final int DISTANCIA_MINIMA_ARRASTRE = 5; // píxeles: menos que esto es un clic, más es un rectángulo de selección
    public static final int RADIO_CLIC_ANIMAL = 18; // unidades de mapa: qué tan cerca debe caer el clic de un animal para tomarlo
    public static final int TIEMPO_SUAVIZADO_MS = 80; // en este tiempo un animal dibujado recorre casi todo el camino hasta su posición real
    public static final Color COLOR_ANILLO_SELECCION = new Color(255, 235, 90);
    public static final float GROSOR_ANILLO_SELECCION = 3.5f;
    public static final Color COLOR_RECTANGULO_SELECCION = new Color(255, 235, 90, 70);
    public static final Color COLOR_BORDE_RECTANGULO_SELECCION = new Color(255, 235, 90, 220);
    public static final String TEXTO_JUEGO_SELECCIONADOS = "Seleccionados: ";
}
