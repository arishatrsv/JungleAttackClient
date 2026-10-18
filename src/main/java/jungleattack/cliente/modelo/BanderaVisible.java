package jungleattack.cliente.modelo;

/**
 * Bandera visible en el mapa. Puede ser la del propio jugador o la del oponente.
 */
public class BanderaVisible extends ElementoVisible {

    private final boolean propia;

    public BanderaVisible(PuntoVisible posicion, int zona, boolean propia) {
        super(posicion, zona);
        this.propia = propia;
    }

    public boolean consultaPropia() {
        return propia;
    }
}
