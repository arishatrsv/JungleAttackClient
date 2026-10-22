package jungleattack.cliente.modelo;

import common.model.Especie;

/**
 * Un efecto visual de combate que se está mostrando: el golpe de un ataque sobre el animal atacado, o la marca
 * de una muerte. Viene de un evento de STATE_UPDATE y dura poco (ConstantesCliente.DURACION_EFECTO_MS).
 */
public class EfectoCombate {

    private final boolean muerte; // false: golpe de un ataque; true: un animal murió
    private final Especie especie; // la del atacante (su efecto visual es el que se dibuja)
    private final String objetivo; // identificador del animal atacado o que murió
    private final int x; // dónde ocurrió, en coordenadas del mapa
    private final int y;
    private final long inicio; // instante (milisegundos) en que empezó a mostrarse

    public EfectoCombate(boolean muerte, Especie especie, String objetivo, int x, int y, long inicio) {
        this.muerte = muerte;
        this.especie = especie;
        this.objetivo = objetivo;
        this.x = x;
        this.y = y;
        this.inicio = inicio;
    }

    public boolean consultaMuerte() {
        return muerte;
    }

    public Especie consultaEspecie() {
        return especie;
    }

    public String consultaObjetivo() {
        return objetivo;
    }

    public int consultaX() {
        return x;
    }

    public int consultaY() {
        return y;
    }

    public long consultaInicio() {
        return inicio;
    }
}
