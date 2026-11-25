import java.util.List;
import java.util.Scanner;

/**
 * Menu de consola para administrar y clasificar rutas de transporte.
 */
public class Main {
    private Main() {
    }

    public static void main(String[] args) {
        Grafo grafo = crearGrafo();
        Scanner entrada = new Scanner(System.in);
        Dijkstra dijkstra = new Dijkstra();
        FloydWarshall floydWarshall = new FloydWarshall();
        OrdenadorRutas ordenador = new OrdenadorRutas();
        BusquedaAnchura busquedaAnchura = new BusquedaAnchura();
        BusquedaProfundidad busquedaProfundidad = new BusquedaProfundidad();
        boolean continuar = true;

        while (continuar) {
            mostrarMenu();
            int opcion = leerEntero(entrada, "Seleccione una opcion: ");
            if (opcion == Integer.MIN_VALUE) {
                break;
            }

            switch (opcion) {
                case 1:
                    imprimirRutas(grafo, grafo.getRutas());
                    break;
                case 2:
                    ordenarRutas(grafo, ordenador, true);
                    break;
                case 3:
                    ordenarRutas(grafo, ordenador, false);
                    break;
                case 4:
                    consultarDesdeOrigen(entrada, grafo, dijkstra);
                    break;
                case 5:
                    imprimirMatrizFloyd(grafo, floydWarshall.calcular(grafo));
                    break;
                case 6:
                    consultarRuta(entrada, grafo, dijkstra, floydWarshall);
                    break;
                case 7:
                    ejecutarBfs(entrada, grafo, busquedaAnchura);
                    break;
                case 8:
                    ejecutarDfs(entrada, grafo, busquedaProfundidad);
                    break;
                case 0:
                    continuar = false;
                    System.out.println("Programa finalizado.");
                    break;
                default:
                    System.out.println("Opcion no valida. Intente de nuevo.");
            }
        }
        entrada.close();
    }

    private static void mostrarMenu() {
        System.out.println("\n=== Gestion de rutas de transporte ===");
        System.out.println("1. Mostrar todas las rutas");
        System.out.println("2. Ordenar rutas de menor a mayor distancia");
        System.out.println("3. Ordenar rutas de mayor a menor distancia");
        System.out.println("4. Calcular rutas minimas desde una ciudad (Dijkstra)");
        System.out.println("5. Mostrar distancias minimas entre todas las ciudades (Floyd-Warshall)");
        System.out.println("6. Consultar ruta entre dos ciudades");
        System.out.println("7. Recorrer ciudades por niveles (BFS / busqueda en anchura)");
        System.out.println("8. Recorrer ciudades en profundidad (DFS / busqueda en profundidad)");
        System.out.println("0. Salir");
    }

    private static Grafo crearGrafo() {
        Grafo grafo = new Grafo(
            "Bogota", "Medellin", "Cali", "Barranquilla", "Bucaramanga", "Cartagena"
        );
        grafo.agregarArista(0, 1, 415);
        grafo.agregarArista(0, 2, 464);
        grafo.agregarArista(0, 3, 1000);
        grafo.agregarArista(0, 4, 397);
        grafo.agregarArista(0, 5, 1050);
        grafo.agregarArista(1, 0, 415);
        grafo.agregarArista(1, 2, 419);
        grafo.agregarArista(1, 3, 640);
        grafo.agregarArista(1, 4, 390);
        grafo.agregarArista(2, 0, 464);
        grafo.agregarArista(2, 1, 419);
        grafo.agregarArista(2, 5, 1040);
        grafo.agregarArista(3, 0, 1000);
        grafo.agregarArista(3, 1, 640);
        grafo.agregarArista(3, 4, 710);
        grafo.agregarArista(3, 5, 120);
        grafo.agregarArista(4, 0, 397);
        grafo.agregarArista(4, 1, 390);
        grafo.agregarArista(4, 3, 710);
        grafo.agregarArista(5, 0, 1050);
        grafo.agregarArista(5, 2, 1040);
        grafo.agregarArista(5, 3, 120);
        return grafo;
    }

    private static void ordenarRutas(
        Grafo grafo, OrdenadorRutas ordenador, boolean ascendente
    ) {
        String sentido = ascendente ? "menor a mayor" : "mayor a menor";
        System.out.println("\nRutas ordenadas de " + sentido + " distancia:");
        imprimirRutas(grafo, ordenador.ordenar(grafo.getRutas(), ascendente));
    }

    private static void imprimirRutas(Grafo grafo, List<Ruta> rutas) {
        if (rutas.isEmpty()) {
            System.out.println("No hay rutas registradas.");
            return;
        }
        System.out.printf("%-18s %-18s %s%n", "Origen", "Destino", "Distancia (km)");
        for (Ruta ruta : rutas) {
            System.out.printf(
                "%-18s %-18s %d%n",
                grafo.getNombreCiudad(ruta.getOrigen()),
                grafo.getNombreCiudad(ruta.getDestino()),
                ruta.getDistancia()
            );
        }
    }

    private static void consultarDesdeOrigen(
        Scanner entrada, Grafo grafo, Dijkstra dijkstra
    ) {
        int origen = seleccionarCiudad(entrada, grafo, "origen");
        if (origen < 0) {
            return;
        }
        ResultadoDijkstra resultado = dijkstra.calcular(grafo, origen);
        System.out.println("\nRutas minimas desde " + grafo.getNombreCiudad(origen) + ":");
        for (int destino = 0; destino < grafo.getCantidadCiudades(); destino++) {
            System.out.printf("%-18s ", grafo.getNombreCiudad(destino));
            imprimirRutaYDistancia(
                grafo, resultado.getRutaHasta(destino), resultado.getDistanciaHasta(destino)
            );
        }
    }

    private static void imprimirMatrizFloyd(
        Grafo grafo, ResultadoFloydWarshall resultado
    ) {
        System.out.println("\nDistancias minimas entre todas las ciudades (km):");
        System.out.printf("%-18s", "");
        for (int ciudad = 0; ciudad < grafo.getCantidadCiudades(); ciudad++) {
            System.out.printf("%-16s", grafo.getNombreCiudad(ciudad));
        }
        System.out.println();

        for (int origen = 0; origen < grafo.getCantidadCiudades(); origen++) {
            System.out.printf("%-18s", grafo.getNombreCiudad(origen));
            for (int destino = 0; destino < grafo.getCantidadCiudades(); destino++) {
                System.out.printf(
                    "%-16s", formatearCosto(resultado.getDistancia(origen, destino))
                );
            }
            System.out.println();
        }
    }

    private static void consultarRuta(
        Scanner entrada,
        Grafo grafo,
        Dijkstra dijkstra,
        FloydWarshall floydWarshall
    ) {
        int origen = seleccionarCiudad(entrada, grafo, "origen");
        if (origen < 0) {
            return;
        }
        int destino = seleccionarCiudad(entrada, grafo, "destino");
        if (destino < 0) {
            return;
        }

        int distanciaDirecta = grafo.getCosto(origen, destino);
        System.out.println("\nConsulta: " + grafo.getNombreCiudad(origen)
            + " -> " + grafo.getNombreCiudad(destino));
        System.out.println("Distancia directa: " + formatearCosto(distanciaDirecta));

        ResultadoDijkstra resultadoDijkstra = dijkstra.calcular(grafo, origen);
        ResultadoFloydWarshall resultadoFloyd = floydWarshall.calcular(grafo);
        System.out.print("Dijkstra: ");
        imprimirRutaYDistancia(
            grafo,
            resultadoDijkstra.getRutaHasta(destino),
            resultadoDijkstra.getDistanciaHasta(destino)
        );
        System.out.print("Floyd-Warshall: ");
        imprimirRutaYDistancia(
            grafo,
            resultadoFloyd.getRuta(origen, destino),
            resultadoFloyd.getDistancia(origen, destino)
        );
    }

    private static int seleccionarCiudad(Scanner entrada, Grafo grafo, String etiqueta) {
        System.out.println("\nSeleccione la ciudad de " + etiqueta + ":");
        for (int ciudad = 0; ciudad < grafo.getCantidadCiudades(); ciudad++) {
            System.out.printf("%d. %s%n", ciudad + 1, grafo.getNombreCiudad(ciudad));
        }
        int seleccion = leerEntero(entrada, "Numero de ciudad (0 para cancelar): ");
        if (seleccion == Integer.MIN_VALUE || seleccion == 0) {
            return -1;
        }
        if (seleccion < 1 || seleccion > grafo.getCantidadCiudades()) {
            System.out.println("Numero de ciudad no valido.");
            return -1;
        }
        return seleccion - 1;
    }

    private static int leerEntero(Scanner entrada, String mensaje) {
        while (true) {
            System.out.print(mensaje);
            if (!entrada.hasNextLine()) {
                return Integer.MIN_VALUE;
            }
            String texto = entrada.nextLine().trim();
            try {
                return Integer.parseInt(texto);
            } catch (NumberFormatException excepcion) {
                System.out.println("Ingrese un numero entero valido.");
            }
        }
    }

    private static void imprimirRutaYDistancia(
        Grafo grafo, List<Integer> ruta, int distancia
    ) {
        if (ruta.isEmpty() || distancia == Grafo.INFINITO) {
            System.out.println("No existe una ruta.");
            return;
        }
        for (int i = 0; i < ruta.size(); i++) {
            if (i > 0) {
                System.out.print(" -> ");
            }
            System.out.print(grafo.getNombreCiudad(ruta.get(i)));
        }
        System.out.println(" (" + distancia + " km)");
    }

    private static void ejecutarBfs(
        Scanner entrada, Grafo grafo, BusquedaAnchura busqueda
    ) {
        int origen = seleccionarCiudad(entrada, grafo, "origen del BFS");
        if (origen >= 0) {
            imprimirRecorrido(grafo, busqueda.recorrer(grafo, origen), "BFS");
        }
    }

    private static void ejecutarDfs(
        Scanner entrada, Grafo grafo, BusquedaProfundidad busqueda
    ) {
        int origen = seleccionarCiudad(entrada, grafo, "origen del DFS");
        if (origen >= 0) {
            imprimirRecorrido(grafo, busqueda.recorrer(grafo, origen), "DFS");
        }
    }

    private static void imprimirRecorrido(
        Grafo grafo, List<Integer> recorrido, String algoritmo
    ) {
        System.out.println("\nRecorrido " + algoritmo + ":");
        for (int i = 0; i < recorrido.size(); i++) {
            if (i > 0) {
                System.out.print(" -> ");
            }
            System.out.print(grafo.getNombreCiudad(recorrido.get(i)));
        }
        System.out.println();
    }

    private static String formatearCosto(int costo) {
        return costo == Grafo.INFINITO ? "Sin ruta" : costo + " km";
    }
}
