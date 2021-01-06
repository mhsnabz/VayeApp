package com.vaye.app.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class TaskUser implements Parcelable {
      String email , name , number ,   priority ,profileImage ,thumb_image ,schoolName  ,short_school  ,bolum  ,fakulte  ,uid  ,username ,linkedin ,instagram,twitter,github,unvan;
        ArrayList<String> slientUser;

    public TaskUser() {
    }

    public TaskUser(String email, String name, String number, String priority, String profileImage, String thumb_image, String schoolName, String short_school, String bolum, String fakulte, String uid, String username, String linkedin, String instagram, String twitter, String github, String unvan, ArrayList<String> slientUser) {
        this.email = email;
        this.name = name;
        this.number = number;
        this.priority = priority;
        this.profileImage = profileImage;
        this.thumb_image = thumb_image;
        this.schoolName = schoolName;
        this.short_school = short_school;
        this.bolum = bolum;
        this.fakulte = fakulte;
        this.uid = uid;
        this.username = username;
        this.linkedin = linkedin;
        this.instagram = instagram;
        this.twitter = twitter;
        this.github = github;
        this.unvan = unvan;
        this.slientUser = slientUser;
    }

    protected TaskUser(Parcel in) {
        email = in.readString();
        name = in.readString();
        number = in.readString();
        priority = in.readString();
        profileImage = in.readString();
        thumb_image = in.readString();
        schoolName = in.readString();
        short_school = in.readString();
        bolum = in.readString();
        fakulte = in.readString();
        uid = in.readString();
        username = in.readString();
        linkedin = in.readString();
        instagram = in.readString();
        twitter = in.readString();
        github = in.readString();
        unvan = in.readString();
        slientUser = in.createStringArrayList();
    }

    public static final Creator<TaskUser> CREATOR = new Creator<TaskUser>() {
        @Override
        public TaskUser createFromParcel(Parcel in) {
            return new TaskUser(in);
        }

        @Override
        public TaskUser[] newArray(int size) {
            return new TaskUser[size];
        }
    };

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public String getPriority() {
        return priority;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public String getThumb_image() {
        return thumb_image;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public String getShort_school() {
        return short_school;
    }

    public String getBolum() {
        return bolum;
    }

    public String getFakulte() {
        return fakulte;
    }

    public String getUid() {
        return uid;
    }

    public String getUsername() {
        return username;
    }

    public String getLinkedin() {
        return linkedin;
    }

    public String getInstagram() {
        return instagram;
    }

    public String getTwitter() {
        return twitter;
    }

    public String getGithub() {
        return github;
    }

    public String getUnvan() {
        return unvan;
    }

    public ArrayList<String> getSlientUser() {
        return slientUser;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public void setShort_school(String short_school) {
        this.short_school = short_school;
    }

    public void setBolum(String bolum) {
        this.bolum = bolum;
    }

    public void setFakulte(String fakulte) {
        this.fakulte = fakulte;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setLinkedin(String linkedin) {
        this.linkedin = linkedin;
    }

    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public void setGithub(String github) {
        this.github = github;
    }

    public void setUnvan(String unvan) {
        this.unvan = unvan;
    }

    public void setSlientUser(ArrayList<String> slientUser) {
        this.slientUser = slientUser;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(email);
        parcel.writeString(name);
        parcel.writeString(number);
        parcel.writeString(priority);
        parcel.writeString(profileImage);
        parcel.writeString(thumb_image);
        parcel.writeString(schoolName);
        parcel.writeString(short_school);
        parcel.writeString(bolum);
        parcel.writeString(fakulte);
        parcel.writeString(uid);
        parcel.writeString(username);
        parcel.writeString(linkedin);
        parcel.writeString(instagram);
        parcel.writeString(twitter);
        parcel.writeString(github);
        parcel.writeString(unvan);
        parcel.writeStringList(slientUser);
    }
}
