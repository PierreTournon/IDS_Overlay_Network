package overlay;

import java.io.Serializable;
import java.util.ArrayList;

public class Message implements Serializable {
    private String msg;
    private ArrayList<Integer> clientsIds;

    public Message(String msg, ArrayList<Integer> clientsIds){
        this.msg = msg;
        this.clientsIds = clientsIds;
    }

    public String getMsg(){
        return msg;
    }

    public ArrayList<Integer> getClientsIds() {
        return clientsIds;
    }

    public Integer popClients(){
        return clientsIds.remove(0);
    }

    public String nextClient(){
        return clientsIds.get(0).toString();
    }
}