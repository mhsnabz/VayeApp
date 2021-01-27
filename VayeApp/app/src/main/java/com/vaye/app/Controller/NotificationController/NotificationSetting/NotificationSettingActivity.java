package com.vaye.app.Controller.NotificationController.NotificationSetting;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.vaye.app.Controller.HomeController.StudentSetNewPost.StudentNewPostActivity;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.R;
import com.vaye.app.Util.Helper;

public class NotificationSettingActivity extends AppCompatActivity {
    CurrentUser currentUser;
    TextView toolbarTitle;
    Toolbar toolbar;
    SwitchCompat switch1,switch2,switch3,switch4,switch5;
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
        }
    }

    private void setToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setSupportActionBar(toolbar);

        toolbar.setTitle("");
        toolbar.setSubtitle("");
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
        switch1 = (SwitchCompat)findViewById(R.id.switch1);
        switch2 = (SwitchCompat)findViewById(R.id.switch2);
        switch3 = (SwitchCompat)findViewById(R.id.switch3);
        switch4 = (SwitchCompat)findViewById(R.id.switch4);
        switch5 = (SwitchCompat)findViewById(R.id.switch5);

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
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Helper.shared().back(NotificationSettingActivity.this);
    }
}