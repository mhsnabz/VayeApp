package com.vaye.app.Services;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.vaye.app.Interfaces.CallBackCount;
import com.vaye.app.Interfaces.Notifications;
import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.Model.CommentModel;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.LessonPostModel;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class CommentService {
    private static final CommentService instance = new CommentService();

    public static CommentService shared() {
        return instance;
    }
    public void removeCommentLike(CurrentUser currentUser , CommentModel commentModel , LessonPostModel post){
        DocumentReference ref = FirebaseFirestore.getInstance()
                .collection("comment")
                .document(commentModel.getPostId())
                .collection("comment")
                .document(commentModel.getCommentId());
        Map<String , Object> map =  new HashMap<>();
        map.put("likes", FieldValue.arrayRemove(currentUser.getUid()));
        ref.set(map , SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    NotificaitonService.shared().removeCommentLikeNotification(post , currentUser  , Notifications.NotificationType.comment_like);
                }
            }
        });
    }

    public void setRepliedComment(CurrentUser currentUser , String  targetComment ,String commentId, String  commentText , String  postId , TrueFalse<Boolean> completion){

        DocumentReference ref = FirebaseFirestore.getInstance()
                .collection("comment")
                .document(postId)
                .collection("comment-replied")
                .document("comment")
                .collection(targetComment)
                .document(commentId);

        Map<String , Object> map = new HashMap<>();
        map.put("senderName",currentUser.getName());
        map.put("senderUid",currentUser.getUid());
        map.put("username",currentUser.getUsername());
        map.put("time",FieldValue.serverTimestamp());
        map.put("comment",commentText);
        map.put("commentId",commentId);
        map.put("postId",postId);
        map.put("likes", FieldValue.arrayUnion());
        map.put("replies",FieldValue.arrayUnion());
        map.put("senderImage",currentUser.getThumb_image());
        ref.set(map , SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    setRepliedCommentId(targetComment , commentId , currentUser ,postId);
                }
            }
        });
    }
    //targetCommentID : String , commentId : String, currentUser : CurrentUser , postId : String ,completion :
    private void setRepliedCommentId(String targetCommentId , String commentID , CurrentUser currentUser , String postID){
        //  let db = Firestore.firestore().collection(currentUser.short_school)
        //            .document("lesson-post").collection("post").document(postId).collection("comment").document(targetCommentID)
        DocumentReference ref = FirebaseFirestore.getInstance()
                .collection("comment")
                .document(postID)
                .collection("comment")
                .document(targetCommentId);
        Map<String , Object> map = new HashMap<>();
        map.put("replies",FieldValue.arrayUnion(commentID));
        ref.set(map , SetOptions.merge());
    }
}




