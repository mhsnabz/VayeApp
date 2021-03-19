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
import com.vaye.app.Model.SchoolModel;

import java.util.HashMap;

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
}
