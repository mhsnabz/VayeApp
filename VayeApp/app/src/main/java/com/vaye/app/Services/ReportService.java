package com.vaye.app.Services;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.Model.CurrentUser;

import java.util.HashMap;
import java.util.Map;

public class ReportService {
    private static final ReportService instance = new ReportService();
    public static ReportService shared() {
        return instance;
    }

    public void setReport(Activity activity, String reportType , String  reportTarget , String  postId, CurrentUser currentUser ,
                          String text , String otherUserId , TrueFalse<Boolean> completion){
        Map<String, Object> map = new HashMap<>();
        map.put("text",text);
        map.put("reportType",reportType);
        map.put("reportTarget",reportTarget);
        map.put("postId",postId);
        map.put("reportedTo",otherUserId);
        map.put("reportedBy",currentUser.getUid());
        map.put("time", FieldValue.serverTimestamp());

        CollectionReference ref = FirebaseFirestore.getInstance().collection("report")
                .document("reportType")
                .collection("reportTo")
                .document(otherUserId)
                .collection("report");
        ref.add(map).addOnCompleteListener(activity, new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()){
                    completion.callBack(true);
                }
            }
        });
    }
    public void setAppReport(Activity activity,String reportType , String reportTarget , CurrentUser currentUser ,String text , TrueFalse<Boolean> completion ){

        Map<String , Object> map = new HashMap<>();
        map.put("text",text);
        map.put("reportTarget",reportTarget);
        map.put("reportType",reportType);
        map.put("time", FieldValue.serverTimestamp());
        map.put("reportedBy", currentUser.getUid());

        CollectionReference ref = FirebaseFirestore.getInstance().collection("report")
                .document(reportType)
                .collection("reportTo")
                .document(currentUser.getUid())
                .collection("report");
        ref.add(map).addOnCompleteListener(activity, new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()){
                    completion.callBack(true);
                }
            }
        });
    }

}
