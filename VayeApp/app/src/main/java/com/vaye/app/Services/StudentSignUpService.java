package com.vaye.app.Services;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.kongzue.dialog.v3.TipDialog;
import com.kongzue.dialog.v3.WaitDialog;
import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.Model.SchoolModel;

import java.util.HashMap;

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
    public void checkIsUserNameValid(String _username , TrueFalse<Boolean> callback){

        CollectionReference ref = FirebaseFirestore.getInstance().collection("username");
        ref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (DocumentSnapshot doc : task.getResult().getDocuments()){
                        if (doc.getId().equals(_username)){
                            callback.callBack(false);
                        }else{
                            callback.callBack(true);
                        }
                    }
                }
            }
        });
    }
}
