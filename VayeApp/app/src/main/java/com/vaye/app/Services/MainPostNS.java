package com.vaye.app.Services;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.vaye.app.Controller.NotificationService.MainPostNotification;
import com.vaye.app.Controller.NotificationService.PushNotificationService;
import com.vaye.app.Controller.NotificationService.PushNotificationTarget;
import com.vaye.app.Controller.NotificationService.PushNotificationType;
import com.vaye.app.Interfaces.MainPostFollowers;
import com.vaye.app.Interfaces.StringArrayListInterface;
import com.vaye.app.Interfaces.StringCompletion;
import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.Model.CommentModel;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.LessonFallowerUser;
import com.vaye.app.Model.MainPostModel;
import com.vaye.app.Model.MainPostTopicFollower;
import com.vaye.app.Model.NewPostDataModel;
import com.vaye.app.Util.Helper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MainPostNS {
    private static final MainPostNS instance = new MainPostNS();
    public static MainPostNS shared() {
        return instance;
    }

    public void setPostLikeNotification(CurrentUser currentUser,String postType , MainPostModel post , String  text , String type){
        if (post.getSenderUid().equals(currentUser.getUid())){
            return;
        }else{
            if (!post.getSilent().contains(post.getSenderUid())){
                String notificationId = String.valueOf(Calendar.getInstance().getTimeInMillis());
                DocumentReference ref = FirebaseFirestore.getInstance().collection("user")
                        .document(post.getSenderUid()).collection("notification").document(notificationId);

                ref.set(Helper.shared().getDictionary(postType,type,post.getText(),currentUser,notificationId,null,post.getPostId(),null,null,post.getPostType()),SetOptions.merge());

                PushNotificationService.shared().sendPushNotification("empty",currentUser,PushNotificationType.like,notificationId,post.getSenderUid(),null, PushNotificationTarget.like,currentUser.getName(),post.getText(), MainPostNotification.descp.post_like,currentUser.getUid());

            }
        }
    }

    public void setNewCommentNotification(MainPostModel postModel ,  CurrentUser currentUser , String type , String  text
    ){
        if (postModel.getSenderUid().equals(currentUser.getUid())){
            return;
        }else{
            if (!postModel.getSilent().contains(currentUser.getUid())){
                String notificationId = String.valueOf(Calendar.getInstance().getTimeInMillis());
                DocumentReference ref = FirebaseFirestore.getInstance().collection("user")
                        .document(postModel.getSenderUid()).collection("notification")
                        .document(notificationId);
                ref.set(map(currentUser,notificationId , postModel.getPostId(),text , type) , SetOptions.merge());
            }
        }
    }

    public void setMentionedCommentNotificaiton(String username , CurrentUser currentUser,MainPostModel post , String type , String text){
        UserService.shared().getOthUserIdByMention(username, new StringCompletion() {
            @Override
            public void getString(String otherUserUid) {
                if (otherUserUid!=null){
                    if (otherUserUid.equals(currentUser.getUid())){
                        return;
                    }else{
                        if (!post.getSilent().contains(post.getSenderUid())) {
                            if (!currentUser.getSlient().contains(otherUserUid)){
                                String notificaitonId = String.valueOf(Calendar.getInstance().getTimeInMillis());
                                DocumentReference ref = FirebaseFirestore.getInstance().collection("user")
                                        .document(otherUserUid).collection("notification")
                                        .document(notificaitonId);
                                ref.set(map(currentUser , notificaitonId , post.getPostId() , text , type) , SetOptions.merge());
                            }
                        }
                    }
                }

            }
        });
    }

    public void setNewPostNotification(ArrayList<MainPostTopicFollower>  notificaitonGetter, CurrentUser currentUser, String notificationId, String postType , String text , String type , String postId){
        if ( notificaitonGetter!=null && !notificaitonGetter.isEmpty()){
            for (MainPostTopicFollower item : notificaitonGetter){
                if (!item.getUserId().equals(currentUser.getUid())){
                    DocumentReference ref1 = FirebaseFirestore.getInstance().collection("user")
                            .document(item.getUserId())
                            .collection("notification")
                            .document(notificationId);
                    ref1.set(map(currentUser , notificationId , postId , text , type),SetOptions.merge());
                }
            }
        }
    }
    public void setMentionedPost(String username , CurrentUser currentUser, String postId , String type , String text ){
        UserService.shared().getOthUserIdByMention(username, new StringCompletion() {
            @Override
            public void getString(String otherUserUid) {
                if (otherUserUid!=null){
                    if (otherUserUid.equals(currentUser.getUid())){
                        return;
                    }else{

                        if (!currentUser.getSlient().contains(otherUserUid)){
                            String notificaitonId = String.valueOf(Calendar.getInstance().getTimeInMillis());
                            DocumentReference ref = FirebaseFirestore.getInstance().collection("user")
                                    .document(otherUserUid).collection("notification")
                                    .document(notificaitonId);
                            ref.set(map(currentUser , notificaitonId , postId, text , type  ) , SetOptions.merge());
                        }

                    }
                }

            }
        });
    }
    public void setNewPost(String  postType, String type , String locationName, String value,CurrentUser currentUser, GeoPoint location, long postId , ArrayList<String> followers
            , String msgText , ArrayList<NewPostDataModel> datas , TrueFalse<Boolean> val){

        Map<String , Object> map = new HashMap<>();

        if (datas.isEmpty()){
            map.put("type","post");
            map.put("data",FieldValue.arrayUnion());
            map.put("thumb_data",FieldValue.arrayUnion());
        }else{

        }
        map.put("postTime",FieldValue.serverTimestamp());
        map.put("senderName",currentUser.getName());
        map.put("text",msgText);
        map.put("postType",postType);
        if (locationName!=null && !locationName.isEmpty()){
            map.put("locationName",locationName);

        }else{
            map.put("locationName","");
        }

        map.put("likes",FieldValue.arrayUnion());
        map.put("geoPoint",location);
        map.put("postId",String.valueOf(postId));
        map.put("senderUid",currentUser.getUid());
        map.put("silent",FieldValue.arrayUnion());
        map.put("comment",0);
        map.put("dislike",FieldValue.arrayUnion());
        map.put("post_ID",postId);
        map.put("username",currentUser.getUsername());
        map.put("thumb_image",currentUser.getThumb_image());
        map.put("value",value);

        setPostForCurrentUser( currentUser.getUid() ,String.valueOf(postId));
        setPostForUniversity(String.valueOf(postId),postType,currentUser.getShort_school());
        setPostForFollowers(currentUser.getUid(),followers,String.valueOf(postId));

        DocumentReference reference = FirebaseFirestore.getInstance().collection("main-post")
                .document("post").collection("post").document(String.valueOf(postId));
        reference.set(map , SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    val.callBack(true);

                }
            }
        });
    }

    private void setPostForFollowers(String senderUid,ArrayList<String> followers,String postId) {

        for (String item : followers){
            DocumentReference ref = FirebaseFirestore.getInstance().collection("user")
                    .document(item).collection("main-post")
                    .document(postId);
            Map<String ,String> map = new HashMap<>();
            map.put("postId",postId);
            map.put("senderUid",senderUid);
            ref.set(map,SetOptions.merge());
        }
    }

    private void setPostForUniversity(String postId,String postType, String short_school) {
        Log.d("setPostForUniversity", "setPostForUniversity: " + short_school);
        Log.d("setPostForUniversity", "setPostForUniversity: " + postType);
        Log.d("setPostForUniversity", "setPostForUniversity: " + postId);
    DocumentReference ref = FirebaseFirestore.getInstance().collection(short_school)
                .document("main-post")
                .collection(postType)
                .document(postId);
        Map<String ,String> map = new HashMap<>();
        map.put("postId",postId);
        ref.set(map,SetOptions.merge());

    }

    private void setPostForCurrentUser(String uid,String postId) {
        //let db = Firestore.firestore().collection("user")
        //            .document(currentUser.uid).collection("user-main-post").document(postId)
        DocumentReference ref = FirebaseFirestore.getInstance().collection("user").document(uid).collection("user-main-post")
                .document(postId);
        Map<String ,String> map = new HashMap<>();
        map.put("postId",postId);
        ref.set(map,SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                DocumentReference ref = FirebaseFirestore.getInstance().collection("user")
                        .document(uid).collection("main-post")
                        .document(String.valueOf(postId));
                Map<String ,String> mapa = new HashMap<>();
                mapa.put("postId",postId);
                mapa.put("senderUid",uid);
                ref.set(map,SetOptions.merge());
            }
        });

    }

    public void getMyFollowers(String uid , StringArrayListInterface list){
        ArrayList<String> followersList = new ArrayList<>();
        CollectionReference ref = FirebaseFirestore.getInstance().collection("user")
                .document(uid)
                .collection("fallowers");
        ref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()){
                    if (task.getResult().isEmpty()){
                        list.getArrayList(followersList);
                    }else{
                        for (DocumentSnapshot item : task.getResult().getDocuments()){
                            followersList.add(item.getId());
                        }
                        list.getArrayList(followersList);
                    }
                    if (task.getResult() == null){
                        list.getArrayList(followersList);
                    }
                }else{
                    list.getArrayList(followersList);
                }

            }
        });

    }

    private Map<String , Object>  map (CurrentUser currentUser ,String notificationId , String postId, String text , String type){
        Map<String , Object> map = new HashMap<>();
        map.put("type",type);
        map.put("text",text);
        map.put("senderUid",currentUser.getUid());
        map.put("time", FieldValue.serverTimestamp());
        map.put("senderImage",currentUser.getThumb_image());
        map.put("not_id",notificationId);
        map.put("isRead","false");
        map.put("username",currentUser.getUsername());
        map.put("postId",postId);
        map.put("senderName",currentUser.getName());
        map.put("lessonName",null);
        return map;
    }


}
