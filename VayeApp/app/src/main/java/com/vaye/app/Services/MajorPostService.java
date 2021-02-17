package com.vaye.app.Services;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.kongzue.dialog.v3.TipDialog;
import com.kongzue.dialog.v3.WaitDialog;
import com.vaye.app.Interfaces.MajorPostFallower;
import com.vaye.app.Interfaces.Notifications;
import com.vaye.app.Interfaces.OtherUserOptionsCompletion;
import com.vaye.app.Interfaces.StringCompletion;
import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.LessonFallowerUser;
import com.vaye.app.Model.LessonPostModel;
import com.vaye.app.Model.NewPostDataModel;
import com.vaye.app.Model.OtherUser;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MajorPostService {
    ArrayList<LessonPostModel> models;


    private static final MajorPostService instance = new MajorPostService();
    public static MajorPostService shared() {
        return instance;
    }

    public void setLike(CurrentUser currentUser , LessonPostModel post , TrueFalse<Boolean> result ){
        DocumentReference ref = FirebaseFirestore.getInstance().collection(currentUser.getShort_school())
                .document("lesson-post")
                .collection("post")
                .document(post.getPostId());
        Map<String , Object> mapLike = new HashMap<>();
        Map<String , Object> mapDisike = new HashMap<>();

        if (!post.getLikes().contains(currentUser.getUid())){
            post.getLikes().add(currentUser.getUid());
            post.getDislike().remove(currentUser.getUid());
            result.callBack(true);
            mapLike.put("likes",FieldValue.arrayUnion(currentUser.getUid()));
            mapDisike.put("dislike",FieldValue.arrayRemove(currentUser.getUid()));
            ref.update(mapLike);
            ref.update(mapDisike);
            NotificaitonService.shared().setPost_CommentLike(post , currentUser , Notifications.NotificationDescription.like_home,Notifications.NotificationType.like_home);
        }
        else{
            post.getLikes().remove(currentUser.getUid());
            result.callBack(true);
            mapLike.put("likes",FieldValue.arrayRemove(currentUser.getUid()));
            ref.update(mapLike);
            NotificaitonService.shared().remove_Like_Post_Comment_Notification(post,currentUser, Notifications.NotificationType.like_home);

        }
    }

    public void setDislike(CurrentUser currentUser , LessonPostModel post , TrueFalse<Boolean> result){
        DocumentReference ref = FirebaseFirestore.getInstance().collection(currentUser.getShort_school())
                .document("lesson-post")
                .collection("post")
                .document(post.getPostId());
        Map<String , Object> mapLike = new HashMap<>();


        if (!post.getDislike().contains(currentUser.getUid())){
            post.getLikes().remove(currentUser.getUid());
            post.getDislike().add(currentUser.getUid());
            result.callBack(true);
            mapLike.put("likes",FieldValue.arrayRemove(currentUser.getUid()));
            mapLike.put("dislike",FieldValue.arrayUnion(currentUser.getUid()));
            ref.update(mapLike);
        }else{
            post.getDislike().remove(currentUser.getUid());
            result.callBack(true);
            mapLike.put("dislike",FieldValue.arrayRemove(currentUser.getUid()));
            ref.update(mapLike);
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
        Map<String, Object> mapFav = new HashMap<>();
        Map<String , Object> mapUser = new HashMap<>();
        if (!post.getFavori().contains(currentUser.getUid())){
            post.getFavori().add(currentUser.getUid());
            result.callBack(true);
            mapFav.put("favori", FieldValue.arrayUnion(currentUser.getUid()));
            ref.update(mapFav).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isComplete()){
                        mapUser.put("postId",post.getPostId());
                        refUser.set(mapUser, SetOptions.merge());
                    }
                }
            });
        }else{
            post.getFavori().remove(currentUser.getUid());
            result.callBack(true);
            mapFav.put("favori",FieldValue.arrayRemove(currentUser.getUid()));
            ref.update(mapFav).addOnCompleteListener(new OnCompleteListener<Void>() {
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

  }

  public void setOtherUserOPtions(CurrentUser currentUser , OtherUser otherUser , LessonPostModel postModel , OtherUserOptionsCompletion<Boolean> completion){

      DocumentReference ref = FirebaseFirestore.getInstance().collection(currentUser.getShort_school())
              .document("lesson-post")
              .collection(currentUser.getBolum())
              .document(postModel.getLessonName())
              .collection("notification_getter")
              .document(currentUser.getUid());
      ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
          @Override
          public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    completion.isSilent(false);
                    if ( otherUser.getSlient().contains(currentUser.getUid())){
                        completion.isMute(true);
                    }else{
                        completion.isMute(false);
                    }
                    WaitDialog.dismiss();
                }else{
                    completion.isSilent(true);
                    if ( otherUser.getSlient().contains(currentUser.getUid())){
                        completion.isMute(true);
                    }else{
                        completion.isMute(false);
                    }
                    WaitDialog.dismiss();
                }
          }
      });


      // if slientUser.contains(currentUser.uid){
      //            completion(true)
      //        }else{
      //            completion(false)
      //        }



  }


}
