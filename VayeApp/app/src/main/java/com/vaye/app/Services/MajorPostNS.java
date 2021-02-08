package com.vaye.app.Services;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.vaye.app.Model.CurrentUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MajorPostNS {
    private static final MajorPostNS instance = new MajorPostNS();
    public static MajorPostNS shared() {
        return instance;
    }

    public void sendNewPostNotification(CurrentUser currentUser, String notificationId, String lessonName , String text , String type , String postId){
        ArrayList<String> notificationGetter = new ArrayList<>();
        CollectionReference ref = FirebaseFirestore
                .getInstance().collection(currentUser.getShort_school())
                .document("lesson")
                .collection(currentUser.getBolum())
                .document(lessonName)
                .collection("notification_getter");
        ref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().isEmpty()){

                    }else{
                        for (DocumentSnapshot id : task.getResult().getDocuments()){
                            notificationGetter.add(id.getId());
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
                            map.put("lessonName",lessonName);

                                if (!id.getId().equals(currentUser.getUid())){
                                    DocumentReference ref1 = FirebaseFirestore.getInstance().collection("user")
                                            .document(id.getId())
                                            .collection("notification")
                                            .document(notificationId);
                                    ref1.set(map , SetOptions.merge());
                                }

                        }

                    }
                }
            }
        });
    }

}
