package jungleattack.cliente.modelo;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import common.constantes.ConstantesComunes;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jungleattack.cliente.constantes.ConstantesCliente;

/**
 * El mapa que dibuja el cliente: 12 zonas unidas por 15 caminos.
 * El servidor no envía el mapa: el cliente lo carga de mapa.json, que viene en los recursos de common
 * y es el mismo archivo que lee el servidor.
 */
public class MapaCliente {

    private final List<ZonaVisible> zonas;
    private final List<CaminoVisible> caminos;
    private final int ancho;
    private final int alto;

    private MapaCliente(int ancho, int alto, List<ZonaVisible> zonas, List<CaminoVisible> caminos) {
        this.ancho = ancho;
        this.alto = alto;
        this.zonas = Collections.unmodifiableList(zonas);
        this.caminos = Collections.unmodifiableList(caminos);
    }

    // Carga mapa.json de los recursos de common.
    public static MapaCliente cargaDesdeRecurso() {
        InputStream entrada = MapaCliente.class.getClassLoader().getResourceAsStream(ConstantesComunes.RECURSO_MAPA);
        if (entrada == null) {
            throw new IllegalStateException("No se encontró " + ConstantesComunes.RECURSO_MAPA + " en los recursos");
        }
        try (Reader lector = new InputStreamReader(entrada, StandardCharsets.UTF_8)) {
            return cargaDesdeLector(lector);
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo leer el mapa", e);
        }
    }

    // Arma el mapa a partir de un texto JSON con el formato de mapa.json (también sirve para las pruebas).
    public static MapaCliente cargaDesdeLector(Reader lector) {
        JsonObject raiz = JsonParser.parseReader(lector).getAsJsonObject();
        List<ZonaVisible> zonas = new ArrayList<>();
        Map<Integer, ZonaVisible> zonasPorId = new HashMap<>();
        for (JsonElement elemento : raiz.getAsJsonArray(ConstantesComunes.MAPA_ZONAS)) {
            ZonaVisible zona = lecturaZona(elemento.getAsJsonObject());
            zonas.add(zona);
            zonasPorId.put(zona.consultaIdentificador(), zona);
        }
        // Los caminos se arman después de las zonas, porque cada camino necesita sus dos zonas.
        List<CaminoVisible> caminos = new ArrayList<>();
        Map<Integer, List<common.model.Camino>> conexiones = new HashMap<>();
        for (ZonaVisible zona : zonas) {
            conexiones.put(zona.consultaIdentificador(), new ArrayList<>());
        }
        for (JsonElement elemento : raiz.getAsJsonArray(ConstantesComunes.MAPA_CAMINOS)) {
            JsonObject datos = elemento.getAsJsonObject();
            ZonaVisible desde = zonasPorId.get(datos.get(ConstantesComunes.CAMINO_DESDE).getAsInt());
            ZonaVisible hasta = zonasPorId.get(datos.get(ConstantesComunes.CAMINO_HASTA).getAsInt());
            if (desde == null || hasta == null) {
                throw new IllegalArgumentException("Un camino une una zona que no existe");
            }
            CaminoVisible camino = new CaminoVisible(desde, hasta, datos.get(ConstantesComunes.CAMINO_LONGITUD).getAsDouble());
            caminos.add(camino);
            conexiones.get(desde.consultaIdentificador()).add(camino);
            conexiones.get(hasta.consultaIdentificador()).add(camino);
        }
        for (ZonaVisible zona : zonas) {
            zona.actualizacionInformacion(zona.consultaPosicion(), conexiones.get(zona.consultaIdentificador()));
        }
        return new MapaCliente(raiz.get(ConstantesComunes.MAPA_ANCHO).getAsInt(),
                raiz.get(ConstantesComunes.MAPA_ALTO).getAsInt(), zonas, caminos);
    }

    private static ZonaVisible lecturaZona(JsonObject datos) {
        List<PuntoVisible> poligono = new ArrayList<>();
        for (JsonElement vertice : datos.getAsJsonArray(ConstantesComunes.ZONA_POLIGONO)) {
            JsonArray coordenadas = vertice.getAsJsonArray();
            poligono.add(new PuntoVisible(coordenadas.get(0).getAsInt(), coordenadas.get(1).getAsInt()));
        }
        JsonObject etiqueta = datos.getAsJsonObject(ConstantesCliente.CAMPO_ETIQUETA);
        return new ZonaVisible(
                datos.get(ConstantesComunes.ZONA_ID).getAsInt(),
                datos.get(ConstantesComunes.ZONA_NOMBRE).getAsString(),
                new PuntoVisible(datos.get(ConstantesComunes.ZONA_X).getAsInt(), datos.get(ConstantesComunes.ZONA_Y).getAsInt()),
                poligono,
                new PuntoVisible(etiqueta.get(ConstantesCliente.CAMPO_ETIQUETA_X).getAsInt(), etiqueta.get(ConstantesCliente.CAMPO_ETIQUETA_Y).getAsInt()),
                etiqueta.get(ConstantesCliente.CAMPO_ETIQUETA_ANCLAJE).getAsString());
    }

    // Devuelve la zona con ese número, o null si no existe.
    public ZonaVisible zonaPorIdentificador(int identificador) {
        for (ZonaVisible zona : zonas) {
            if (zona.consultaIdentificador() == identificador) {
                return zona;
            }
        }
        return null;
    }

    /**
     * Devuelve la zona donde está un punto del mapa. Si cae justo en un borde (o fuera de todas),
     * devuelve la zona con el centro más cercano, igual que hace el servidor.
     */
    public ZonaVisible zonaPorPosicion(common.model.Punto posicion) {
        ZonaVisible masCercana = null;
        double menorDistancia = Double.MAX_VALUE;
        for (ZonaVisible zona : zonas) {
            if (zona.contienePunto(posicion)) {
                return zona;
            }
            double distancia = Math.hypot(posicion.consultaPosicionX() - zona.consultaPosicion().consultaPosicionX(),
                    posicion.consultaPosicionY() - zona.consultaPosicion().consultaPosicionY());
            if (distancia < menorDistancia) {
                menorDistancia = distancia;
                masCercana = zona;
            }
        }
        return masCercana;
    }

    public List<ZonaVisible> consultaZonas() {
        return zonas;
    }

    public List<CaminoVisible> consultaCaminos() {
        return caminos;
    }

    public int consultaAncho() {
        return ancho;
    }

    public int consultaAlto() {
        return alto;
    }
}
