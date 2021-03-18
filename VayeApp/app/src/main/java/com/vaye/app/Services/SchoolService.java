package com.vaye.app.Services;

public class SchoolService {
    private static final SchoolService instance = new SchoolService();
    public static SchoolService shared() {
        return instance;
    }

}
