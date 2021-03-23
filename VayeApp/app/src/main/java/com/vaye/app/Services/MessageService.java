package com.vaye.app.Services;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.OtherUser;

import java.util.HashMap;
import java.util.Map;

public class MessageService {
    private static final String TAG = "MessageService";
    private static final MessageService instance = new MessageService();
    public static MessageService shared() {
        return instance;
    }

    public void setCurrentUserOnline(CurrentUser currentUser , OtherUser otherUser , Boolean bool){
        DocumentReference ref =  FirebaseFirestore.getInstance().collection("user")
                .document(currentUser.getUid())
                .collection("msg-list")
                .document(otherUser.getUid());
        Map<String , Object> map = new HashMap<>();
        map.put("isOnline",bool);
        ref.set(map, SetOptions.merge());
    }
    public void deleteBadge(CurrentUser currentUser , OtherUser otherUser){
        Query deleteBadgeDb = FirebaseFirestore.getInstance().collection("user")
                .document(currentUser.getUid())
                .collection("msg-list")
                .document(otherUser.getUid()).collection("badgeCount").whereEqualTo("badge","badge");
        CollectionReference dbc = FirebaseFirestore.getInstance().collection("user")
                .document(currentUser.getUid())
                .collection("msg-list")
                .document(otherUser.getUid()).collection("badgeCount");
        deleteBadgeDb.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    if (!task.getResult().getDocuments().isEmpty()){
                        for (DocumentSnapshot item : task.getResult().getDocuments()){
                            dbc.document(item.getId()).delete();
                        }
                    }
                }
            }
        });

    }
}
