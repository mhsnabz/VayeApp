package com.vaye.app.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;

public class ChatListModel implements Parcelable {

    String name  , thumbImage,lastMsg,username,type,uid;
    Timestamp time;
    Boolean isOnline;
    int badgeCount;

    public ChatListModel(String name, String thumbImage, String lastMsg, String username, String type, String uid, Timestamp time, Boolean isOnline, int badgeCount) {
        this.name = name;
        this.thumbImage = thumbImage;
        this.lastMsg = lastMsg;
        this.username = username;
        this.type = type;
        this.uid = uid;
        this.time = time;
        this.isOnline = isOnline;
        this.badgeCount = badgeCount;
    }

    public ChatListModel() {
    }

    protected ChatListModel(Parcel in) {
        name = in.readString();
        thumbImage = in.readString();
        lastMsg = in.readString();
        username = in.readString();
        type = in.readString();
        uid = in.readString();
        time = in.readParcelable(Timestamp.class.getClassLoader());
        byte tmpIsOnline = in.readByte();
        isOnline = tmpIsOnline == 0 ? null : tmpIsOnline == 1;
        badgeCount = in.readInt();
    }

    public static final Creator<ChatListModel> CREATOR = new Creator<ChatListModel>() {
        @Override
        public ChatListModel createFromParcel(Parcel in) {
            return new ChatListModel(in);
        }

        @Override
        public ChatListModel[] newArray(int size) {
            return new ChatListModel[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThumbImage() {
        return thumbImage;
    }

    public void setThumbImage(String thumbImage) {
        this.thumbImage = thumbImage;
    }

    public String getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public Boolean getOnline() {
        return isOnline;
    }

    public void setOnline(Boolean online) {
        isOnline = online;
    }

    public int getBadgeCount() {
        return badgeCount;
    }

    public void setBadgeCount(int badgeCount) {
        this.badgeCount = badgeCount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(thumbImage);
        parcel.writeString(lastMsg);
        parcel.writeString(username);
        parcel.writeString(type);
        parcel.writeString(uid);
        parcel.writeParcelable(time, i);
        parcel.writeByte((byte) (isOnline == null ? 0 : isOnline ? 1 : 2));
        parcel.writeInt(badgeCount);
    }
}
