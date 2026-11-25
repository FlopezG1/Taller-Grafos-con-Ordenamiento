import java.util.ArrayList;
import java.util.List;

/**
 * Ordena rutas por distancia mediante el algoritmo merge sort.
 */
public final class OrdenadorRutas {
    /**
     * Ordena las rutas de forma ascendente o descendente sin modificar la lista original.
     *
     * @param rutas rutas que se desean ordenar
     * @param ascendente true para menor a mayor; false para mayor a menor
     * @return nuevas rutas ordenadas
     */
    public List<Ruta> ordenar(List<Ruta> rutas, boolean ascendente) {
        List<Ruta> resultado = new ArrayList<>(rutas);
        if (resultado.size() > 1) {
            ordenar(resultado, new Ruta[resultado.size()], 0, resultado.size() - 1, ascendente);
        }
        return resultado;
    }

    private void ordenar(
        List<Ruta> rutas, Ruta[] auxiliar, int inicio, int fin, boolean ascendente
    ) {
        if (inicio >= fin) {
            return;
        }

        int medio = inicio + (fin - inicio) / 2;
        ordenar(rutas, auxiliar, inicio, medio, ascendente);
        ordenar(rutas, auxiliar, medio + 1, fin, ascendente);
        mezclar(rutas, auxiliar, inicio, medio, fin, ascendente);
    }

    private void mezclar(
        List<Ruta> rutas,
        Ruta[] auxiliar,
        int inicio,
        int medio,
        int fin,
        boolean ascendente
    ) {
        int izquierda = inicio;
        int derecha = medio + 1;
        int posicion = inicio;

        while (izquierda <= medio && derecha <= fin) {
            int comparacion = Integer.compare(
                rutas.get(izquierda).getDistancia(),
                rutas.get(derecha).getDistancia()
            );
            boolean tomarIzquierda = ascendente ? comparacion <= 0 : comparacion >= 0;
            auxiliar[posicion++] = tomarIzquierda
                ? rutas.get(izquierda++)
                : rutas.get(derecha++);
        }
        while (izquierda <= medio) {
            auxiliar[posicion++] = rutas.get(izquierda++);
        }
        while (derecha <= fin) {
            auxiliar[posicion++] = rutas.get(derecha++);
        }
        for (int i = inicio; i <= fin; i++) {
            rutas.set(i, auxiliar[i]);
        }
    }
}
