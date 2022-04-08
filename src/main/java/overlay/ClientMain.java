package overlay;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.apache.commons.lang3.SerializationUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientMain {
    private static final String EXCHANGE_NAME = "direct";
    private static BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
    private static Map<Integer, List<Integer>> graph = new HashMap<>();
    private static Map<Integer, Integer> physToVirtu = new HashMap<>();

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        String id = argv[0];
        Integer idInt = Integer.parseInt(id);
        ArrayList<Integer> nodesList = new ArrayList<>();   //va contenir tous les noeuds du graphe

        //Gestion du graphe
        Path file = Path.of(argv[1]);
        try {
            for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
                String[] parsedLine = line.split(" ");
                List<Integer> list = new ArrayList<>();
                for (String s : parsedLine) list.add(Integer.parseInt(s));
                nodesList.add(list.get(0));
                graph.put(list.get(0), list.subList(1, list.size())); //Construction du graphe.
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Lancement de Dijkstra
        Dijkstra dijkstra = new Dijkstra(getGraph());
        Integer indexNode = nodesList.indexOf(idInt);
        ArrayList<Integer> R;
        ArrayList<Integer> L;

        //noeud de droite = id du noeud suivant modulo(nombre de noeuds)
        if (indexNode.equals(nodesList.size()-1)){
            R = dijkstra.shortestPath(idInt, (nodesList.get(0)));
        } else {
            R = dijkstra.shortestPath(idInt, (nodesList.get(nodesList.indexOf(idInt) + 1)));
        }

        //noeud de gauche = id du noeud precedant modulo(nombre de noeuds)
        if (indexNode.equals(0)){
            L  = dijkstra.shortestPath(idInt, nodesList.get(nodesList.size() - 1));
        }else {
            L = dijkstra.shortestPath(idInt, (nodesList.get(nodesList.indexOf(idInt) - 1)));
        }

        ArrayList<String> RString = new ArrayList<String>();
        ArrayList<String> LString = new ArrayList<String>();

        //creation des listes avec des strings
        for (Integer integer : R) {
            RString.add(integer.toString());
        }
        for (Integer integer : L) {
            LString.add(integer.toString());
        }
        //on instancie les donnes du client
        Client client = new Client(id, RString, LString);
        System.out.println("Node : "+id);

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            String queueName = channel.queueDeclare().getQueue();
            //Déclaration de l'exchange en routage direct
            channel.exchangeDeclare(EXCHANGE_NAME, "direct");
            channel.queueBind(queueName, EXCHANGE_NAME, id);

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                Message message = SerializationUtils.deserialize(delivery.getBody());
                message.popClients();
                if (message.getClientsIds().isEmpty()) {
                    System.out.println(id + " a reçu : " + message.getMsg());
                } else {
                    Message newMessage = new Message(message.getMsg(), message.getClientsIds());
                    byte[] newMessageBytes = SerializationUtils.serialize(newMessage);
                    channel.basicPublish(EXCHANGE_NAME, message.nextClient(), null, newMessageBytes);
                }
            };
            channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
            });

            //Partie sendRigth ou sendLeft du ring
            System.out.println("Tapez !r \"message\" ou !l \"message\" pour envoyez un message à droite ou à gauche");
            String msg = "";
            while (true) {
                msg = stdIn.readLine();
                //sendRigth()
                if (msg.split(" ")[0].equals("!r")) {
                    msg = msg.replaceFirst("!r ", ""); // on enleve le !r du message
                    String nextNode = client.getFirstNodeRight();
                    Message newMessage = new Message(msg, client.getRight());
                    byte[] newMessageBytes = SerializationUtils.serialize(newMessage);
                    channel.basicPublish(EXCHANGE_NAME, nextNode, null, newMessageBytes);
                    System.out.println("message envoyé à droite : " + msg);
                }
                //sendLeft()
                else if (msg.split(" ")[0].equals("!l")) {
                    msg = msg.replaceFirst("!l ", ""); // on enleve le !l du message
                    String nextNode = client.getFirstNodeLeft();
                    Message newMessage = new Message(msg, client.getLeft());
                    byte[] newMessageBytes = SerializationUtils.serialize(newMessage);
                    channel.basicPublish(EXCHANGE_NAME, nextNode, null, newMessageBytes);
                    System.out.println("message envoyé à gauche : " + msg);
                }
                else {   //mauvaise saisie
                    System.out.println("mauvaise saisie, ecrivez !r [message] ou !l [message]");
                }
            }
        }
    }

    public static Map<Integer, List<Integer>> getGraph () {
        return graph;
    }

}


