package Models;

import java.io.Serializable;

public class RequestMailBox_Model implements Serializable {

    private String status;
    private String cplatformPost_ID;
    private String requester_UID;
    private String requestMailBox_ID;
    private String owner_UID;

    public RequestMailBox_Model(){}
    public RequestMailBox_Model(String status, String cplatformPost_ID, String requester_UID, String requestMailBox_ID, String owner_UID) {
        this.status = status;
        this.cplatformPost_ID = cplatformPost_ID;
        this.requester_UID = requester_UID;
        this.requestMailBox_ID = requestMailBox_ID;
        this.owner_UID = owner_UID;
    }

    public String getRequestMailBox_ID() {
        return requestMailBox_ID;
    }

    public void setRequestMailBox_ID(String requestMailBox_ID) {
        this.requestMailBox_ID = requestMailBox_ID;
    }

    public String getOwner_UID() {
        return owner_UID;
    }

    public void setOwner_UID(String owner_UID) {
        this.owner_UID = owner_UID;
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
