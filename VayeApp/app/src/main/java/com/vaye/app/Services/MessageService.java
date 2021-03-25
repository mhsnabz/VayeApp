package com.vaye.app.Services;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.vaye.app.Controller.NotificationService.MajorPostNotification;
import com.vaye.app.Controller.NotificationService.MsgNotification;
import com.vaye.app.Controller.NotificationService.PushNotificationService;
import com.vaye.app.Controller.NotificationService.PushNotificationTarget;
import com.vaye.app.LoginRegister.MessageType;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.MessagesModel;
import com.vaye.app.Model.OtherUser;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MessageService {
    private static final String TAG = "MessageService";
    private static final MessageService instance = new MessageService();
    public static MessageService shared() {
        return instance;
    }

    public void setCurrentUserOnline(CurrentUser currentUser , OtherUser otherUser , Boolean bool){
        DocumentReference ref =  FirebaseFirestore.getInstance().collection("user")
                .document(currentUser.getUid())
                .collection("msg-list")
                .document(otherUser.getUid());
        Map<String , Object> map = new HashMap<>();
        map.put("isOnline",bool);
        ref.set(map, SetOptions.merge());
    }
    public void deleteBadge(CurrentUser currentUser , OtherUser otherUser){
        Query deleteBadgeDb = FirebaseFirestore.getInstance().collection("user")
                .document(currentUser.getUid())
                .collection("msg-list")
                .document(otherUser.getUid()).collection("badgeCount").whereEqualTo("badge","badge");
        CollectionReference dbc = FirebaseFirestore.getInstance().collection("user")
                .document(currentUser.getUid())
                .collection("msg-list")
                .document(otherUser.getUid()).collection("badgeCount");
        deleteBadgeDb.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    if (!task.getResult().getDocuments().isEmpty()){
                        for (DocumentSnapshot item : task.getResult().getDocuments()){
                            dbc.document(item.getId()).delete();
                        }
                    }
                }
            }
        });

    }


    public void sendTextMsg(CurrentUser currentUser , OtherUser otherUser , String fileName,Boolean isOnline ,long time,GeoPoint geoPoint,int duration,Float width , Float heigth,String msg , String messageId , String type){
            if (currentUser.getFriendList().contains(otherUser.getUid())){
                DocumentReference dbSender = FirebaseFirestore.getInstance().collection("messages")
                        .document(currentUser.getUid())
                        .collection(otherUser.getUid())
                        .document(messageId);
                dbSender.set(getHasMap(msg,messageId,type,fileName,currentUser,width,heigth,duration,geoPoint,time)).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            DocumentReference db = FirebaseFirestore.getInstance().collection("user")
                                    .document(currentUser.getUid())
                                    .collection("msg-list")
                                    .document(otherUser.getUid());
                            db.set(dicSenderLastMessage(msg,otherUser,type),SetOptions.merge());
                        }
                    }
                });
                DocumentReference dbGetter = FirebaseFirestore.getInstance().collection("messages")
                        .document(otherUser.getUid())
                        .collection(currentUser.getUid())
                        .document(messageId);
                dbGetter.set(getHasMap(msg,messageId,type,fileName,currentUser,width,heigth,duration,geoPoint,time)).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            DocumentReference db = FirebaseFirestore.getInstance().collection("user")
                                    .document(currentUser.getUid())
                                    .collection("msg-list")
                                    .document(otherUser.getUid());
                            db.set(dicGetterLastMessage(msg,currentUser,type),SetOptions.merge());
                        }
                    }
                });
                getBadgeCount(currentUser,"msg-list",isOnline,otherUser);
                setBadgeCount(currentUser,isOnline,otherUser,"msg-list");

                if (!isOnline) {
                    if (type.equals(MessageType.text)){
                        PushNotificationService.shared().sendPushNotification(String.valueOf(Calendar.getInstance().getTimeInMillis()),otherUser.getUid(),otherUser, MsgNotification.type.new_msg,currentUser.getName(),msg, MsgNotification.descp.new_msg,currentUser.getUid());

                    }else if (type.equals(MessageType.photo)){
                        PushNotificationService.shared().sendPushNotification(String.valueOf(Calendar.getInstance().getTimeInMillis()),otherUser.getUid(),otherUser, MsgNotification.type.new_image,currentUser.getName(),"Resim Gönderdi", MsgNotification.descp.new_image,currentUser.getUid());

                    }else if (type.equals(MessageType.audio)){
                        PushNotificationService.shared().sendPushNotification(String.valueOf(Calendar.getInstance().getTimeInMillis()),otherUser.getUid(),otherUser, MsgNotification.type.new_record,currentUser.getName(),"Ses Kaydı Gönderdi", MsgNotification.descp.new_record,currentUser.getUid());

                    }else if (type.equals(MessageType.location)){
                        PushNotificationService.shared().sendPushNotification(String.valueOf(Calendar.getInstance().getTimeInMillis()),otherUser.getUid(),otherUser, MsgNotification.type.new_location,currentUser.getName(),"Konum Gönderdi", MsgNotification.descp.new_location,currentUser.getUid());

                    }
                }
            }else{
                DocumentReference dbSender = FirebaseFirestore.getInstance().collection("messages")
                        .document(currentUser.getUid())
                        .collection(otherUser.getUid())
                        .document(messageId);
                dbSender.set(getHasMap(msg,messageId,type,fileName,currentUser,width,heigth,duration,geoPoint,time),SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            DocumentReference db = FirebaseFirestore.getInstance().collection("user")
                                    .document(currentUser.getUid())
                                    .collection("msg-list")
                                    .document(otherUser.getUid());
                            db.set(dicSenderLastMessage(msg,otherUser,type),SetOptions.merge());

                        }
                    }
                });
                DocumentReference dbGetter = FirebaseFirestore.getInstance().collection("messages")
                        .document(otherUser.getUid())
                        .collection(currentUser.getUid())
                        .document(messageId);
                dbGetter.set(getHasMap(msg,messageId,type,fileName,currentUser,width,heigth,duration,geoPoint,time),SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            DocumentReference db = FirebaseFirestore.getInstance().collection("user")
                                    .document(otherUser.getUid())
                                    .collection("msg-request")
                                    .document(currentUser.getUid());
                            db.set(dicGetterLastMessage(msg,currentUser,type),SetOptions.merge());

                        }
                    }
                });
                getBadgeCount(currentUser,"msg-request",isOnline,otherUser);
                setBadgeCount(currentUser,isOnline,otherUser,"msg-request");


                    if (!isOnline) {
                        PushNotificationService.shared().sendPushNotification(String.valueOf(Calendar.getInstance().getTimeInMillis()),otherUser.getUid(),otherUser, MsgNotification.type.new_rqst,currentUser.getName(),"Size Mesaj Göndermek İstiyor", MsgNotification.descp.new_rqst,currentUser.getUid());
                    }

            }
    }

    private HashMap<String , Object> getHasMap(String msg , String id , String  type ,String fileName, CurrentUser currentUser , Float width, Float heigth, int duration , GeoPoint geoPoint ,long time){
        HashMap<String , Object> map = new HashMap<>();
        map.put("content",msg);
        map.put("id",id);
        map.put("type",type);
        map.put("senderUid",currentUser.getUid());
        map.put("name",currentUser.getName());
        map.put("fileName",fileName);
        map.put("geoPoint",geoPoint);
        map.put("date", FieldValue.serverTimestamp());
        map.put("time",time);
        map.put("is_read",false);
        map.put("width",width);
        map.put("heigth",heigth);
        map.put("duration",duration);
        return map;

    }
    private HashMap<String , Object> dicGetterLastMessage(String lastMsg ,CurrentUser currentUser , String type ){
        HashMap<String , Object> map = new HashMap<>();
        map.put("lastMsg",lastMsg);
        map.put("time",FieldValue.serverTimestamp());
        map.put("thumbImage",currentUser.getThumb_image());
        map.put("username",currentUser.getUsername());
        map.put("uid",currentUser.getUid());
        map.put("name",currentUser.getName());
        map.put("type",type);
        return map;
    }

    private HashMap<String , Object> dicSenderLastMessage(String lastMsg ,OtherUser otherUser , String type ){
        HashMap<String , Object> map = new HashMap<>();
        map.put("lastMsg",lastMsg);
        map.put("time",FieldValue.serverTimestamp());
        map.put("thumbImage",otherUser.getThumb_image());
        map.put("username",otherUser.getUsername());
        map.put("uid",otherUser.getUid());
        map.put("name",otherUser.getName());
        map.put("type",type);
        return map;
    }

    private void getBadgeCount(CurrentUser currentUser , String target , boolean isOnline , OtherUser otherUser){
      if (!isOnline){
          Query db = FirebaseFirestore.getInstance().collection("user")
                  .document(otherUser.getUid())
                  .collection(target).document(currentUser.getUid())
                  .collection("badgeCount").whereEqualTo("badge","badge");
          db.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
              @Override
              public void onComplete(@NonNull Task<QuerySnapshot> task) {
                  if (task.isSuccessful()){
                      if (task.getResult().getDocuments().size()>0){
                          DocumentReference db = FirebaseFirestore.getInstance().collection("user")
                                  .document(otherUser.getUid())
                                  .collection(target).document(currentUser.getUid());
                          Map<String , Object> map = new HashMap<>();
                          map.put("badgeCount",task.getResult().getDocuments().size());
                          db.set(map,SetOptions.merge());

                      }
                  }
              }
          });
      }
    }

    private void setBadgeCount(CurrentUser currentUser , boolean isOnline , OtherUser otherUser , String  target){
        if (!isOnline){
            CollectionReference db = FirebaseFirestore.getInstance().collection("user")
                    .document(otherUser.getUid())
                    .collection(target).document(currentUser.getUid()) .collection("badgeCount");
            Map<String,Object> map = new HashMap<>();
            map.put("badge","badge");
            db.add(map);

        }
    }
}