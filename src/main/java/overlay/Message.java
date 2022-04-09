package overlay;

import java.io.Serializable;
import java.util.ArrayList;

public class Message implements Serializable {
    private String msg;
    private ArrayList<String> clientsIds;

    public Message(String msg, ArrayList<String> clientsIds){
        this.msg = msg;
        this.clientsIds = clientsIds;
    }

    public String getMsg(){
        return msg;
    }

    public ArrayList<String> getClientsIds() {
        return clientsIds;
    }

    public String popClients(){
        return clientsIds.remove(0);
    }

    public String nextClient(){
        return clientsIds.get(0);
    }
}