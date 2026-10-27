package jungleattack.cliente.modelo;

import common.model.Especie;
import jungleattack.cliente.constantes.ConstantesCliente;

/**
 * Animal que el jugador puede ver: de su manada, del oponente o un bot.
 * Hereda de common.model.Animal (identificador, energía, vida, posición y especie) y agrega lo que
 * solo necesita el cliente para dibujarlo: si es propio, si es bot y su energía máxima (para la barra).
 */
public class AnimalVisible extends common.model.Animal {

    private final boolean propio;
    private final boolean bot;
    private final int energiaMaxima;

    // Posición con la que se dibuja. La posición real es la que manda el servidor (la de common.model.Animal);
    // la dibujada se va acercando a ella poco a poco, para que el movimiento se vea fluido aunque el servidor
    // avise solo unas 10 veces por segundo.
    private double visualX;
    private double visualY;

    public AnimalVisible(String identificador, Especie especie, int energia, int energiaMaxima,
            PuntoVisible posicion, boolean propio, boolean bot) {
        super(identificador, especie, energia, posicion);
        this.energiaMaxima = energiaMaxima;
        this.propio = propio;
        this.bot = bot;
        this.visualX = posicion.consultaPosicionX();
        this.visualY = posicion.consultaPosicionY();
    }

    /**
     * Guarda lo que informó el servidor: la energía y la posición real del animal.
     * La posición dibujada no salta: se acerca a ella en los siguientes cuadros (ver actualizacionVisual).
     */
    public void actualizacionDesdeServidor(int energia, common.model.Punto posicion) {
        actualizacionEnergia(energia);
        actualizacionPosicion(posicion);
    }

    /**
     * Acerca la posición dibujada a la real. Se llama en cada cuadro de la animación.
     * Mientras más tiempo pasó, más se acerca; si ya está casi encima, queda exactamente en la posición real.
     *
     * @param milisegundos tiempo desde el cuadro anterior
     */
    public void actualizacionVisual(long milisegundos) {
        double destinoX = consultaPosicion().consultaPosicionX();
        double destinoY = consultaPosicion().consultaPosicionY();
        double fraccion = 1 - Math.exp(-(double) milisegundos / ConstantesCliente.TIEMPO_SUAVIZADO_MS);
        visualX += (destinoX - visualX) * fraccion;
        visualY += (destinoY - visualY) * fraccion;
        if (Math.hypot(destinoX - visualX, destinoY - visualY) < 0.5) {
            visualX = destinoX;
            visualY = destinoY;
        }
    }

    // Deja la posición dibujada justo en la real, sin animación (por ejemplo, al aparecer un animal en la vista).
    public void reinicioPosicionVisual() {
        visualX = consultaPosicion().consultaPosicionX();
        visualY = consultaPosicion().consultaPosicionY();
    }

    public double consultaVisualX() {
        return visualX;
    }

    public double consultaVisualY() {
        return visualY;
    }

    public boolean consultaPropio() {
        return propio;
    }

    public boolean consultaBot() {
        return bot;
    }

    public int consultaEnergiaMaxima() {
        return energiaMaxima;
    }
}