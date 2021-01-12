package com.vaye.app.Services;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.kongzue.dialog.v3.TipDialog;
import com.kongzue.dialog.v3.WaitDialog;
import com.vaye.app.Interfaces.CallBackCount;
import com.vaye.app.Interfaces.StringArrayListInterface;
import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.LessonModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LessonSettingService {
    private static final LessonSettingService instance = new LessonSettingService();
    public static LessonSettingService shared() {
        return instance;
    }
    public void getFollowersCont(String lessonName , CurrentUser currentUser ,  CallBackCount count){
//İSTE/lesson/Bilgisayar Mühendisliği/Bilgisayar Programlama/fallowers
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

    public void addLesson(LessonModel model , CurrentUser currentUser , Activity activity){
        WaitDialog.show((AppCompatActivity) activity, "Ders Ekleniyor");
        Map<String  , Object> mapFollow =  new HashMap<>();

        mapFollow.put("username",currentUser.getUsername());
        mapFollow.put("name",currentUser.getUsername());
        mapFollow.put("email",currentUser.getUsername());
        mapFollow.put("number",currentUser.getUsername());
        mapFollow.put("uid",currentUser.getUsername());
        if (currentUser.getThumb_image()!=null){
            mapFollow.put("thumb_image",currentUser.getThumb_image());
        }else {
            mapFollow.put("thumb_image","");
        }

        DocumentReference refFallow = FirebaseFirestore.getInstance().collection(currentUser.getShort_school())
                .document("lesson")
                .collection(currentUser.getBolum())
                .document(model.getLessonName())
                .collection("fallowers")
                .document(currentUser.getUsername());
        Map<String , Object> map = new HashMap<>();
        map.put("teacherName",model.getTeacherName());
        map.put("lesson_key",model.getLesson_key());
        map.put("teacherId",model.getTeacherId());
        map.put("teacherEmail",model.getTeacherEmail());
        map.put("lessonName",model.getLessonName());
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
                                    getAllPost(currentUser, model.getLessonName(), activity, new StringArrayListInterface() {
                                        @Override
                                        public void getArrayList(ArrayList<String> list) {
                                            addAllLessonId(list, currentUser, new TrueFalse<Boolean>() {
                                                @Override
                                                public void callBack(Boolean _value) {
                                                    if (_value){
                                                        setNotificationGetter(currentUser, model.getLessonName(), new TrueFalse<Boolean>() {
                                                            @Override
                                                            public void callBack(Boolean _value) {
                                                                TipDialog.show((AppCompatActivity) activity, "Ders Eklendi", TipDialog.TYPE.SUCCESS);
                                                                TipDialog.dismiss(1500);
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
    private void getAllPost(CurrentUser currentUser, String lessonName ,Activity activity, StringArrayListInterface list){
        ArrayList<String> postId = new ArrayList<>();
        //let db = Firestore.firestore().collection(currentUser.short_school)
            //    .document("lesson-post").collection("post").whereField("lessonName", isEqualTo: lessonName)

        Query db = FirebaseFirestore.getInstance().collection(currentUser.getShort_school())
                .document("lesson-post")
                .collection("post")
                .whereEqualTo("lessonName" , lessonName).limit(10);
        db.get().addOnCompleteListener(activity, new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.getResult().isEmpty()){
                    list.getArrayList(postId);
                }else {
                    for (DocumentSnapshot id : task.getResult().getDocuments()){
                        postId.add(id.getId());
                    }
                    list.getArrayList(postId);
                }
            }
        });

    }
    private void addAllLessonId(ArrayList<String> postId , CurrentUser currentUser , TrueFalse<Boolean> completion){
        //        //user/2YZzIIAdcUfMFHnreosXZOTLZat1/lesson-post/1599800825321
        CollectionReference ref = FirebaseFirestore.getInstance().collection("user").document(currentUser.getUid()).collection("lesson-post");
        for (String id : postId){
            Map<String , String> idMap = new HashMap<>();
            idMap.put("postId",id);
            ref.document(id).set(idMap).addOnCompleteListener(new OnCompleteListener<Void>() {
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


}
