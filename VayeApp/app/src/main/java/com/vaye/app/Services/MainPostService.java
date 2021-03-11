package com.vaye.app.Services;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.kongzue.dialog.v3.WaitDialog;
import com.vaye.app.Controller.NotificationService.CommentNotificationService;
import com.vaye.app.Interfaces.CallBackCount;
import com.vaye.app.Interfaces.MainPostFollowers;
import com.vaye.app.Interfaces.MajorPostFallower;
import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.Model.CommentModel;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.MainPostModel;
import com.vaye.app.Model.MainPostTopicFollower;
import com.vaye.app.Model.OtherUser;

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
                        MainPostNS.shared().setPostLikeNotification(currentUser,post , text , type);
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

    public void setCommentLike(CommentModel comment ,String type , String text, MainPostModel post , CurrentUser currentUser , TrueFalse<Boolean> callback){
       // let db = Firestore.firestore().collection("main-post")
        //                .document("post")
        //                .collection("post")
        //                .document(post.postId)
        //                .collection("comment").document(comment.commentId!)

        DocumentReference ref = FirebaseFirestore.getInstance().collection("main-post")
                .document("post").collection("post").document(post.getPostId())
                .collection("comment").document(comment.getCommentId());

        if (!comment.getLikes().contains(currentUser.getUid())){
            comment.getLikes().add(currentUser.getUid());
            callback.callBack(true);
         Map<String , Object> map = new HashMap<>();
         map.put("likes", FieldValue.arrayUnion(currentUser.getUid()));
         ref.set(map , SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
             @Override
             public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                   // MainPostNS.shared().setCommentLikeNotification(post , comment , currentUser ,type , text );
                    CommentNotificationService.shared().likeMainPostComment(post,comment,currentUser,text,type);
                }
             }
         });
        }
    }

    public void setNewComment(CurrentUser currentUser , String commentText , String commentId , String postId , TrueFalse<Boolean> callback){
        DocumentReference ref  = FirebaseFirestore.getInstance().collection("main-post")
                .document("post")
                .collection("post")
                .document(postId).collection("comment").document(commentId);

        Map<String , Object> map = new HashMap<>();
        map.put("senderName",currentUser.getName());
        map.put("senderUid",currentUser.getUid());
        map.put("username",currentUser.getUsername());
        map.put("time",FieldValue.serverTimestamp());
        map.put("comment",commentText);
        map.put("commentId",commentId);
        map.put("postId",postId);
        map.put("likes",FieldValue.arrayUnion());
        map.put("replies",FieldValue.arrayUnion());
        map.put("senderImage",currentUser.getThumb_image());
        ref.set(map , SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    callback.callBack(true);
                getTotalCommentCount(postId, new CallBackCount() {
                    @Override
                    public void callBackCount(long count) {
                        DocumentReference commentCountRef  = FirebaseFirestore.getInstance().collection("main-post")
                                .document("post")
                                .collection("post")
                                .document(postId);
                        Map<String , Object> map1 = new HashMap<>();
                        map1.put("comment", count);
                        commentCountRef.set(map1 , SetOptions.merge());
                    }
                });
                }
            }
        });
    }

    private void getTotalCommentCount(String postId , CallBackCount count){
        CollectionReference ref  = FirebaseFirestore.getInstance().collection("main-post")
                .document("post")
                .collection("post")
                .document(postId).collection("comment");
        ref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
             public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()){
                        if (task.getResult().isEmpty()){
                            count.callBackCount(0);
                        }else{
                            count.callBackCount(task.getResult().getDocuments().size());
                        }
                    }
            }
        });
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
