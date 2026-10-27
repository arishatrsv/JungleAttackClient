package jungleattack.cliente.modelo;

/**
 * Taza de café visible en el mapa. Los datos vienen del servidor: el cliente solo los dibuja.
 */
public class TazaVisible extends ElementoVisible {

    private final String identificador;
    private boolean consumida;

    public TazaVisible(String identificador, PuntoVisible posicion, int zona) {
        super(posicion, zona);
        this.identificador = identificador;
    }

    public String consultaIdentificador() {
        return identificador;
    }

    public boolean consultaConsumida() {
        return consumida;
    }
}
