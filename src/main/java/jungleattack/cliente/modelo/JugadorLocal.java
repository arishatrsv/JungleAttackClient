package jungleattack.cliente.modelo;

import common.model.Especie;

/**
 * El jugador de este cliente: su apodo, su especie y el código de la partida en la que está.
 */
public class JugadorLocal {

    private String apodo;
    // Es null hasta que el jugador elija su especie.
    private Especie especie;
    // Es null hasta que se cree o se una a una partida.
    private String codigoPartida;

    // Borra la especie y el código de la partida (cuando se cancela la partida o se pierde la conexión).
    // El apodo se conserva para no tener que escribirlo otra vez.
    public void reinicio() {
        this.especie = null;
        this.codigoPartida = null;
    }
    
    public String consultaApodo() {
        return apodo;
    }
    public void actualizacionApodo(String apodo) {
        this.apodo = apodo;
    }
    public Especie consultaEspecie() {
        return especie;
    }
    public void actualizacionEspecie(Especie especie) {
        this.especie = especie;
    }
    public String consultaCodigoPartida() {
        return codigoPartida;
    }
    public void actualizacionCodigoPartida(String codigoPartida) {
        this.codigoPartida = codigoPartida;
    }
}
