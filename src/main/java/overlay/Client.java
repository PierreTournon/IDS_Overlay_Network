package overlay;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.apache.commons.lang3.SerializationUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;


public class Client {
    private static final String EXCHANGE_NAME = "direct";
    private Integer id;
    private String queueName;
    private Connection connection;
    private Channel channel;
    private List<Integer> right;
    private List<Integer> left;

    public Client(Integer id,List<Integer> right) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        this.id = id;
        this.right = right;
        this.left = new ArrayList<>();
        this.connection = factory.newConnection();
        this.channel = connection.createChannel();
        queueName = channel.queueDeclare().getQueue();

        //Déclaration de l'exchange en routage direct
        channel.exchangeDeclare(EXCHANGE_NAME, "direct");
        channel.queueBind(queueName, EXCHANGE_NAME, id.toString());
    }


    public void receiveMessage() throws IOException {
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                Message message = SerializationUtils.deserialize(delivery.getBody());
                message.popClients();
                if (message.getClientsIds().isEmpty()){
                    System.out.println(id + " a reçu :" + message.getMsg());
                }else{
                    Message newMessage = new Message(message.getMsg(), message.getClientsIds());
                    byte[] newMessageBytes = SerializationUtils.serialize(newMessage);
                    channel.basicPublish(EXCHANGE_NAME, message.nextClient(), null, newMessageBytes);
                }
            };
            channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
        }

    /*
    public void sendMessage(String str, ArrayList<Integer> path) throws IOException, TimeoutException {
            ArrayList<Integer> list_test = new ArrayList<Integer>();
            list_test.add(1);
            Message newMessage = new Message(str,list_test);
            byte[] newMessageBytes = SerializationUtils.serialize(newMessage);
            String nextNode = path.get(0).toString();
            channel.basicPublish(EXCHANGE_NAME, nextNode, null, newMessageBytes);
            System.out.println("message envoyé "+str);
        }
    */

}


