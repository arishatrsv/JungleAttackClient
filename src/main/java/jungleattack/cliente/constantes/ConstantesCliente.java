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
    public static final String TEXTO_ESPERA_BASE = "Esperando al otro jugador"; // sin puntos: los puntos de la espera se animan
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
    public static final String TEXTO_ERROR_ANIMAL_INVALIDO = "Ese animal no se puede usar para esa orden.";
    public static final String TEXTO_ERROR_DESTINO_INVALIDO = "Ese destino no está dentro del mapa.";
    public static final String TEXTO_ERROR_OBJETIVO_INVALIDO = "No puedes atacar a ese animal.";
    public static final String TEXTO_ERROR_FUERA_DE_ALCANCE = "Tus animales están muy lejos de ese objetivo. Acércalos primero.";

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
    public static final String TEXTO_JUEGO_BOTS = "Bots: ";
    public static final String TEXTO_JUEGO_ANIMALES = "Animales: ";
    public static final String TEXTO_JUEGO_SEPARADOR = "  |  ";

    // Pantalla de resumen (final de la partida)
    public static final String TEXTO_RESUMEN_VICTORIA = "¡Ganaste!";
    public static final String TEXTO_RESUMEN_DERROTA = "Perdiste";
    public static final String TEXTO_RESUMEN_RAZON_BANDERA = "Una manada llegó a la bandera del oponente.";
    public static final String TEXTO_RESUMEN_RAZON_ABANDONO = "Tu oponente se desconectó, así que ganaste la partida.";
    public static final String TEXTO_RESUMEN_RAZON_BANDERA_PERDIDA = "El oponente llegó a tu bandera.";
    public static final String TEXTO_RESUMEN_DURACION = "Duración: ";
    public static final String TEXTO_RESUMEN_BOTS = "Bots eliminados";
    public static final String TEXTO_RESUMEN_PERDIDOS = "Animales perdidos";
    public static final String TEXTO_RESUMEN_TAZAS = "Tazas consumidas";
    public static final String TEXTO_RESUMEN_TU = "Tú";
    public static final String TEXTO_BOTON_VOLVER = "Volver al inicio";
    public static final Color COLOR_RESUMEN_VICTORIA = new Color(100, 225, 130);
    public static final Color COLOR_RESUMEN_DERROTA = new Color(245, 100, 100);
    public static final Color COLOR_CAPA_VICTORIA = new Color(20, 110, 40, 120); // tiñe el fondo del resumen
    public static final Color COLOR_CAPA_DERROTA = new Color(140, 20, 20, 120);

    // Aspecto común de las pantallas de inicio, espera, selección y resumen
    public static final String RUTA_FONDO_INICIO = "imagenes/ui/fondo_inicio.svg";
    public static final String NOMBRE_FONDO_INICIO = "fondo_inicio";
    public static final int ANCHO_FONDO_INICIO = 1600; // misma proporción que el SVG (1000 x 650); se rasteriza una sola vez
    public static final int ALTO_FONDO_INICIO = 1040;
    public static final Color COLOR_FONDO_PANTALLA = new Color(25, 45, 30); // se usa si el SVG del fondo no se pudo cargar
    public static final Color COLOR_CAPA_FONDO = new Color(0, 0, 0, 90); // oscurece el fondo para que se lea el texto
    public static final Color COLOR_TARJETA = new Color(15, 30, 20, 205);
    public static final Color COLOR_BORDE_TARJETA = new Color(255, 255, 255, 70);
    public static final int ARCO_TARJETA = 28;
    public static final int RELLENO_TARJETA = 28;
    public static final int ESPACIO_PANTALLA = 10; // separación entre los elementos de una pantalla
    public static final Color COLOR_TEXTO_PANTALLA = Color.WHITE;
    public static final Color COLOR_TEXTO_SECUNDARIO = new Color(205, 220, 200);
    public static final Color COLOR_ACENTO = new Color(245, 195, 60);
    public static final Color COLOR_BOTON_PRINCIPAL = new Color(46, 125, 50);
    public static final Color COLOR_BOTON_SECUNDARIO = new Color(120, 85, 48);
    public static final Color COLOR_BOTON_CANCELAR = new Color(120, 55, 55);
    public static final Color COLOR_TEXTO_BOTON = Color.WHITE;
    public static final Color COLOR_FICHA_CODIGO = new Color(20, 40, 25); // sólido: se pinta en un componente opaco
    public static final String NOMBRE_FUENTE = Font.SANS_SERIF;
    public static final String NOMBRE_FUENTE_CODIGO = Font.MONOSPACED;
    public static final int TAMANO_FUENTE_TITULO = 44;
    public static final int TAMANO_FUENTE_SUBTITULO = 17;
    public static final int TAMANO_FUENTE_ETIQUETA = 15;
    public static final int TAMANO_FUENTE_BOTON = 16;
    public static final int TAMANO_FUENTE_CODIGO = 56;
    public static final int TAMANO_FUENTE_ENCABEZADO = 19;
    public static final int TAMANO_FUENTE_ESTADISTICA = 22;
    public static final int TAMANO_FUENTE_TITULO_RESUMEN = 52;
    public static final int RELLENO_BOTON_VERTICAL = 10;
    public static final int RELLENO_CAMPO = 5; // margen interno de los campos de texto
    public static final int RELLENO_BOTON_HORIZONTAL = 26;
    public static final int GROSOR_FICHA_CODIGO = 2;
    public static final int RELLENO_FICHA_VERTICAL = 8;
    public static final int RELLENO_FICHA_HORIZONTAL = 20;
    public static final int TAMANO_ICONO_TITULO_RESUMEN = 84;
    public static final int TAMANO_ICONO_TARJETA_RESUMEN = 96;
    public static final int ESPACIO_TARJETAS_RESUMEN = 24;
    public static final int INTERVALO_PUNTOS_ESPERA_MS = 450; // cada cuánto aparece un punto más en "Esperando..."
    public static final int PUNTOS_ESPERA_MAXIMO = 3;

    // Textos nuevos de las pantallas de inicio, espera y resumen
    public static final String TEXTO_SUBTITULO_INICIO = "Batalla en la selva costarricense";
    public static final String TEXTO_O_UNIRSE = "— o únete a una partida —";
    public static final String TEXTO_ESPERA_TITULO = "¡Partida creada!";
    public static final String TEXTO_ESPERA_AYUDA = "Comparte este código con tu oponente";
    public static final String TEXTO_BOTON_COPIAR = "Copiar código";
    public static final String TEXTO_BOTON_COPIADO = "¡Copiado!";
    public static final String TEXTO_BOTON_CANCELAR = "Cancelar";

    // Aviso breve sobre el mapa (errores de las órdenes durante la partida: no interrumpen el juego con una ventana)
    public static final int DURACION_AVISO_MS = 3000;
    public static final int MARGEN_AVISO = 28; // distancia al borde de abajo de la ventana
    public static final Color COLOR_PANEL_AVISO = new Color(150, 40, 40, 225);
    public static final Color COLOR_TEXTO_AVISO = Color.WHITE;

    // Efectos del combate
    public static final int DURACION_EFECTO_MS = 450; // cuánto se ve el efecto de un ataque sobre el animal atacado
    public static final int TAMANO_EFECTO_MAPA = 46;
    public static final Color COLOR_EFECTO_MUERTE = new Color(240, 60, 60);

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
