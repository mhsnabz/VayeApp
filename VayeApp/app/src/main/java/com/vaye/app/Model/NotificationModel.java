package com.vaye.app.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;

public class NotificationModel  {


    String lessonName , not_id,postId,senderImage,senderName,senderUid,type,text,username,postType,targetCommentId,isRead;
    Timestamp time;

    public NotificationModel() {
    }

    public NotificationModel(String lessonName, String not_id, String postId, String senderImage, String senderName, String senderUid, String type, String text, String username, String postType, String targetCommentId, String isRead, Timestamp time) {
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
        this.isRead = isRead;
        this.time = time;
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

    public String getIsRead() {
        return isRead;
    }

    public void setIsRead(String isRead) {
        this.isRead = isRead;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }
}
