package com.vaye.app.Services;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.kongzue.dialog.v3.WaitDialog;
import com.vaye.app.Interfaces.Notifications;
import com.vaye.app.Interfaces.OtherUserService;
import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.Model.CommentModel;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.LessonPostModel;
import com.vaye.app.Model.OtherUser;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class NotificaitonService {

    private static final NotificaitonService instance = new NotificaitonService();
    public static NotificaitonService shared() {
        return instance;
    }


    public void start_following_you(CurrentUser currentUser , OtherUser otherUser , String  text , String type , TrueFalse<Boolean> completion){
        String  notId = String.valueOf(Calendar.getInstance().getTimeInMillis());
        DocumentReference reference = FirebaseFirestore.getInstance()
                .collection("user")
                .document(otherUser.getUid())
                .collection("notification")
                .document(notId);
        Map<String , Object> map = new HashMap<>();

        map.put("type",type);
        map.put("text",text);
        map.put("senderUid",currentUser.getUid());
        map.put("time",currentUser.getUid());
        map.put("senderImage",currentUser.getUid());
        map.put("not_id",currentUser.getUid());
        map.put("isRead",currentUser.getUid());
        map.put("username",currentUser.getUid());
        map.put("postId","currentUser.getUid()");
        map.put("senderName",currentUser.getUid());
        map.put("lessonName","currentUser.getUid()");
        reference.set(map , SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    completion.callBack(true);
                }
            }
        });
    }



    //TODO:: local notifications setting

    public void setLocalNotification(AppCompatActivity activity , Boolean isEnable,CurrentUser currentUser , String topic , TrueFalse<Boolean> val){

        WaitDialog.show(activity , null);
        DocumentReference ref = FirebaseFirestore.getInstance().collection("user")
                .document(currentUser.getUid());

        Map<String , Object> map = new HashMap<>();
        map.put(topic , isEnable);
        ref.set(map , SetOptions.merge()).addOnCompleteListener(activity, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    val.callBack(isEnable);
                }else{
                    val.callBack(isEnable);
                }
            }
        });

    }



    //TODO:: home post notifications


    public void setPost_CommentLike(LessonPostModel post , CurrentUser currentUser , String text , String type ){
        if (post.getSenderUid().equals(currentUser.getUid())){
            return;
        }else{
            if (!post.getSilent().contains(post.getSenderUid())) {
                String notificationId = String.valueOf(Calendar.getInstance().getTimeInMillis());
                DocumentReference ref = FirebaseFirestore
                        .getInstance()
                        .collection("user")
                        .document(post.getSenderUid())
                        .collection("notification")
                        .document(notificationId);
                Map<String , Object> map = new HashMap<>();
                map.put("type",type);
                map.put("text",text);
                map.put("senderUid",currentUser.getUid());
                map.put("time", FieldValue.serverTimestamp());
                map.put("senderImage",currentUser.getThumb_image());
                map.put("not_id",notificationId);
                map.put("isRead",false);
                map.put("username",currentUser.getUsername());
                map.put("postId",post.getPostId());
                map.put("senderName",currentUser.getName());
                map.put("lessonName",post.getLessonName());
                ref.set(map , SetOptions.merge());


            }else{
                return;
            }
        }
    }
    public void remove_Like_Post_Comment_Notification(LessonPostModel post , CurrentUser currentUser , String type){
        Query db = FirebaseFirestore
                .getInstance().collection("user")
                .document(post.getSenderUid())
                .collection("notification")
                .whereEqualTo("postId",post.getPostId())
                .whereEqualTo("senderUid",currentUser.getUid())
                .whereEqualTo("type", type);
        db.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().isEmpty()){
                        return;
                    }else{
                        for (DocumentSnapshot item : task.getResult().getDocuments()){
                            DocumentReference ref = FirebaseFirestore.getInstance()
                                    .collection("user")
                                    .document(post.getSenderUid())
                                    .collection("notification")
                                    .document(item.getId());
                            ref.delete();
                        }
                    }
                }
            }
        });
    }


    public void removeCommentLikeNotification(LessonPostModel post , CurrentUser currentUser , String type){
        Query db = FirebaseFirestore
                .getInstance().collection("user")
                .document(post.getSenderUid())
                .collection("notification")
                .whereEqualTo("postId",post.getPostId())
                .whereEqualTo("senderUid",currentUser.getUid())
                .whereEqualTo("type", type);
        db.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().isEmpty()){
                        return;
                    }else{
                        for (DocumentSnapshot item : task.getResult().getDocuments()){
                            DocumentReference ref = FirebaseFirestore.getInstance()
                                    .collection("user")
                                    .document(post.getSenderUid())
                                    .collection("notification")
                                    .document(item.getId());
                            ref.delete();
                        }
                    }
                }
            }
        });
    }
    public void setCommentLikeNotification(Activity activity, CommentModel commentModel,LessonPostModel post  , CurrentUser currentUser , String text , String type){
        if (commentModel.getSenderUid().equals(currentUser.getUid())){
            return;
        }else{

            if (currentUser.getSlient().contains(commentModel.getSenderUid())){
                return;
            }else{
                String notificationId = String.valueOf(Calendar.getInstance().getTimeInMillis());
                DocumentReference ref = FirebaseFirestore
                        .getInstance()
                        .collection("user")
                        .document(commentModel.getSenderUid())
                        .collection("notification")
                        .document(notificationId);
                Map<String , Object> map = new HashMap<>();
                map.put("type",type);
                map.put("text",text);
                map.put("senderUid",currentUser.getUid());
                map.put("time", FieldValue.serverTimestamp());
                map.put("senderImage",currentUser.getThumb_image());
                map.put("not_id",notificationId);
                map.put("isRead",false);
                map.put("username",currentUser.getUsername());
                map.put("postId",post.getPostId());
                map.put("senderName",currentUser.getName());
                map.put("lessonName",post.getLessonName());
                ref.set(map , SetOptions.merge());
            }



        }
    }


    public void sendCommentNotification(LessonPostModel post ,CommentModel comment, CurrentUser currentUser , String text , String type){
        if (comment.getSenderUid().equals(currentUser.getUid())){
            return;
        }
        else{
            if (!post.getSilent().contains(post.getSenderUid())){
                String notificaitonId = String.valueOf(Calendar.getInstance().getTimeInMillis());
                DocumentReference ref = FirebaseFirestore.getInstance()
                        .collection("user")
                        .document(comment.getSenderUid())
                        .collection("notification").document(notificaitonId);
                Map<String , Object> map = new HashMap<>();

                map.put("type",type);
                map.put("text",text);
                map.put("senderUid",currentUser.getUid());
                map.put("time",FieldValue.serverTimestamp());
                map.put("senderImage",currentUser.getThumb_image());
                map.put("not_id",notificaitonId);
                map.put("isRead",false);
                map.put("username",currentUser.getUsername());
                map.put("postId",post.getPostId());
                map.put("senderName",currentUser.getName());
                map.put("lessonName",post.getLessonName());
                ref.set(map , SetOptions.merge());

            }
        }
    }

    public void sendRepliedCommentByMention(Activity activity,String username,String type, CurrentUser currentUser , String text , LessonPostModel post){
        UserService.shared().getUserByMention(activity, username, new OtherUserService() {
            @Override
            public void callback(OtherUser user) {
                if (currentUser.getUsername().equals(user.getUsername())){
                    return;
                }

                else{
                    if (!post.getSilent().contains(post.getSenderUid())){
                        if (!currentUser.getSlient().contains(user.getUid())){
                            String notificaitonId = String.valueOf(Calendar.getInstance().getTimeInMillis());
                            DocumentReference ref = FirebaseFirestore.getInstance().collection("user")
                                    .document(user.getUid())
                                    .collection("notification")
                                    .document(notificaitonId);
                            Map<String , Object> map = new HashMap<>();

                            map.put("type",type);
                            map.put("text",text);
                            map.put("senderUid",currentUser.getUid());
                            map.put("time",FieldValue.serverTimestamp());
                            map.put("senderImage",currentUser.getThumb_image());
                            map.put("not_id",notificaitonId);
                            map.put("isRead",false);
                            map.put("username",currentUser.getUsername());
                            map.put("postId",post.getPostId());
                            map.put("senderName",currentUser.getName());
                            map.put("lessonName",post.getLessonName());
                            ref.set(map , SetOptions.merge());
                        }
                    }
                }
            }
        });
    }


}
