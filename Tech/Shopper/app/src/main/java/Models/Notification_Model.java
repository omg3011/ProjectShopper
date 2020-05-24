package Models;

public class Notification_Model {

    String timestamp;
    String uid;
    String message;

    public Notification_Model(){}

    public Notification_Model(String timestamp, String uid, String message) {
        this.timestamp = timestamp;
        this.uid = uid;
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
