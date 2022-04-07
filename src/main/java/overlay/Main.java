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

    public static void main(String[] args) {
        Map<Integer, List<Integer>> graph = new HashMap<>();

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
    }
}

