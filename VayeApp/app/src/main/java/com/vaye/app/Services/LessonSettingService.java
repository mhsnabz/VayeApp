package com.vaye.app.Services;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.kongzue.dialog.v3.TipDialog;
import com.kongzue.dialog.v3.WaitDialog;
import com.vaye.app.Interfaces.CallBackCount;
import com.vaye.app.Interfaces.PostSenderList;
import com.vaye.app.Interfaces.StringArrayListInterface;
import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.LessonModel;
import com.vaye.app.Model.LessonPostModel;
import com.vaye.app.Model.PostSenderInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LessonSettingService {
    String TAG = "LessonSettingService";
    private static final LessonSettingService instance = new LessonSettingService();
    public static LessonSettingService shared() {
        return instance;
    }
    public void getFollowersCont(String lessonName , CurrentUser currentUser ,  CallBackCount count){
        Log.d(TAG, "getFollowersCont: " + lessonName);
        CollectionReference ref = FirebaseFirestore.getInstance().collection(currentUser.getShort_school())
                .document("lesson")
                .collection(currentUser.getBolum())
                .document(lessonName)
                .collection("fallowers");
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
    public void checkIsFollowing(String  lessonName , CurrentUser currentUser , TrueFalse<Boolean> val){
        // let db = Firestore.firestore().collection("user").document(user.uid).collection("lesson").document(lessonName)
        DocumentReference ref = FirebaseFirestore.getInstance().collection("user")
                .document(currentUser.getUid())
                .collection("lesson")
                .document(lessonName);
        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().exists()){
                        val.callBack(true);
                    }else{
                        val.callBack(false);
                    }
                }
            }
        });
    }

   //TODO:-add lesson functions

    public void addLesson(LessonModel model , CurrentUser currentUser , Activity activity , TrueFalse<Boolean> callBack){
        WaitDialog.show((AppCompatActivity) activity, "Ders Ekleniyor");
        Map<String  , Object> mapFollow =  new HashMap<>();
        Map<String , Object> map = new HashMap<>();
        mapFollow.put("username",currentUser.getUsername());
        mapFollow.put("name",currentUser.getName());
        mapFollow.put("email",currentUser.getEmail());
        mapFollow.put("number",currentUser.getNumber());
        mapFollow.put("uid",currentUser.getUid());
        if (currentUser.getThumb_image()!=null){
            mapFollow.put("thumb_image",currentUser.getThumb_image());
        }else {
            mapFollow.put("thumb_image","");
        }
        if (model.getTopic()!=null && !model.getTopic().isEmpty()){
            FirebaseMessaging.getInstance().subscribeToTopic(model.getTopic()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Log.d(TAG, "onComplete: " + "subscribeToTopic : " + model.getTopic() );

                    }
                }
            });
        }

        DocumentReference refFallow = FirebaseFirestore.getInstance().collection(currentUser.getShort_school())
                .document("lesson")
                .collection(currentUser.getBolum())
                .document(model.getLessonName())
                .collection("fallowers")
                .document(currentUser.getUsername());

        map.put("teacherName",model.getTeacherName());
        map.put("lesson_key",model.getLesson_key());
        map.put("teacherId",model.getTeacherId());
        map.put("teacherEmail",model.getTeacherEmail());
        map.put("lessonName",model.getLessonName());
        if (model.getTopic()!=null && !model.getTopic().isEmpty()){
            map.put("topic",model.getTopic());
        }else{
            map.put("topic","empty");
        }
        DocumentReference ref = FirebaseFirestore.getInstance().collection("user")
                .document(currentUser.getUid())
                .collection("lesson")
                .document(model.getLessonName());
        ref.set(map , SetOptions.merge()).addOnCompleteListener(activity, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                        refFallow.set(mapFollow,SetOptions.merge()).addOnCompleteListener(activity, new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    getAllPost(currentUser, model.getLessonName(), activity, new PostSenderList() {
                                        @Override
                                        public void getPostSenderList(ArrayList<LessonPostModel> list) {
                                            if (list.isEmpty()){
                                                setNotificationGetter(currentUser, model.getLessonName(), new TrueFalse<Boolean>() {
                                                    @Override
                                                    public void callBack(Boolean _value) {
                                                        if (_value){
                                                            TipDialog.show((AppCompatActivity) activity, "Ders Eklendi", TipDialog.TYPE.SUCCESS);
                                                            TipDialog.dismiss(1500);
                                                            callBack.callBack(true);
                                                        }

                                                    }
                                                });

                                            }else{
                                                addAllLessonId(list,model.getLessonName(), currentUser, new TrueFalse<Boolean>() {
                                                    @Override
                                                    public void callBack(Boolean _value) {
                                                        if (_value){
                                                            setNotificationGetter(currentUser, model.getLessonName(), new TrueFalse<Boolean>() {
                                                                @Override
                                                                public void callBack(Boolean _value) {
                                                                    if (_value){
                                                                        TipDialog.show((AppCompatActivity) activity, "Ders Eklendi", TipDialog.TYPE.SUCCESS);
                                                                        TipDialog.dismiss(1500);
                                                                        callBack.callBack(true);
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
                            }
                        });
                }
            }
        });
    }
    private void getAllPost(CurrentUser currentUser, String lessonName ,Activity activity, PostSenderList list){
        ArrayList<String> postId = new ArrayList<>();
        ArrayList<LessonPostModel> posts = new ArrayList<>();
        Query db = FirebaseFirestore.getInstance().collection(currentUser.getShort_school())
                .document("lesson-post")
                .collection("post")
                .whereEqualTo("lessonName" , lessonName).orderBy("postId", Query.Direction.DESCENDING).orderBy("post_ID", Query.Direction.DESCENDING).limit(30);
        db.get().addOnCompleteListener(activity, new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.getResult().isEmpty()){
                  //  list.getArrayList(postId);
                    list.getPostSenderList(posts);

                }else {

                    for (DocumentSnapshot id : task.getResult().getDocuments()){
                        postId.add(id.getId());
                        posts.add(id.toObject(LessonPostModel.class));
                        Log.d(TAG, "onComplete: " + id.getId());
                    }
                    list.getPostSenderList(posts);
                }
            }
        });

    }
    private void addAllLessonId(ArrayList<LessonPostModel> postId ,String lessonName, CurrentUser currentUser , TrueFalse<Boolean> completion){
        //        //user/2YZzIIAdcUfMFHnreosXZOTLZat1/lesson-post/1599800825321
        CollectionReference ref = FirebaseFirestore.getInstance().collection("user").document(currentUser.getUid()).collection("lesson-post");
        for (LessonPostModel id : postId){
            Map<String , Object> idMap = new HashMap<>();
            idMap.put("lessonName",lessonName);
            idMap.put("postId",id.getPostId());
            idMap.put("postID",id.getPost_ID());
            idMap.put("senderUid",id.getSenderUid());
            ref.document(id.getPostId()).set(idMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isComplete()){
                        completion.callBack(true);
                    }
                }
            });
        }
    }
    private void setNotificationGetter(CurrentUser currentUser , String lessonName , TrueFalse<Boolean> completion ){

        DocumentReference ref = FirebaseFirestore.getInstance().collection(currentUser.getShort_school())
                .document("lesson")
                .collection(currentUser.getBolum())
                .document(lessonName)
                .collection("notification_getter")
                .document(currentUser.getUid());
        Map<String , String> map = new HashMap<>();
        map.put("uid",currentUser.getUid());
        ref.set(map , SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isComplete()){
                    completion.callBack(true);
                }
            }
        });
    }



    //TODO:-rremove lesson functions
    public void removeLesson(LessonModel model , CurrentUser currentUser , Activity activity){
        WaitDialog.show((AppCompatActivity) activity, "Ders Siliniyor");
        //  let db = Firestore.firestore().collection("user")
        //            .document(currentUser.uid).collection("lesson")
        //            .document(lessonName!)
        if (model.getTopic()!=null && !model.getTopic().isEmpty()){
            FirebaseMessaging.getInstance().unsubscribeFromTopic(model.getTopic()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Log.d(TAG, "onComplete: " + "unsubscribeFromTopic : " + model.getTopic());
                    }
                }
            });
        }
        DocumentReference reference = FirebaseFirestore.getInstance().collection("user")
                .document(currentUser.getUid())
                .collection("lesson")
                .document(model.getLessonName());
        reference.delete().addOnCompleteListener(activity, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    removeLessonFallower(currentUser, model.getLessonName(), activity, new TrueFalse<Boolean>() {
                        @Override
                        public void callBack(Boolean _value) {
                            if (_value){
                            getUserLessonPostId(currentUser, model.getLessonName(), new StringArrayListInterface() {
                                @Override
                                public void getArrayList(ArrayList<String> list) {
                                    removeAllPostId(currentUser, list, new TrueFalse<Boolean>() {
                                        @Override
                                        public void callBack(Boolean _value) {
                                            if (_value){
                                                removeNotificationGetterList(currentUser, model.getLessonName(), new TrueFalse<Boolean>() {
                                                    @Override
                                                    public void callBack(Boolean _value) {
                                                        if (_value){
                                                            TipDialog.show((AppCompatActivity) activity, "Ders Silindi", TipDialog.TYPE.SUCCESS);
                                                            TipDialog.dismiss(2000);
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    });
                                }
                            });
                            }
                        }
                    });
                }
            }
        });
    }

    private void removeLessonFallower(CurrentUser currentUser , String  lessonName , Activity activity , TrueFalse<Boolean> completion){
//let abc = Firestore.firestore().collection(currentUser.short_school)
//                    .document("lesson").collection(currentUser.bolum)
//                    .document(lessonName!).collection("fallowers").document(currentUser.username)
        DocumentReference ref = FirebaseFirestore.getInstance().collection(currentUser.getShort_school())
                .document("lesson")
                .collection(currentUser.getBolum())
                .document(lessonName)
                .collection("fallowers")
                .document(currentUser.getUsername());
        ref.delete().addOnCompleteListener(activity, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    completion.callBack(true);
                }
            }
        });
    }

    private void getUserLessonPostId(CurrentUser currentUser , String  lessonName , StringArrayListInterface list){
        ArrayList<String> postId = new ArrayList<>();
        Query db = FirebaseFirestore.getInstance().collection("user")
                .document(currentUser.getUid())
                .collection("lesson-post").whereEqualTo("lessonName",lessonName);
        db.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().isEmpty()){
                        list.getArrayList(postId);
                    }else{
                        for (DocumentSnapshot id : task.getResult().getDocuments()){
                            postId.add(id.getId());
                        }
                        list.getArrayList(postId);
                    }
                }
            }
        });
    }
    private void removeAllPostId(CurrentUser currentUser , ArrayList<String> list , TrueFalse<Boolean> completion){
        CollectionReference db = FirebaseFirestore.getInstance().collection("user")
                .document(currentUser.getUid())
                .collection("lesson-post");
        if (list.isEmpty()){
            completion.callBack(true);
        }else{
            for (String  id : list){
                db.document(id).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                     public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            completion.callBack(true);
                        }
                    }
                });
            }
        }
    }

    private void removeNotificationGetterList(CurrentUser currentUser ,String  lessonName , TrueFalse<Boolean> completion) {
        //let dbNoti = Firestore.firestore().collection(currentUser.short_school)
        //                                            .document("lesson").collection(currentUser.bolum)
        //                                            .document(lessonName!).collection("notification_getter").
        //                                            document(currentUser.uid)
        DocumentReference ref = FirebaseFirestore.getInstance().collection(currentUser.getShort_school())
                .document("lesson")
                .collection(currentUser.getBolum())
                .document(lessonName)
                .collection("notification_getter")
                .document(currentUser.getUid());
        ref.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    completion.callBack(true);
                }
            }
        });

    }


    public void addTeacherOnLesson(CurrentUser currentUser , LessonModel lessonName , TrueFalse<Boolean> callback){
        Map<String , Object> map = new HashMap<>();
        map.put("teacherName",currentUser.getName());
        map.put("teacherId",currentUser.getUid());
        map.put("teacherEmail",currentUser.getEmail());
        map.put("lessonName",lessonName);

        if (lessonName.getTopic()!=null && !lessonName.getTopic().isEmpty()){
            FirebaseMessaging.getInstance().subscribeToTopic(lessonName.getTopic()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        map.put("topic",lessonName.getTopic());
                        Log.d(TAG, "onComplete: " + "subscribeToTopic: " + lessonName.getTopic());
                    }
                }
            });
        }
        DocumentReference db = FirebaseFirestore.getInstance().collection(currentUser.getShort_school()).document("lesson")
                .collection(currentUser.getBolum())
                .document(lessonName.getLessonName());
        db.set(map , SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    setNotificationGetter(currentUser, lessonName.getLessonName(), new TrueFalse<Boolean>() {
                        @Override
                        public void callBack(Boolean _value) {
                            if (_value){
                                //user/2YZzIIAdcUfMFHnreosXZOTLZat1/lesson/Bilgisayar Programlama
                                DocumentReference db = FirebaseFirestore.getInstance().collection("user")
                                        .document(currentUser.getUid())
                                        .collection("lesson")
                                        .document(lessonName.getLessonName());
                                db.set(map,SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            Query db = FirebaseFirestore.getInstance().collection(currentUser.getShort_school())
                                                    .document("lesson")
                                                    .collection(currentUser.getBolum())
                                                    .document(lessonName.getLessonName())
                                                    .collection("lesson-post").limitToLast(30).orderBy("postId");
                                            ////user/2YZzIIAdcUfMFHnreosXZOTLZat1/lesson/Bilgisayar Programlama
                                            CollectionReference dbLesson = FirebaseFirestore.getInstance().collection("user")
                                                    .document(currentUser.getUid())
                                                    .collection("lesson-post");
                                            db.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()){
                                                        if (task.getResult().getDocuments().isEmpty()){
                                                            callback.callBack(true);
                                                        }else{
                                                            for (DocumentSnapshot item : task.getResult().getDocuments()) {
                                                                Map<String  , String > lessonMap = new HashMap<>();
                                                                lessonMap.put("postId",item.getId());
                                                                lessonMap.put("lessonName",lessonName.getLessonName());
                                                                dbLesson.document(item.getId()).set(lessonMap,SetOptions.merge());
                                                            }
                                                            callback.callBack(true);
                                                        }
                                                    }
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    callback.callBack(false);
                                                }
                                            });

                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        callback.callBack(false);
                                    }
                                });

                            }
                        }
                    });

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callback.callBack(false);
            }
        });
    }
    public void removeTeacheronLesson(Context context, CurrentUser currentUser , LessonModel lessonName , TrueFalse<Boolean> callbaclk){
        Map<String , Object> map = new HashMap<>();
        map.put("teacherName","empty");
        map.put("teacherId","empty");
        map.put("teacherEmail","empty");
        map.put("lessonName",lessonName);
        if (lessonName.getTopic()!=null && !lessonName.getTopic().isEmpty()){
            FirebaseMessaging.getInstance().unsubscribeFromTopic(lessonName.getTopic()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                     if (task.isSuccessful()){
                         Log.d(TAG, "onComplete: " + "unsubscribeFromTopic : " + lessonName.getTopic());
                    }
                }
            });
        }
        DocumentReference db = FirebaseFirestore.getInstance().collection(currentUser.getShort_school()).document("lesson")
                .collection(currentUser.getBolum())
                .document(lessonName.getLessonName());
        db.set(map , SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                          removeNotificationGetterList(currentUser, lessonName.getLessonName(), new TrueFalse<Boolean>() {
                              @Override
                              public void callBack(Boolean _value) {
                                  getUserLessonPostId(currentUser, lessonName.getLessonName(), new StringArrayListInterface() {
                                      @Override
                                      public void getArrayList(ArrayList<String> list) {
                                          removeAllPostId(currentUser, list, new TrueFalse<Boolean>() {
                                              @Override
                                              public void callBack(Boolean _value) {
                                                  if (_value){
                                                      removeNotificationGetterList(currentUser, lessonName.getLessonName(), new TrueFalse<Boolean>() {
                                                          @Override
                                                          public void callBack(Boolean _value) {
                                                              if (_value){
                                                                  DocumentReference ref = FirebaseFirestore.getInstance().collection("user")
                                                                          .document(currentUser.getUid())
                                                                          .collection("lesson")
                                                                          .document(lessonName.getLessonName());
                                                                  ref.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                      @Override
                                                                      public void onComplete(@NonNull Task<Void> task) {
                                                                          if (task.isSuccessful()){
                                                                              WaitDialog.dismiss();
                                                                              callbaclk.callBack(true);
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
                                  });
                              }
                          });
                }
            }
        });
    }

}
