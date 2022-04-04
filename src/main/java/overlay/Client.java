package overlay;

import java.util.ArrayList;

public class Client {
    private String id;
    private ArrayList<String> right;
    private ArrayList<String> left;

    public Client(String id,ArrayList<String> right, ArrayList<String> left){
        this.id = id;
        this.right = right;
        this.left = left;

    }

    public String getId() {
        return id;
    }

    public ArrayList<String> getLeft() {
        return left;
    }

    public ArrayList<String> getRight() {
        return right;
    }

    public String getFirstNodeLeft(){
        return left.get(0);
    }

    public String getFirstNodeRight(){
        return right.get(0);
    }

}


