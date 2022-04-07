package overlay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Dijkstra {

    private Map<Integer, Integer> pred;
    private Map<Integer, List<Integer>> graph;

    public Dijkstra (Map<Integer, List<Integer>> graph) {
        this.graph = graph;
        this.pred = new HashMap<>();
    }

    /**
     * Fonction renvoyant le plus court chemin selon l'algorithme de Dijkstra de l'attribut "graph" entre un noeud "source" et un noeud "target" passés en paramètre.
     * @param source : entier représentant le noeud de départ du chemin que l'on souhaite évaluer.
     * @param target : entier représentant le noeud d'arrivé du chemin que l'on souhaite évaluer.
     * @return : une ArrayList d'entiers du plus court chemin entre le noeud "source" et le noeud "target" dans l'attribut "graph".
     */
    public ArrayList<Integer> shortestPath (int source, int target) {
        ArrayList<Integer> shortestPath = new ArrayList<>();

        //Vérification de la connexité du graphe.
        if(!this.isConnected()) {
            System.out.println("Le graphe n'est pas connexe, risque de chemin inexisitant. \nListe vite en valeur de retour");
            return shortestPath;
        }

        //Vérification de l'existence de noeuds passés en paramètre.
        if(!graph.containsKey(source) || !graph.containsKey(target)) {
            System.out.println("Au moins un des noeuds passé en paramètre n'existe pas dans le graphe. \nListe vide en valeur de retour.");
            return shortestPath;
        }

        ArrayList<Integer> reversedShortestPath = new ArrayList<>();
        Map<Integer, Integer> dists = new HashMap<>();
        List<Integer> nodes = new ArrayList<>(this.graph.keySet()); //Liste de tous les noeuds.

        //Initialisation des distances
        for(int d : this.graph.keySet()) {
            if(d == source)
                dists.put(d, 0);
            else
                dists.put(d, Integer.MAX_VALUE);
        }

        //On applique l'algo de Dijkstra en lui même
        while (!nodes.isEmpty()) {
            int s1 = findMin(dists, nodes);
            nodes.remove(nodes.indexOf(s1));
            for(int s2 : this.graph.get(s1))
                updDists(s1, s2, dists);
        }

        //On remonte la liste pour enfin calculer le plus court chemin
        while (target != source) {
            reversedShortestPath.add(target);
            target = pred.get(target);
        }
        reversedShortestPath.add(source);

        //On retourne le plus court chemin pour qu'il soit dans le bon sens
        for(int i = reversedShortestPath.size() - 1; i >= 0; i--)
            shortestPath.add(reversedShortestPath.get(i));

        return shortestPath;
    }

    /**
     * Cherche le noeud le plus proche selon le tableau de distances.
     * @param dists : Map<Integer, Integer> représentant la distance d'un noeud par rapport à un noeud source en clé
     * @param nodes : List<Integer> De tous les noeuds du graphe.
     * @return : retourne le noeud le plus proche.
     */
    private int findMin (Map<Integer, Integer> dists, List<Integer> nodes) {
        int minPath = Integer.MAX_VALUE;
        int node = -1;
        for(int s : nodes)
            if(dists.get(s) < minPath) {
                minPath = dists.get(s);
                node = s;
            }

        return node;
    }

    /**
     * Met à jour la distance entre un noeud A et un noeud B passés en paramètre dans le tableau des distances entre noeuds passé en paramètre.
     * @param nodeA : entier représentant un noeud.
     * @param nodeB : entier représentant un noeud.
     * @param dists : tableau des distances entre paires de noeuds.
     */
    private void updDists (int nodeA, int nodeB, Map<Integer, Integer> dists) {
        if(dists.get(nodeB) > dists.get(nodeA) + 1) {
            dists.replace(nodeB, dists.get(nodeA) + 1);
            this.pred.put(nodeB, nodeA);
        }
    }

    /**
     * Vérifie la connexité du graphe en attribut.
     * @return true si le graphe est connexe, false si non connexe
     */
    public boolean isConnected () {
        //HashMap associant un noeud avec un booléen indiquant s'il a été visité ou non.
        Map<Integer, Boolean> seen = new HashMap<>();
        for(int node : this.graph.keySet())
            seen.put(node, false);

        //Appel du parcours en profondeur.
        this.explore(seen, 1);

        //Si un noeud n'a pas été visité alors le grpahe est non-connexe sinon il est connexe.
        for(int node : seen.keySet())  {
            if(!seen.get(node))
                return false;
        }

        return true;
    }

    /**
     * Fonction récursive de parcours en profondeur
     * @param seen : Map associant un noeud de type int avec un boolean true si le noeud a été exploré ou false si non exploré.
     * @param node : noeud de départ du parcours.
     */
    private void explore (Map<Integer, Boolean> seen, int node) {
        seen.put(node, true); //On marque le sommet "node"

        //Pour tous les sommets de "node"
        for(int e : this.graph.get(node))
            if(!seen.get(e))
                explore(seen, e);
    }




}
