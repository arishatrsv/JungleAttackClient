package jungleattack.cliente.protocolo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;

public class ConversorMensajesTest {

    @Test
    void unMensajeSobreviveLaIdaYVueltaPorJson() {
        Mensaje original = ConversorMensajes.creacionMensaje("CREATE_MATCH", "apodo", "Ana");
        Mensaje recuperado = ConversorMensajes.conversionDesdeJson(ConversorMensajes.conversionAJson(original));
        assertEquals("CREATE_MATCH", recuperado.consultaTipo());
        assertEquals("Ana", ConversorMensajes.campoDelContenido(recuperado, "apodo"));
    }

    @Test
    void unCampoQueNoExisteDaNull() {
        Mensaje mensaje = ConversorMensajes.creacionMensaje("X", "a", "1");
        assertNull(ConversorMensajes.campoDelContenido(mensaje, "b"));
    }

    @Test
    void unJsonSinTipoOMalFormadoLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> ConversorMensajes.conversionDesdeJson("{\"contenido\":\"{}\"}"));
        assertThrows(IllegalArgumentException.class, () -> ConversorMensajes.conversionDesdeJson("esto no es json {"));
    }

    @Test
    void cadaCampoNecesitaSuValor() {
        assertThrows(IllegalArgumentException.class, () -> ConversorMensajes.creacionMensaje("X", "a"));
    }

    @Test
    void elContenidoSePuedeLeerComoObjetoConListasAnidadas() {
        Mensaje mensaje = new Mensaje("GAME_STARTED", "{\"zonaInicial\":7,\"zonasVisibles\":[7,8]}");
        JsonObject contenido = ConversorMensajes.contenidoComoObjeto(mensaje);
        assertEquals(7, contenido.get("zonaInicial").getAsInt());
        assertEquals(2, contenido.getAsJsonArray("zonasVisibles").size());
    }

    @Test
    void unContenidoQueNoEsUnObjetoLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> ConversorMensajes.contenidoComoObjeto(new Mensaje("X", "[1,2]")));
    }
}
