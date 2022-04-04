package overlay;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.apache.commons.lang3.SerializationUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ClientMain {
    private static final String EXCHANGE_NAME = "direct";
    private static BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        String id = argv[0];
        //TEST
        ArrayList <String> R = new ArrayList();
        R.add("2");
        R.add("3");
        ArrayList <String> L = new ArrayList();
        L.add("3");
        L.add("1");
        Client client = new Client(id,R,L);

        System.out.println(id);

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

            //Partie send_rigt ou send_left du ring
            System.out.println("Tapez !r \"message\" ou !l \"message\" pour envoyez un message à droite ou à gauche");
            String msg = "";
            while(true) {
                msg = stdIn.readLine();
                if (msg.split(" ")[0].equals("!r")){
                    msg = msg.replaceFirst("!r ", ""); // on enleve le !r du message
                    String nextNode = client.getFirstNodeRight();
                    Message newMessage = new Message(msg, client.getRight());
                    byte[] newMessageBytes = SerializationUtils.serialize(newMessage);
                    channel.basicPublish(EXCHANGE_NAME, nextNode, null, newMessageBytes);
                    System.out.println("message envoyé à droite : "+ msg);
                }
                else if (msg.split(" ")[0].equals("!l")){
                    msg = msg.replaceFirst("!l ", ""); // on enleve le !l du message
                    String nextNode = client.getFirstNodeLeft();
                    Message newMessage = new Message(msg, client.getLeft());
                    byte[] newMessageBytes = SerializationUtils.serialize(newMessage);
                    channel.basicPublish(EXCHANGE_NAME, nextNode, null, newMessageBytes);
                    System.out.println("message envoyé à gauche : "+ msg);
                }
            }
        }
    }
}
