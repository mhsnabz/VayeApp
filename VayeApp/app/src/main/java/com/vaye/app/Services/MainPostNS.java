package com.vaye.app.Services;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.vaye.app.Interfaces.StringCompletion;
import com.vaye.app.Model.CommentModel;
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
    public void setCommentLikeNotification(MainPostModel postModel , CommentModel commentModel , CurrentUser currentUser , String type , String  text
    ){
        if (commentModel.getSenderUid().equals(currentUser.getUid())){
            return;
        }else{
            if (!postModel.getSilent().contains(currentUser.getUid())){
                String notificationId = String.valueOf(Calendar.getInstance().getTimeInMillis());
                DocumentReference ref = FirebaseFirestore.getInstance().collection("user")
                        .document(commentModel.getSenderUid()).collection("notification")
                        .document(notificationId);
                ref.set(map(currentUser,notificationId , postModel.getPostId(),text , type) , SetOptions.merge());
            }
        }
    }
    public void setNewCommentNotification(MainPostModel postModel ,  CurrentUser currentUser , String type , String  text
    ){
        if (postModel.getSenderUid().equals(currentUser.getUid())){
            return;
        }else{
            if (!postModel.getSilent().contains(currentUser.getUid())){
                String notificationId = String.valueOf(Calendar.getInstance().getTimeInMillis());
                DocumentReference ref = FirebaseFirestore.getInstance().collection("user")
                        .document(postModel.getSenderUid()).collection("notification")
                        .document(notificationId);
                ref.set(map(currentUser,notificationId , postModel.getPostId(),text , type) , SetOptions.merge());
            }
        }
    }

    public void setMentionedCommentNotificaiton(String username , CurrentUser currentUser,MainPostModel post , String type , String text){
        UserService.shared().getOthUserIdByMention(username, new StringCompletion() {
            @Override
            public void getString(String otherUserUid) {
                if (otherUserUid!=null){
                    if (otherUserUid.equals(currentUser.getUid())){
                        return;
                    }else{
                        if (!post.getSilent().contains(post.getSenderUid())) {
                            if (!currentUser.getSlient().contains(otherUserUid)){
                                String notificaitonId = String.valueOf(Calendar.getInstance().getTimeInMillis());
                                DocumentReference ref = FirebaseFirestore.getInstance().collection("user")
                                        .document(otherUserUid).collection("notification")
                                        .document(notificaitonId);
                                ref.set(map(currentUser , notificaitonId , post.getPostId() , text , type) , SetOptions.merge());
                            }
                        }
                    }
                }

            }
        });
    }


    private Map<String , Object>  map (CurrentUser currentUser ,String notificationId , String postId, String text , String type){
        Map<String , Object> map = new HashMap<>();
        map.put("type",type);
        map.put("text",text);
        map.put("senderUid",currentUser.getUid());
        map.put("time", FieldValue.serverTimestamp());
        map.put("senderImage",currentUser.getThumb_image());
        map.put("not_id",notificationId);
        map.put("isRead",false);
        map.put("username",currentUser.getUsername());
        map.put("postId",postId);
        map.put("senderName",currentUser.getName());
        map.put("lessonName",null);
        return map;
    }


}
