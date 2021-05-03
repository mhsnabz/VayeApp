package com.vaye.app.Services;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.kongzue.dialog.v3.TipDialog;
import com.kongzue.dialog.v3.WaitDialog;
import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.LoginRegister.StudentSignUp;
import com.vaye.app.Model.SchoolModel;
import com.vaye.app.Model.TaskUser;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TeacherRegisterService {
    private static final TeacherRegisterService instance = new TeacherRegisterService();
    public static TeacherRegisterService shared() {
        return instance;
    }

    public String getDoaminName(String email){
        String s = email;
        String[] words = s.split("@");
        return words[1];
    }

    public void setTaskTeacher(Context context, String name, String unvan , String email , String uid , String priority , SchoolModel model , TrueFalse<Boolean> callback){

        HashMap<String , Object> map = new HashMap<>();
        map.put("number","");
        map.put("schoolName",model.getName());
        map.put("short_school",model.getShortName());
        map.put("name",name);
        map.put("isValid",false);
        map.put("profileImage","");
        map.put("unvan",unvan);
        map.put("email",email);
        map.put("thumb_image","");
        map.put("priority",priority);
        map.put("username","");
        map.put("instagram","");
        map.put("twitter","");
        map.put("linkedin","");
        map.put("github","");
        map.put("uid",uid);
        map.put("slient", FieldValue.arrayUnion());
        DocumentReference ref = FirebaseFirestore.getInstance().collection("task-teacher")
                .document(uid);
        ref.set(map , SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    callback.callBack(true);
                }else {
                    callback.callBack(false);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e.getLocalizedMessage().equals("The email address is already in use by another account.")){
                    WaitDialog.dismiss();
                    TipDialog.show((AppCompatActivity)context,"Bu E-posta Adresi Zaten Kayıtlı", TipDialog.TYPE.ERROR);

                    TipDialog.dismiss(1000);
                    callback.callBack(false);
                    return;
                }else {
                    WaitDialog.dismiss();
                    TipDialog.show((AppCompatActivity)context,"Lütfen İnternet Bağlantınızı Kontrol Ediniz", TipDialog.TYPE.ERROR);

                    TipDialog.dismiss(1000);
                    callback.callBack(false);
                    return;
                }
            }
        });
    }

    public void setPriority (Context context,String  uid , String priority,TrueFalse<Boolean> callback){
        DocumentReference ref = FirebaseFirestore.getInstance().collection("priority")
                .document(uid);
        HashMap<String ,String > map = new HashMap<>();
        map.put("priority",priority);
        ref.set(map, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    callback.callBack(true);
                }else{
                    callback.callBack(false);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e.getLocalizedMessage().equals("The email address is already in use by another account.")){
                    WaitDialog.dismiss();
                    TipDialog.show((AppCompatActivity)context,"Bu E-posta Adresi Zaten Kayıtlı", TipDialog.TYPE.ERROR);

                    TipDialog.dismiss(1000);
                    callback.callBack(false);
                    return;
                }else {
                    WaitDialog.dismiss();
                    TipDialog.show((AppCompatActivity)context,"Lütfen İnternet Bağlantınızı Kontrol Ediniz", TipDialog.TYPE.ERROR);

                    TipDialog.dismiss(1000);
                    callback.callBack(false);
                    return;
                }
            }
        });
    }

    public void setStatus(Context context,String uid , TrueFalse<Boolean> callback){
        DocumentReference ref = FirebaseFirestore.getInstance().collection("status")
                .document(uid);
        HashMap<String ,Object > map = new HashMap<>();
        map.put("status",false);
        ref.set(map, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    callback.callBack(true);
                }else{
                    callback.callBack(false);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e.getLocalizedMessage().equals("The email address is already in use by another account.")){
                    WaitDialog.dismiss();
                    TipDialog.show((AppCompatActivity)context,"Bu E-posta Adresi Zaten Kayıtlı", TipDialog.TYPE.ERROR);

                    TipDialog.dismiss(1000);
                    callback.callBack(false);
                    return;
                }else {
                    WaitDialog.dismiss();
                    TipDialog.show((AppCompatActivity)context,"Lütfen İnternet Bağlantınızı Kontrol Ediniz", TipDialog.TYPE.ERROR);

                    TipDialog.dismiss(1000);
                    callback.callBack(false);
                    return;
                }
            }
        });
    }

    public void completeSignUp(TaskUser taskUser ,String  bolum , String fakulte ,String bolum_key , TrueFalse<Boolean> callback){

        DocumentReference db = FirebaseFirestore.getInstance().collection("user")
                .document(taskUser.getUid());
        Map<String , Object> map = new HashMap<>();
        map.put("number","");
        map.put("allowRequest",true);
        map.put("bolum_key",bolum_key);
        map.put("short_school",taskUser.getShort_school());
        map.put("schoolName",taskUser.getSchoolName());
        map.put("fakulte",fakulte);
        map.put("bolum",bolum);
        map.put("name",taskUser.getName());
        map.put("slient",FieldValue.arrayUnion());
        map.put("friendList",FieldValue.arrayUnion());
        map.put("mention",true);
        map.put("comment",true);
        map.put("like",true);
        map.put("follow",true);
        map.put("lessonNotices",true);
        map.put("profileImage","");
        map.put("unvan",taskUser.getUnvan());
        map.put("thumb_image","");
        map.put("email",taskUser.getEmail());
        map.put("priority","teacher");
        map.put("username",taskUser.getUsername());
        map.put("instagram","");
        map.put("twitter","");
        map.put("linkedin","");
        map.put("github","");
        map.put("uid",taskUser.getUid());
        map.put("blockList",FieldValue.arrayUnion());
        map.put("blockByOtherUser",FieldValue.arrayUnion());
        db.set(map, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    DocumentReference db = FirebaseFirestore.getInstance().collection("status")
                            .document(taskUser.getUid());
                    Map<String , Object> mapStatus = new HashMap<>();
                    mapStatus.put("status",true);
                    db.set(mapStatus,SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                DocumentReference db = FirebaseFirestore.getInstance().collection(taskUser.getShort_school())
                                        .document("teacher")
                                        .collection("uid").document(taskUser.getUid());
                                Map<String ,String> matTeacher = new HashMap<>();
                                matTeacher.put("uid",taskUser.getUid());
                                db.set(matTeacher,SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            setUserName(taskUser, new TrueFalse<Boolean>() {
                                                @Override
                                                public void callBack(Boolean _value) {
                                                    DocumentReference db = FirebaseFirestore.getInstance().collection("user")
                                                            .document(taskUser.getUid())
                                                            .collection("saved-task")
                                                            .document("task");
                                                    map.put("data",FieldValue.arrayUnion());
                                                    db.set(map,SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()){
                                                                callback.callBack(true);
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
            }
        });
    }

    public void setUserName(TaskUser taskUser , TrueFalse<Boolean> callback){
        DocumentReference db = FirebaseFirestore.getInstance().collection("username")
                .document(taskUser.getUsername());
        //        let dic = ["uid":currentUser.uid as Any , "email":currentUser.email  as String, "username":currentUser.username as String] as [String : Any]
        Map<String,Object> map = new HashMap<>();
        map.put("uid",taskUser.getUid());
        map.put("email",taskUser.getEmail());
        map.put("username",taskUser.getUsername());
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
