package com.vaye.app.Model;

import java.util.ArrayList;

public class SchoolPostNotificationModel {
    ArrayList<String> followers;
    String name;

    public SchoolPostNotificationModel(ArrayList<String> followers, String name) {
        this.followers = followers;
        this.name = name;
    }

    public SchoolPostNotificationModel() {
    }

    public ArrayList<String> getFollowers() {
        return followers;
    }

    public void setFollowers(ArrayList<String> followers) {
        this.followers = followers;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
