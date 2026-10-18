package jungleattack.cliente.vista;

import jungleattack.cliente.modelo.PuntoVisible;

/**
 * Convierte entre las coordenadas del mapa (las de mapa.json, 950 x 550) y los píxeles del panel.
 * El mapa se agranda o se achica para caber en el panel sin deformarse (misma escala en ancho y alto)
 * y queda centrado: si sobra espacio, se reparte en los dos lados.
 */
public class ConversorCoordenadas {

    private double escala = 1;
    private double desplazamientoX;
    private double desplazamientoY;

    // Recalcula la escala y el desplazamiento. Se llama cada vez que cambia el tamaño del panel.
    public void actualizacionVentana(int anchoPanel, int altoPanel, int anchoMapa, int altoMapa) {
        escala = Math.min((double) anchoPanel / anchoMapa, (double) altoPanel / altoMapa);
        desplazamientoX = (anchoPanel - anchoMapa * escala) / 2;
        desplazamientoY = (altoPanel - altoMapa * escala) / 2;
    }

    public PuntoVisible conversionPantallaAMapa(PuntoVisible puntoPantalla) {
        return new PuntoVisible(
                (int) Math.round((puntoPantalla.consultaPosicionX() - desplazamientoX) / escala),
                (int) Math.round((puntoPantalla.consultaPosicionY() - desplazamientoY) / escala));
    }

    public PuntoVisible conversionMapaAPantalla(PuntoVisible puntoMapa) {
        return new PuntoVisible(
                (int) Math.round(puntoMapa.consultaPosicionX() * escala + desplazamientoX),
                (int) Math.round(puntoMapa.consultaPosicionY() * escala + desplazamientoY));
    }

    // Cuántos píxeles mide una unidad del mapa. Sirve para escalar tamaños (imágenes, grosores, letras).
    public double consultaEscala() {
        return escala;
    }

    public double consultaDesplazamientoX() {
        return desplazamientoX;
    }

    public double consultaDesplazamientoY() {
        return desplazamientoY;
    }
}

