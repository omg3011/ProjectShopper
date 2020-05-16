package Models;

import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

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
    String onlineStatus;

    String mallName;
    String storeName;
    String storeUnit;
    List<String> storeTags;
    boolean setup_profile;
    float ratingValue;
    List<Integer> ratingList;

    //--------------------------------------------------------------//
    // Constructor(s)
    //--------------------------------------------------------------//
    public User_Model(){}

    public User_Model(String email, String uid, String name, String phone, String image, String onlineStatus, String mallName, String storeName, String storeUnit, List<String> storeTags, boolean setup_profile, float ratingValue, List<Integer> ratingList) {
        this.email = email;
        this.uid = uid;
        this.name = name;
        this.phone = phone;
        this.image = image;
        this.onlineStatus = onlineStatus;
        this.mallName = mallName;
        this.storeName = storeName;
        this.storeUnit = storeUnit;
        this.storeTags = storeTags;
        this.setup_profile = setup_profile;
        this.ratingValue = ratingValue;
        this.ratingList = ratingList;
    }

    //--------------------------------------------------------------//
    // Getter(s) / Setter(s)
    //--------------------------------------------------------------//
    public float getRatingValue() {
        return ratingValue;
    }

    public void setRatingValue(float ratingValue) {
        this.ratingValue = ratingValue;
    }

    public List<Integer> getRatingList() {
        return ratingList;
    }

    public void setRatingList(List<Integer> ratingList) {
        this.ratingList = ratingList;
    }

    public List<String> getStoreTags() {
        return storeTags;
    }

    public void setStoreTags(List<String> storeTags) {
        this.storeTags = storeTags;
    }

    public boolean isSetup_profile() {
        return setup_profile;
    }

    public void setSetup_profile(boolean setup_profile) {
        this.setup_profile = setup_profile;
    }

    public String getMallName() {
        return mallName;
    }

    public void setMallName(String mallName) {
        this.mallName = mallName;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStoreUnit() {
        return storeUnit;
    }

    public void setStoreUnit(String storeUnit) {
        this.storeUnit = storeUnit;
    }


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


}
