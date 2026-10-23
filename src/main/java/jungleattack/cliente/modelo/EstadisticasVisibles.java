package jungleattack.cliente.modelo;

import common.model.Especie;

/**
 * Lo que hizo un jugador durante la partida, tal como llega en el resumen final: bots eliminados,
 * animales de su manada que murieron y tazas consumidas.
 */
public class EstadisticasVisibles {

    private final String apodo;
    private final Especie especie;
    private final int botsEliminados;
    private final int animalesPerdidos;
    private final int tazasConsumidas;

    public EstadisticasVisibles(String apodo, Especie especie, int botsEliminados, int animalesPerdidos, int tazasConsumidas) {
        this.apodo = apodo;
        this.especie = especie;
        this.botsEliminados = botsEliminados;
        this.animalesPerdidos = animalesPerdidos;
        this.tazasConsumidas = tazasConsumidas;
    }

    public String consultaApodo() {
        return apodo;
    }

    public Especie consultaEspecie() {
        return especie;
    }

    public int consultaBotsEliminados() {
        return botsEliminados;
    }

    public int consultaAnimalesPerdidos() {
        return animalesPerdidos;
    }

    public int consultaTazasConsumidas() {
        return tazasConsumidas;
    }
}
