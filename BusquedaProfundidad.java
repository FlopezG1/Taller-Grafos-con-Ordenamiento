import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Recorre un grafo dirigido usando busqueda en profundidad (DFS).
 */
public final class BusquedaProfundidad {
    /**
     * Devuelve el recorrido de las ciudades alcanzables desde el origen.
     *
     * @param grafo grafo que se recorre
     * @param origen indice de la ciudad inicial
     * @return ciudades visitadas en orden DFS
     */
    public List<Integer> recorrer(Grafo grafo, int origen) {
        if (origen < 0 || origen >= grafo.getCantidadCiudades()) {
            throw new IndexOutOfBoundsException("Indice de origen fuera del grafo: " + origen);
        }
        boolean[] visitadas = new boolean[grafo.getCantidadCiudades()];
        List<Integer> recorrido = new ArrayList<>();
        recorrerDesde(grafo, origen, visitadas, recorrido);
        return Collections.unmodifiableList(recorrido);
    }

    private void recorrerDesde(
        Grafo grafo, int actual, boolean[] visitadas, List<Integer> recorrido
    ) {
        visitadas[actual] = true;
        recorrido.add(actual);
        for (int destino = 0; destino < grafo.getCantidadCiudades(); destino++) {
            if (grafo.existeArista(actual, destino) && !visitadas[destino]) {
                recorrerDesde(grafo, destino, visitadas, recorrido);
            }
        }
    }
}
