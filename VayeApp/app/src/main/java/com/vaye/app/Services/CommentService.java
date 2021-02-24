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
    public void setCommentLike(Activity activity, CurrentUser currentUser , CommentModel commentModel , LessonPostModel post){
        //  let db = Firestore.firestore().collection(currentUser.short_school)
        //                .document("lesson-post").collection("post").document(comment.postId!).collection("comment").document(comment.commentId!)
        DocumentReference ref = FirebaseFirestore.getInstance()
                .collection("comment")
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


    public void sendNewComment(CurrentUser currentUser , String commentText , String postId , String commentId , TrueFalse<Boolean> val){
        DocumentReference ref = FirebaseFirestore.getInstance()
                .collection("comment")
                .document(postId)
                .collection("comment")
                .document(commentId);

        Map<String , Object> map =  new HashMap<>();
        map.put("senderName",currentUser.getName());
        map.put("senderUid",currentUser.getUid());
        map.put("username",currentUser.getUsername());
        map.put("time",FieldValue.serverTimestamp());
        map.put("comment",commentText);
        map.put("commentId",commentId);
        map.put("likes",FieldValue.arrayUnion());
        map.put("replies",FieldValue.arrayUnion());
        map.put("senderImage",currentUser.getThumb_image());
        map.put("postId",postId);
        ref.set(map , SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    getTotalCommentCount(currentUser, postId, new CallBackCount() {
                        @Override
                        public void callBackCount(long count) {
                            //let commentCount = Firestore.firestore().collection(currentUser.short_school)
                            //                        .document("lesson-post").collection("post").document(postId)
                            DocumentReference db = FirebaseFirestore.getInstance().collection(currentUser.getShort_school())
                                    .document("lesson-post")
                                    .collection("post")
                                    .document(postId);
                            Map<String , Object> map1 = new HashMap<>();
                            map1.put("comment",count);
                            db.set(map1 , SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){

                                        val.callBack(true);
                                    }
                                }
                            });
                        }
                    });
                }
            }
        });
    }

    public void getTotalRepliedCount(CurrentUser currentUser  ,String repliedCommentId,  String postId , CallBackCount count){
        ///İSTE/lesson-post/post/1610997978137/comment-replied/comment/1612028213709/
        CollectionReference ref = FirebaseFirestore.getInstance()
                .collection("comment")
                .document(repliedCommentId)
                .collection("comment-replied");
        ref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().isEmpty()){
                        count.callBackCount(0);
                    }else{
                        count.callBackCount(task.getResult().getDocuments().size());
                    }
                }else{
                    count.callBackCount(0);
                }
            }
        });
    }

    public void getTotalCommentCount(CurrentUser currentUser , String postId , CallBackCount count){
      //       let db = Firestore.firestore().collection(currentUser.short_school)
        //            .document("lesson-post").collection("post").document(postId).collection("comment")
        CollectionReference ref = FirebaseFirestore.getInstance()
                .collection("comment")
                .document(postId)
                .collection("comment");
        ref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().getDocuments().size() > 0){
                        count.callBackCount(task.getResult().getDocuments().size());
                    }else{
                        count.callBackCount(0);
                    }
                }else{
                    count.callBackCount(0);
                }
            }
        });
    }
//currentUser : CurrentUser , targetCommentId : String , commentId : String,commentText : String, postId : String , completion : @escaping(Bool) ->Void
    public void setRepliedComment(CurrentUser currentUser , String  targetComment ,String commentId, String  commentText , String  postId , TrueFalse<Boolean> completion){
       //
        //        let db = Firestore.firestore().collection(currentUser.short_school)
        //            .document("lesson-post").collection("post").document(postId).collection("comment-replied")
        //            .document("comment").collection(targetCommentId).document(commentId)
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




