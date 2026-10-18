package jungleattack.cliente.modelo;

/**
 * Punto del mapa que usa el cliente. Hereda de common.model.Punto.
 * Las coordenadas están en el sistema de mapa.json (950 x 550), no en píxeles de la ventana.
 */
public class PuntoVisible extends common.model.Punto {

    public PuntoVisible() {
    }

    public PuntoVisible(int posicionX, int posicionY) {
        super(posicionX, posicionY);
    }
}
