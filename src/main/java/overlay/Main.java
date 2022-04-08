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

    private static Map<Integer, List<Integer>> graph = new HashMap<>();
    private static Map<Integer, String> physToVirtu = new HashMap<>();

    public static void main(String[] args) {
        Path file = Path.of(args[0]);
        try{
            for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
                String[] parsedLine = line.split(" ");
                List<Integer> list = new ArrayList<>();
                for(int i = 0; i < parsedLine.length; i++)
                    list.add(Integer.parseInt(parsedLine[i]));

                graph.put(list.get(0), list.subList(1, list.size())); //Construction du graphe.
            }
        }catch(IOException e) {
            e.printStackTrace();
        }

        makeVirtualIds();
    }

    /**
     * Création des IDs virtuelles associées aux IDs physiques.
     */
    private static void makeVirtualIds () {
        int id = 0;
        for(int e : graph.keySet()) {
            physToVirtu.put(e, String.valueOf(0));
            i++;
        }
    }

    public static String getVirtualId (int physicalId) {
        return physToVirtu.get(physicalId);
    }

    public static Map<Integer, List<Integer>> getGraph () {
        return graph;
    }

}

