package Models;

public class RequestMailBox_Model {

    private String status;
    private String postID;
    private String uid;

    public RequestMailBox_Model(){}
    public RequestMailBox_Model(String status, String postID, String uid) {
        this.status = status;
        this.postID = postID;
        this.uid = uid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
