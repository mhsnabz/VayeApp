package com.vaye.app.Controller.HomeController.SettingController;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.marcoscg.easylicensesdialog.EasyLicensesDialogCompat;
import com.vaye.app.Controller.HomeController.SettingController.Settings.GizlilikActivity;
import com.vaye.app.Controller.HomeController.SettingController.Settings.HizmetActivity;
import com.vaye.app.Controller.HomeController.SettingController.Settings.PaswordSettingActivity;
import com.vaye.app.Controller.NotificationController.NotificationSetting.NotificationSettingActivity;
import com.vaye.app.Controller.ReportController.ReportActivity;
import com.vaye.app.Interfaces.Report;
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
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("plain/text");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] { "destek@vaye.app" });
        intent.putExtra(Intent.EXTRA_SUBJECT, "destek");
        startActivity(Intent.createChooser(intent, ""));
    }

    public void rateUs(View view) {
    }

    public void report(View view) {
        Intent i = new Intent(SettingActivity.this  , ReportActivity.class);
        i.putExtra("currentUser",currentUser);
        i.putExtra("reportType", Report.ReportType.appReport);
        i.putExtra("target", Report.ReportType.appReport);
        startActivity(i);
        Helper.shared().go(SettingActivity.this);
    }

    public void gizlilikPolitikasi(View view) {
        Intent i = new Intent(SettingActivity.this , GizlilikActivity.class);
        startActivity(i);
        Helper.shared().go(SettingActivity.this);
    }

    public void hizmetKosullari(View view) {
        Intent i = new Intent(SettingActivity.this , HizmetActivity.class);
        startActivity(i);
        Helper.shared().go(SettingActivity.this);
    }

    public void lisanslar(View view) {
        new EasyLicensesDialogCompat(this)
                .setTitle("Lisanslar")
                .setPositiveButton("Tamam", null)
                .show();
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

    public void passwordSetting(View view) {
        Intent i = new Intent(SettingActivity.this , PaswordSettingActivity.class);

        startActivity(i);
        Helper.shared().go(SettingActivity.this);
    }
}