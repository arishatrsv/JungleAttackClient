package jungleattack.cliente.vista;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import jungleattack.cliente.constantes.ConstantesCliente;
import jungleattack.cliente.controlador.ControladorJuego;
import jungleattack.cliente.modelo.FaseCliente;
import jungleattack.cliente.modelo.ModeloJuego;
import jungleattack.cliente.red.ConexionServidor;
import org.junit.jupiter.api.Test;

class PantallaEsperaTest {

    private final ModeloJuego modelo = new ModeloJuego();
    private final ControladorJuego controlador = new ControladorJuego(modelo, new ConexionServidor());

    private PantallaEspera pantalla() {
        modelo.consultaJugadorLocal().actualizacionCodigoPartida("A7K2");
        modelo.actualizacionEstado(FaseCliente.ESPERA);
        PantallaEspera pantalla = new PantallaEspera(modelo, controlador);
        pantalla.dibujo();
        return pantalla;
    }

    private void recolectar(Container contenedor, List<Component> destino) {
        for (Component componente : contenedor.getComponents()) {
            destino.add(componente);
            if (componente instanceof Container hijo) {
                recolectar(hijo, destino);
            }
        }
    }

    private List<String> textosDeEtiquetas(PantallaEspera pantalla) {
        List<Component> componentes = new ArrayList<>();
        recolectar(pantalla, componentes);
        List<String> textos = new ArrayList<>();
        for (Component componente : componentes) {
            if (componente instanceof JLabel etiqueta) {
                textos.add(etiqueta.getText());
            }
        }
        return textos;
    }

    private JButton boton(PantallaEspera pantalla, String texto) {
        List<Component> componentes = new ArrayList<>();
        recolectar(pantalla, componentes);
        for (Component componente : componentes) {
            if (componente instanceof JButton boton && texto.equals(boton.getText())) {
                return boton;
            }
        }
        return null;
    }

    @Test
    void muestraElCodigoUnaFichaPorCaracter() {
        List<String> textos = textosDeEtiquetas(pantalla());
        for (String caracter : new String[] {"A", "7", "K", "2"}) {
            assertTrue(textos.contains(caracter), "falta la ficha " + caracter);
        }
    }

    @Test
    void muestraElTituloLaAyudaYElMensajeDeEspera() {
        List<String> textos = textosDeEtiquetas(pantalla());
        assertTrue(textos.contains(ConstantesCliente.TEXTO_ESPERA_TITULO));
        assertTrue(textos.contains(ConstantesCliente.TEXTO_ESPERA_AYUDA));
        assertTrue(textos.contains(ConstantesCliente.TEXTO_ESPERA));
    }

    @Test
    void losPuntosDeLaEsperaSeAnimanSinCambiarElMensaje() {
        PantallaEspera pantalla = pantalla();
        for (int paso = 0; paso <= ConstantesCliente.PUNTOS_ESPERA_MAXIMO; paso++) {
            pantalla.avanceDePuntos();
            boolean hayMensaje = false;
            for (String texto : textosDeEtiquetas(pantalla)) {
                hayMensaje |= texto.startsWith(ConstantesCliente.TEXTO_ESPERA_BASE);
            }
            assertTrue(hayMensaje);
        }
    }

    @Test
    void elBotonCancelarVuelveAlInicio() {
        PantallaEspera pantalla = pantalla();
        JButton cancelar = boton(pantalla, ConstantesCliente.TEXTO_BOTON_CANCELAR);
        assertNotNull(cancelar);
        cancelar.doClick();
        assertEquals(FaseCliente.INICIO, modelo.consultaFase());
    }

    @Test
    void copiarElCodigoNoFallaAunqueNoHayaPortapapeles() {
        JButton copiar = boton(pantalla(), ConstantesCliente.TEXTO_BOTON_COPIAR);
        assertNotNull(copiar);
        copiar.doClick(); // en un equipo sin pantalla no hay portapapeles: no debe lanzar nada
    }
}
