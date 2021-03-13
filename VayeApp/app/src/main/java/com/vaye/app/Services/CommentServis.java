package com.vaye.app.Services;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.vaye.app.Controller.NotificationService.PostName;
import com.vaye.app.Interfaces.CallBackCount;
import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.Model.CurrentUser;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class CommentServis {
    private static final CommentServis instance = new CommentServis();

    public static CommentServis shared() {
        return instance;
    }

    public void sendNewComment(String postType , CurrentUser currentUser , String commentText , String postId , String commentId , TrueFalse<Boolean> callback ){
        DocumentReference db = FirebaseFirestore.getInstance().collection("comment")
                .document(postId)
                .collection("comment")
                .document(commentId);
        db.set(map(currentUser,commentText,commentId,postId), SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    callback.callBack(true);
                    getTotalCommentCount(postId, new CallBackCount() {
                        @Override
                        public void callBackCount(long count) {
                            setTotalCommentCount(postType,currentUser , postId , (int) count);
                        }
                    });
                }
            }
        });
    }
    private void getTotalCommentCount(String postId , CallBackCount count){
        CollectionReference db = FirebaseFirestore.getInstance().collection("comment")
                .document(postId)
                .collection("comment");
        db.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
    private void setTotalCommentCount(String postType , CurrentUser currentUser , String postId , int count){
        Map<String , Object> map = new HashMap<>();
        map.put("comment",count);

        if (postType.equals(PostName.lessonPost)){
            DocumentReference db = FirebaseFirestore.getInstance().collection(currentUser.getShort_school())
                    .document("lesson-post")
                    .collection("post")
                    .document(postId);
            db.set(map , SetOptions.merge());
        }else if (postType.equals(PostName.mainPost)){
            DocumentReference db = FirebaseFirestore.getInstance().collection(postType)
                    .document("post")
                    .collection("post")
                    .document(postId);
            db.set(map , SetOptions.merge());
        }else if (postType.equals(PostName.noticesPost)){
            DocumentReference db = FirebaseFirestore.getInstance().collection(currentUser.getShort_school())
                    .document(postType)
                    .collection("post")
                    .document(postId);
            db.set(map , SetOptions.merge());
        }
    }


    private HashMap<String , Object> map (CurrentUser currentUser, String commentText , String commentId , String postId){
        HashMap<String , Object> map = new HashMap<>();
        map.put("senderName",currentUser.getName());
        map.put("senderUid",currentUser.getUid());
        map.put("username",currentUser.getUsername());
        map.put("time", FieldValue.serverTimestamp());
        map.put("comment",commentText);
        map.put("commentId",commentId);
        map.put("postId",postId);
        map.put("senderImage",currentUser.getThumb_image());
        map.put("likes", FieldValue.arrayUnion());
        map.put("replies",FieldValue.arrayUnion());

        return  map;
    }

}
