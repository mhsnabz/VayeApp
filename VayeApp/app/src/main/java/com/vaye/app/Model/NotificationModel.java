package com.vaye.app.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;

public class NotificationModel implements Parcelable {

    Boolean isRead;
    String lessonName , not_id,postId,senderImage,senderName,senderUid,type,text,username,postType,targetCommentId;
    Timestamp time;

    public NotificationModel() {
    }

    public NotificationModel(Boolean isRead, String lessonName, String not_id, String postId, String senderImage, String senderName, String senderUid, String type, String text, String username, String postType, String targetCommentId, Timestamp time) {
        this.isRead = isRead;
        this.lessonName = lessonName;
        this.not_id = not_id;
        this.postId = postId;
        this.senderImage = senderImage;
        this.senderName = senderName;
        this.senderUid = senderUid;
        this.type = type;
        this.text = text;
        this.username = username;
        this.postType = postType;
        this.targetCommentId = targetCommentId;
        this.time = time;
    }

    protected NotificationModel(Parcel in) {
        byte tmpIsRead = in.readByte();
        isRead = tmpIsRead == 0 ? null : tmpIsRead == 1;
        lessonName = in.readString();
        not_id = in.readString();
        postId = in.readString();
        senderImage = in.readString();
        senderName = in.readString();
        senderUid = in.readString();
        type = in.readString();
        text = in.readString();
        username = in.readString();
        postType = in.readString();
        targetCommentId = in.readString();
        time = in.readParcelable(Timestamp.class.getClassLoader());
    }

    public static final Creator<NotificationModel> CREATOR = new Creator<NotificationModel>() {
        @Override
        public NotificationModel createFromParcel(Parcel in) {
            return new NotificationModel(in);
        }

        @Override
        public NotificationModel[] newArray(int size) {
            return new NotificationModel[size];
        }
    };

    public Boolean getRead() {
        return isRead;
    }

    public void setRead(Boolean read) {
        isRead = read;
    }

    public String getLessonName() {
        return lessonName;
    }

    public void setLessonName(String lessonName) {
        this.lessonName = lessonName;
    }

    public String getNot_id() {
        return not_id;
    }

    public void setNot_id(String not_id) {
        this.not_id = not_id;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getSenderImage() {
        return senderImage;
    }

    public void setSenderImage(String senderImage) {
        this.senderImage = senderImage;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderUid() {
        return senderUid;
    }

    public void setSenderUid(String senderUid) {
        this.senderUid = senderUid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPostType() {
        return postType;
    }

    public void setPostType(String postType) {
        this.postType = postType;
    }

    public String getTargetCommentId() {
        return targetCommentId;
    }

    public void setTargetCommentId(String targetCommentId) {
        this.targetCommentId = targetCommentId;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeByte((byte) (isRead == null ? 0 : isRead ? 1 : 2));
        parcel.writeString(lessonName);
        parcel.writeString(not_id);
        parcel.writeString(postId);
        parcel.writeString(senderImage);
        parcel.writeString(senderName);
        parcel.writeString(senderUid);
        parcel.writeString(type);
        parcel.writeString(text);
        parcel.writeString(username);
        parcel.writeString(postType);
        parcel.writeString(targetCommentId);
        parcel.writeParcelable(time, i);
    }
}
