import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;

/**
 * Recorre un grafo dirigido por niveles usando busqueda en anchura (BFS).
 */
public final class BusquedaAnchura {
    /**
     * Devuelve el recorrido de las ciudades alcanzables desde el origen.
     *
     * @param grafo grafo que se recorre
     * @param origen indice de la ciudad inicial
     * @return ciudades visitadas en orden BFS
     */
    public List<Integer> recorrer(Grafo grafo, int origen) {
        validarOrigen(grafo, origen);
        boolean[] visitadas = new boolean[grafo.getCantidadCiudades()];
        List<Integer> recorrido = new ArrayList<>();
        Queue<Integer> pendientes = new ArrayDeque<>();

        visitadas[origen] = true;
        pendientes.add(origen);
        while (!pendientes.isEmpty()) {
            int actual = pendientes.remove();
            recorrido.add(actual);
            for (int destino = 0; destino < grafo.getCantidadCiudades(); destino++) {
                if (grafo.existeArista(actual, destino) && !visitadas[destino]) {
                    visitadas[destino] = true;
                    pendientes.add(destino);
                }
            }
        }
        return Collections.unmodifiableList(recorrido);
    }

    private void validarOrigen(Grafo grafo, int origen) {
        if (origen < 0 || origen >= grafo.getCantidadCiudades()) {
            throw new IndexOutOfBoundsException("Indice de origen fuera del grafo: " + origen);
        }
    }
}
