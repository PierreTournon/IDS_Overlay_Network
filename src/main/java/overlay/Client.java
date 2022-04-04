package overlay;

import com.rabbitmq.client.DeliverCallback;
import org.apache.commons.lang3.SerializationUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;


public class Client {
    private String id;
    private ArrayList<String> right;
    private ArrayList<String> left;

    public Client(String id,ArrayList<String> right){
        this.id = id;
        this.right = right;
        this.left = new ArrayList<>();

    }

    public String getId() {
        return id;
    }

    public ArrayList<String> nextNodeLeft() {
        //left.remove(0);
        return left;
    }

    public ArrayList<String> nextNodeRight() {
        //right.remove(0);
        return right;
    }

    public String getFirstNodeLeft(){
        return left.get(0);
    }

    public String getFirstNodeRight(){
        return right.get(0);
    }

    /*public void receiveMessage() throws IOException {
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


