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
    String promotionPost_uid;
    boolean collab_closed_flag;


    //--------------------------------------------------------------//
    // Constructor(s)
    //--------------------------------------------------------------//
    public CPromotion_Model(){}
    public CPromotion_Model(String promotionPost_uid, String title, String description, String duration, String timestampStart, String timestampEnd, String timestampPost, List<String> tags, String posterUid, List<String> uploads, boolean collab_closed_flag) {
        this.promotionPost_uid = promotionPost_uid;
        this.description = description;
        this.title = title;
        this.duration = duration;
        this.timestampStart = timestampStart;
        this.timestampEnd = timestampEnd;
        this.timestampPost = timestampPost;
        this.tags = tags;
        this.posterUid = posterUid;
        this.uploads = uploads;
        this.collab_closed_flag = collab_closed_flag;
    }

    //--------------------------------------------------------------//
    // Getter / Setter (s)
    //--------------------------------------------------------------//

    public String getPromotionPost_uid() {
        return promotionPost_uid;
    }

    public void setPromotionPost_uid(String promotionPost_uid) {
        this.promotionPost_uid = promotionPost_uid;
    }

    public boolean isCollab_closed_flag() {
        return collab_closed_flag;
    }

    public void setCollab_closed_flag(boolean collab_closed_flag) {
        this.collab_closed_flag = collab_closed_flag;
    }

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
