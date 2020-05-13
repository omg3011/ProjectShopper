package Models;

public class Chat_Model {

    //--------------------------------------------------------------//
    // Variable(s) Declaration
    //--------------------------------------------------------------//
    String sender;
    String receiver;
    String message;
    String timestamp;
    boolean isSeen;

    //--------------------------------------------------------------//
    // Constructor(s)
    //--------------------------------------------------------------//
    public Chat_Model(){}

    public Chat_Model(String sender, String receiver, String message, String timestamp, boolean isSeen) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.timestamp = timestamp;
        this.isSeen = isSeen;
    }

    //--------------------------------------------------------------//
    // Getter / Setter (s)
    //--------------------------------------------------------------//
    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isSeen() {
        return isSeen;
    }

    public void setSeen(boolean seen) {
        isSeen = seen;
    }
}
