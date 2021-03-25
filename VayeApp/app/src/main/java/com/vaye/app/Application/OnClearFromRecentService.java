package com.vaye.app.Application;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class OnClearFromRecentService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("ClearFromRecentService", "Service Started");
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("ClearFromRecentService", "Service Destroyed");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.e("ClearFromRecentService", "END");
        Map<String , Object> map = new HashMap<>();
        map.put("isOnline",false);
        map.put("badgeCount",0);
        CollectionReference reference = FirebaseFirestore.getInstance().collection("user");
        CollectionReference setCurrentUserOnline =  FirebaseFirestore.getInstance().collection("user")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("msg-list");
        setCurrentUserOnline.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    if (!task.getResult().getDocuments().isEmpty()){
                             for (DocumentSnapshot item : task.getResult().getDocuments()){
                                 reference.document(item.getId()).collection("msg-list").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).update(map);
                                 Log.e("ClearFromRecentService","set offline");

                             }
                    }
                }
            }
        });
        stopSelf();
    }
}