package com.vaye.app.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class CurrentUser implements Parcelable {

    String email  , bolum_key , name,number,priority,profileImage,thumb_image,schoolName,short_school,bolum,fakulte,uid,username,linkedin,instagram,twitter,github;
    int totalBadge ;
    Boolean allowRequest,comment,mention,like,follow,lessonNotices;
     ArrayList<String> slient,friendList,slientChatUser,blockList,blockByOtherUser;
    String tokenID;

    public CurrentUser(String email, String bolum_key, String name, String number, String priority, String profileImage, String thumb_image, String schoolName, String short_school, String bolum, String fakulte, String uid, String username, String linkedin, String instagram, String twitter, String github, int totalBadge, Boolean allowRequest, Boolean comment, Boolean mention, Boolean like, Boolean follow, Boolean lessonNotices, ArrayList<String> slient, ArrayList<String> friendList, ArrayList<String> slientChatUser, ArrayList<String> blockList, ArrayList<String> blockByOtherUser, String tokenID) {
        this.email = email;
        this.bolum_key = bolum_key;
        this.name = name;
        this.number = number;
        this.priority = priority;
        this.profileImage = profileImage;
        this.thumb_image = thumb_image;
        this.schoolName = schoolName;
        this.short_school = short_school;
        this.bolum = bolum;
        this.fakulte = fakulte;
        this.uid = uid;
        this.username = username;
        this.linkedin = linkedin;
        this.instagram = instagram;
        this.twitter = twitter;
        this.github = github;
        this.totalBadge = totalBadge;
        this.allowRequest = allowRequest;
        this.comment = comment;
        this.mention = mention;
        this.like = like;
        this.follow = follow;
        this.lessonNotices = lessonNotices;
        this.slient = slient;
        this.friendList = friendList;
        this.slientChatUser = slientChatUser;
        this.blockList = blockList;
        this.blockByOtherUser = blockByOtherUser;
        this.tokenID = tokenID;
    }

    public CurrentUser() {
    }

    protected CurrentUser(Parcel in) {
        email = in.readString();
        bolum_key = in.readString();
        name = in.readString();
        number = in.readString();
        priority = in.readString();
        profileImage = in.readString();
        thumb_image = in.readString();
        schoolName = in.readString();
        short_school = in.readString();
        bolum = in.readString();
        fakulte = in.readString();
        uid = in.readString();
        username = in.readString();
        linkedin = in.readString();
        instagram = in.readString();
        twitter = in.readString();
        github = in.readString();
        totalBadge = in.readInt();
        byte tmpAllowRequest = in.readByte();
        allowRequest = tmpAllowRequest == 0 ? null : tmpAllowRequest == 1;
        byte tmpComment = in.readByte();
        comment = tmpComment == 0 ? null : tmpComment == 1;
        byte tmpMention = in.readByte();
        mention = tmpMention == 0 ? null : tmpMention == 1;
        byte tmpLike = in.readByte();
        like = tmpLike == 0 ? null : tmpLike == 1;
        byte tmpFollow = in.readByte();
        follow = tmpFollow == 0 ? null : tmpFollow == 1;
        byte tmpLessonNotices = in.readByte();
        lessonNotices = tmpLessonNotices == 0 ? null : tmpLessonNotices == 1;
        slient = in.createStringArrayList();
        friendList = in.createStringArrayList();
        slientChatUser = in.createStringArrayList();
        blockList = in.createStringArrayList();
        blockByOtherUser = in.createStringArrayList();
        tokenID = in.readString();
    }

    public static final Creator<CurrentUser> CREATOR = new Creator<CurrentUser>() {
        @Override
        public CurrentUser createFromParcel(Parcel in) {
            return new CurrentUser(in);
        }

        @Override
        public CurrentUser[] newArray(int size) {
            return new CurrentUser[size];
        }
    };

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBolum_key() {
        return bolum_key;
    }

    public void setBolum_key(String bolum_key) {
        this.bolum_key = bolum_key;
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

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getThumb_image() {
        return thumb_image;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getShort_school() {
        return short_school;
    }

    public void setShort_school(String short_school) {
        this.short_school = short_school;
    }

    public String getBolum() {
        return bolum;
    }

    public void setBolum(String bolum) {
        this.bolum = bolum;
    }

    public String getFakulte() {
        return fakulte;
    }

    public void setFakulte(String fakulte) {
        this.fakulte = fakulte;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLinkedin() {
        return linkedin;
    }

    public void setLinkedin(String linkedin) {
        this.linkedin = linkedin;
    }

    public String getInstagram() {
        return instagram;
    }

    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public String getGithub() {
        return github;
    }

    public void setGithub(String github) {
        this.github = github;
    }

    public int getTotalBadge() {
        return totalBadge;
    }

    public void setTotalBadge(int totalBadge) {
        this.totalBadge = totalBadge;
    }

    public Boolean getAllowRequest() {
        return allowRequest;
    }

    public void setAllowRequest(Boolean allowRequest) {
        this.allowRequest = allowRequest;
    }

    public Boolean getComment() {
        return comment;
    }

    public void setComment(Boolean comment) {
        this.comment = comment;
    }

    public Boolean getMention() {
        return mention;
    }

    public void setMention(Boolean mention) {
        this.mention = mention;
    }

    public Boolean getLike() {
        return like;
    }

    public void setLike(Boolean like) {
        this.like = like;
    }

    public Boolean getFollow() {
        return follow;
    }

    public void setFollow(Boolean follow) {
        this.follow = follow;
    }

    public Boolean getLessonNotices() {
        return lessonNotices;
    }

    public void setLessonNotices(Boolean lessonNotices) {
        this.lessonNotices = lessonNotices;
    }

    public ArrayList<String> getSlient() {
        return slient;
    }

    public void setSlient(ArrayList<String> slient) {
        this.slient = slient;
    }

    public ArrayList<String> getFriendList() {
        return friendList;
    }

    public void setFriendList(ArrayList<String> friendList) {
        this.friendList = friendList;
    }

    public ArrayList<String> getSlientChatUser() {
        return slientChatUser;
    }

    public void setSlientChatUser(ArrayList<String> slientChatUser) {
        this.slientChatUser = slientChatUser;
    }

    public ArrayList<String> getBlockList() {
        return blockList;
    }

    public void setBlockList(ArrayList<String> blockList) {
        this.blockList = blockList;
    }

    public ArrayList<String> getBlockByOtherUser() {
        return blockByOtherUser;
    }

    public void setBlockByOtherUser(ArrayList<String> blockByOtherUser) {
        this.blockByOtherUser = blockByOtherUser;
    }

    public String getTokenID() {
        return tokenID;
    }

    public void setTokenID(String tokenID) {
        this.tokenID = tokenID;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(email);
        parcel.writeString(bolum_key);
        parcel.writeString(name);
        parcel.writeString(number);
        parcel.writeString(priority);
        parcel.writeString(profileImage);
        parcel.writeString(thumb_image);
        parcel.writeString(schoolName);
        parcel.writeString(short_school);
        parcel.writeString(bolum);
        parcel.writeString(fakulte);
        parcel.writeString(uid);
        parcel.writeString(username);
        parcel.writeString(linkedin);
        parcel.writeString(instagram);
        parcel.writeString(twitter);
        parcel.writeString(github);
        parcel.writeInt(totalBadge);
        parcel.writeByte((byte) (allowRequest == null ? 0 : allowRequest ? 1 : 2));
        parcel.writeByte((byte) (comment == null ? 0 : comment ? 1 : 2));
        parcel.writeByte((byte) (mention == null ? 0 : mention ? 1 : 2));
        parcel.writeByte((byte) (like == null ? 0 : like ? 1 : 2));
        parcel.writeByte((byte) (follow == null ? 0 : follow ? 1 : 2));
        parcel.writeByte((byte) (lessonNotices == null ? 0 : lessonNotices ? 1 : 2));
        parcel.writeStringList(slient);
        parcel.writeStringList(friendList);
        parcel.writeStringList(slientChatUser);
        parcel.writeStringList(blockList);
        parcel.writeStringList(blockByOtherUser);
        parcel.writeString(tokenID);
    }
}
