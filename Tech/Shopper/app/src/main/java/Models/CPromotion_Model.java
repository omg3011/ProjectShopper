package Models;

import java.io.Serializable;
import java.util.List;

public class CPromotion_Model implements Serializable {

    //--------------------------------------------------------------//
    // Variable(s) Declaration
    //--------------------------------------------------------------//
    String title;
    String description;
    String duration;
    String timestampStart;
    String timestampEnd;
    String timestampPost;
    List<String> tags;
    String posterUid;
    List<String> uploads;


    //--------------------------------------------------------------//
    // Constructor(s)
    //--------------------------------------------------------------//
    public CPromotion_Model(){}
    public CPromotion_Model(String title, String description, String duration, String timestampStart, String timestampEnd, String timestampPost, List<String> tags, String posterUid, List<String> uploads) {
        this.description = description;
        this.title = title;
        this.duration = duration;
        this.timestampStart = timestampStart;
        this.timestampEnd = timestampEnd;
        this.timestampPost = timestampPost;
        this.tags = tags;
        this.posterUid = posterUid;
        this.uploads = uploads;
    }

    //--------------------------------------------------------------//
    // Getter / Setter (s)
    //--------------------------------------------------------------//

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTimestampPost() {
        return timestampPost;
    }

    public void setTimestampPost(String timestampPost) {
        this.timestampPost = timestampPost;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getTimestampStart() {
        return timestampStart;
    }

    public void setTimestampStart(String timestampStart) {
        this.timestampStart = timestampStart;
    }

    public String getTimestampEnd() {
        return timestampEnd;
    }

    public void setTimestampEnd(String timestampEnd) {
        this.timestampEnd = timestampEnd;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getPosterUid() {
        return posterUid;
    }

    public void setPosterUid(String posterUid) {
        this.posterUid = posterUid;
    }

    public List<String> getUploads() {
        return uploads;
    }

    public void setUploads(List<String> uploads) {
        this.uploads = uploads;
    }
}
