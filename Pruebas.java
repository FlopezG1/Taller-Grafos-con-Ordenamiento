import java.util.*;

public class Pruebas {
    static int ok = 0, fallos = 0;
    static void check(boolean c, String msg) { if (c) ok++; else { fallos++; System.out.println("FALLO: " + msg); } }
    static boolean lanza(Runnable r, Class<? extends Throwable> t) {
        try { r.run(); } catch (Throwable e) { return t.isInstance(e); } return false;
    }

    // Referencias independientes
    static long[] bellmanFord(int n, int[][] w, int s) {
        long INF = Long.MAX_VALUE / 4; long[] d = new long[n]; Arrays.fill(d, INF); d[s] = 0;
        for (int k = 0; k < n; k++) for (int u = 0; u < n; u++) for (int v = 0; v < n; v++)
            if (u != v && w[u][v] >= 0 && d[u] < INF && d[u] + w[u][v] < d[v]) d[v] = d[u] + w[u][v];
        return d;
    }
    static int costoRuta(Grafo g, List<Integer> r) {
        int s = 0; for (int i = 0; i + 1 < r.size(); i++) s += g.getCosto(r.get(i), r.get(i + 1)); return s;
    }
    static Set<Integer> alcanzables(Grafo g, int o) {
        Set<Integer> v = new HashSet<>(); Deque<Integer> p = new ArrayDeque<>(); p.add(o); v.add(o);
        while (!p.isEmpty()) { int a = p.poll(); for (int d = 0; d < g.getCantidadCiudades(); d++)
            if (g.existeArista(a, d) && v.add(d)) p.add(d); } return v;
    }

    public static void main(String[] a) {
        Random rnd = new Random(42);
        // ---------- 1. Aleatorio: Dijkstra vs Floyd vs Bellman-Ford; BFS/DFS; orden ----------
        for (int t = 0; t < 3000; t++) {
            int n = 1 + rnd.nextInt(9);
            String[] nom = new String[n]; for (int i = 0; i < n; i++) nom[i] = "C" + i;
            Grafo g = new Grafo(nom);
            int[][] w = new int[n][n]; for (int[] f : w) Arrays.fill(f, -1);
            double dens = rnd.nextDouble();
            for (int i = 0; i < n; i++) for (int j = 0; j < n; j++)
                if (i != j && rnd.nextDouble() < dens) { int c = rnd.nextInt(5) == 0 ? 0 : 1 + rnd.nextInt(100); g.agregarArista(i, j, c); w[i][j] = c; }
            ResultadoFloydWarshall fw = new FloydWarshall().calcular(g);
            for (int s = 0; s < n; s++) {
                ResultadoDijkstra dj = new Dijkstra().calcular(g, s);
                long[] ref = bellmanFord(n, w, s);
                Set<Integer> alc = alcanzables(g, s);
                for (int d = 0; d < n; d++) {
                    boolean alcanz = ref[d] < Long.MAX_VALUE / 4;
                    int dd = dj.getDistanciaHasta(d), df = fw.getDistancia(s, d);
                    check(alcanz ? dd == ref[d] : dd == Grafo.INFINITO, "Dijkstra dist t=" + t + " " + s + "->" + d);
                    check(alcanz ? df == ref[d] : df == Grafo.INFINITO, "Floyd dist t=" + t + " " + s + "->" + d);
                    List<Integer> rd = dj.getRutaHasta(d), rf = fw.getRuta(s, d);
                    if (alcanz) {
                        check(!rd.isEmpty() && rd.get(0) == s && rd.get(rd.size()-1) == d && costoRuta(g, rd) == ref[d], "ruta Dijkstra t=" + t + " " + s + "->" + d + " " + rd);
                        check(!rf.isEmpty() && rf.get(0) == s && rf.get(rf.size()-1) == d && costoRuta(g, rf) == ref[d], "ruta Floyd t=" + t + " " + s + "->" + d + " " + rf);
                    } else {
                        check(rd.isEmpty() && rf.isEmpty(), "ruta vacia inalcanzable t=" + t);
                    }
                }
                List<Integer> bfs = new BusquedaAnchura().recorrer(g, s), dfs = new BusquedaProfundidad().recorrer(g, s);
                check(bfs.get(0) == s && new HashSet<>(bfs).equals(alc) && bfs.size() == alc.size(), "BFS conjunto t=" + t);
                check(dfs.get(0) == s && new HashSet<>(dfs).equals(alc) && dfs.size() == alc.size(), "DFS conjunto t=" + t);
            }
            // orden
            for (boolean asc : new boolean[]{true, false}) {
                List<Ruta> in = g.getRutas(), out = new OrdenadorRutas().ordenar(in, asc);
                List<Ruta> esp = new ArrayList<>(in);
                esp.sort(asc ? Comparator.comparingInt(Ruta::getDistancia) : Comparator.comparingInt(Ruta::getDistancia).reversed()); // sort estable
                check(out.equals(esp), "merge sort estable asc=" + asc + " t=" + t);
            }
        }
        // ---------- 2. Casos limite ----------
        Grafo g1 = new Grafo("Solo");
        check(new Dijkstra().calcular(g1, 0).getRutaHasta(0).equals(List.of(0)), "1 ciudad Dijkstra");
        check(new FloydWarshall().calcular(g1).getRuta(0, 0).equals(List.of(0)), "1 ciudad Floyd");
        check(new OrdenadorRutas().ordenar(new ArrayList<>(), true).isEmpty(), "ordenar vacio");
        check(lanza(() -> new Grafo(), IllegalArgumentException.class), "grafo sin ciudades");
        check(lanza(() -> new Grafo((String[]) null), IllegalArgumentException.class), "grafo null");
        Grafo g2 = new Grafo("A", "B", "C");
        check(lanza(() -> g2.agregarArista(0, 1, -1), IllegalArgumentException.class), "costo negativo");
        check(lanza(() -> g2.agregarArista(0, 3, 1), IndexOutOfBoundsException.class), "indice alto");
        check(lanza(() -> g2.agregarArista(-1, 0, 1), IndexOutOfBoundsException.class), "indice negativo");
        check(lanza(() -> new Dijkstra().calcular(g2, 5), IndexOutOfBoundsException.class), "Dijkstra origen invalido");
        check(lanza(() -> new BusquedaAnchura().recorrer(g2, -1), IndexOutOfBoundsException.class), "BFS origen invalido");
        check(lanza(() -> new BusquedaProfundidad().recorrer(g2, 9), IndexOutOfBoundsException.class), "DFS origen invalido");
        check(lanza(() -> g2.getRutas().add(new Ruta(0, 1, 1)), UnsupportedOperationException.class), "getRutas inmutable");
        g2.agregarArista(0, 1, 10); g2.agregarArista(0, 1, 3);
        check(g2.getRutas().size() == 1 && g2.getRutas().get(0).getDistancia() == 3 && g2.getCosto(0, 1) == 3, "reemplazo de arista");
        check(new Dijkstra().calcular(g2, 0).getRutaHasta(2).isEmpty(), "ciudad aislada");
        // Estados de Dijkstra: un estado por ciudad alcanzada y copias defensivas
        ResultadoDijkstra rd = new Dijkstra().calcular(g2, 0);
        check(rd.getEstados().size() == 2, "estados = ciudades alcanzadas");
        // ---------- 3. Arista a si misma ----------
        Grafo g3 = new Grafo("A", "B");
        check(lanza(() -> g3.agregarArista(0, 0, 7), IllegalArgumentException.class), "autolazo rechazado");
        check(lanza(() -> g3.agregarArista(0, 1, Grafo.INFINITO), IllegalArgumentException.class), "costo INFINITO rechazado");
        check(g3.getRutas().isEmpty() && new FloydWarshall().calcular(g3).getDistancia(0, 0) == 0, "estado intacto tras rechazos");
        // ---------- 4. Grafo de Main ----------
        Grafo m = new Grafo("Bogota","Medellin","Cali","Barranquilla","Bucaramanga","Cartagena");
        int[][] e = {{0,1,415},{0,2,464},{0,3,1000},{0,4,397},{0,5,1050},{1,0,415},{1,2,419},{1,3,640},{1,4,390},{2,0,464},{2,1,419},{2,5,1040},{3,0,1000},{3,1,640},{3,4,710},{3,5,120},{4,0,397},{4,1,390},{4,3,710},{5,0,1050},{5,2,1040},{5,3,120}};
        for (int[] x : e) m.agregarArista(x[0], x[1], x[2]);
        check(new FloydWarshall().calcular(m).getDistancia(2, 3) == 1059, "Cali->Barranquilla 1059");
        check(new FloydWarshall().calcular(m).getDistancia(1, 5) == 760, "Medellin->Cartagena 760");
        System.out.println("Pruebas OK: " + ok + "  FALLOS: " + fallos);
    }
}
