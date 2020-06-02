package Models;

public class FriendList_Model {
    String friendPost_uid;
    String requester_uid;
    String owner_uid;
    String cpost_uid;

    public FriendList_Model(String friendPost_uid, String requester_uid, String owner_uid, String cpost_uid) {
        this.friendPost_uid = friendPost_uid;
        this.requester_uid = requester_uid;
        this.owner_uid = owner_uid;
        this.cpost_uid = cpost_uid;
    }

    public String getFriendPost_uid() {
        return friendPost_uid;
    }

    public void setFriendPost_uid(String friendPost_uid) {
        this.friendPost_uid = friendPost_uid;
    }

    public String getRequester_uid() {
        return requester_uid;
    }

    public void setRequester_uid(String requester_uid) {
        this.requester_uid = requester_uid;
    }

    public String getOwner_uid() {
        return owner_uid;
    }

    public void setOwner_uid(String owner_uid) {
        this.owner_uid = owner_uid;
    }

    public String getCpost_uid() {
        return cpost_uid;
    }

    public void setCpost_uid(String cpost_uid) {
        this.cpost_uid = cpost_uid;
    }
}
