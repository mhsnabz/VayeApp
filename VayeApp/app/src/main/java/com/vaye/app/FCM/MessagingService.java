package com.vaye.app.FCM;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.vaye.app.Controller.NotificationService.PushNotificationService;
import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.R;
import com.vaye.app.Services.MessageService;
import com.vaye.app.Services.UserService;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static com.vaye.app.Application.VayeApp.SHARED_PREFS;

public class MessagingService extends FirebaseMessagingService {
    private static final String TAG = "MessagingService";
    FirebaseAuth auth = FirebaseAuth.getInstance();
    String uid = auth.getUid();

    public static final String active_context_name = "Controller.ChatController.Conservation.ConservationController";
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "onMessageReceived: " + remoteMessage);

        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
            //Show Notfication
        }
        showNotification(remoteMessage.getNotification() , remoteMessage.getData());

    }


    private void showNotification(RemoteMessage.Notification data, Map<String, String> remoteMessageData) {
        String title = data.getTitle();
        String text = data.getBody();
        Log.d(TAG, "showNotification: " + remoteMessageData.get("type"));
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        if (sharedPreferences.getString("context", "") != null && sharedPreferences.getString("context", "").equals(active_context_name)){
            Log.d(TAG, "showNotification: " + sharedPreferences.getString("context", ""));
            return;
        }else{
            NotificationManager manager=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            String channel = "com.vaye.app";
            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
                Notification.Builder notificationBuilder =
                        new Notification.Builder(getApplicationContext(), channel)
                                .setSmallIcon(R.drawable.notification_icon)
                                .setContentTitle(title)
                                .setContentText(text)
                                .setAutoCancel(true);
                NotificationChannel notificationChannel = new NotificationChannel(channel, "VayeApp", NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.setDescription("VayeApp");
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.BLUE);
                notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
                notificationChannel.enableVibration(true);
                manager.createNotificationChannel(notificationChannel);
                manager.notify(new Random().nextInt(),notificationBuilder.build());
            }else{
                NotificationCompat.Builder notificationBuilder =
                        new NotificationCompat.Builder(getApplicationContext())
                                .setSmallIcon(R.drawable.notification_icon)
                                .setContentTitle(title)
                                .setContentText(text)
                                .setAutoCancel(true)
                                .setPriority(Notification.PRIORITY_MAX);
                manager.notify(new Random().nextInt(),notificationBuilder.build());
            }

        }

    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        if (uid!=null){
            UserService.shared().isUserExist(uid, new TrueFalse<Boolean>() {
                @Override
                public void callBack(Boolean _value) {
                    if (_value){
                        final Map<String,Object> map=new HashMap<>();
                        map.put("tokenID",s);
                        DocumentReference db = FirebaseFirestore.getInstance().collection("user")
                                .document(uid);
                        db.get().addOnSuccessListener( new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.exists()){
                                    db.set(map , SetOptions.merge());
                                }
                            }
                        });
                    }
                }
            });
        }

    }

}
