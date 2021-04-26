package com.vaye.app.Services;

import android.content.Context;

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
import com.google.firebase.iid.FirebaseInstanceId;
import com.kongzue.dialog.v3.TipDialog;
import com.kongzue.dialog.v3.WaitDialog;
import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.Model.SchoolModel;
import com.vaye.app.Model.TaskUser;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class StudentSignUpService {
    private static final StudentSignUpService instance = new StudentSignUpService();
    public static StudentSignUpService shared() {
        return instance;
    }
    public void checkNumberIsExist(Context context, SchoolModel model, String number , TrueFalse<Boolean> callback){

        if (number==null && number.isEmpty()){
            WaitDialog.dismiss();
            TipDialog.show((AppCompatActivity)context,"Lütfen Geçerli Bir Numara Giriniz", TipDialog.TYPE.ERROR);
            TipDialog.dismiss(750);
            return;
        }
        DocumentReference ref = FirebaseFirestore.getInstance().collection(model.getShortName())
                .document("students")
                .collection("student-number").document(number);
        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().exists()){
                        callback.callBack(true);
                    }else {
                        callback.callBack(false);
                    }
                }
            }
        });
    }
    public void setTaskStudent(Context context, String name, String email ,String  number, String uid , String priority , SchoolModel model , TrueFalse<Boolean> callback){
        HashMap<String , Object> map = new HashMap<>();
        map.put("number",number);
        map.put("schoolName",model.getName());
        map.put("short_school",model.getShortName());
        map.put("name",name);
        map.put("isValid",false);
        map.put("profileImage","");
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
        DocumentReference ref = FirebaseFirestore.getInstance().collection("task-user")
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
    public void setPriority (Context context, String  uid , String priority, TrueFalse<Boolean> callback){
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

    public void setStatus(Context context,String uid ,Boolean val, TrueFalse<Boolean> callback){
        DocumentReference ref = FirebaseFirestore.getInstance().collection("status")
                .document(uid);
        HashMap<String ,Object > map = new HashMap<>();
        map.put("status",val);
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
    public void checkIsUserNameValid(String _username , TrueFalse<Boolean> callback){

        Query ref = FirebaseFirestore.getInstance().collection("username")
                .whereEqualTo("username",_username);
        ref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().isEmpty()){
                        callback.callBack(true);
                    }else{
                        callback.callBack(false);
                    }
                }
            }
        });
     /*   CollectionReference ref = FirebaseFirestore.getInstance().collection("username");
        ref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (DocumentSnapshot doc : task.getResult().getDocuments()){
                        if (doc.getId().equals(_username)){
                            callback.callBack(false);
                        }else{
                            callback.callBack(true);
                            return;
                        }
                    }
                }
            }
        });*/
    }


    public void setSetStudentNumber(TaskUser taskUser , TrueFalse<Boolean> callback){
        DocumentReference number = FirebaseFirestore.getInstance().collection(taskUser.getShort_school())
                .document("students").collection("student-number").document(taskUser.getNumber());

        Map<String , Object> map = new HashMap<>();
        map.put("name",taskUser.getName());
        map.put("short_school",taskUser.getShort_school());
        map.put("uid",taskUser.getUid());
        map.put("email",taskUser.getEmail());
        number.set(map,SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    callback.callBack(true);
                }
            }
        });
    }
    public void deleteTaskUser(TaskUser taskUser ,TrueFalse<Boolean> callback){
        DocumentReference db = FirebaseFirestore.getInstance().collection("task-user")
                .document(taskUser.getUid());
        db.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    String tokenID = FirebaseInstanceId.getInstance().getToken();
                    final Map<String,Object> map=new HashMap<>();
                    map.put("tokenID",tokenID);
                    DocumentReference db = FirebaseFirestore.getInstance().collection("user")
                            .document(taskUser.getUid());
                    db.get().addOnSuccessListener( new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()){
                                db.set(map , SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            setSetStudentNumber(taskUser, new TrueFalse<Boolean>() {
                                                @Override
                                                public void callBack(Boolean _value) {
                                                    if (_value){

                                                        callback.callBack(true);
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

    public void completeSignUp(Context context,TaskUser taskUser ,String priority, String bolum ,String bolum_key, String fakulte , TrueFalse<Boolean> callback){

        Map<String , Object> map = new HashMap<>();
        map.put("name",taskUser.getName());
        map.put("slient",FieldValue.arrayUnion());
        map.put("uid",taskUser.getUid());
        map.put("profileImage","");
        map.put("thumb_image","");
        map.put("mention",true);
        map.put("like",true);
        map.put("comment",true);
        map.put("follow",true);
        map.put("allowRequest",true);
        map.put("lessonNotices",true);
        map.put("friendList",FieldValue.arrayUnion());
        map.put("slientChatUser",FieldValue.arrayUnion());
        map.put("email",taskUser.getEmail());
        map.put("priority",priority);
        map.put("bolum",bolum);
        map.put("fakulte",fakulte);
        map.put("bolum_key",bolum_key);
        map.put("blockList",FieldValue.arrayUnion());
        map.put("blockByOtherUser",FieldValue.arrayUnion());
        map.put("number",taskUser.getNumber());
        map.put("short_school",taskUser.getShort_school());
        map.put("schoolName",taskUser.getSchoolName());
        map.put("username",taskUser.getUsername());
        map.put("instagram","");
        map.put("twitter","");
        map.put("github","");
        map.put("linkedin","");
        DocumentReference db = FirebaseFirestore.getInstance().collection("user")
                .document(taskUser.getUid());
        db.set(map , SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Map<String , Object> usernameMap = new HashMap<>();
                    usernameMap.put("username",taskUser.getUsername());
                    usernameMap.put("uid",taskUser.getUid());
                    usernameMap.put("email",taskUser.getEmail());
                    DocumentReference refUsername = FirebaseFirestore.getInstance().collection("username")
                            .document(taskUser.getUsername());
                    refUsername.set(usernameMap,SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    setPriority(context, taskUser.getUid(), priority, new TrueFalse<Boolean>() {
                                        @Override
                                        public void callBack(Boolean _value) {
                                            if (_value){
                                                setStatus(context, taskUser.getUid(),true, new TrueFalse<Boolean>() {
                                                    @Override
                                                    public void callBack(Boolean _value) {
                                                        if (_value){
                                                            DocumentReference dbTask = FirebaseFirestore.getInstance().collection("user")
                                                                    .document(taskUser.getUid()).collection("saved-task")
                                                                    .document("task");
                                                            Map<String , Object> savedTask = new HashMap<>();
                                                                    savedTask.put("task",FieldValue.arrayUnion());
                                                                    dbTask.set(savedTask, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()){
                                                                                deleteTaskUser(taskUser, new TrueFalse<Boolean>() {
                                                                                    @Override
                                                                                    public void callBack(Boolean _value) {
                                                                                        if (_value){
                                                                                            DocumentReference dbc = FirebaseFirestore.getInstance().collection("priority").document(taskUser.getUid());
                                                                                            HashMap<String ,String> priorityMap = new HashMap<>();
                                                                                            priorityMap.put("priority",priority);
                                                                                            dbc.set(priorityMap,SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
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
}
