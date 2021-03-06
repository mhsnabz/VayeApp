package com.vaye.app.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;

public class NoticesMainModel implements Parcelable {
    String clupName,senderName,text,senderUid,postId,link,id,thumb_image,username;
    ArrayList<String> thumbData,data,silent,favori,dislike,likes;
    Long post_ID;
    int comment;
    Timestamp postTime;
    UnifiedNativeAd nativeAd;
    String empty, type;
    String lesson_key;

    public NoticesMainModel() {
    }
    public NoticesMainModel(String clupName, String senderName, String text, String senderUid, String postId, String link, String id, String thumb_image, String username, ArrayList<String> thumbData, ArrayList<String> data, ArrayList<String> silent, ArrayList<String> favori, ArrayList<String> dislike, ArrayList<String> likes, Long post_ID, int comment, Timestamp postTime, UnifiedNativeAd nativeAd, String empty, String type, String lesson_key) {
        this.clupName = clupName;
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
        this.nativeAd = nativeAd;
        this.empty = empty;
        this.type = type;
        this.lesson_key = lesson_key;
    }

    protected NoticesMainModel(Parcel in) {
        clupName = in.readString();
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
        type = in.readString();
        lesson_key = in.readString();
    }

    public static final Creator<NoticesMainModel> CREATOR = new Creator<NoticesMainModel>() {
        @Override
        public NoticesMainModel createFromParcel(Parcel in) {
            return new NoticesMainModel(in);
        }

        @Override
        public NoticesMainModel[] newArray(int size) {
            return new NoticesMainModel[size];
        }
    };

    public String getClupName() {
        return clupName;
    }

    public void setClupName(String clupName) {
        this.clupName = clupName;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSenderUid() {
        return senderUid;
    }

    public void setSenderUid(String senderUid) {
        this.senderUid = senderUid;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getThumb_image() {
        return thumb_image;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ArrayList<String> getThumbData() {
        return thumbData;
    }

    public void setThumbData(ArrayList<String> thumbData) {
        this.thumbData = thumbData;
    }

    public ArrayList<String> getData() {
        return data;
    }

    public void setData(ArrayList<String> data) {
        this.data = data;
    }

    public ArrayList<String> getSilent() {
        return silent;
    }

    public void setSilent(ArrayList<String> silent) {
        this.silent = silent;
    }

    public ArrayList<String> getFavori() {
        return favori;
    }

    public void setFavori(ArrayList<String> favori) {
        this.favori = favori;
    }

    public ArrayList<String> getDislike() {
        return dislike;
    }

    public void setDislike(ArrayList<String> dislike) {
        this.dislike = dislike;
    }

    public ArrayList<String> getLikes() {
        return likes;
    }

    public void setLikes(ArrayList<String> likes) {
        this.likes = likes;
    }

    public Long getPost_ID() {
        return post_ID;
    }

    public void setPost_ID(Long post_ID) {
        this.post_ID = post_ID;
    }

    public int getComment() {
        return comment;
    }

    public void setComment(int comment) {
        this.comment = comment;
    }

    public Timestamp getPostTime() {
        return postTime;
    }

    public void setPostTime(Timestamp postTime) {
        this.postTime = postTime;
    }

    public UnifiedNativeAd getNativeAd() {
        return nativeAd;
    }

    public void setNativeAd(UnifiedNativeAd nativeAd) {
        this.nativeAd = nativeAd;
    }

    public String getEmpty() {
        return empty;
    }

    public void setEmpty(String empty) {
        this.empty = empty;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLesson_key() {
        return lesson_key;
    }

    public void setLesson_key(String lesson_key) {
        this.lesson_key = lesson_key;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(clupName);
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
        parcel.writeString(type);
        parcel.writeString(lesson_key);
    }
}
