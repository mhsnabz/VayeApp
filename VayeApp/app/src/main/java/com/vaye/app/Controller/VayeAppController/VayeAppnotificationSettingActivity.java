package com.vaye.app.Controller.VayeAppController;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import com.kongzue.dialog.v3.WaitDialog;
import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.R;
import com.vaye.app.Services.VayeAppPostService;
import com.vaye.app.Util.Helper;

public class VayeAppnotificationSettingActivity extends AppCompatActivity {
    CurrentUser currentUser;
    Toolbar toolbar;
    TextView title;
    Switch switch1,switch2,switch3;
    Boolean isFollowingBuySell = false , isFollowingCamp = false ,isFollowingFoodMe = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vaye_appnotification_setting);
        Bundle extras = getIntent().getExtras();
        Intent intentIncoming = getIntent();
        if (extras != null){
            currentUser = intentIncoming.getParcelableExtra("currentUser");
            setToolbar();
            configureUI(currentUser);
        }else {
            finish();
        }
    }

    private void setToolbar(){
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        title = toolbar.findViewById(R.id.toolbar_title);

        toolbar.setTitle("");
        toolbar.setSubtitle("");
        title.setText("Anlık Bildirim Ayarları");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Helper.shared().go(VayeAppnotificationSettingActivity.this);
            }
        });
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }
    private void configureUI(CurrentUser currentUser){
        WaitDialog.show(VayeAppnotificationSettingActivity.this , null);
        switch1 = (Switch)findViewById(R.id.switch1);
        switch2 = (Switch)findViewById(R.id.switch2);
        switch3 = (Switch)findViewById(R.id.switch3);

        VayeAppPostService.shared().checkIsFollowing(currentUser.getUid(), "sell-buy", new TrueFalse<Boolean>() {
            @Override
            public void callBack(Boolean _value) {
                switch1.setChecked(_value);
                isFollowingBuySell = _value;
                WaitDialog.dismiss();
            }
        });
        VayeAppPostService.shared().checkIsFollowing(currentUser.getUid(), "food-me", new TrueFalse<Boolean>() {
            @Override
            public void callBack(Boolean _value) {
                switch2.setChecked(_value);
                isFollowingFoodMe = _value;
            }
        });
        VayeAppPostService.shared().checkIsFollowing(currentUser.getUid(), "camping", new TrueFalse<Boolean>() {
            @Override
            public void callBack(Boolean _value) {
                switch3.setChecked(_value);
                isFollowingCamp = _value;
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Helper.shared().go(VayeAppnotificationSettingActivity.this);
    }
}