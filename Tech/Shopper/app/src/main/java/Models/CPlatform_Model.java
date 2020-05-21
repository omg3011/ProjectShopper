package Models;

import java.util.List;

public class CPlatform_Model {

    //--------------------------------------------------------------//
    // Variable(s) Declaration
    //--------------------------------------------------------------//
    String timestamp;
    List<String> uploads;
    String posterUid;       // Can get store info, store tag, etc..
    List<String> collabTag;
    String description;


    //--------------------------------------------------------------//
    // Constructor(s)
    //--------------------------------------------------------------//
    public CPlatform_Model(){
    }
    public CPlatform_Model(String timestamp, List<String> uploads, String posterUid, List<String> collabTag, String description) {
        this.timestamp = timestamp;
        this.uploads = uploads;
        this.posterUid = posterUid;
        this.collabTag = collabTag;
        this.description = description;
    }

    //--------------------------------------------------------------//
    // Getter / Setter (s)
    //--------------------------------------------------------------//

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