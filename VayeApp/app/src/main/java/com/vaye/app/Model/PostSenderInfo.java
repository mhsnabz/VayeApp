package com.vaye.app.Model;

public class PostSenderInfo {
    String postId,senderUid,post_ID;

    public PostSenderInfo() {
    }

    public PostSenderInfo(String postId, String senderUid, String post_ID) {
        this.postId = postId;
        this.senderUid = senderUid;
        this.post_ID = post_ID;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getSenderUid() {
        return senderUid;
    }

    public void setSenderUid(String senderUid) {
        this.senderUid = senderUid;
    }

    public String getPostID() {
        return post_ID;
    }

    public void setPostID(String post_ID) {
        this.post_ID = post_ID;
    }
}
