package com.vaye.app.Services;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.vaye.app.Interfaces.StringCompletion;
import com.vaye.app.Model.CommentModel;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.MainPostModel;
import com.vaye.app.Model.NoticesMainModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class SchoolPostNS {
    String TAG = "SchoolPostNS";
    private static final SchoolPostNS instance = new SchoolPostNS();
    public static SchoolPostNS shared() {
        return instance;
    }

    public void setNewNoticesNotification(ArrayList<String > notificationGetter , CurrentUser currentUser ,String clupName,String notificaitonId , String postId , String text , String  type){
        for (String getterUid : notificationGetter){
            if (getterUid.equals(currentUser.getUid())){
                return;
            }else {
                DocumentReference ref1 = FirebaseFirestore.getInstance().collection("user")
                        .document(getterUid)
                        .collection("notification")
                        .document(notificaitonId);
                ref1.set(map(currentUser , notificaitonId ,postId , text , type , clupName) , SetOptions.merge());
            }
        }

    }
    public void setMentionedNotification(String username , CurrentUser currentUser, String postId , String type , String text , String clupName){
        UserService.shared().getOthUserIdByMention(username, new StringCompletion() {
            @Override
            public void getString(String otherUserUid) {
                if (otherUserUid!=null){
                    if (otherUserUid.equals(currentUser.getUid())){
                        Log.d(TAG, "getString: "+otherUserUid);
                        return;
                    }else{

                        if (!currentUser.getSlient().contains(otherUserUid)){
                            Log.d(TAG, "getString: "+"!currentUser.getSlient().contains(otherUserUid)");
                            if (!otherUserUid.equals(currentUser.getUid())){
                                Log.d(TAG, "getString: "+"!otherUserUid.equals(currentUser.getUid())");
                                String notificaitonId = String.valueOf(Calendar.getInstance().getTimeInMillis()) ;

                                DocumentReference ref = FirebaseFirestore.getInstance().collection("user")
                                        .document(otherUserUid).collection("notification")
                                        .document(notificaitonId);
                                        ref.set(map(currentUser , notificaitonId , postId , text ,type , clupName)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Log.d(TAG, "getString: "+"notification sent");
                                            }
                                        });
                            }
                        }


                    }
                }

            }
        });
    }
    public void sendLikeNotification(NoticesMainModel post ,  CurrentUser currentUser , String type , String  text ){
        if (post.getSenderUid().equals(currentUser.getUid())){
            return;
        }else{
            if (!post.getSilent().contains(post.getSenderUid())){
                String notificationId = String.valueOf(Calendar.getInstance().getTimeInMillis());
                DocumentReference ref = FirebaseFirestore.getInstance().collection("user")
                        .document(post.getSenderUid()).collection("notification").document(notificationId);
                ref.set(map(currentUser , notificationId , post.getPostId(),text,type,post.getClupName()));
            }
        }
    }
    private Map<String , Object> map (CurrentUser currentUser , String notificationId , String postId, String text , String type , String clupName){
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
        map.put("lessonName",clupName);
        return map;
    }
}
