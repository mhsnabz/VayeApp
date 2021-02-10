package com.vaye.app.Services;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.vaye.app.Interfaces.Notifications;
import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.MainPostModel;

import java.util.HashMap;
import java.util.Map;

public class FoodMeService {
    private static final FoodMeService instance = new FoodMeService();
    public static FoodMeService shared() {
        return instance;
    }


    public void setLike(MainPostModel post , CurrentUser currentUser , TrueFalse<Boolean> result){
        if (!post.getLikes().contains(currentUser.getUid())){
            post.getLikes().add(currentUser.getUid());
            post.getDislike().remove(currentUser.getUid());
            result.callBack(true);
            DocumentReference ref = FirebaseFirestore.getInstance().collection("main-post")
                    .document("post")
                    .collection("post")
                    .document(post.getPostId());
            Map<String , Object> map = new HashMap<>();
            map.put("likes", FieldValue.arrayUnion(currentUser.getUid()));
            map.put("dislike", FieldValue.arrayRemove(currentUser.getUid()));
            ref.set(map , SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    NotificaitonService.shared().send_mainpost_like_notification(post,currentUser , Notifications.NotificationDescription.like_food_me, Notifications.NotificationType.like_food_me);
                }
            });
        }else{
            post.getLikes().remove(currentUser.getUid());
            result.callBack(true);
            DocumentReference ref = FirebaseFirestore.getInstance().collection("main-post")
                    .document("post")
                    .collection("post").document(post.getPostId());
            Map<String , Object> map =  new HashMap<>();
            map.put("likes", FieldValue.arrayRemove(currentUser.getUid()));
            ref.set(map , SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    NotificaitonService.shared().remove_foodme_like_notification(post,currentUser);
                }
            });
        }
    }

    public void setDislike(CurrentUser currentUser , MainPostModel post , TrueFalse<Boolean> val){
        if (!post.getDislike().contains(currentUser.getUid())){
            post.getLikes().remove(currentUser.getUid());
            post.getDislike().add(currentUser.getUid());
            val.callBack(true);
            DocumentReference ref = FirebaseFirestore.getInstance().collection("main-post")
                    .document("post").collection("post")
                    .document(post.getPostId());
            Map<String , Object> map = new HashMap<>();
            map.put("likes", FieldValue.arrayRemove(currentUser.getUid()));
            map.put("dislike", FieldValue.arrayUnion(currentUser.getUid()));
            ref.set(map , SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    NotificaitonService.shared().remove_foodme_like_notification(post,currentUser);
                }
            });
        }else{
            post.getDislike().remove(currentUser.getUid());
            val.callBack(true);
            DocumentReference ref = FirebaseFirestore.getInstance().collection("main-post")
                    .document("post").collection("post")
                    .document(post.getPostId());
            Map<String , Object> map = new HashMap<>();

            map.put("dislike", FieldValue.arrayRemove(currentUser.getUid()));
            ref.set(map , SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                }
            });
        }
    }





}
