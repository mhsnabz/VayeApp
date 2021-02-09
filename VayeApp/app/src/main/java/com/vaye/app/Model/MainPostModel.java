package com.vaye.app.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;

public class MainPostModel implements Parcelable {

    String senderName,text,senderUid,link,locationName,postId,empty,value,postType,id,username,thumb_image,type;
    int comment;
    Timestamp postTime;
    ArrayList<String> likes,dislike,data,thumbData,silent;
    GeoPoint geoPoint;
    UnifiedNativeAd nativeAd;


    public MainPostModel() {
    }

    public MainPostModel(String senderName, String text, String senderUid, String link, String locationName, String postId, String empty, String value, String postType, String id, String username, String thumb_image, String type, int comment, Timestamp postTime, ArrayList<String> likes, ArrayList<String> dislike, ArrayList<String> data, ArrayList<String> thumbData, ArrayList<String> silent, GeoPoint geoPoint, UnifiedNativeAd nativeAd) {
        this.senderName = senderName;
        this.text = text;
        this.senderUid = senderUid;
        this.link = link;
        this.locationName = locationName;
        this.postId = postId;
        this.empty = empty;
        this.value = value;
        this.postType = postType;
        this.id = id;
        this.username = username;
        this.thumb_image = thumb_image;
        this.type = type;
        this.comment = comment;

        this.postTime = postTime;
        this.likes = likes;
        this.dislike = dislike;
        this.data = data;
        this.thumbData = thumbData;
        this.silent = silent;
        this.geoPoint = geoPoint;
        this.nativeAd = nativeAd;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    protected MainPostModel(Parcel in) {
        senderName = in.readString();
        text = in.readString();
        senderUid = in.readString();
        link = in.readString();
        locationName = in.readString();
        postId = in.readString();
        empty = in.readString();
        value = in.readString();
        postType = in.readString();
        id = in.readString();
        username = in.readString();
        thumb_image = in.readString();
        comment = in.readInt();

        postTime = in.readParcelable(Timestamp.class.getClassLoader());
        likes = in.createStringArrayList();
        dislike = in.createStringArrayList();
        data = in.createStringArrayList();
        thumbData = in.createStringArrayList();
        silent = in.createStringArrayList();
    }

    public static final Creator<MainPostModel> CREATOR = new Creator<MainPostModel>() {
        @Override
        public MainPostModel createFromParcel(Parcel in) {
            return new MainPostModel(in);
        }

        @Override
        public MainPostModel[] newArray(int size) {
            return new MainPostModel[size];
        }
    };

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

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getEmpty() {
        return empty;
    }

    public void setEmpty(String empty) {
        this.empty = empty;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getPostType() {
        return postType;
    }

    public void setPostType(String postType) {
        this.postType = postType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getThumb_image() {
        return thumb_image;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
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

    public ArrayList<String> getLikes() {
        return likes;
    }

    public void setLikes(ArrayList<String> likes) {
        this.likes = likes;
    }

    public ArrayList<String> getDislike() {
        return dislike;
    }

    public void setDislike(ArrayList<String> dislike) {
        this.dislike = dislike;
    }

    public ArrayList<String> getData() {
        return data;
    }

    public void setData(ArrayList<String> data) {
        this.data = data;
    }

    public ArrayList<String> getThumbData() {
        return thumbData;
    }

    public void setThumbData(ArrayList<String> thumbData) {
        this.thumbData = thumbData;
    }

    public ArrayList<String> getSilent() {
        return silent;
    }

    public void setSilent(ArrayList<String> silent) {
        this.silent = silent;
    }

    public GeoPoint getGeoPoint() {
        return geoPoint;
    }

    public void setGeoPoint(GeoPoint geoPoint) {
        this.geoPoint = geoPoint;
    }

    public UnifiedNativeAd getNativeAd() {
        return nativeAd;
    }

    public void setNativeAd(UnifiedNativeAd nativeAd) {
        this.nativeAd = nativeAd;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(senderName);
        parcel.writeString(text);
        parcel.writeString(senderUid);
        parcel.writeString(link);
        parcel.writeString(locationName);
        parcel.writeString(postId);
        parcel.writeString(empty);
        parcel.writeString(value);
        parcel.writeString(postType);
        parcel.writeString(id);
        parcel.writeString(username);
        parcel.writeString(thumb_image);
        parcel.writeInt(comment);

        parcel.writeParcelable(postTime, i);
        parcel.writeStringList(likes);
        parcel.writeStringList(dislike);
        parcel.writeStringList(data);
        parcel.writeStringList(thumbData);
        parcel.writeStringList(silent);
    }
}
