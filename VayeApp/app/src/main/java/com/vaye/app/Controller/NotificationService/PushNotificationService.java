package com.vaye.app.Controller.NotificationService;


import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.vaye.app.Interfaces.OtherUserService;
import com.vaye.app.Model.OtherUser;
import com.vaye.app.Services.UserService;

import java.util.HashMap;

public class PushNotificationService {

    private static final PushNotificationService instance = new PushNotificationService();
    public static PushNotificationService shared() {
        return instance;
    }
    String TAG =  "PushNotificationService";
    public void  sendPushNotification(String not_id , String getterUid , OtherUser otherUser , String  target
    , String senderName , String mainText , String type , String senderUid){
        DocumentReference db = FirebaseFirestore.getInstance().collection("notification").document(not_id);
        String title = senderName;
        String text = type + " : "+mainText;
        HashMap<String , Object> map = new HashMap<>();
        map.put("title",title);
        map.put("text",text);
        map.put("senderUid",senderUid);
        map.put("not_id",not_id);
        if (otherUser != null){
            if (otherUser.getTokenID() !=null && !otherUser.getTokenID().isEmpty()){
                map.put("tokenId",otherUser.getTokenID());
            }else{
                return;
            }

            if (target.equals(PushNotificationTarget.like)){
                if (otherUser.getLike()){
                    db.set(map , SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Log.d(TAG, "onComplete: " + "notificaiton send");
                            }
                        }
                    });
                }
            }else if (target.equals(PushNotificationTarget.comment)){
                if (otherUser.getComment()){
                    db.set(map , SetOptions.merge());
                }
            }
            else if (target.equals(PushNotificationTarget.follow)){
                if (otherUser.getFollow()){
                    db.set(map , SetOptions.merge());
                }
            }
            else if (target.equals(PushNotificationTarget.mention)){
                if (otherUser.getMention()){
                    db.set(map , SetOptions.merge());
                }
            }
            else if (target.equals(PushNotificationTarget.newpost_lessonpost)){
                if (otherUser.getLessonNotices()){
                    db.set(map , SetOptions.merge());
                }
            }else{
                return;
            }
        }
        else{
            UserService.shared().otherUser(getterUid, new OtherUserService() {
                @Override
                public void callback(OtherUser otherUser) {
                    if (otherUser != null){
                        if (otherUser.getTokenID() !=null && !otherUser.getTokenID().isEmpty()){
                            map.put("tokenId",otherUser.getTokenID());
                        }else{
                            return;
                        }

                        if (target.equals(PushNotificationTarget.like)){
                            if (otherUser.getLike()){
                                db.set(map , SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            Log.d(TAG, "onComplete: " + "notificaiton send");
                                        }
                                    }
                                });;
                            }
                        }else if (target.equals(PushNotificationTarget.comment)){
                            if (otherUser.getComment()){
                                db.set(map , SetOptions.merge());
                            }
                        }
                        else if (target.equals(PushNotificationTarget.follow)){
                            if (otherUser.getFollow()){
                                db.set(map , SetOptions.merge());
                            }
                        }
                        else if (target.equals(PushNotificationTarget.mention)){
                            if (otherUser.getMention()){
                                db.set(map , SetOptions.merge());
                            }
                        }
                        else if (target.equals(PushNotificationTarget.newpost_lessonpost)){
                            if (otherUser.getLessonNotices()){
                                db.set(map , SetOptions.merge());
                            }
                        }else{
                            return;
                        }
                    }else
                    {
                        return;
                    }
                }
            });
        }

    }
}
