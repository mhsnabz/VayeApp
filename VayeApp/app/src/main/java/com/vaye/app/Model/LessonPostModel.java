package com.vaye.app.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.ads.nativead.NativeAd;
import com.google.firebase.Timestamp;

import java.util.ArrayList;

public class LessonPostModel implements Parcelable {
    String lessonName,senderName,text,senderUid,postId,link,id,thumb_image,username;
    ArrayList<String> thumbData,data,silent,favori,dislike,likes;
    Long post_ID;
    int comment;
    Timestamp postTime;
    NativeAd nativeAd;
    String empty;
    public LessonPostModel(Timestamp postTime, NativeAd nativeAd) {
        this.postTime = postTime;
        this.nativeAd = nativeAd;
    }

    public LessonPostModel(String postId, String empty) {
        this.postId = postId;
        this.empty = empty;
    }

    public LessonPostModel(String lessonName, String senderName, String text, String senderUid, String postId, String link, String id, String thumb_image, String username, ArrayList<String> thumbData, ArrayList<String> data, ArrayList<String> silent, ArrayList<String> favori, ArrayList<String> dislike, ArrayList<String> likes, Long post_ID, int comment, Timestamp postTime) {
        this.lessonName = lessonName;
        this.senderName = senderName;
        this.text = text;
        this.senderUid = senderUid;
        this.postId = postId;
        this.link = link;
        this.id = id;
        this.thumb_image = thumb_image;
        this.username = username;
        this.thumbData = thumbData;
        this.data = data;
        this.silent = silent;
        this.favori = favori;
        this.dislike = dislike;
        this.likes = likes;
        this.post_ID = post_ID;
        this.comment = comment;
        this.postTime = postTime;
    }

    public LessonPostModel() {
    }

    protected LessonPostModel(Parcel in) {
        lessonName = in.readString();
        senderName = in.readString();
        text = in.readString();
        senderUid = in.readString();
        postId = in.readString();
        link = in.readString();
        id = in.readString();
        thumb_image = in.readString();
        username = in.readString();
        thumbData = in.createStringArrayList();
        data = in.createStringArrayList();
        silent = in.createStringArrayList();
        favori = in.createStringArrayList();
        dislike = in.createStringArrayList();
        likes = in.createStringArrayList();
        if (in.readByte() == 0) {
            post_ID = null;
        } else {
            post_ID = in.readLong();
        }
        comment = in.readInt();
        postTime = in.readParcelable(Timestamp.class.getClassLoader());
        empty = in.readString();
    }

    public static final Creator<LessonPostModel> CREATOR = new Creator<LessonPostModel>() {
        @Override
        public LessonPostModel createFromParcel(Parcel in) {
            return new LessonPostModel(in);
        }

        @Override
        public LessonPostModel[] newArray(int size) {
            return new LessonPostModel[size];
        }
    };

    public String getLessonName() {
        return lessonName;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getText() {
        return text;
    }

    public String getSenderUid() {
        return senderUid;
    }

    public String getPostId() {
        return postId;
    }

    public String getLink() {
        return link;
    }

    public String getId() {
        return id;
    }

    public String getThumb_image() {
        return thumb_image;
    }

    public String getUsername() {
        return username;
    }

    public ArrayList<String> getThumbData() {
        return thumbData;
    }

    public ArrayList<String> getData() {
        return data;
    }

    public ArrayList<String> getSilent() {
        return silent;
    }

    public ArrayList<String> getFavori() {
        return favori;
    }

    public ArrayList<String> getDislike() {
        return dislike;
    }

    public ArrayList<String> getLikes() {
        return likes;
    }

    public Long getPost_ID() {
        return post_ID;
    }

    public int getComment() {
        return comment;
    }

    public Timestamp getPostTime() {
        return postTime;
    }

    public NativeAd getNativeAd() {
        return nativeAd;
    }

    public String getEmpty() {
        return empty;
    }

    public void setLessonName(String lessonName) {
        this.lessonName = lessonName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setSenderUid(String senderUid) {
        this.senderUid = senderUid;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setThumbData(ArrayList<String> thumbData) {
        this.thumbData = thumbData;
    }

    public void setData(ArrayList<String> data) {
        this.data = data;
    }

    public void setSilent(ArrayList<String> silent) {
        this.silent = silent;
    }

    public void setFavori(ArrayList<String> favori) {
        this.favori = favori;
    }

    public void setDislike(ArrayList<String> dislike) {
        this.dislike = dislike;
    }

    public void setLikes(ArrayList<String> likes) {
        this.likes = likes;
    }

    public void setPost_ID(Long post_ID) {
        this.post_ID = post_ID;
    }

    public void setComment(int comment) {
        this.comment = comment;
    }

    public void setPostTime(Timestamp postTime) {
        this.postTime = postTime;
    }

    public void setNativeAd(NativeAd nativeAd) {
        this.nativeAd = nativeAd;
    }

    public void setEmpty(String empty) {
        this.empty = empty;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(lessonName);
        parcel.writeString(senderName);
        parcel.writeString(text);
        parcel.writeString(senderUid);
        parcel.writeString(postId);
        parcel.writeString(link);
        parcel.writeString(id);
        parcel.writeString(thumb_image);
        parcel.writeString(username);
        parcel.writeStringList(thumbData);
        parcel.writeStringList(data);
        parcel.writeStringList(silent);
        parcel.writeStringList(favori);
        parcel.writeStringList(dislike);
        parcel.writeStringList(likes);
        if (post_ID == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(post_ID);
        }
        parcel.writeInt(comment);
        parcel.writeParcelable(postTime, i);
        parcel.writeString(empty);
    }
}
