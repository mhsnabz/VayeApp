package com.vaye.app.Controller.HomeController.SettingController;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.vaye.app.Controller.NotificationController.NotificationSetting.NotificationSettingActivity;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.R;
import com.vaye.app.Util.Helper;

public class SettingActivity extends AppCompatActivity {
    TextView toolbarTitle;
    Toolbar toolbar;
    CurrentUser currentUser;
    TextView emailAdress,contackUs,insta,twit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Bundle extras = getIntent().getExtras();
        Intent intentIncoming = getIntent();
        if (extras != null){
            currentUser = intentIncoming.getParcelableExtra("currentUser");
            setToolbar();
            configureUI(currentUser);

        }

    }
    private void configureUI(CurrentUser currentUser){
        emailAdress = (TextView)findViewById(R.id.emailAdress);
        emailAdress.setText(currentUser.getEmail());
        contackUs = (TextView)findViewById(R.id.contackUs);
        contackUs.setText("destek@vaye.app");
        insta = (TextView)findViewById(R.id.insta);
        twit = (TextView)findViewById(R.id.twit);
        twit.setText("@vaye.app");
        insta.setText("@vaye.app");
    }
    private void setToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setSupportActionBar(toolbar);

        toolbar.setTitle("");
        toolbar.setSubtitle("");
        toolbarTitle = (TextView)toolbar.findViewById(R.id.toolbar_title);
        toolbarTitle.setText("Ayarlar");
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Helper.shared().back(SettingActivity.this);
            }
        });
    }
    public void contackMail(View view) {
    }

    public void rateUs(View view) {
    }

    public void report(View view) {
    }

    public void gizlilikPolitikasi(View view) {
    }

    public void hizmetKosullari(View view) {
    }

    public void lisanslar(View view) {
    }

    public void eMailSetting(View view) {
    }

    public void twitterClick(View view) {
    }

    public void instaClick(View view) {
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Helper.shared().back(SettingActivity.this);
    }
}