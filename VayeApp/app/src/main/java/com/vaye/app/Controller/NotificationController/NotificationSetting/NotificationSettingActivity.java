package com.vaye.app.Controller.NotificationController.NotificationSetting;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import android.app.Notification;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.kongzue.dialog.v3.WaitDialog;
import com.vaye.app.Controller.HomeController.StudentSetNewPost.StudentNewPostActivity;
import com.vaye.app.Controller.NotificationService.PushNotificationType;
import com.vaye.app.Interfaces.Notifications;
import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.R;
import com.vaye.app.Services.NotificaitonService;
import com.vaye.app.Util.Helper;

import static com.vaye.app.Application.VayeApp.SHARED_PREFS;

public class NotificationSettingActivity extends AppCompatActivity {
    CurrentUser currentUser;
    TextView toolbarTitle;
    Toolbar toolbar;
    Switch switch1,switch2,switch3,switch4,switch5;
    String TAG = "NotificationSettingActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_setting);
        Bundle extras = getIntent().getExtras();
        Intent intentIncoming = getIntent();
        if (extras != null){
            currentUser = intentIncoming.getParcelableExtra("currentUser");
            setToolbar();
            configureUI(currentUser);
        }
    }

    private void setToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setSupportActionBar(toolbar);

        toolbar.setTitle("");
        toolbar.setSubtitle("");
        toolbarTitle = (TextView)toolbar.findViewById(R.id.toolbar_title);
        toolbarTitle.setText("Anlık Bildirim Ayarları");
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Helper.shared().back(NotificationSettingActivity.this);
            }
        });
    }

    private void configureUI(CurrentUser currentUser){
        switch1 = (Switch)findViewById(R.id.switch1);
        switch2 = (Switch)findViewById(R.id.switch2);
        switch3 = (Switch)findViewById(R.id.switch3);
        switch4 = (Switch)findViewById(R.id.switch4);
        switch5 = (Switch)findViewById(R.id.switch5);

        if (currentUser.getLessonNotices()){
            switch1.setChecked(true);
        }else{
            switch1.setChecked(false);
        }
        if (currentUser.getComment()){
            switch2.setChecked(true);
        }else{
            switch2.setChecked(false);
        }
        if (currentUser.getLike()){
            switch3.setChecked(true);
        }else{
            switch3.setChecked(false);
        }
        if (currentUser.getFollow()){
            switch4.setChecked(true);
        }else{
            switch4.setChecked(false);
        }
        if (currentUser.getMention()){
            switch5.setChecked(true);
        }else{
            switch5.setChecked(false);
        }

        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Log.d(TAG, "onCheckedChanged: "+ b);
                NotificaitonService.shared().setLocalNotification(NotificationSettingActivity.this, b, currentUser, Notifications.LocalNotifications.lessonNotices, new TrueFalse<Boolean>() {
                    @Override
                    public void callBack(Boolean _value) {

                       currentUser.setLessonNotices(b);
                        setNotificaitonPref(PushNotificationType.lessonNotices,b);
                        WaitDialog.dismiss();
                    }
                });
            }
        });
        switch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Log.d(TAG, "onCheckedChanged: "+ b);
                NotificaitonService.shared().setLocalNotification(NotificationSettingActivity.this, b, currentUser, Notifications.LocalNotifications.comment, new TrueFalse<Boolean>() {
                    @Override
                    public void callBack(Boolean _value) {
                        currentUser.setComment(b);
                        setNotificaitonPref(PushNotificationType.comment,b);
                        WaitDialog.dismiss();
                    }
                });
            }
        });
        switch3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Log.d(TAG, "onCheckedChanged: "+ b);
                NotificaitonService.shared().setLocalNotification(NotificationSettingActivity.this, b, currentUser, Notifications.LocalNotifications.like, new TrueFalse<Boolean>() {
                    @Override
                    public void callBack(Boolean _value) {
                        currentUser.setLike(b);
                        setNotificaitonPref(PushNotificationType.like,b);
                        WaitDialog.dismiss();
                    }
                });
            }
        });
        switch4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Log.d(TAG, "onCheckedChanged: "+ b);
                NotificaitonService.shared().setLocalNotification(NotificationSettingActivity.this, b, currentUser, Notifications.LocalNotifications.follow, new TrueFalse<Boolean>() {
                    @Override
                    public void callBack(Boolean _value) {
                        currentUser.setFollow(b);
                        setNotificaitonPref(PushNotificationType.follow,b);
                        WaitDialog.dismiss();
                    }
                });
            }
        });
        switch5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Log.d(TAG, "onCheckedChanged: "+ b);
                NotificaitonService.shared().setLocalNotification(NotificationSettingActivity.this, b, currentUser, Notifications.LocalNotifications.mention, new TrueFalse<Boolean>() {
                    @Override
                    public void callBack(Boolean _value) {
                        currentUser.setMention(b);
                        setNotificaitonPref("mention",b);
                        WaitDialog.dismiss();
                    }
                });
            }
        });

    }
    public void setNotificaitonPref( String type ,Boolean val ) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean(type,val);

        editor.apply();

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Helper.shared().back(NotificationSettingActivity.this);
    }
}