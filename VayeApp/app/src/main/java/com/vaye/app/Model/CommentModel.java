package com.vaye.app.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;

import java.util.ArrayList;

public class CommentModel implements Parcelable {

    String senderName , senderUid , username ,comment , senderImage , postId,targetComment,commentId;
    Timestamp time;
    ArrayList<String> likes,replies;

    public CommentModel() {
    }

    public CommentModel(String senderName, String senderUid, String username, String comment, String senderImage, String postId, String targetComment, String commentId, Timestamp time, ArrayList<String> likes, ArrayList<String> replies) {
        this.senderName = senderName;
        this.senderUid = senderUid;
        this.username = username;
        this.comment = comment;
        this.senderImage = senderImage;
        this.postId = postId;
        this.targetComment = targetComment;
        this.commentId = commentId;
        this.time = time;
        this.likes = likes;
        this.replies = replies;
    }

    protected CommentModel(Parcel in) {
        senderName = in.readString();
        senderUid = in.readString();
        username = in.readString();
        comment = in.readString();
        senderImage = in.readString();
        postId = in.readString();
        targetComment = in.readString();
        commentId = in.readString();
        time = in.readParcelable(Timestamp.class.getClassLoader());
        likes = in.createStringArrayList();
        replies = in.createStringArrayList();
    }

    public static final Creator<CommentModel> CREATOR = new Creator<CommentModel>() {
        @Override
        public CommentModel createFromParcel(Parcel in) {
            return new CommentModel(in);
        }

        @Override
        public CommentModel[] newArray(int size) {
            return new CommentModel[size];
        }
    };

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getSenderImage() {
        return senderImage;
    }

    public void setSenderImage(String senderImage) {
        this.senderImage = senderImage;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getTargetComment() {
        return targetComment;
    }

    public void setTargetComment(String targetComment) {
        this.targetComment = targetComment;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public ArrayList<String> getLikes() {
        return likes;
    }

    public void setLikes(ArrayList<String> likes) {
        this.likes = likes;
    }

    public ArrayList<String> getReplies() {
        return replies;
    }

    public void setReplies(ArrayList<String> replies) {
        this.replies = replies;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(senderName);
        parcel.writeString(senderUid);
        parcel.writeString(username);
        parcel.writeString(comment);
        parcel.writeString(senderImage);
        parcel.writeString(postId);
        parcel.writeString(targetComment);
        parcel.writeString(commentId);
        parcel.writeParcelable(time, i);
        parcel.writeStringList(likes);
        parcel.writeStringList(replies);
    }
}
