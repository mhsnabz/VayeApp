package com.vaye.app.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class MainPostTopicFollower implements Parcelable {
    String school, userId;

    public MainPostTopicFollower(String school, String userId) {
        this.school = school;
        this.userId = userId;
    }

    public MainPostTopicFollower() {
    }

    protected MainPostTopicFollower(Parcel in) {
        school = in.readString();
        userId = in.readString();
    }

    public static final Creator<MainPostTopicFollower> CREATOR = new Creator<MainPostTopicFollower>() {
        @Override
        public MainPostTopicFollower createFromParcel(Parcel in) {
            return new MainPostTopicFollower(in);
        }

        @Override
        public MainPostTopicFollower[] newArray(int size) {
            return new MainPostTopicFollower[size];
        }
    };

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(school);
        parcel.writeString(userId);
    }
}
