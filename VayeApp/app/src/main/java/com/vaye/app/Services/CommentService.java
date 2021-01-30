package com.vaye.app.Services;

public class CommentService {
    private static final CommentService instance = new CommentService();
    public static CommentService shared() {
        return instance;
    }



}
