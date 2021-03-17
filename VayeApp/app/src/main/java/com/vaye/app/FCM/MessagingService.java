package com.vaye.app.FCM;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.vaye.app.R;

import java.util.Map;
import java.util.Random;

public class MessagingService extends FirebaseMessagingService {
    private static final String TAG = "MessagingService";
    FirebaseAuth auth = FirebaseAuth.getInstance();
    String uid = auth.getUid();
    private NotificationManager notificationManager;
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "onMessageReceived: " + remoteMessage);
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
            //Show Notfication
        }
        showNotification(remoteMessage.getNotification());

    }

    private void showNotification(RemoteMessage.Notification data) {
        String title = data.getTitle();
        String text = data.getBody();
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
            notificationManager.createNotificationChannel(notificationChannel);
            notificationManager.notify(new Random().nextInt(),notificationBuilder.build());
        }else{
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(getApplicationContext())
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle(title)
                            .setContentText(text)
                            .setAutoCancel(true)
                            .setPriority(Notification.PRIORITY_MAX);
            notificationManager.notify(new Random().nextInt(),notificationBuilder.build());
        }

    }


}
