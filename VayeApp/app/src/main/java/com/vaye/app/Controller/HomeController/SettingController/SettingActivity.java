package com.vaye.app.Controller.HomeController.SettingController;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.kongzue.dialog.v3.WaitDialog;
import com.marcoscg.easylicensesdialog.EasyLicensesDialogCompat;
import com.vaye.app.BuildConfig;
import com.vaye.app.Controller.HomeController.HomeActivity;
import com.vaye.app.Controller.HomeController.SettingController.Settings.BlocedUserActivity;
import com.vaye.app.Controller.HomeController.SettingController.Settings.GizlilikActivity;
import com.vaye.app.Controller.HomeController.SettingController.Settings.HizmetActivity;
import com.vaye.app.Controller.HomeController.SettingController.Settings.PaswordSettingActivity;
import com.vaye.app.Controller.ReportController.ReportActivity;
import com.vaye.app.Interfaces.CurrentUserService;
import com.vaye.app.Interfaces.Report;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.R;
import com.vaye.app.Services.UserService;
import com.vaye.app.Util.Helper;

public class SettingActivity extends AppCompatActivity {
    TextView toolbarTitle;
    Toolbar toolbar;
    CurrentUser currentUser;
    TextView blockedUser,contackUs,insta,twit,versiyon;
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
        blockedUser = (TextView)findViewById(R.id.blockedUser);

        contackUs = (TextView)findViewById(R.id.contackUs);
        contackUs.setText("destek@vaye.app");
        insta = (TextView)findViewById(R.id.insta);
        twit = (TextView)findViewById(R.id.twit);
        twit.setText("@vaye.app");
        insta.setText("@vaye.app");
        versiyon = (TextView)findViewById(R.id.versiyon);
        versiyon.setText(BuildConfig.VERSION_NAME);
        blockedUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WaitDialog.show(SettingActivity.this,null);
                UserService.shared().getCurrentUser(currentUser.getUid(), new CurrentUserService() {
                    @Override
                    public void onCallback(CurrentUser user) {
                        Intent i = new Intent(SettingActivity.this, BlocedUserActivity.class);
                        i.putExtra("currentUser",user);
                        startActivity(i);
                        Helper.shared().go(SettingActivity.this);
                    }
                });
            }
        });
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

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.vaye.app"));
        startActivity(browserIntent);
       /* ReviewManager manager = ReviewManagerFactory.create(SettingActivity.this);
            com.google.android.play.core.tasks.Task<ReviewInfo> request = manager.requestReviewFlow();
            request.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // We can get the ReviewInfo object

                    ReviewInfo reviewInfo = task.getResult();
                    com.google.android.play.core.tasks.Task<Void> flow = manager.launchReviewFlow(SettingActivity.this, reviewInfo);
                    flow.addOnCompleteListener(taskk -> {

                        Log.d("SettingActivity", "rateFunc: succes");
                        // The flow has finished. The API does not indicate whether the user
                        // reviewed or not, or even whether the review dialog was shown. Thus, no
                        // matter the result, we continue our app flow.
                    });
                } else {
                    // There was some problem, log or handle the error code.
                    ;
                    Log.d("SettingActivity", "rateFunc: " +  task.getException());
                }
            });*/
       /* request.addOnCompleteListener(new com.google.android.play.core.tasks.OnCompleteListener<ReviewInfo>() {
            @Override
            public void onComplete(@NonNull com.google.android.play.core.tasks.Task<ReviewInfo> task) {
                if (task.isSuccessful()){
                  ReviewInfo rewiewInfo = task.getResult();
                    com.google.android.play.core.tasks.Task<Void> flow = manager.launchReviewFlow(HomeActivity.this,rewiewInfo);
                    flow.addOnSuccessListener(new com.google.android.play.core.tasks.OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void result) {

                        }
                    });
                }else{


                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.vaye.app"));
                    startActivity(browserIntent);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.vaye.app"));
                startActivity(browserIntent);
            }
        });*/

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