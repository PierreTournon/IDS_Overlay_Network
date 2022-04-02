package overlay;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.apache.commons.lang3.SerializationUtils;

import java.time.LocalDateTime;

public class ClientMain {
    private static final String EXCHANGE_NAME = "direct";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        String id = argv[0];
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
                    System.out.println(id + " a reçu :" + message.getMsg());
                } else {
                    Message newMessage = new Message(message.getMsg(), message.getClientsIds());
                    byte[] newMessageBytes = SerializationUtils.serialize(newMessage);
                    channel.basicPublish(EXCHANGE_NAME, message.nextClient(), null, newMessageBytes);
                }
            };
            channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
            });

            System.out.println("Tapez /q pour quittez le chat");
            String msg = "";
            while (true);
            /*
            while(true) {
                Message newMessage = new Message(str, list_test);
                byte[] newMessageBytes = SerializationUtils.serialize(newMessage);
                String nextNode = path.get(0).toString();
                channel.basicPublish(EXCHANGE_NAME, nextNode, null, newMessageBytes);
                System.out.println("message envoyé " + str);

             */

        }


    }
}
