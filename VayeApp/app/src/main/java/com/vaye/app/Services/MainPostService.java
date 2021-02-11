package com.vaye.app.Services;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.kongzue.dialog.v3.WaitDialog;
import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.MainPostModel;
import com.vaye.app.Model.OtherUser;

import java.util.HashMap;
import java.util.Map;

public class MainPostService {
    private static final MainPostService instance = new MainPostService();
    public static MainPostService shared() {
        return instance;
    }

    public void SetPostSlient(Activity activity, MainPostModel postModel , CurrentUser currentUser , TrueFalse<Boolean> callback){
        WaitDialog.show((AppCompatActivity) activity ,null);
        //   let db = Firestore.firestore().collection("main-post")
        //            .document("sell-buy")
        //            .collection("post")
        //            .document(post!.postId)
        DocumentReference ref = FirebaseFirestore.getInstance().collection("main-post")
                .document("post").collection("post").document(postModel.getPostId());
        Map<String , Object> map = new HashMap<>();
        map.put("silent", FieldValue.arrayUnion(currentUser.getUid()));
        ref.set(map , SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    postModel.getSilent().add(currentUser.getUid());
                    callback.callBack(true);
                    WaitDialog.dismiss();
                }
            }
        });
    }
    public void RemoveSlientFromPost(Activity activity, MainPostModel postModel , CurrentUser currentUser, TrueFalse<Boolean> callback){
        WaitDialog.show((AppCompatActivity) activity ,null);
        DocumentReference ref = FirebaseFirestore.getInstance().collection("main-post")
                .document("post").collection("post").document(postModel.getPostId());
        Map<String , Object> map = new HashMap<>();
        map.put("silent", FieldValue.arrayRemove(currentUser.getUid()));
        ref.set(map , SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    postModel.getSilent().remove(currentUser.getUid());
                    callback.callBack(true);
                    WaitDialog.dismiss();
                }
            }
        });
    }


    public void setUserSlient(CurrentUser currentUser , OtherUser otherUser , TrueFalse<Boolean> callback){
        //        let db = Firestore.firestore().collection("user").document(otherUserUid)
        DocumentReference ref = FirebaseFirestore.getInstance().collection("user")
                .document(otherUser.getUid());
        Map<String , Object> map = new HashMap<>();
        map.put("slient",FieldValue.arrayUnion(currentUser.getUid()));
        ref.set(map , SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){

                    callback.callBack(true);
                }
            }
        });

    }

    public void setUserNotSlient(CurrentUser currentUser , OtherUser otherUser , TrueFalse<Boolean> callback){
        DocumentReference ref = FirebaseFirestore.getInstance().collection("user")
                .document(otherUser.getUid());
        Map<String , Object> map = new HashMap<>();
        map.put("slient",FieldValue.arrayRemove(currentUser.getUid()));
        ref.set(map , SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){

                    callback.callBack(true);
                }
            }
        });
    }
}
