package Models;

public class Notification_Model {

    String timestamp;
    String uid;
    String message;
    String notificationPost_uid;

    public Notification_Model(){}

    public Notification_Model(String timestamp, String uid, String message, String notificationPost_uid) {
        this.timestamp = timestamp;
        this.uid = uid;
        this.message = message;
        this.notificationPost_uid = notificationPost_uid;
    }

    public String getNotificationPost_uid() {
        return notificationPost_uid;
    }

    public void setNotificationPost_uid(String notificationPost_uid) {
        this.notificationPost_uid = notificationPost_uid;
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
