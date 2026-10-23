package jungleattack.cliente.vista;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import common.constantes.ConstantesProtocolo;
import common.model.Especie;
import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import jungleattack.cliente.constantes.ConstantesCliente;
import jungleattack.cliente.controlador.ControladorJuego;
import jungleattack.cliente.modelo.EstadisticasVisibles;
import jungleattack.cliente.modelo.FaseCliente;
import jungleattack.cliente.modelo.ModeloJuego;
import jungleattack.cliente.modelo.ResumenVisible;
import jungleattack.cliente.red.ConexionServidor;
import org.junit.jupiter.api.Test;

class PantallaResumenTest {

    private final ModeloJuego modelo = new ModeloJuego();
    private final ControladorJuego controlador = new ControladorJuego(modelo, new ConexionServidor());

    private PantallaResumen pantallaConResumen(boolean victoria, String razon) {
        modelo.actualizacionResumen(new ResumenVisible(victoria, razon, victoria ? "Ana" : "Luis", 125,
                new EstadisticasVisibles("Ana", Especie.JAGUAR, 3, 2, 4), new EstadisticasVisibles("Luis", Especie.TUCAN, 1, 5, 0)));
        PantallaResumen pantalla = new PantallaResumen(modelo, controlador);
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

    private List<String> textos(PantallaResumen pantalla) {
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

    private JButton boton(PantallaResumen pantalla) {
        List<Component> componentes = new ArrayList<>();
        recolectar(pantalla, componentes);
        for (Component componente : componentes) {
            if (componente instanceof JButton boton) {
                return boton;
            }
        }
        return null;
    }

    @Test
    void unaVictoriaPorBanderaMuestraLosDatosDeLaPartida() {
        List<String> textos = textos(pantallaConResumen(true, ConstantesProtocolo.RAZON_BANDERA));
        assertTrue(textos.contains(ConstantesCliente.TEXTO_RESUMEN_VICTORIA));
        assertTrue(textos.contains(ConstantesCliente.TEXTO_RESUMEN_RAZON_BANDERA));
        assertTrue(textos.contains(ConstantesCliente.TEXTO_RESUMEN_DURACION + "2:05"));
        assertTrue(textos.contains(ConstantesCliente.TEXTO_RESUMEN_BOTS));
        assertTrue(textos.contains(ConstantesCliente.TEXTO_RESUMEN_PERDIDOS));
        assertTrue(textos.contains(ConstantesCliente.TEXTO_RESUMEN_TAZAS));
    }

    @Test
    void laTablaMuestraLosNumerosDeLosDosJugadores() {
        List<String> textos = textos(pantallaConResumen(true, ConstantesProtocolo.RAZON_BANDERA));
        assertTrue(textos.contains("3")); // bots de Ana
        assertTrue(textos.contains("2")); // animales perdidos de Ana
        assertTrue(textos.contains("4")); // tazas de Ana
        assertTrue(textos.contains("1")); // bots de Luis
        assertTrue(textos.contains("5")); // animales perdidos de Luis
        assertTrue(textos.contains("0")); // tazas de Luis
        assertTrue(textos.contains("Luis"));
        assertTrue(textos.contains(ConstantesCliente.TEXTO_RESUMEN_TU + " (Ana)"));
    }

    @Test
    void unaDerrotaMuestraElTextoDeDerrotaYLaRazonDesdeSuPerspectiva() {
        List<String> textos = textos(pantallaConResumen(false, ConstantesProtocolo.RAZON_BANDERA));
        assertTrue(textos.contains(ConstantesCliente.TEXTO_RESUMEN_DERROTA));
        assertTrue(textos.contains(ConstantesCliente.TEXTO_RESUMEN_RAZON_BANDERA_PERDIDA));
        assertFalse(textos.contains(ConstantesCliente.TEXTO_RESUMEN_VICTORIA));
    }

    @Test
    void ganarPorAbandonoLoDiceAsi() {
        List<String> textos = textos(pantallaConResumen(true, ConstantesProtocolo.RAZON_ABANDONO));
        assertTrue(textos.contains(ConstantesCliente.TEXTO_RESUMEN_RAZON_ABANDONO));
    }

    @Test
    void elBotonVolverAlInicioReiniciaLaPartida() {
        PantallaResumen pantalla = pantallaConResumen(true, ConstantesProtocolo.RAZON_BANDERA);
        JButton boton = boton(pantalla);
        assertEquals(ConstantesCliente.TEXTO_BOTON_VOLVER, boton.getText());
        boton.doClick();
        assertEquals(FaseCliente.INICIO, modelo.consultaFase());
        assertEquals(null, modelo.consultaResumen());
    }

    @Test
    void sinResumenLaPantallaQuedaVaciaYNoFalla() {
        PantallaResumen pantalla = new PantallaResumen(modelo, controlador);
        pantalla.dibujo();
        assertEquals(0, pantalla.getComponentCount());
    }

    @Test
    void laDuracionSeMuestraEnMinutosYSegundos() {
        assertEquals("0:00", PantallaResumen.formatoDuracion(0));
        assertEquals("0:09", PantallaResumen.formatoDuracion(9));
        assertEquals("2:05", PantallaResumen.formatoDuracion(125));
        assertEquals("61:01", PantallaResumen.formatoDuracion(3661));
    }
}
