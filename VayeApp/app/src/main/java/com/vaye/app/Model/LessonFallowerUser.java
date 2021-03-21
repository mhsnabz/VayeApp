package com.vaye.app.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class LessonFallowerUser implements Parcelable {

    String username , name , number , thumb_image,email,uid;

    public LessonFallowerUser() {
    }

    public LessonFallowerUser(String username, String name, String number, String thumb_image, String email, String uid) {
        this.username = username;
        this.name = name;
        this.number = number;
        this.thumb_image = thumb_image;
        this.email = email;
        this.uid = uid;
    }

    protected LessonFallowerUser(Parcel in) {
        username = in.readString();
        name = in.readString();
        number = in.readString();
        thumb_image = in.readString();
        email = in.readString();
        uid = in.readString();
    }

    public static final Creator<LessonFallowerUser> CREATOR = new Creator<LessonFallowerUser>() {
        @Override
        public LessonFallowerUser createFromParcel(Parcel in) {
            return new LessonFallowerUser(in);
        }

        @Override
        public LessonFallowerUser[] newArray(int size) {
            return new LessonFallowerUser[size];
        }
    };

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getThumb_image() {
        return thumb_image;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(username);
        parcel.writeString(name);
        parcel.writeString(number);
        parcel.writeString(thumb_image);
        parcel.writeString(email);
        parcel.writeString(uid);
    }
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof LessonUserList))
            return false;

        return uid.equals(((LessonUserList) obj).getUid());
    }

    @Override
    public int hashCode() {
        return (uid == null) ? 0 : uid.hashCode();
    }
}
