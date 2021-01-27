package com.vaye.app.Services;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.OtherUser;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class NotificaitonService {

    private static final NotificaitonService instance = new NotificaitonService();
    public static NotificaitonService shared() {
        return instance;
    }


    public void start_following_you(CurrentUser currentUser , OtherUser otherUser , String  text , String type , TrueFalse<Boolean> completion){
        String  notId = String.valueOf(Calendar.getInstance().getTimeInMillis());
        DocumentReference reference = FirebaseFirestore.getInstance()
                .collection("user")
                .document(otherUser.getUid())
                .collection("notification")
                .document(notId);
        Map<String , Object> map = new HashMap<>();

        map.put("type",type);
        map.put("text",text);
        map.put("senderUid",currentUser.getUid());
        map.put("time",currentUser.getUid());
        map.put("senderImage",currentUser.getUid());
        map.put("not_id",currentUser.getUid());
        map.put("isRead",currentUser.getUid());
        map.put("username",currentUser.getUid());
        map.put("postId","currentUser.getUid()");
        map.put("senderName",currentUser.getUid());
        map.put("lessonName","currentUser.getUid()");
        reference.set(map , SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    completion.callBack(true);
                }
            }
        });
    }
}
