package Models;

public class RequestMailBox_Model {

    private String status;
    private String cplatformPost_ID;
    private String requester_UID;
    private String requestMailBox_ID;

    public RequestMailBox_Model(){}
    public RequestMailBox_Model(String status, String cplatformPost_ID, String requester_UID, String requestMailBox_ID) {
        this.status = status;
        this.cplatformPost_ID = cplatformPost_ID;
        this.requester_UID = requester_UID;
        this.requestMailBox_ID = requestMailBox_ID;
    }

    public String getRequestMailBoxID() {
        return requestMailBox_ID;
    }

    public void setMyRequestMailBoxID(String requestID) {
        this.requestMailBox_ID = requestID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCplatformPost_ID() {
        return cplatformPost_ID;
    }

    public void setCplatformPost_ID(String cplatformPost_ID) {
        this.cplatformPost_ID = cplatformPost_ID;
    }

    public String getRequester_UID() {
        return requester_UID;
    }

    public void setRequester_UID(String requester_UID) {
        this.requester_UID = requester_UID;
    }
}
