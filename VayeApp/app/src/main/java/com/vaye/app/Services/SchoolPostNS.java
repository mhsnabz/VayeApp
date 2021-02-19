package com.vaye.app.Services;

public class SchoolPostNS {
    private static final SchoolPostNS instance = new SchoolPostNS();
    public static SchoolPostNS shared() {
        return instance;
    }
}
