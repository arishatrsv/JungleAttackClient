package jungleattack.cliente.vista;

import javax.swing.Timer;
import jungleattack.cliente.modelo.AnimalVisible;
import jungleattack.cliente.modelo.ModeloJuego;
import jungleattack.cliente.modelo.ObservadorModelo;
import jungleattack.cliente.modelo.PuntoVisible;

public class PantallaJuego extends PantallaBase implements ObservadorModelo {

    private ModeloJuego modelo;

    private Timer temporizadorAnimacion;

    @Override
    public void dibujo() {
        // TODO: implementar
    }

    public void actualizacionAnimacion() {
        // TODO: implementar
    }

    @Override
    public void actualizacionModelo() {
        // TODO: implementar
    }

    public void representacionMovimiento(AnimalVisible animal, PuntoVisible destino) {
        // TODO: implementar
    }
}
