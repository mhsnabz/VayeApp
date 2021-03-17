package com.vaye.app.Services;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.kongzue.dialog.v3.TipDialog;
import com.kongzue.dialog.v3.WaitDialog;
import com.vaye.app.Controller.HomeController.SinglePost.SinglePostActivity;
import com.vaye.app.Controller.NotificationService.CommentNotificationService;
import com.vaye.app.Controller.NotificationService.MainPostNotification;
import com.vaye.app.Controller.NotificationService.NotificationPostType;
import com.vaye.app.Controller.NotificationService.PushNotificationService;
import com.vaye.app.Interfaces.CallBackCount;
import com.vaye.app.Interfaces.MainPostFollowers;
import com.vaye.app.Interfaces.MajorPostFallower;
import com.vaye.app.Interfaces.SingleMainPost;
import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.Model.CommentModel;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.MainPostModel;
import com.vaye.app.Model.MainPostTopicFollower;
import com.vaye.app.Model.OtherUser;
import com.vaye.app.Util.Helper;

import org.w3c.dom.Comment;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainPostService {
    private static final MainPostService instance = new MainPostService();
    public static MainPostService shared() {
        return instance;
    }


    public void getMainPost(Context context , CurrentUser currentUser , String postId , SingleMainPost post){
        DocumentReference ref = FirebaseFirestore.getInstance().collection("main-post")
                .document("post")
                .collection("post")
                .document(postId);
        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().exists()){
                        post.getPost(task.getResult().toObject(MainPostModel.class));

                    }else{
                        post.getPost(null);
                        WaitDialog.dismiss();
                        TipDialog.show((AppCompatActivity)context,"Gönderi Silinmiş", TipDialog.TYPE.ERROR);
                        TipDialog.dismiss(1000);
                    }
                }else{
                    post.getPost(null);
                    WaitDialog.dismiss();
                    TipDialog.show((AppCompatActivity)context,"Gönderi Silinmiş", TipDialog.TYPE.ERROR);
                    TipDialog.dismiss(1000);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                post.getPost(null);
                WaitDialog.dismiss();
                TipDialog.show((AppCompatActivity)context,"Hata Oluştu", TipDialog.TYPE.ERROR);
                TipDialog.dismiss(1000);
            }
        });
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
    /**
     * @param type notificaiton type
     * @param  text notification text
     * */
    public void setPostLike(MainPostModel post , CurrentUser currentUser ,String type , String text, TrueFalse<Boolean> callback){
        if (!post.getLikes().contains(currentUser.getUid())){
            post.getLikes().add(currentUser.getUid());
            post.getDislike().remove(currentUser.getUid());
            callback.callBack(true);
            DocumentReference db = FirebaseFirestore.getInstance().collection("main-post")
                    .document("post").collection("post").document(post.getPostId());
            Map<String , Object> map = new HashMap<>();

            map.put("likes",FieldValue.arrayUnion(currentUser.getUid()));
            map.put("dislike",FieldValue.arrayRemove(currentUser.getUid()));

            db.set(map , SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        MainPostNS.shared().setPostLikeNotification(currentUser, NotificationPostType.name.mainPost,post,post.getText(), MainPostNotification.type.post_like);
                    }
                }
            });
        }else{
            post.getLikes().remove(currentUser.getUid());
            callback.callBack(true);
            DocumentReference db = FirebaseFirestore.getInstance().collection("main-post")
                    .document("post").collection("post").document(post.getPostId());
            Map<String , Object> map = new HashMap<>();

            map.put("likes",FieldValue.arrayRemove(currentUser.getUid()));
            map.put("dislike",FieldValue.arrayUnion(currentUser.getUid()));

            db.set(map , SetOptions.merge());
        }


    }
    public void setPostDislike(MainPostModel post , CurrentUser currentUser, TrueFalse<Boolean> callback){
        if (!post.getDislike().contains(currentUser.getUid())){
            post.getLikes().remove(currentUser.getUid());
            post.getDislike().add(currentUser.getUid());
            callback.callBack(true);
            DocumentReference ref = FirebaseFirestore.getInstance().collection("main-post")
                    .document("post").collection("post").document(post.getPostId());
            Map<String , Object> map = new HashMap<>();
            map.put("likes",FieldValue.arrayRemove(currentUser.getUid()));
            map.put("dislike",FieldValue.arrayUnion(currentUser.getUid()));
            ref.set(map, SetOptions.merge());

        }else{
            post.getDislike().remove(currentUser.getUid());
            callback.callBack(true);
            DocumentReference ref = FirebaseFirestore.getInstance().collection("main-post")
                    .document("post").collection("post").document(post.getPostId());
            Map<String , Object> map = new HashMap<>();
            map.put("dislike",FieldValue.arrayRemove(currentUser.getUid()));
            ref.set(map, SetOptions.merge());
        }
    }





    public void getTopicFollowers(String topic , MainPostFollowers list){
        ArrayList<MainPostTopicFollower> followers = new ArrayList<>();
        CollectionReference ref = FirebaseFirestore.getInstance().collection("main-post")
                .document(topic)
                .collection("followers");
        ref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().isEmpty()){
                        list.getTopicFollower(null);
                    }else {
                        for (DocumentSnapshot doc : task.getResult().getDocuments()){
                            followers.add(doc.toObject(MainPostTopicFollower.class));
                        }
                        list.getTopicFollower(followers);
                    }
                }else{
                    list.getTopicFollower(null);
                }
            }
        });
    }

}
