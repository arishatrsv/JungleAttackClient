package jungleattack.cliente.vista;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import common.constantes.ConstantesProtocolo;
import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import jungleattack.cliente.constantes.ConstantesCliente;
import jungleattack.cliente.controlador.ControladorJuego;
import jungleattack.cliente.modelo.ModeloJuego;
import jungleattack.cliente.red.ConexionServidor;
import org.junit.jupiter.api.Test;

class PantallaInicioTest {

    private final ModeloJuego modelo = new ModeloJuego();
    private final ControladorJuego controlador = new ControladorJuego(modelo, new ConexionServidor());

    private void recolectar(Container contenedor, List<Component> destino) {
        for (Component componente : contenedor.getComponents()) {
            destino.add(componente);
            if (componente instanceof Container hijo) {
                recolectar(hijo, destino);
            }
        }
    }

    private JButton boton(PantallaInicio pantalla, String texto) {
        List<Component> componentes = new ArrayList<>();
        recolectar(pantalla, componentes);
        for (Component componente : componentes) {
            if (componente instanceof JButton boton && texto.equals(boton.getText())) {
                return boton;
            }
        }
        return null;
    }

    private PantallaInicio pantalla() {
        PantallaInicio pantalla = new PantallaInicio(controlador);
        pantalla.dibujo();
        return pantalla;
    }

    @Test
    void tieneLosBotonesDeCrearYDeUnirse() {
        PantallaInicio pantalla = pantalla();
        assertNotNull(boton(pantalla, ConstantesCliente.TEXTO_BOTON_CREAR));
        assertNotNull(boton(pantalla, ConstantesCliente.TEXTO_BOTON_UNIRSE));
    }

    @Test
    void crearSinApodoMuestraElErrorDelApodo() {
        boton(pantalla(), ConstantesCliente.TEXTO_BOTON_CREAR).doClick();
        assertEquals(ConstantesProtocolo.MOTIVO_APODO_INVALIDO, modelo.consultaError());
    }

    @Test
    void unirseSinApodoMuestraElErrorDelApodo() {
        boton(pantalla(), ConstantesCliente.TEXTO_BOTON_UNIRSE).doClick();
        assertEquals(ConstantesProtocolo.MOTIVO_APODO_INVALIDO, modelo.consultaError());
    }

    @Test
    void volverADibujarLaPantallaNoDuplicaSusComponentes() {
        PantallaInicio pantalla = pantalla();
        List<Component> antes = new ArrayList<>();
        recolectar(pantalla, antes);
        pantalla.dibujo();
        List<Component> despues = new ArrayList<>();
        recolectar(pantalla, despues);
        assertEquals(antes.size(), despues.size());
    }
}
