package com.vaye.app.Services;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.kongzue.dialog.v3.TipDialog;
import com.kongzue.dialog.v3.WaitDialog;
import com.vaye.app.Controller.HomeController.SinglePost.SinglePostActivity;
import com.vaye.app.Controller.NotificationService.MajorPostNotification;
import com.vaye.app.Controller.NotificationService.MajorPostNotificationService;
import com.vaye.app.Controller.NotificationService.NotificationPostType;
import com.vaye.app.Controller.NotificationService.PushNotificationService;
import com.vaye.app.Controller.NotificationService.PushNotificationTarget;
import com.vaye.app.Interfaces.LessonFallowersCallback;
import com.vaye.app.Interfaces.MajorPostFallower;
import com.vaye.app.Interfaces.Notifications;
import com.vaye.app.Interfaces.OtherUserOptionsCompletion;
import com.vaye.app.Interfaces.SingleLessonPost;
import com.vaye.app.Interfaces.StringCompletion;
import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.LessonFallowerUser;
import com.vaye.app.Model.LessonPostModel;
import com.vaye.app.Model.NewPostDataModel;
import com.vaye.app.Model.OtherUser;
import com.vaye.app.Util.Helper;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MajorPostService {
    ArrayList<LessonPostModel> models;


    private static final MajorPostService instance = new MajorPostService();
    public static MajorPostService shared() {
        return instance;
    }


    public void getPost(Context context , CurrentUser currentUser , String postId , SingleLessonPost post){
        DocumentReference reference = FirebaseFirestore.getInstance().collection(currentUser.getShort_school())
                .document("lesson-post")
                .collection("post")
                .document(postId);
        reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().exists()){
                        post.getPost(task.getResult().toObject(LessonPostModel.class));

                    }else{
                        post.getPost(null);
                        WaitDialog.dismiss();
                        TipDialog.show((AppCompatActivity)context,"Gönderi Silinmiş", TipDialog.TYPE.ERROR);
                        TipDialog.dismiss(1000);
                    }

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                post.getPost(null);
                WaitDialog.dismiss();
                TipDialog.show((AppCompatActivity)context,"Hata Oluştu", TipDialog.TYPE.ERROR);
                TipDialog.dismiss(1000);

            }
        });
    }

    public void setLike(CurrentUser currentUser , LessonPostModel post , TrueFalse<Boolean> result ){
        DocumentReference ref = FirebaseFirestore.getInstance().collection(currentUser.getShort_school())
                .document("lesson-post")
                .collection("post")
                .document(post.getPostId());


        if (!post.getLikes().contains(currentUser.getUid())){
            post.getLikes().add(currentUser.getUid());
            post.getDislike().remove(currentUser.getUid());
            result.callBack(true);
            Map<String , Object> mapLike = new HashMap<>();
            mapLike.put("likes",FieldValue.arrayUnion(currentUser.getUid()));
            mapLike.put("dislike",FieldValue.arrayRemove(currentUser.getUid()));
            ref.set(mapLike , SetOptions.merge());
            MajorPostNotificationService.shared().setPostLike(NotificationPostType.name.lessonPost,post,currentUser,post.getText(), MajorPostNotification.type.post_like);
            PushNotificationService.shared().sendPushNotification(String.valueOf(Calendar.getInstance().getTimeInMillis()),post.getSenderUid(),null,PushNotificationTarget.like,currentUser.getName(),post.getText(),MajorPostNotification.descp.post_like,currentUser.getUid());
        }
        else{
            post.getLikes().remove(currentUser.getUid());
            result.callBack(true);
            Map<String , Object> mapLike = new HashMap<>();
            mapLike.put("likes",FieldValue.arrayRemove(currentUser.getUid()));
            ref.update(mapLike);


        }
    }

    public void setDislike(CurrentUser currentUser , LessonPostModel post , TrueFalse<Boolean> result){
        DocumentReference ref = FirebaseFirestore.getInstance().collection(currentUser.getShort_school())
                .document("lesson-post")
                .collection("post")
                .document(post.getPostId());



        if (!post.getDislike().contains(currentUser.getUid())){
            post.getLikes().remove(currentUser.getUid());
            post.getDislike().add(currentUser.getUid());
            result.callBack(true);
            Map<String , Object> mapLike = new HashMap<>();
            mapLike.put("likes",FieldValue.arrayRemove(currentUser.getUid()));
            mapLike.put("dislike",FieldValue.arrayUnion(currentUser.getUid()));
            ref.set(mapLike,SetOptions.merge());
        }else{
            post.getDislike().remove(currentUser.getUid());
            result.callBack(true);
            Map<String , Object> mapLike = new HashMap<>();
            mapLike.put("dislike",FieldValue.arrayRemove(currentUser.getUid()));
            ref.set(mapLike , SetOptions.merge());
        }
    }

    public void setFav(CurrentUser currentUser , LessonPostModel post , TrueFalse<Boolean> result){
        DocumentReference ref = FirebaseFirestore.getInstance().collection(currentUser.getShort_school())
                .document("lesson-post")
                .collection("post")
                .document(post.getPostId());
        DocumentReference refUser =  FirebaseFirestore.getInstance().collection("user")
                .document(currentUser.getUid())
                .collection("fav-post")
                .document(post.getPostId());

        Map<String , Object> mapUser = new HashMap<>();
        if (!post.getFavori().contains(currentUser.getUid())){
            post.getFavori().add(currentUser.getUid());
            result.callBack(true);
            Map<String, Object> mapFav = new HashMap<>();
            mapFav.put("favori", FieldValue.arrayUnion(currentUser.getUid()));
            ref.set(mapFav, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isComplete()){
                        mapUser.put("postId",post.getPostId());
                        refUser.set(mapUser, SetOptions.merge());
                    }
                }
            });
        }else{
            Map<String, Object> mapFav = new HashMap<>();
            post.getFavori().remove(currentUser.getUid());
            result.callBack(true);
            mapFav.put("favori",FieldValue.arrayRemove(currentUser.getUid()));
            ref.set(mapFav,SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isComplete()){
                        refUser.delete();
                    }
                }
            });
        }
    }

    public void setCurrentUserPostSlient(Activity activity , CurrentUser currentUser , LessonPostModel post , TrueFalse<Boolean> completion){
        WaitDialog.show((AppCompatActivity) activity, null);

        DocumentReference ref = FirebaseFirestore.getInstance().collection(currentUser.getShort_school())
                .document("lesson-post")
                .collection("post")
                .document(post.getPostId());
        Map<String , Object> map = new HashMap<>();
        map.put("silent",FieldValue.arrayUnion(currentUser.getUid()));
        ref.set(map , SetOptions.merge()).addOnCompleteListener(activity, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    TipDialog.show((AppCompatActivity) activity, "Gönderi Bildirimleri Kapatıldı ", TipDialog.TYPE.SUCCESS);
                    post.getSilent().add(currentUser.getUid());
                    completion.callBack(true);
                }
            }
        });
    }
    public void setCurrentUserPostNotSilent(Activity activity , CurrentUser currentUser , LessonPostModel post , TrueFalse<Boolean> completion){
        WaitDialog.show((AppCompatActivity) activity, null);
        DocumentReference ref = FirebaseFirestore.getInstance().collection(currentUser.getShort_school())
                .document("lesson-post")
                .collection("post")
                .document(post.getPostId());
        Map<String , Object> map = new HashMap<>();
        map.put("silent",FieldValue.arrayRemove(currentUser.getUid()));
        ref.set(map , SetOptions.merge()).addOnCompleteListener(activity, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    TipDialog.show((AppCompatActivity) activity, "Gönderi Bildirimleri Açıldı ", TipDialog.TYPE.SUCCESS);
                    post.getSilent().remove(currentUser.getUid());

                    completion.callBack(true);
                }
            }
        });
    }

    public void check_currentUser_post_is_slient(CurrentUser currentUser , LessonPostModel model , TrueFalse<Boolean> completion){
        if (model.getSilent().contains(currentUser.getUid())){
            completion.callBack(true);
        }else{
            completion.callBack(false);
        }
    }

    public String getLink(String url) throws URISyntaxException {
        URI uri = new URI(url);
        String domain = uri.getHost();
        if (  domain == null ||domain.isEmpty()){
            return  "";
        }
        return domain.startsWith("www.") ? domain.substring(4) : domain;
    }

    public void deleteImage(Activity activity ,String url , String thumb_url,CurrentUser currentUser ,LessonPostModel postModel, TrueFalse<Boolean> val){
        WaitDialog.show((AppCompatActivity) activity, "Dosya Siliniyor");
        FirebaseStorage ref = FirebaseStorage.getInstance();

        Map<String , Object> map = new HashMap<>();
        map.put("data",FieldValue.arrayRemove(url));
        ref.getReferenceFromUrl(url).delete().addOnCompleteListener(activity, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    DocumentReference reference = FirebaseFirestore.getInstance().collection(currentUser.getShort_school())
                            .document("lesson-post").collection("post").document(postModel.getPostId());
                    reference.set(map,SetOptions.merge()).addOnCompleteListener(activity, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                deleteThumbUrl(activity, thumb_url, currentUser, postModel, new TrueFalse<Boolean>() {
                                    @Override
                                    public void callBack(Boolean _value) {
                                        if (_value){
                                            val.callBack(_value);
                                            WaitDialog.dismiss();
                                            TipDialog.show((AppCompatActivity) activity, "Dosya Silindi", TipDialog.TYPE.SUCCESS);
                                            TipDialog.dismiss(1500);
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });

    }

    private void deleteThumbUrl(Activity activity , String url ,CurrentUser currentUser , LessonPostModel postModel , TrueFalse<Boolean> val){
        //let db = Firestore.firestore().collection(currentUser.short_school)
        //                    .document("lesson-post").collection("post")
        //                    .document(postId)
        FirebaseStorage ref = FirebaseStorage.getInstance();
        ref.getReferenceFromUrl(url).delete().addOnCompleteListener(activity, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    DocumentReference reference = FirebaseFirestore.getInstance().collection(currentUser.getShort_school())
                            .document("lesson-post").collection("post").document(postModel.getPostId());
                        Map<String , Object> map = new HashMap<>();
                    map.put("thumbData",FieldValue.arrayRemove(url));
                    reference.set(map,SetOptions.merge()).addOnCompleteListener(activity, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                val.callBack(true);
                            }
                        }
                    });
                }
            }
        });
    }


    public void deleteFileReference(Activity activity , String url ,String thumb_url ,CurrentUser currentUser,  TrueFalse<Boolean> val){
        FirebaseStorage ref = FirebaseStorage.getInstance();
        ref.getReferenceFromUrl(url).delete().addOnSuccessListener(activity, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                ref.getReferenceFromUrl(thumb_url).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                            ///user/VUSU6uA0odX7vuF5giXWbOUYzni1/saved-task/task
                        DocumentReference ref = FirebaseFirestore.getInstance().collection("user")
                                .document(currentUser.getUid()).collection("saved-task").document("task");
                        Map<String , Object> map = new HashMap<>();
                        map.put("data",FieldValue.arrayRemove(url));
                        map.put("thumbData", FieldValue.arrayRemove(thumb_url));
                        ref.set(map , SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
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
        });
    }
    public void addLink(LessonPostModel post,String link , CurrentUser currentUser , Activity activity , TrueFalse<Boolean> val){
        // let db = Firestore.firestore().collection(currentUser.short_school)
        //                .document("lesson-post").collection("post").document(post.postId)
        DocumentReference reference = FirebaseFirestore.getInstance().collection(currentUser.getShort_school())
                .document("lesson-post")
                .collection("post")
                .document(post.getPostId());
        Map<String , String> map = new HashMap<>();
        map.put("link",link);
        reference.set(map , SetOptions.merge()).addOnCompleteListener(activity, new OnCompleteListener<Void>() {
            @Override
             public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        val.callBack(true);
                    }
            }
        });
    }


    public void deleteLink(LessonPostModel post , CurrentUser currentUser , Activity activity , TrueFalse<Boolean> val){
        WaitDialog.show((AppCompatActivity) activity,"");
        DocumentReference reference = FirebaseFirestore.getInstance().collection(currentUser.getShort_school())
                .document("lesson-post")
                .collection("post")
                .document(post.getPostId());
        Map<String , String> map = new HashMap<>();
        map.put("link","");
        reference.set(map , SetOptions.merge()).addOnCompleteListener(activity, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    val.callBack(true);
                    WaitDialog.dismiss();
                }
            }
        });
    }

    public void updatePost(Activity activity,String _text ,String postId, CurrentUser currentUser , TrueFalse<Boolean> val){
            WaitDialog.show((AppCompatActivity) activity , null);
        DocumentReference ref = FirebaseFirestore.getInstance().collection(currentUser.getShort_school())
                .document("lesson-post")
                .collection("post")
                .document(postId);
        Map<String , String> map = new HashMap<>();
        map.put("text",_text);
        ref.set(map,SetOptions.merge()).addOnCompleteListener(activity, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    val.callBack(true);
                    WaitDialog.dismiss();
                    TipDialog.show((AppCompatActivity) activity ,"Gönderiniz Güncellendi", TipDialog.TYPE.SUCCESS);
                    TipDialog.dismiss(1500);
                }
            }
        });
    }

    public void setLinkOnSavedTask(CurrentUser currentUser , String link , TrueFalse<Boolean> val){
        DocumentReference ref = FirebaseFirestore.getInstance().collection("user")
                .document(currentUser.getUid())
                .collection("saved-task").document("task");
        Map<String , Object> map = new HashMap<>();
        map.put("link",link);
        ref.set(map , SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                val.callBack(true);
            }
        });
    }
    public void deleteSavedLink(CurrentUser currentUser , TrueFalse<Boolean> val){
        DocumentReference ref = FirebaseFirestore.getInstance().collection("user")
                .document(currentUser.getUid())
                .collection("saved-task").document("task");
        Map<String , Object> map = new HashMap<>();
        map.put("link","");

        ref.set(map , SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                val.callBack(true);
            }
        });
    }

    public void moveSavedLinkOnpost(String postId , CurrentUser currentUser , TrueFalse<Boolean> val){
        DocumentReference ref = FirebaseFirestore.getInstance().collection("user")
                .document(currentUser.getUid())
                .collection("saved-task")
                .document("task");
                ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.getString("link")!=null && !documentSnapshot.getString("link").isEmpty())   {

                        //İSTE/lesson-post/post/1610231975623
                            DocumentReference reference = FirebaseFirestore.getInstance().collection(currentUser.getShort_school())
                                    .document("lesson-post")
                                    .collection("post")
                                    .document(postId);
                            Map<String , String> map = new HashMap<>();
                            map.put("link",documentSnapshot.getString("link"));
                            reference.set(map , SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    val.callBack(true);
                                }
                            });
                        }else{
                            val.callBack(true);
                        }
                    }
                });
    }


    public void teacherSetNewPost(String  lesson_key , String  link , CurrentUser currentUser , long postId , ArrayList<String> lessonFallowerUsers
            , String msgText , ArrayList<NewPostDataModel> datas , String lessonName , TrueFalse<Boolean> val){
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
        map.put("lessonName",lessonName);
        map.put("lesson_key",lesson_key);
        map.put("likes",FieldValue.arrayUnion());
        map.put("favori", FieldValue.arrayUnion());
        map.put("postId",String.valueOf(postId));
        map.put("senderUid",currentUser.getUid());
        map.put("silent",FieldValue.arrayUnion());
        map.put("comment",0);
        map.put("dislike",FieldValue.arrayUnion());
        map.put("post_ID",postId);
        map.put("username",currentUser.getUsername());
        map.put("thumb_image",currentUser.getThumb_image());
        if (link == null || link.isEmpty()){
            map.put("link","");
        }else{
            map.put("link",link);
        }



        DocumentReference ref = FirebaseFirestore.getInstance().collection("user")
                .document(currentUser.getUid()).collection("my-post").document(String.valueOf(postId));
        Map<String , String> myPost = new HashMap<>();
        myPost.put("postId",String.valueOf(postId));
        ref.set(myPost,SetOptions.merge());

        setPostForLesson(datas,currentUser, map, lessonName, String.valueOf(postId), new TrueFalse<Boolean>() {
            @Override
            public void callBack(Boolean _value) {
                if (_value){
                    setPostForLessonFollower(currentUser.getUid(),lessonFallowerUsers, String.valueOf(postId), new TrueFalse<Boolean>() {
                        @Override
                        public void callBack(Boolean _value) {
                            DocumentReference ref = FirebaseFirestore.getInstance().collection("user")
                                    .document(currentUser.getUid()).collection("lesson-post").document(String.valueOf(postId));
                            HashMap<String , Object> stringObjectHashMap = new HashMap<>();
                            stringObjectHashMap.put("postId",String.valueOf(postId));
                            stringObjectHashMap.put("lessonName",lessonName);
                            ref.set(stringObjectHashMap,SetOptions.merge());
                            val.callBack(true);
                        }
                    });
                }
            }
        });
    }

    public void setNewPost( String  lesson_key , String  link , CurrentUser currentUser , long postId , ArrayList<LessonFallowerUser> lessonFallowerUsers
    , String msgText , ArrayList<NewPostDataModel> datas , String lessonName , TrueFalse<Boolean> val){

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
        map.put("lessonName",lessonName);
        map.put("lesson_key",lesson_key);
        map.put("likes",FieldValue.arrayUnion());
        map.put("favori", FieldValue.arrayUnion());
        map.put("postId",String.valueOf(postId));
        map.put("senderUid",currentUser.getUid());
        map.put("silent",FieldValue.arrayUnion());
        map.put("comment",0);
        map.put("dislike",FieldValue.arrayUnion());
        map.put("post_ID",postId);
        map.put("username",currentUser.getUsername());
        map.put("thumb_image",currentUser.getThumb_image());
        if (link == null || link.isEmpty()){
            map.put("link","");
        }else{
            map.put("link",link);
        }

        DocumentReference ref = FirebaseFirestore.getInstance().collection("user")
                .document(currentUser.getUid()).collection("my-post").document(String.valueOf(postId));
        Map<String , String> myPost = new HashMap<>();
        myPost.put("postId",String.valueOf(postId));
        ref.set(myPost,SetOptions.merge());
        setPostForLesson(datas,currentUser, map, lessonName, String.valueOf(postId), new TrueFalse<Boolean>() {
            @Override
            public void callBack(Boolean _value) {
                if (_value){
                    setPostForUser(lessonFallowerUsers, String.valueOf(postId), new TrueFalse<Boolean>() {
                        @Override
                        public void callBack(Boolean _value) {



                            val.callBack(true);
                        }
                    });
                }
            }
        });
    }

    private void setPostForLesson(ArrayList<NewPostDataModel> datas,CurrentUser currentUser , Map<String, Object> dic , String lessonName , String  postId , TrueFalse<Boolean> val){
        //let db = Firestore.firestore().collection(short_school).document("lesson-post")
        //            .collection("post").document(postId)
        DocumentReference ref = FirebaseFirestore.getInstance().collection(currentUser.getShort_school())
                .document("lesson-post")
                .collection("post")
                .document(postId);
        ref.set(dic,SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                   // let db = Firestore.firestore().collection(short_school).document("lesson").collection(currentUser.bolum)
                //                    .document(lessonName).collection("lesson-post").document(postId)
                DocumentReference reference = FirebaseFirestore.getInstance().collection(currentUser.getShort_school())
                        .document("lesson")
                        .collection(currentUser.getBolum())
                        .document(lessonName)
                        .collection("lesson-post")
                        .document(postId);
                Map<String , String > postMap = new HashMap<>();
                postMap.put("postId",postId);
                postMap.put("lessonName",lessonName);
                reference.set(postMap,SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        val.callBack(true);
                    }
                });
            }
        });
    }
    private void setPostForUser(ArrayList<LessonFallowerUser> userId , String postId , TrueFalse<Boolean> val){
        Map<String  , String> map = new HashMap<>();
        map.put("postId",postId);
        CollectionReference ref = FirebaseFirestore.getInstance().collection("user")  ;
        for (LessonFallowerUser user : userId){
            ref.document(user.getUid())
                    .collection("lesson-post")
                    .document(postId).set(map , SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    val.callBack(true);
                }
            });

        }
    }

    public void setPostForLessonFollower(String currentUserUid,ArrayList<String> userId , String postId , TrueFalse<Boolean> val){
        Map<String  , String> map = new HashMap<>();
        map.put("postId",postId);
        CollectionReference ref = FirebaseFirestore.getInstance().collection("user")  ;
        for (String user : userId){
            ref.document(user)
                    .collection("lesson-post")
                    .document(postId).set(map , SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    val.callBack(true);

                }
            });

        }
    }
  public void getLessonFallower(CurrentUser currentUser , String lessonName , MajorPostFallower completion){
        ArrayList<LessonFallowerUser> list = new ArrayList<>();
//let db = Firestore.firestore().collection(sorthSchoolName)
//               .document("lesson").collection(major).document(lessonName).collection("fallowers")
      CollectionReference ref = FirebaseFirestore.getInstance().collection(currentUser.getShort_school())
              .document("lesson")
              .collection(currentUser.getBolum())
              .document(lessonName).collection("fallowers");
      ref.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
          @Override
          public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.isEmpty()){
                    completion.onCallback(list);
                }else{
                    for (DocumentSnapshot item : queryDocumentSnapshots.getDocuments()){
                        list.add(item.toObject(LessonFallowerUser.class));
                    }
                    completion.onCallback(list);
                }
          }
      });

  }

  public void deleteCurrentUserPost(Activity activity ,CurrentUser currentUser ,LessonPostModel postModel , TrueFalse<Boolean> val){

      WaitDialog.show((AppCompatActivity) activity , "Gönderi Siliniyor\n Lütfen Bekleyin..");
      DocumentReference ref = FirebaseFirestore.getInstance().collection(currentUser.getShort_school())
              .document("lesson-post")
              .collection("post")
              .document(postModel.getPostId());
        if ( postModel.getThumbData()==null || postModel.getThumbData().isEmpty() ){

            ref.delete().addOnSuccessListener(activity, new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    val.callBack(true);
                }
            });
        }else{

            ref.delete().addOnSuccessListener(activity, new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    for (int i = 0 ; i < postModel.getThumbData().size() ; i++){
                        deleteFileReference(activity, postModel.getData().get(i), postModel.getThumbData().get(i),currentUser, new TrueFalse<Boolean>() {
                            @Override
                            public void callBack(Boolean _value) {
                                val.callBack(true);
                            }
                        });
                    }
                }
            });

        }

        CollectionReference refComment = FirebaseFirestore.getInstance().collection("comment")
                .document(postModel.getPostId()).collection("comment");
        refComment.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().isEmpty()){
                        return;
                    }else {
                        for (DocumentSnapshot doc : task.getResult().getDocuments()){
                            if (doc.get("replies") != null){
                                ArrayList<String> repliedComment = (ArrayList<String>) doc.get("replies");
                                deleteRepliedComment(repliedComment,postModel.getPostId(),doc.getId());
                                Log.d("array list ", "onComplete: "+ (ArrayList<String>) doc.get("replies"));
                            }
                            DocumentReference ref = FirebaseFirestore.getInstance().collection("comment")
                                    .document(postModel.getPostId()).collection("comment").document(doc.getId());
                            ref.delete();
                        }
                    }
                }
            }
        });


  }

  private void deleteRepliedComment(ArrayList<String> comment , String postId,String commentId){
        for (String item : comment){
            Query refDeleteRepliedComment = FirebaseFirestore.getInstance().collection("comment")
                    .document(postId).collection("comment-replied").document("comment").collection(commentId).whereEqualTo("commentId",item);
            refDeleteRepliedComment.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()){
                        if (!task.getResult().isEmpty()){
                            for (DocumentSnapshot doc : task.getResult().getDocuments()){
                                DocumentReference reference =  FirebaseFirestore.getInstance().collection("comment")
                                        .document(postId).collection("comment-replied").document("comment").collection(commentId).document(doc.getId());
                                reference.delete();
                            }

                        }
                    }
                }
            });
        }

  }





}
