package jungleattack.cliente.modelo;

/**
 * Camino del mapa: una línea recta entre los centros de dos zonas. Hereda de common.model.Camino.
 */
public class CaminoVisible extends common.model.Camino {

    public CaminoVisible(ZonaVisible inicio, ZonaVisible destino, double longitud) {
        super(inicio, destino, longitud);
    }
}
