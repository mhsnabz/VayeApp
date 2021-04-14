package com.vaye.app.Services;

import android.content.Context;

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
import com.vaye.app.Controller.CommentController.CommentActivity;
import com.vaye.app.Controller.NotificationService.PostName;
import com.vaye.app.Interfaces.CallBackCount;
import com.vaye.app.Interfaces.RepliedCommentModel;
import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.Model.CommentModel;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.NoticesMainModel;

import org.w3c.dom.Comment;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class CommentServis {
    private static final CommentServis instance = new CommentServis();

    public static CommentServis shared() {
        return instance;
    }

    public void getRepliedComment(Context context,String targetCommentId , String postId , RepliedCommentModel comment){
        DocumentReference commentData = FirebaseFirestore.getInstance().collection("comment")
                .document(postId)
                .collection("comment")
                .document(targetCommentId);
        commentData.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().exists()){
                        comment.getComment(task.getResult().toObject(CommentModel.class));


                    }else{
                        comment.getComment(null);
                        WaitDialog.dismiss();
                        TipDialog.show((AppCompatActivity)context,"Gönderi Silinmiş", TipDialog.TYPE.ERROR);
                        TipDialog.dismiss(1000);
                    }
                }else{
                    comment.getComment(null);
                    WaitDialog.dismiss();
                    TipDialog.show((AppCompatActivity)context,"Gönderi Silinmiş", TipDialog.TYPE.ERROR);
                    TipDialog.dismiss(1000);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                comment.getComment(null);
                WaitDialog.dismiss();
                TipDialog.show((AppCompatActivity)context,"Hata Oluştu", TipDialog.TYPE.ERROR);
                TipDialog.dismiss(1000);
            }
        });
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
    public void setRepliedComment(String postId , String targetCommentId , String commentText , CurrentUser currentUser , String commentId ,TrueFalse<Boolean> callback){
        DocumentReference db = FirebaseFirestore.getInstance().collection("comment")
                .document(postId)
                .collection("comment-replied")
                .document("comment")
                .collection(targetCommentId)
                .document(commentId) ;
            db.set(map(currentUser,commentText,commentId,postId),SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        DocumentReference db = FirebaseFirestore.getInstance().collection("comment")
                                .document(postId).collection("comment").document(targetCommentId);
                        Map<String , Object> map = new HashMap<>();
                        map.put("replies",FieldValue.arrayUnion(commentId));
                        db.set(map,SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    callback.callBack(true);
                                }
                            }
                        });

                    }
                }
            });
    }
    public void getTotalCommentCount(String postId , CallBackCount count){
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
    public void setTotalCommentCount(String postType , CurrentUser currentUser , String postId , int count){
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

    public void setRepliedCommentLike(CommentModel commentModel , String targetComment, CurrentUser currentUser , TrueFalse<Boolean> callback){
        DocumentReference db = FirebaseFirestore.getInstance().collection("comment")
                .document(commentModel.getPostId())
            .collection("comment-replied")
                .document("comment")
                .collection(targetComment)
                .document(commentModel.getCommentId());
        Map<String , Object> map = new HashMap<>();
        map.put("likes",FieldValue.arrayUnion(currentUser.getUid()));
                db.update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            callback.callBack(true);
                        }
                    }
                });
    }

    public void setCommentLike(CommentModel commentModel , CurrentUser currentUser , TrueFalse<Boolean> callback){
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
                if(task.isSuccessful()){
                    callback.callBack(true);
                }
            }
        });
    }
    public void removeCommentLike(CommentModel commentModel , CurrentUser currentUser ){
        DocumentReference ref = FirebaseFirestore.getInstance()
                .collection("comment")
                .document(commentModel.getPostId())
                .collection("comment")
                .document(commentModel.getCommentId());
        Map<String , Object> map =  new HashMap<>();
        map.put("likes", FieldValue.arrayRemove(currentUser.getUid()));
        ref.set(map , SetOptions.merge());
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
