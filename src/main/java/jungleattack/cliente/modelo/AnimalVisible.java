package jungleattack.cliente.modelo;

import common.model.Especie;

/**
 * Animal que el jugador puede ver: de su manada, del oponente o un bot.
 * Hereda de common.model.Animal (identificador, energía, vida, posición y especie) y agrega lo que
 * solo necesita el cliente para dibujarlo: si es propio, si es bot y su energía máxima (para la barra).
 */
public class AnimalVisible extends common.model.Animal {

    private final boolean propio;
    private final boolean bot;
    private final int energiaMaxima;

    public AnimalVisible(String identificador, Especie especie, int energia, int energiaMaxima,
            PuntoVisible posicion, boolean propio, boolean bot) {
        super(identificador, especie, energia, posicion);
        this.energiaMaxima = energiaMaxima;
        this.propio = propio;
        this.bot = bot;
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

    public void actualizacionEnergia() {
        // TODO: implementar
    }

    public void actualizacionEstado() {
        // TODO: implementar
    }

    public void actualizacionMovimiento(PuntoVisible destino) {
        // TODO: implementar
    }
}
