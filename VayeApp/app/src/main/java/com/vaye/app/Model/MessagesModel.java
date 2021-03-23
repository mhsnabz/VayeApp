package com.vaye.app.Model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

public class MessagesModel {
    String content , id,type,senderUid,fileName,name;
    GeoPoint geoPoint;
    Timestamp date;
    long time;
    boolean is_read;
    float width , heigth;
    int duration;

    public MessagesModel() {
    }

    public MessagesModel(String content, String id, String type, String senderUid, String fileName, String name, GeoPoint geoPoint, Timestamp date, long time, boolean is_read, float width, float heigth, int duration) {
        this.content = content;
        this.id = id;
        this.type = type;
        this.senderUid = senderUid;
        this.fileName = fileName;
        this.name = name;
        this.geoPoint = geoPoint;
        this.date = date;
        this.time = time;
        this.is_read = is_read;
        this.width = width;
        this.heigth = heigth;
        this.duration = duration;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSenderUid() {
        return senderUid;
    }

    public void setSenderUid(String senderUid) {
        this.senderUid = senderUid;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GeoPoint getGeoPoint() {
        return geoPoint;
    }

    public void setGeoPoint(GeoPoint geoPoint) {
        this.geoPoint = geoPoint;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isIs_read() {
        return is_read;
    }

    public void setIs_read(boolean is_read) {
        this.is_read = is_read;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeigth() {
        return heigth;
    }

    public void setHeigth(float heigth) {
        this.heigth = heigth;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
