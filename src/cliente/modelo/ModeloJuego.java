package jungleattack.cliente.modelo;

import java.util.List;

public class ModeloJuego {

    private FaseCliente fase;

    private JugadorLocal jugadorLocal;

    private List<ZonaVisible> zonasVisibles;

    private List<AnimalVisible> animalesVisibles;

    private List<TazaVisible> tazasVisibles;

    private List<BanderaVisible> banderasVisibles;

    private ResumenVisible resumen;

    private List<ObservadorModelo> observadores;

    public void actualizacionEstado(Object estado /* TODO: definir el tipo del estado */) {
        // TODO: implementar
    }

    public void actualizacionZona(ZonaVisible zona) {
        // TODO: implementar
    }

    public void actualizacionResumen(ResumenVisible resumen) {
        // TODO: implementar
    }

    public void actualizacionMovimiento(AnimalVisible animal, PuntoVisible destino) {
        // TODO: implementar
    }

    public void actualizacionAnimal(AnimalVisible animal) {
        // TODO: implementar
    }

    public void registroObservador(ObservadorModelo observador) {
        // TODO: implementar
    }

    public void notificacionObservadores() {
        // TODO: implementar
    }
}
