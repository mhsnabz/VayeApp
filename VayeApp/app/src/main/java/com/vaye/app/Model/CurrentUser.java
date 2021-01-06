package com.vaye.app.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class CurrentUser implements Parcelable {

    String email , name,number,priority,profileImage,thumb_image,schoolName,short_school,bolum,fakulte,uid,username,linkedin,instagram,twitter,github;
    int totalBadge ;
    Boolean allowRequest,comment,mention,like,follow,lessonNotices;
   ArrayList<String> slientUser,friendList,slientChatUser;

    public CurrentUser() {
    }

    public CurrentUser(String email, String name, String number, String priority, String profileImage, String thumb_image, String schoolName, String short_school, String bolum, String fakulte, String uid, String username, String linkedin, String instagram, String twitter, String github, int totalBadge, Boolean allowRequest, Boolean comment, Boolean mention, Boolean like, Boolean follow, Boolean lessonNotices, ArrayList<String> slientUser, ArrayList<String> friendList, ArrayList<String> slientChatUser) {
        this.email = email;
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
        this.slientUser = slientUser;
        this.friendList = friendList;
        this.slientChatUser = slientChatUser;
    }

    protected CurrentUser(Parcel in) {
        email = in.readString();
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
        slientUser = in.createStringArrayList();
        friendList = in.createStringArrayList();
        slientChatUser = in.createStringArrayList();
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

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public String getPriority() {
        return priority;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public String getThumb_image() {
        return thumb_image;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public String getShort_school() {
        return short_school;
    }

    public String getBolum() {
        return bolum;
    }

    public String getFakulte() {
        return fakulte;
    }

    public String getUid() {
        return uid;
    }

    public String getUsername() {
        return username;
    }

    public String getLinkedin() {
        return linkedin;
    }

    public String getInstagram() {
        return instagram;
    }

    public String getTwitter() {
        return twitter;
    }

    public String getGithub() {
        return github;
    }

    public int getTotalBadge() {
        return totalBadge;
    }

    public Boolean getAllowRequest() {
        return allowRequest;
    }

    public Boolean getComment() {
        return comment;
    }

    public Boolean getMention() {
        return mention;
    }

    public Boolean getLike() {
        return like;
    }

    public Boolean getFollow() {
        return follow;
    }

    public Boolean getLessonNotices() {
        return lessonNotices;
    }

    public ArrayList<String> getSlientUser() {
        return slientUser;
    }

    public ArrayList<String> getFriendList() {
        return friendList;
    }

    public ArrayList<String> getSlientChatUser() {
        return slientChatUser;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public void setShort_school(String short_school) {
        this.short_school = short_school;
    }

    public void setBolum(String bolum) {
        this.bolum = bolum;
    }

    public void setFakulte(String fakulte) {
        this.fakulte = fakulte;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setLinkedin(String linkedin) {
        this.linkedin = linkedin;
    }

    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public void setGithub(String github) {
        this.github = github;
    }

    public void setTotalBadge(int totalBadge) {
        this.totalBadge = totalBadge;
    }

    public void setAllowRequest(Boolean allowRequest) {
        this.allowRequest = allowRequest;
    }

    public void setComment(Boolean comment) {
        this.comment = comment;
    }

    public void setMention(Boolean mention) {
        this.mention = mention;
    }

    public void setLike(Boolean like) {
        this.like = like;
    }

    public void setFollow(Boolean follow) {
        this.follow = follow;
    }

    public void setLessonNotices(Boolean lessonNotices) {
        this.lessonNotices = lessonNotices;
    }

    public void setSlientUser(ArrayList<String> slientUser) {
        this.slientUser = slientUser;
    }

    public void setFriendList(ArrayList<String> friendList) {
        this.friendList = friendList;
    }

    public void setSlientChatUser(ArrayList<String> slientChatUser) {
        this.slientChatUser = slientChatUser;
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
        parcel.writeStringList(slientUser);
        parcel.writeStringList(friendList);
        parcel.writeStringList(slientChatUser);
    }
}
