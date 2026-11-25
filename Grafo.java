import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Representa un grafo dirigido y ponderado mediante una matriz de costos.
 */
public final class Grafo {
    /** Valor centinela que representa la ausencia de una arista. */
    public static final int INFINITO = 1_000_000_000;

    /** Nombres de las ciudades, ordenados por su indice en el grafo. */
    private final String[] ciudades;

    /** Costos de las aristas; las filas y columnas se mantienen encapsuladas. */
    private final int[][] costos;

    /** Rutas registradas, una por cada par dirigido de ciudades. */
    private final List<Ruta> rutas;

    /**
     * Crea un grafo sin aristas y con los nombres de las ciudades indicados.
     *
     * @param ciudades nombres de los vertices, en el orden de sus indices
     * @throws IllegalArgumentException si no se proporcionan ciudades
     */
    public Grafo(String... ciudades) {
        if (ciudades == null || ciudades.length == 0) {
            throw new IllegalArgumentException("El grafo debe tener al menos una ciudad.");
        }

        this.ciudades = ciudades.clone();
        this.costos = new int[ciudades.length][ciudades.length];
        this.rutas = new ArrayList<>();
        for (int i = 0; i < costos.length; i++) {
            Arrays.fill(costos[i], INFINITO);
            costos[i][i] = 0;
        }
    }

    /**
     * Agrega o reemplaza una arista dirigida entre dos ciudades.
     *
     * @param origen indice de la ciudad de salida
     * @param destino indice de la ciudad de llegada
     * @param costo peso no negativo de la arista
     * @throws IndexOutOfBoundsException si alguno de los indices no pertenece al grafo
     * @throws IllegalArgumentException si el costo es negativo o no es menor que
     *         {@link #INFINITO}, o si origen y destino son la misma ciudad
     */
    public void agregarArista(int origen, int destino, int costo) {
        validarIndice(origen);
        validarIndice(destino);
        if (origen == destino) {
            throw new IllegalArgumentException("Una ruta debe unir dos ciudades distintas.");
        }
        if (costo < 0) {
            throw new IllegalArgumentException("El costo de una ruta no puede ser negativo.");
        }
        if (costo >= INFINITO) {
            throw new IllegalArgumentException("El costo de una ruta debe ser menor que INFINITO.");
        }
        costos[origen][destino] = costo;
        for (int i = 0; i < rutas.size(); i++) {
            Ruta ruta = rutas.get(i);
            if (ruta.getOrigen() == origen && ruta.getDestino() == destino) {
                rutas.set(i, new Ruta(origen, destino, costo));
                return;
            }
        }
        rutas.add(new Ruta(origen, destino, costo));
    }

    /**
     * Obtiene la cantidad de vertices del grafo.
     *
     * @return cantidad de ciudades
     */
    public int getCantidadCiudades() {
        return ciudades.length;
    }

    /**
     * Obtiene el nombre de una ciudad.
     *
     * @param indice indice de la ciudad
     * @return nombre de la ciudad indicada
     * @throws IndexOutOfBoundsException si el indice no pertenece al grafo
     */
    public String getNombreCiudad(int indice) {
        validarIndice(indice);
        return ciudades[indice];
    }

    /**
     * Obtiene el costo de una arista o {@link #INFINITO} si no existe.
     *
     * @param origen indice de la ciudad de salida
     * @param destino indice de la ciudad de llegada
     * @return costo de la arista o el valor centinela
     * @throws IndexOutOfBoundsException si alguno de los indices no pertenece al grafo
     */
    public int getCosto(int origen, int destino) {
        validarIndice(origen);
        validarIndice(destino);
        return costos[origen][destino];
    }

    /**
     * Indica si existe una arista directa entre dos ciudades distintas.
     *
     * @param origen indice de la ciudad de salida
     * @param destino indice de la ciudad de llegada
     * @return {@code true} si hay una arista dirigida entre las ciudades
     * @throws IndexOutOfBoundsException si alguno de los indices no pertenece al grafo
     */
    public boolean existeArista(int origen, int destino) {
        validarIndice(origen);
        validarIndice(destino);
        return origen != destino && getCosto(origen, destino) != INFINITO;
    }

    /**
     * Obtiene las rutas registradas en el grafo.
     *
     * @return lista inmutable de rutas dirigidas
     */
    public List<Ruta> getRutas() {
        return Collections.unmodifiableList(new ArrayList<>(rutas));
    }

    /**
     * Verifica que un indice corresponda a una ciudad del grafo.
     *
     * @param indice indice que se valida
     * @throws IndexOutOfBoundsException si el indice es invalido
     */
    private void validarIndice(int indice) {
        if (indice < 0 || indice >= ciudades.length) {
            throw new IndexOutOfBoundsException("Indice de ciudad fuera del grafo: " + indice);
        }
    }
}
