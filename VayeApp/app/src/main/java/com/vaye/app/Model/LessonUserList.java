package com.vaye.app.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class LessonUserList implements Parcelable {
    String email,name,number,thumb_image,username,uid;

    public LessonUserList() {
    }

    public LessonUserList(String email, String name, String number, String thumb_image, String username, String uid) {
        this.email = email;
        this.name = name;
        this.number = number;
        this.thumb_image = thumb_image;
        this.username = username;
        this.uid = uid;
    }

    protected LessonUserList(Parcel in) {
        email = in.readString();
        name = in.readString();
        number = in.readString();
        thumb_image = in.readString();
        username = in.readString();
        uid = in.readString();
    }

    public static final Creator<LessonUserList> CREATOR = new Creator<LessonUserList>() {
        @Override
        public LessonUserList createFromParcel(Parcel in) {
            return new LessonUserList(in);
        }

        @Override
        public LessonUserList[] newArray(int size) {
            return new LessonUserList[size];
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

    public String getThumb_image() {
        return thumb_image;
    }

    public String getUsername() {
        return username;
    }

    public String getUid() {
        return uid;
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
        parcel.writeString(thumb_image);
        parcel.writeString(username);
        parcel.writeString(uid);
    }
}