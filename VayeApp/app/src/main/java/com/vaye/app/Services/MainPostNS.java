package com.vaye.app.Services;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.MainPostModel;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MainPostNS {
    private static final MainPostNS instance = new MainPostNS();
    public static MainPostNS shared() {
        return instance;
    }

    public void setPostLikeNotification(CurrentUser currentUser , MainPostModel post , String  text , String type){
        if (post.getSenderUid().equals(currentUser.getUid())){
            return;
        }else{
            if (!post.getSilent().contains(post.getSenderUid())){
                String notificationId = String.valueOf(Calendar.getInstance().getTimeInMillis());
                DocumentReference ref = FirebaseFirestore.getInstance().collection("user")
                        .document(post.getSenderUid()).collection("notification").document(notificationId);
                Map<String , Object> map = new HashMap<>();
                map.put("type",type);
                map.put("text",text);
                map.put("senderUid",currentUser.getUid());
                map.put("time", FieldValue.serverTimestamp());
                map.put("senderImage",currentUser.getThumb_image());
                map.put("not_id",notificationId);
                map.put("isRead",false);
                map.put("username",currentUser.getUsername());
                map.put("postId",post.getPostId());
                map.put("senderName",currentUser.getName());
                map.put("lessonName",null);
                ref.set(map , SetOptions.merge());
            }
        }
    }

}
