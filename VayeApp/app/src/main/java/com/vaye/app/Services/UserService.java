package com.vaye.app.Services;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.vaye.app.Interfaces.FirestoreService;
import com.vaye.app.Model.CurrentUser;

public class UserService {

    private static final UserService instance = new UserService();
    public static UserService shared() {
        return instance;
    }

    public void getCurrentUser(String uid , FirestoreService  result ){
        DocumentReference ref = FirebaseFirestore.getInstance().collection("user")
                .document(uid);
        ref.get().addOnCompleteListener( new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().exists()){
                        result.onCallback(task.getResult().toObject(CurrentUser.class));
                    }
                }
            }
        }).addOnFailureListener( new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }



}
