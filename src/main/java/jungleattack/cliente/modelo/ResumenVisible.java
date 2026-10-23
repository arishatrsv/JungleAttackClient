package jungleattack.cliente.modelo;

public class ResumenVisible {

    private final boolean victoria; // true si el jugador de este cliente ganó
    private final String razon; // ConstantesProtocolo.RAZON_BANDERA o RAZON_ABANDONO
    private final String ganador; // apodo del ganador
    private final int duracion; // segundos
    private final EstadisticasVisibles propio;
    private final EstadisticasVisibles oponente;

    public ResumenVisible(boolean victoria, String razon, String ganador, int duracion,
            EstadisticasVisibles propio, EstadisticasVisibles oponente) {
        this.victoria = victoria;
        this.razon = razon;
        this.ganador = ganador;
        this.duracion = duracion;
        this.propio = propio;
        this.oponente = oponente;
    }

    public boolean consultaVictoria() {
        return victoria;
    }

    public String consultaRazon() {
        return razon;
    }

    public String consultaGanador() {
        return ganador;
    }

    public int consultaDuracion() {
        return duracion;
    }

    public EstadisticasVisibles consultaPropio() {
        return propio;
    }

    public EstadisticasVisibles consultaOponente() {
        return oponente;
    }
}
