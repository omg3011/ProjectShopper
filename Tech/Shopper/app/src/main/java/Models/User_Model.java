package Models;

public class User_Model
{
    //--------------------------------------------------------------//
    // Variable(s) Declaration
    //--------------------------------------------------------------//
    String email;
    String uid;
    String name;
    String phone;
    String image;
    String coverImage;
    String onlineStatus;

    //--------------------------------------------------------------//
    // Constructor(s)
    //--------------------------------------------------------------//
    public User_Model(){}

    public User_Model(String email, String uid, String name, String phone, String image, String coverImage, String onlineStatus) {
        this.email = email;
        this.uid = uid;
        this.name = name;
        this.phone = phone;
        this.image = image;
        this.coverImage = coverImage;
        this.onlineStatus = onlineStatus;
    }

    //--------------------------------------------------------------//
    // Getter(s) / Setter(s)
    //--------------------------------------------------------------//


    public String getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(String onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

}
