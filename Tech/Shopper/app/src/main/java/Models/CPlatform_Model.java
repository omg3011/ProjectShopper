package Models;

import java.io.Serializable;
import java.util.List;

public class CPlatform_Model implements Serializable {

    //--------------------------------------------------------------//
    // Variable(s) Declaration
    //--------------------------------------------------------------//
    String cpost_uid;
    String timestamp;
    List<String> uploads;
    String posterUid;       // Can get store info, store tag, etc..
    List<String> collabTag;
    String title;
    String description;
    int pendingRequestCount; // No of ppl pending for request
    boolean collab_closed_flag;
    String shoppingMall;


    //--------------------------------------------------------------//
    // Constructor(s)
    //--------------------------------------------------------------//
    public CPlatform_Model(){
    }
    public CPlatform_Model(String cpost_uid, String timestamp, List<String> uploads, String posterUid, List<String> collabTag, String title, String description, int pendingRequestCount, boolean collab_closed_flag, String shoppingMall) {
        this.cpost_uid = cpost_uid;
        this.timestamp = timestamp;
        this.uploads = uploads;
        this.posterUid = posterUid;
        this.collabTag = collabTag;
        this.description = description;
        this.pendingRequestCount = pendingRequestCount;
        this.collab_closed_flag = collab_closed_flag;
        this.title = title;
        this.shoppingMall = shoppingMall;
    }

    //--------------------------------------------------------------//
    // Getter / Setter (s)
    //--------------------------------------------------------------//


    public String getShoppingMall() {
        return shoppingMall;
    }

    public void setShoppingMall(String shoppingMall) {
        this.shoppingMall = shoppingMall;
    }

    public boolean isCollab_closed_flag() {
        return collab_closed_flag;
    }

    public void setCollab_closed_flag(boolean collab_closed_flag) {
        this.collab_closed_flag = collab_closed_flag;
    }

    public String getCPost_uid() {
        return cpost_uid;
    }

    public void setCpost_uid(String cpost_uid) {
        this.cpost_uid = cpost_uid;
    }

    public int getPendingRequestCount() {
        return pendingRequestCount;
    }

    public void setPendingRequestCount(int pendingRequestCount) {
        this.pendingRequestCount = pendingRequestCount;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public List<String> getUploads() {
        return uploads;
    }

    public void setUploads(List<String> uploads) {
        this.uploads = uploads;
    }

    public String getPosterUid() {
        return posterUid;
    }

    public void setPosterUid(String posterUid) {
        this.posterUid = posterUid;
    }

    public List<String> getCollabTag() {
        return collabTag;
    }

    public void setCollabTag(List<String> collabTag) {
        this.collabTag = collabTag;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
