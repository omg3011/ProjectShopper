package Models;

public class Chat_Model {

    //--------------------------------------------------------------//
    // Variable(s) Declaration
    //--------------------------------------------------------------//
    String sender;
    String receiver;
    String message;
    String timestamp;
    String cpost_uid;
    boolean isSeen;

    //--------------------------------------------------------------//
    // Constructor(s)
    //--------------------------------------------------------------//
    public Chat_Model(){}

    public Chat_Model(String sender, String receiver, String message, String timestamp, boolean isSeen, String cpost_uid) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.timestamp = timestamp;
        this.isSeen = isSeen;
        this.cpost_uid = cpost_uid;
    }

    //--------------------------------------------------------------//
    // Getter / Setter (s)
    //--------------------------------------------------------------//

    public String getCpost_uid() {
        return cpost_uid;
    }

    public void setCpost_uid(String cpost_uid) {
        this.cpost_uid = cpost_uid;
    }

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
