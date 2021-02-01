package com.vaye.app.Services;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.vaye.app.Interfaces.Notifications;
import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.Model.CommentModel;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.LessonPostModel;

import java.util.HashMap;
import java.util.Map;

public class CommentService {
    private static final CommentService instance = new CommentService();

    public static CommentService shared() {
        return instance;
    }
    public void removeCommentLike(CurrentUser currentUser , CommentModel commentModel , LessonPostModel post){
        DocumentReference ref = FirebaseFirestore.getInstance().collection(currentUser.getShort_school())
                .document("lesson-post")
                .collection("post")
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
    public void setCommentLike(Activity activity, CurrentUser currentUser , CommentModel commentModel , LessonPostModel post){
        //  let db = Firestore.firestore().collection(currentUser.short_school)
        //                .document("lesson-post").collection("post").document(comment.postId!).collection("comment").document(comment.commentId!)
        DocumentReference ref = FirebaseFirestore.getInstance().collection(currentUser.getShort_school())
                .document("lesson-post")
                .collection("post")
                .document(commentModel.getPostId())
                .collection("comment")
                .document(commentModel.getCommentId());
        Map<String , Object> map =  new HashMap<>();
        map.put("likes", FieldValue.arrayUnion(currentUser.getUid()));

        ref.set(map , SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    NotificaitonService.shared().setCommentLikeNotification(activity,commentModel,post,currentUser , Notifications.NotificationDescription.comment_like, Notifications.NotificationType.comment_like);
                }
            }
        });


    }
}
