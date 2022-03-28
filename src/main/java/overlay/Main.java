package overlay;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    public static Map<Integer, Integer> pred = new HashMap<Integer, Integer>();

    public static void main(String[] args) {
        Map<Integer, List<Integer>> graph = new HashMap<Integer, List<Integer>>();
        Path file = Path.of(args[0]);
        try{
            for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
                String[] parsedLine = line.split(" ");
                List<Integer> list = new ArrayList<>();
                for(int i = 0; i < parsedLine.length; i++)
                    list.add(Integer.parseInt(parsedLine[i]));

                graph.put(list.get(0), list.subList(1, list.size()));
            }
        }catch(IOException e) {
            e.printStackTrace();
        }
        Dijkstra(1,4, graph);
    }

    public static ArrayList<Integer> Dijkstra (int source, int target, Map<Integer, List<Integer>> graph) {
        ArrayList<Integer> shortestPath = new ArrayList<>();
        Map<Integer, Integer> dists = new HashMap<>();

        //Initialisation des distances
        for(int d : graph.keySet()) {
            if(d == source)
                dists.put(d, 0);
            else
                dists.put(d, Integer.MAX_VALUE);
        }
        /*for(int e : dists.keySet()) {
            System.out.println(e+" "+ dists.get(e));
        }*/

        List<Integer> nodesLi = new ArrayList<Integer>(graph.keySet()); //Liste de tous les noeuds.

        //On applique l'algo de Dijkstra en lui même
        while (!nodesLi.isEmpty()) {
            int s1 = findMin(dists, nodesLi);
            nodesLi.remove(nodesLi.indexOf(s1));
            for(int s2 : graph.get(s1))
                updDists(s1, s2, dists);
        }

        //On remonte la liste pour enfin calculer le plus court chemin
        while (target != source) {
            shortestPath.add(target);
            target = pred.get(target);
        }
        shortestPath.add(source);

        for(int e : shortestPath)
            System.out.println(e);

        return shortestPath;
    }

    public static int findMin (Map<Integer, Integer> dists, List<Integer> graph) {
        int minPath = Integer.MAX_VALUE;
        int node = -1;
        for(int s : graph)
            if(dists.get(s) < minPath) {
                minPath = dists.get(s);
                node = s;
            }

        return node;
    }

    public static void updDists (int nodeA, int nodeB, Map<Integer, Integer> dists) {
        if(dists.get(nodeB) > dists.get(nodeA) + 1) {
            dists.replace(nodeB, dists.get(nodeA) + 1);
            pred.put(nodeB, nodeA);
        }
    }

}

