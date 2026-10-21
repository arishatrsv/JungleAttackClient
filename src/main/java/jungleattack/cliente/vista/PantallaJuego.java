package jungleattack.cliente.vista;

import common.model.Especie;
import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import javax.swing.Timer;
import jungleattack.cliente.constantes.ConstantesCliente;
import jungleattack.cliente.controlador.ControladorJuego;
import jungleattack.cliente.controlador.ControladorRaton;
import jungleattack.cliente.modelo.AnimalVisible;
import jungleattack.cliente.modelo.BanderaVisible;
import jungleattack.cliente.modelo.CaminoVisible;
import jungleattack.cliente.modelo.MapaCliente;
import jungleattack.cliente.modelo.ModeloJuego;
import jungleattack.cliente.modelo.ObservadorModelo;
import jungleattack.cliente.modelo.PuntoVisible;
import jungleattack.cliente.modelo.TazaVisible;
import jungleattack.cliente.modelo.ZonaVisible;
import jungleattack.cliente.recursos.CacheImagenes;

/**
 * Pantalla de la partida. Dibuja el mapa con sus caminos, nodos y nombres de zona, la niebla sobre las zonas
 * que el jugador no ve y, en la zona que está mirando, sus animales, las tazas y las banderas.
 *
 * Solo dibuja lo que hay en el modelo: no tiene lógica de juego. Los cambios del modelo los avisa la
 * VentanaPrincipal llamando a dibujo(). Un Timer repinta unas 30 veces por segundo (queda listo para
 * la animación de los movimientos).
 */
public class PantallaJuego extends PantallaBase implements ObservadorModelo {

    private final ModeloJuego modelo;
    private final ConversorCoordenadas conversor = new ConversorCoordenadas();
    private Timer temporizadorAnimacion;
    // Instante (milisegundos) del cuadro anterior de la animación, para saber cuánto tiempo pasó.
    private long ultimoCuadro;
    // Rectángulo que el jugador está arrastrando para seleccionar animales (en píxeles); null si no hay.
    private Rectangle rectanguloSeleccion;

    /**
     * @param controlador a quien se le avisan la selección y las órdenes del ratón (puede ser null en pruebas:
     *                    entonces el ratón no envía nada)
     */
    public PantallaJuego(ModeloJuego modelo, ControladorJuego controlador) {
        this.modelo = modelo;
        setOpaque(true);
        setBackground(ConstantesCliente.COLOR_FONDO_JUEGO);
        ControladorRaton raton = new ControladorRaton(modelo, this, controlador);
        addMouseListener(raton);
        addMouseMotionListener(raton);
    }

    // Lo usa el ratón para convertir los píxeles de la ventana en puntos del mapa.
    public ConversorCoordenadas consultaConversor() {
        return conversor;
    }

    // Se llama mientras el jugador arrastra para seleccionar. Con null se borra el rectángulo.
    public void actualizacionRectanguloSeleccion(Rectangle rectangulo) {
        this.rectanguloSeleccion = rectangulo;
        repaint();
    }

    @Override
    public void dibujo() {
        repaint();
    }

    @Override
    public void actualizacionModelo() {
        dibujo();
    }

    // El temporizador solo corre mientras la pantalla está en la ventana.
    @Override
    public void addNotify() {
        super.addNotify();
        ultimoCuadro = System.currentTimeMillis();
        temporizadorAnimacion = new Timer(ConstantesCliente.INTERVALO_ANIMACION, evento -> actualizacionAnimacion());
        temporizadorAnimacion.start();
    }

    @Override
    public void removeNotify() {
        if (temporizadorAnimacion != null) {
            temporizadorAnimacion.stop();
        }
        super.removeNotify();
    }

    /**
     * Un cuadro de la animación: acerca cada animal dibujado a su posición real y repinta.
     * Si pasó mucho tiempo desde el cuadro anterior (por ejemplo, la ventana estuvo detenida) se cuenta solo
     * un máximo, para que los animales no den un salto brusco.
     */
    public void actualizacionAnimacion() {
        long ahora = System.currentTimeMillis();
        long transcurrido = Math.max(0, Math.min(ahora - ultimoCuadro, 200));
        ultimoCuadro = ahora;
        for (AnimalVisible animal : modelo.consultaAnimalesVisibles()) {
            animal.actualizacionVisual(transcurrido);
        }
        repaint();
    }

    public void representacionMovimiento(AnimalVisible animal, PuntoVisible destino) {
        // TODO: implementar (movimiento de los animales)
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        MapaCliente mapa = modelo.consultaMapa();
        if (mapa == null) {
            return; // la partida todavía no empezó
        }
        conversor.actualizacionVentana(getWidth(), getHeight(), mapa.consultaAncho(), mapa.consultaAlto());

        // Todo el mapa se dibuja en las unidades de mapa.json: el Graphics2D lo escala y lo centra en el panel.
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            ajusteCalidad(g2);
            g2.translate(conversor.consultaDesplazamientoX(), conversor.consultaDesplazamientoY());
            g2.scale(conversor.consultaEscala(), conversor.consultaEscala());
            dibujoFondo(g2, mapa);
            dibujoCaminos(g2, mapa);
            dibujoNodos(g2, mapa);
            dibujoNiebla(g2, mapa);
            dibujoNombres(g2, mapa);
            dibujoContenidoDeLaZonaObservada(g2, mapa);
        } finally {
            g2.dispose();
        }

        // El rectángulo de selección y el panel de información van en píxeles de la ventana, sin escalar.
        Graphics2D hud = (Graphics2D) g.create();
        try {
            ajusteCalidad(hud);
            dibujoRectanguloSeleccion(hud);
            dibujoPanelInformacion(hud, mapa);
        } finally {
            hud.dispose();
        }
    }

    private void ajusteCalidad(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
    }

    private void dibujoFondo(Graphics2D g2, MapaCliente mapa) {
        int factor = ConstantesCliente.FACTOR_CALIDAD_IMAGEN;
        Image fondo = CacheImagenes.imagenDeRecurso(ConstantesCliente.NOMBRE_FONDO_MAPA, ConstantesCliente.RUTA_FONDO_MAPA,
                mapa.consultaAncho() * factor, mapa.consultaAlto() * factor);
        if (fondo != null) {
            g2.drawImage(fondo, 0, 0, mapa.consultaAncho(), mapa.consultaAlto(), null);
        }
    }

    // Cada camino es una línea recta entre los centros de sus dos zonas.
    private void dibujoCaminos(Graphics2D g2, MapaCliente mapa) {
        for (CaminoVisible camino : mapa.consultaCaminos()) {
            common.model.Zona[] extremos = camino.consultaExtremos();
            Line2D.Double linea = new Line2D.Double(
                    extremos[0].consultaPosicion().consultaPosicionX(), extremos[0].consultaPosicion().consultaPosicionY(),
                    extremos[1].consultaPosicion().consultaPosicionX(), extremos[1].consultaPosicion().consultaPosicionY());
            g2.setStroke(new BasicStroke(ConstantesCliente.GROSOR_CAMINO + 2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(ConstantesCliente.COLOR_BORDE_CAMINO);
            g2.draw(linea);
            g2.setStroke(new BasicStroke(ConstantesCliente.GROSOR_CAMINO, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(ConstantesCliente.COLOR_CAMINO);
            g2.draw(linea);
        }
    }

    private void dibujoNodos(Graphics2D g2, MapaCliente mapa) {
        int radio = ConstantesCliente.RADIO_NODO;
        for (ZonaVisible zona : mapa.consultaZonas()) {
            int x = zona.consultaPosicion().consultaPosicionX();
            int y = zona.consultaPosicion().consultaPosicionY();
            g2.setColor(ConstantesCliente.COLOR_NODO);
            g2.fillOval(x - radio, y - radio, radio * 2, radio * 2);
            g2.setStroke(new BasicStroke(2f));
            g2.setColor(ConstantesCliente.COLOR_NODO_BORDE);
            g2.drawOval(x - radio, y - radio, radio * 2, radio * 2);
        }
    }

    /**
     * La niebla cubre las zonas que el jugador no ve (oscura) y, más suave, las que ve pero no está mirando
     * ahora. La zona que está mirando queda sin niebla.
     */
    private void dibujoNiebla(Graphics2D g2, MapaCliente mapa) {
        int observada = modelo.consultaZonaObservada();
        for (ZonaVisible zona : mapa.consultaZonas()) {
            if (!esVisible(zona)) {
                g2.setColor(ConstantesCliente.COLOR_NIEBLA);
            } else if (zona.consultaIdentificador() != observada) {
                g2.setColor(ConstantesCliente.COLOR_NIEBLA_SUAVE);
            } else {
                continue;
            }
            g2.fill(poligonoDe(zona));
        }
    }

    private boolean esVisible(ZonaVisible zona) {
        for (ZonaVisible visible : modelo.consultaZonasVisibles()) {
            if (visible.consultaIdentificador() == zona.consultaIdentificador()) {
                return true;
            }
        }
        return false;
    }

    private Polygon poligonoDe(ZonaVisible zona) {
        Polygon poligono = new Polygon();
        for (PuntoVisible vertice : zona.consultaPoligono()) {
            poligono.addPoint(vertice.consultaPosicionX(), vertice.consultaPosicionY());
        }
        return poligono;
    }

    // El nombre se escribe sobre la niebla para que el jugador se oriente aunque no vea la zona.
    private void dibujoNombres(Graphics2D g2, MapaCliente mapa) {
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, (float) ConstantesCliente.TAMANO_FUENTE_ZONA));
        FontMetrics medidas = g2.getFontMetrics();
        for (ZonaVisible zona : mapa.consultaZonas()) {
            String nombre = zona.consultaNombre();
            int ancho = medidas.stringWidth(nombre);
            int x = zona.consultaPosicionEtiqueta().consultaPosicionX();
            int y = zona.consultaPosicionEtiqueta().consultaPosicionY();
            switch (zona.consultaAnclajeEtiqueta()) {
                case ZonaVisible.ANCLAJE_FIN -> x -= ancho;
                case ZonaVisible.ANCLAJE_CENTRO -> x -= ancho / 2;
                default -> { } // ANCLAJE_INICIO: el texto empieza en el punto
            }
            g2.setColor(ConstantesCliente.COLOR_SOMBRA_TEXTO);
            g2.drawString(nombre, x + 1, y + 1);
            g2.setColor(ConstantesCliente.COLOR_TEXTO_ZONA);
            g2.drawString(nombre, x, y);
        }
    }

    // Solo se dibuja lo que está en la zona que el jugador mira: banderas, tazas y animales.
    private void dibujoContenidoDeLaZonaObservada(Graphics2D g2, MapaCliente mapa) {
        int observada = modelo.consultaZonaObservada();
        for (BanderaVisible bandera : modelo.consultaBanderasVisibles()) {
            if (bandera.consultaZona() == observada) {
                dibujoBandera(g2, bandera);
            }
        }
        for (TazaVisible taza : modelo.consultaTazasVisibles()) {
            if (taza.consultaZona() == observada && !taza.consultaConsumida()) {
                dibujoTaza(g2, taza);
            }
        }
        for (AnimalVisible animal : modelo.consultaAnimalesEnZonaObservada()) {
            dibujoAnimal(g2, animal);
        }
    }

    // La base del asta queda en la posición de la bandera.
    private void dibujoBandera(Graphics2D g2, BanderaVisible bandera) {
        int factor = ConstantesCliente.FACTOR_CALIDAD_IMAGEN;
        boolean propia = bandera.consultaPropia();
        Image imagen = CacheImagenes.imagenDeRecurso(
                propia ? ConstantesCliente.NOMBRE_BANDERA_PROPIA : ConstantesCliente.NOMBRE_BANDERA_OPONENTE,
                propia ? ConstantesCliente.RUTA_BANDERA_PROPIA : ConstantesCliente.RUTA_BANDERA_OPONENTE,
                ConstantesCliente.ANCHO_BANDERA_MAPA * factor, ConstantesCliente.ALTO_BANDERA_MAPA * factor);
        int x = bandera.consultaPosicion().consultaPosicionX() - ConstantesCliente.ANCHO_BANDERA_MAPA / 2;
        int y = bandera.consultaPosicion().consultaPosicionY() - ConstantesCliente.ALTO_BANDERA_MAPA;
        if (imagen != null) {
            g2.drawImage(imagen, x, y, ConstantesCliente.ANCHO_BANDERA_MAPA, ConstantesCliente.ALTO_BANDERA_MAPA, null);
        }
    }

    private void dibujoTaza(Graphics2D g2, TazaVisible taza) {
        int tamano = ConstantesCliente.TAMANO_TAZA_MAPA;
        Image imagen = CacheImagenes.imagenDeRecurso(ConstantesCliente.NOMBRE_TAZA, ConstantesCliente.RUTA_TAZA,
                tamano * ConstantesCliente.FACTOR_CALIDAD_IMAGEN, tamano * ConstantesCliente.FACTOR_CALIDAD_IMAGEN);
        if (imagen != null) {
            g2.drawImage(imagen, taza.consultaPosicion().consultaPosicionX() - tamano / 2,
                    taza.consultaPosicion().consultaPosicionY() - tamano / 2, tamano, tamano, null);
        }
    }

    // Un animal se dibuja con un anillo (verde propio, rojo del oponente, amarillo bot) y su barra de energía.
    private void dibujoAnimal(Graphics2D g2, AnimalVisible animal) {
        int tamano = ConstantesCliente.TAMANO_ANIMAL_MAPA;
        // Se dibuja en la posición suavizada, no en la última que llegó del servidor.
        int x = (int) Math.round(animal.consultaVisualX()) - tamano / 2;
        int y = (int) Math.round(animal.consultaVisualY()) - tamano / 2;

        g2.setColor(colorAnillo(animal));
        g2.setStroke(new BasicStroke(ConstantesCliente.GROSOR_ANILLO));
        g2.drawOval(x, y, tamano, tamano);
        if (modelo.consultaSeleccionado(animal)) {
            // Un animal seleccionado se resalta con un anillo más grande y de otro color.
            g2.setColor(ConstantesCliente.COLOR_ANILLO_SELECCION);
            g2.setStroke(new BasicStroke(ConstantesCliente.GROSOR_ANILLO_SELECCION));
            g2.drawOval(x - 3, y - 3, tamano + 6, tamano + 6);
        }

        Especie especie = animal.consultaEspecie();
        Image imagen = especie == null ? null
                : CacheImagenes.imagenDeEspecie(especie, tamano * ConstantesCliente.FACTOR_CALIDAD_IMAGEN);
        if (imagen != null) {
            g2.drawImage(imagen, x, y, tamano, tamano, null);
        } else {
            g2.fillOval(x, y, tamano, tamano); // sin imagen: un círculo del color del anillo
        }

        // Barra de energía sobre el animal, centrada.
        int anchoBarra = ConstantesCliente.ANCHO_BARRA_ENERGIA;
        int altoBarra = ConstantesCliente.ALTO_BARRA_ENERGIA;
        int xBarra = x + (tamano - anchoBarra) / 2;
        int yBarra = y - altoBarra - 1;
        double proporcion = animal.consultaEnergiaMaxima() <= 0 ? 0
                : Math.min(1.0, (double) animal.consultaEnergia() / animal.consultaEnergiaMaxima());
        g2.setColor(ConstantesCliente.COLOR_BARRA_FONDO);
        g2.fillRect(xBarra, yBarra, anchoBarra, altoBarra);
        g2.setColor(ConstantesCliente.COLOR_BARRA_ENERGIA);
        g2.fillRect(xBarra, yBarra, (int) Math.round(anchoBarra * proporcion), altoBarra);
    }

    private java.awt.Color colorAnillo(AnimalVisible animal) {
        if (animal.consultaBot()) {
            return ConstantesCliente.COLOR_ANILLO_BOT;
        }
        return animal.consultaPropio() ? ConstantesCliente.COLOR_ANILLO_PROPIO : ConstantesCliente.COLOR_ANILLO_OPONENTE;
    }

    private void dibujoRectanguloSeleccion(Graphics2D g2) {
        if (rectanguloSeleccion == null) {
            return;
        }
        g2.setColor(ConstantesCliente.COLOR_RECTANGULO_SELECCION);
        g2.fill(rectanguloSeleccion);
        g2.setColor(ConstantesCliente.COLOR_BORDE_RECTANGULO_SELECCION);
        g2.setStroke(new BasicStroke(1.5f));
        g2.draw(rectanguloSeleccion);
    }

    // Franja de información arriba a la izquierda: zona que se mira, daño de la manada, tazas consumidas, animales propios y seleccionados.
    private void dibujoPanelInformacion(Graphics2D g2, MapaCliente mapa) {
        ZonaVisible zona = mapa.zonaPorIdentificador(modelo.consultaZonaObservada());
        int propios = 0;
        for (AnimalVisible animal : modelo.consultaAnimalesVisibles()) {
            if (animal.consultaPropio() && animal.consultaVida()) {
                propios++;
            }
        }
        String texto = ConstantesCliente.TEXTO_JUEGO_ZONA + (zona == null ? "" : zona.consultaNombre())
                + ConstantesCliente.TEXTO_JUEGO_SEPARADOR + ConstantesCliente.TEXTO_JUEGO_DANIO + modelo.consultaDanio()
                + ConstantesCliente.TEXTO_JUEGO_SEPARADOR + ConstantesCliente.TEXTO_JUEGO_TAZAS + modelo.consultaTazasConsumidas()
                + ConstantesCliente.TEXTO_JUEGO_SEPARADOR + ConstantesCliente.TEXTO_JUEGO_ANIMALES + propios
                + ConstantesCliente.TEXTO_JUEGO_SEPARADOR + ConstantesCliente.TEXTO_JUEGO_SELECCIONADOS
                + modelo.consultaAnimalesSeleccionados().size();

        g2.setFont(g2.getFont().deriveFont(Font.BOLD, (float) ConstantesCliente.TAMANO_FUENTE_HUD));
        FontMetrics medidas = g2.getFontMetrics();
        int relleno = ConstantesCliente.RELLENO_HUD;
        int ancho = medidas.stringWidth(texto) + relleno * 2;
        int alto = medidas.getHeight() + relleno;
        g2.setColor(ConstantesCliente.COLOR_PANEL_HUD);
        g2.fillRoundRect(ConstantesCliente.MARGEN_HUD, ConstantesCliente.MARGEN_HUD, ancho, alto,
                ConstantesCliente.ARCO_HUD, ConstantesCliente.ARCO_HUD);
        g2.setColor(ConstantesCliente.COLOR_TEXTO_HUD);
        g2.drawString(texto, ConstantesCliente.MARGEN_HUD + relleno,
                ConstantesCliente.MARGEN_HUD + relleno / 2 + medidas.getAscent());
    }
}