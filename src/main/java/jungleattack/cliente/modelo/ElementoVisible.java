package jungleattack.cliente.modelo;

/**
 * Cualquier objeto del mapa que el jugador puede ver y que no es un animal (tazas y banderas).
 * Guarda su posición y el número de la zona donde está.
 */
public abstract class ElementoVisible {

    private PuntoVisible posicion;
    private final int zona;

    protected ElementoVisible(PuntoVisible posicion, int zona) {
        this.posicion = posicion;
        this.zona = zona;
    }

    public void actualizacionPosicion(PuntoVisible posicion) {
        this.posicion = posicion;
    }

    public PuntoVisible consultaPosicion() {
        return posicion;
    }

    public int consultaZona() {
        return zona;
    }
}
