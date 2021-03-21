package com.vaye.app.Services;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.rpc.Help;
import com.vaye.app.Controller.NotificationService.MajorPostNotification;
import com.vaye.app.Controller.NotificationService.PushNotificationService;
import com.vaye.app.Controller.NotificationService.PushNotificationTarget;
import com.vaye.app.Interfaces.Notifications;
import com.vaye.app.Interfaces.OtherUserService;
import com.vaye.app.Interfaces.StringCompletion;
import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.LessonPostModel;
import com.vaye.app.Model.LessonUserList;
import com.vaye.app.Model.MainPostModel;
import com.vaye.app.Model.OtherUser;
import com.vaye.app.Util.Helper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MajorPostNS {
    private static final MajorPostNS instance = new MajorPostNS();
    public static MajorPostNS shared() {
        return instance;
    }
    //lessonName : String ,postType : String ,currentUser : CurrentUser, text : String , type : String , postId : String
    public void sendNewPostNotification(String postType,CurrentUser currentUser, String lessonName , String text , String type , String postId){
        ArrayList<String> notificationGetter = new ArrayList<>();
        CollectionReference ref = FirebaseFirestore
                .getInstance().collection(currentUser.getShort_school())
                .document("lesson")
                .collection(currentUser.getBolum())
                .document(lessonName)
                .collection("notification_getter");
        ref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().isEmpty()){

                    }else{
                        for (DocumentSnapshot id : task.getResult().getDocuments()){
                            notificationGetter.add(id.getId());
                            String notId = String.valueOf(Calendar.getInstance().getTimeInMillis());

                                if (!id.getId().equals(currentUser.getUid())){
                                    DocumentReference ref1 = FirebaseFirestore.getInstance().collection("user")
                                            .document(id.getId())
                                            .collection("notification")
                                            .document(String.valueOf(notId));
                                    ref1.set(Helper.shared().getDictionary(postType,type,text,currentUser,postId,null,postId,lessonName,null,null) , SetOptions.merge());
                                    PushNotificationService.shared().sendPushNotification(notId,id.getId(),null, PushNotificationTarget.newpost_lessonpost,currentUser.getName(),text, MajorPostNotification.descp.new_post,currentUser.getUid());
                                }

                        }

                    }
                }
            }
        });
    }

    public void teacherNewPostNotification(ArrayList<LessonUserList> notificationGetter, String postType, CurrentUser currentUser, String lessonName , String text , String type , String postId){
        String notId = String.valueOf(Calendar.getInstance().getTimeInMillis());
        for (LessonUserList item : notificationGetter){
            if (!item.getUid().equals(currentUser.getUid())){
                DocumentReference ref1 = FirebaseFirestore.getInstance().collection("user")
                        .document(item.getUid())
                        .collection("notification")
                        .document(String.valueOf(notId));
                ref1.set(Helper.shared().getDictionary(postType,type,text,currentUser,postId,null,postId,lessonName,null,null) , SetOptions.merge());
                PushNotificationService.shared().sendPushNotification(notId,item.getUid(),null, PushNotificationTarget.newpost_lessonpost,currentUser.getName(),text, MajorPostNotification.descp.new_post,currentUser.getUid());
            }
        }

    }

    public void setMentionedCommentNotificaiton(String username , CurrentUser currentUser, String postId,String lessonName , String type , String text){
        UserService.shared().getOthUserIdByMention(username, new StringCompletion() {
            @Override
            public void getString(String otherUserUid) {
                if (otherUserUid!=null){
                    if (otherUserUid.equals(currentUser.getUid())){
                        return;
                    }else{
                            checkUserFollowingLesson(username, lessonName, currentUser, new TrueFalse<Boolean>() {
                                @Override
                                public void callBack(Boolean _value) {
                                    if (_value){
                                        if (!currentUser.getSlient().contains(otherUserUid)){
                                            String notificaitonId = String.valueOf(Calendar.getInstance().getTimeInMillis());
                                            DocumentReference ref = FirebaseFirestore.getInstance().collection("user")
                                                    .document(otherUserUid).collection("notification")
                                                    .document(notificaitonId);
                                            ref.set(map(currentUser , notificaitonId ,postId , text , type , lessonName) , SetOptions.merge());
                                        }
                                    }else{
                                        return;
                                    }
                                }
                            });

                        }

                }

            }
        });
    }

    private void checkUserFollowingLesson(String username,String lessonName , CurrentUser currentUser , TrueFalse<Boolean> callback){
        CollectionReference ref = FirebaseFirestore.getInstance().collection(currentUser.getShort_school())
                .document(currentUser.getBolum()).collection(lessonName).document("fallowers").collection(username);
        ref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().getDocuments().isEmpty()){
                        callback.callBack(false);
                    }else{
                        callback.callBack(true);
                    }
                }else{
                    callback.callBack(false);
                }
            }
        });
    }



    private Map<String , Object>  map (CurrentUser currentUser ,String notificationId , String postId, String text , String type , String lessonName){
        Map<String , Object> map = new HashMap<>();
        map.put("type",type);
        map.put("text",text);
        map.put("senderUid",currentUser.getUid());
        map.put("time", FieldValue.serverTimestamp());
        map.put("senderImage",currentUser.getThumb_image());
        map.put("not_id",notificationId);
        map.put("isRead",false);
        map.put("username",currentUser.getUsername());
        map.put("postId",postId);
        map.put("senderName",currentUser.getName());
        map.put("lessonName",lessonName);
        return map;
    }

}
