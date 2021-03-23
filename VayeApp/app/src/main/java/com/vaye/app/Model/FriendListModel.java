package com.vaye.app.Model;

import com.google.firebase.Timestamp;

public class FriendListModel {

    //    var tarih : Timestamp!
    String name,userName,short_school,bolum,uid,thumb_image,profileImage;
    Timestamp tarih;

    public FriendListModel() {
    }

    public FriendListModel(String name, String userName, String short_school, String bolum, String uid, String thumb_image, String profileImage, Timestamp tarih) {
        this.name = name;
        this.userName = userName;
        this.short_school = short_school;
        this.bolum = bolum;
        this.uid = uid;
        this.thumb_image = thumb_image;
        this.profileImage = profileImage;
        this.tarih = tarih;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getShort_school() {
        return short_school;
    }

    public void setShort_school(String short_school) {
        this.short_school = short_school;
    }

    public String getBolum() {
        return bolum;
    }

    public void setBolum(String bolum) {
        this.bolum = bolum;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getThumb_image() {
        return thumb_image;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public Timestamp getTarih() {
        return tarih;
    }

    public void setTarih(Timestamp tarih) {
        this.tarih = tarih;
    }
}
