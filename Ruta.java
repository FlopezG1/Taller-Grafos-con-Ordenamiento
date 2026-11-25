/**
 * Representa una ruta dirigida entre dos ciudades y su distancia en kilometros.
 */
public final class Ruta {
    private final int origen;
    private final int destino;
    private final int distancia;

    /**
     * Crea una ruta.
     *
     * @param origen indice de la ciudad de salida
     * @param destino indice de la ciudad de llegada
     * @param distancia distancia en kilometros
     */
    public Ruta(int origen, int destino, int distancia) {
        this.origen = origen;
        this.destino = destino;
        this.distancia = distancia;
    }

    public int getOrigen() {
        return origen;
    }

    public int getDestino() {
        return destino;
    }

    public int getDistancia() {
        return distancia;
    }
}
