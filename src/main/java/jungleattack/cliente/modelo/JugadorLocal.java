package jungleattack.cliente.modelo;

import common.model.Especie;


//El jugador de este cliente: su apodo, su especie y el código de la partida
public class JugadorLocal {
    private String apodo;
    // Es null hasta que el jugador elija su especie.
    private Especie especie;
    // Es null hasta que se cree o se una a una partida.
    private String codigoPartida;
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
