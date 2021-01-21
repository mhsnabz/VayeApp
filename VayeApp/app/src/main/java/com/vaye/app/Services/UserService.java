package com.vaye.app.Services;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kongzue.dialog.v3.WaitDialog;
import com.vaye.app.Interfaces.CurrentUserService;
import com.vaye.app.Interfaces.OtherUserService;
import com.vaye.app.Interfaces.TaskUserHandler;
import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.OtherUser;
import com.vaye.app.Model.TaskUser;

public class UserService {

    private static final UserService instance = new UserService();
    public static UserService shared() {
        return instance;
    }

    public void getCurrentUser(String uid , CurrentUserService result ){
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


    public void getTaskUser(String uid , TaskUserHandler result){
        DocumentReference ref = FirebaseFirestore.getInstance().collection("task-user")
                .document(uid);
        ref.get().addOnCompleteListener( new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().exists()){
                        result.onCallback(task.getResult().toObject(TaskUser.class));
                    }
                }
            }
        }).addOnFailureListener( new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    public void checkUserIsComplete(String uid , TrueFalse result){
        DocumentReference ref = FirebaseFirestore.getInstance().collection("task-user")
                .document(uid);
        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()){
                    result.callBack(true);
                }else{
                    result.callBack(false);
                }
            }
        });
    }

    public void checkIsFallowing(CurrentUser currentUser  , OtherUser otherUser , TrueFalse<Boolean> val){
        ///user/t01RVvdauThanTbmpmmsLMgiJGx1/following/VUSU6uA0odX7vuF5giXWbOUYzni1

        DocumentReference ref = FirebaseFirestore.getInstance().collection("user")
                .document(currentUser.getUid())
                .collection("following").document(otherUser.getUid());
        ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()){
                        val.callBack(true);
                    }else{
                        val.callBack(false);
                    }
            }
        });
    }

    public void getOtherUser(Activity activity, String otherUserUid , OtherUserService user){
        WaitDialog.show((AppCompatActivity) activity, null);
        DocumentReference ref = FirebaseFirestore.getInstance()
                .collection("user")
                .document(otherUserUid);
        ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    user.callback(documentSnapshot.toObject(OtherUser.class));
                }
            }
        });
    }
}
