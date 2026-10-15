package jungleattack.cliente.aplicacion;

import javax.swing.SwingUtilities;
import jungleattack.cliente.controlador.ControladorJuego;
import jungleattack.cliente.modelo.ModeloJuego;
import jungleattack.cliente.red.ConexionServidor;
import jungleattack.cliente.vista.VentanaPrincipal;

//Punto de entrada del cliente: arma el modelo, la conexión, el controlador y la ventana (MVC).
public class AplicacionCliente {

    public static void main(String[] args) {
        // Swing se debe crear siempre en su propio hilo (el hilo de eventos).
        SwingUtilities.invokeLater(() -> {
            ModeloJuego modelo = new ModeloJuego();
            ConexionServidor conexion = new ConexionServidor();
            ControladorJuego controlador = new ControladorJuego(modelo, conexion);
            VentanaPrincipal ventana = new VentanaPrincipal(modelo, controlador);
            ventana.visualizacion();
        });
    }
}
