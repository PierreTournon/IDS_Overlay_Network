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
        Path file = Path.of(args[0]);
        try{
            for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
                String[] parsedLine = line.split(" ");
                for (String str : parsedLine)
                    System.out.print(str+" ");
                System.out.println("");
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public static Map<Client, ArrayList<Client>> fileToGraph () {
        Map<Client, ArrayList<Client>> graphe = new HashMap<Client, ArrayList<Client>>();
        return graphe;
    }
}

