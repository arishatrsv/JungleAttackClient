package jungleattack.cliente.controlador;

import common.model.Especie;
import java.util.List;
import jungleattack.cliente.modelo.AnimalVisible;
import jungleattack.cliente.modelo.ModeloJuego;
import jungleattack.cliente.modelo.PuntoVisible;
import jungleattack.cliente.modelo.ZonaVisible;
import jungleattack.cliente.protocolo.Mensaje;
import jungleattack.cliente.red.ConexionServidor;

public class ControladorJuego {

    private ModeloJuego modelo;

    private ConexionServidor conexion;

    public void creacionPartida() {
        // TODO: implementar
    }

    public void unionPartida(String codigoPartida) {
        // TODO: implementar
    }

    public void seleccionEspecie(Especie especie) {
        // TODO: implementar
    }

    public void seleccionAnimales(List<AnimalVisible> animales) {
        // TODO: implementar
    }

    public void ordenMovimiento(List<AnimalVisible> animales, PuntoVisible destino) {
        // TODO: implementar
    }

    public void ordenAtaque(List<AnimalVisible> animales, AnimalVisible objetivo) {
        // TODO: implementar
    }

    public void consultaZona(ZonaVisible zona) {
        // TODO: implementar
    }

    public void procesamientoActualizacion(Mensaje mensaje) {
        // TODO: implementar
    }
}
