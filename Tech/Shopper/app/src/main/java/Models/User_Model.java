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

    //--------------------------------------------------------------//
    // Constructor(s)
    //--------------------------------------------------------------//
    public User_Model(String email, String uid, String name, String phone, String image) {
        this.email = email;
        this.uid = uid;
        this.name = name;
        this.phone = phone;
        this.image = image;
    }

    //--------------------------------------------------------------//
    // Getter(s) / Setter(s)
    //--------------------------------------------------------------//
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
}
