package com.vaye.app.Controller.ReportController;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kongzue.dialog.v3.TipDialog;
import com.kongzue.dialog.v3.WaitDialog;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.vaye.app.Controller.HomeController.SetLessons.StudentLessonAdapter;
import com.vaye.app.Controller.HomeController.SetLessons.StudentSetLessonActivity;
import com.vaye.app.Interfaces.Report;
import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.OtherUser;
import com.vaye.app.R;
import com.vaye.app.Services.ReportService;
import com.vaye.app.Util.Helper;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReportActivity extends AppCompatActivity {
    CurrentUser currentUser;
    Toolbar toolbar;
    TextView title;
    ImageButton postIt;
    TextView name , username , lessonName;
    EditText text;
    CircleImageView profileImage;
    ProgressBar progressBar;

    String postId , reportType , target , otherUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        Bundle extras = getIntent().getExtras();
        Intent intentIncoming = getIntent();

        if (extras != null){
            currentUser = intentIncoming.getParcelableExtra("currentUser");

            postId = intentIncoming.getStringExtra("postId");
            reportType = intentIncoming.getStringExtra("reportType");
            target = intentIncoming.getStringExtra("target");
            otherUser = intentIncoming.getStringExtra("otherUser");

            setToolbar();
            setUI(currentUser);
        }
    }


    private void setUI(CurrentUser currentUser){
        profileImage = (CircleImageView)findViewById(R.id.profileImage);
        name = (TextView)findViewById(R.id.name);
        username = (TextView)findViewById(R.id.username);
        lessonName = (TextView)findViewById(R.id.lessonName);
        progressBar = (ProgressBar)findViewById(R.id.progress);
        text = (EditText)findViewById(R.id.text);

        String thumbImage ;
        if (currentUser.getThumb_image()!=null){
            thumbImage = currentUser.getThumb_image();
        }else{
            thumbImage = "";
        }
        Picasso.get().load(thumbImage).placeholder(android.R.color.darker_gray)
                .centerCrop().resize(256,256)
                .into(profileImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        progressBar.setVisibility(View.GONE);

                    }
                });

        name.setText(currentUser.getName());
        username.setText(currentUser.getUsername());
        lessonName.setVisibility(View.GONE);
    }

    private void setToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        title = toolbar.findViewById(R.id.toolbar_title);
        toolbar.setTitle("");
        toolbar.setSubtitle("");
        title.setText("Şikayetiniz Nedir?");
        postIt = (ImageButton)toolbar.findViewById(R.id.postIt);
        postIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Helper.shared().back(ReportActivity.this);
            }
        });
    }


    private  void setReport(){
        WaitDialog.show(ReportActivity.this,"Lütfen Bekleyin");
        String _text = text.getText().toString();
        if (_text.isEmpty()){
            WaitDialog.dismiss();
            TipDialog.show(ReportActivity.this , "Gönderiniz Boş Olamaz", TipDialog.TYPE.ERROR);
            TipDialog.dismiss(1500);
            return;
        }
        else {
            if (Report.ReportType.appReport.equals(reportType) ){
                ReportService.shared().setAppReport(ReportActivity.this, reportType, target, currentUser, _text, new TrueFalse<Boolean>() {
                    @Override
                    public void callBack(Boolean _value) {
                        if (_value){
                            WaitDialog.dismiss();
                            TipDialog.show(ReportActivity.this,"Geri Bildiriminiz İçin Teşekküer Ederiz", TipDialog.TYPE.SUCCESS);
                            TipDialog.dismiss(1500);
                            finish();
                            Helper.shared().back(ReportActivity.this);
                        }
                    }
                });
            }else if (Report.ReportTarget.reportMessages.equals(target)){
                ReportService.shared().setReport(ReportActivity.this, reportType, target, postId, currentUser, _text, otherUser, new TrueFalse<Boolean>() {
                    @Override
                    public void callBack(Boolean _value) {
                        WaitDialog.dismiss();
                        TipDialog.show(ReportActivity.this,"Geri Bildiriminiz İçin Teşekküer Ederiz", TipDialog.TYPE.SUCCESS);
                        TipDialog.dismiss(1500);
                        finish();
                        Helper.shared().back(ReportActivity.this);
                    }
                });
            }else {
                ReportService.shared().setReport(ReportActivity.this, reportType, target, postId, currentUser, _text, otherUser, new TrueFalse<Boolean>() {
                    @Override
                    public void callBack(Boolean _value) {
                        WaitDialog.dismiss();
                        TipDialog.show(ReportActivity.this,"Geri Bildiriminiz İçin Teşekküer Ederiz", TipDialog.TYPE.SUCCESS);
                        TipDialog.dismiss(1500);
                        finish();
                        Helper.shared().back(ReportActivity.this);
                    }
                });
            }
        }
    }

    @Override
    public void finish() {
        super.finish();
        finish();
        Helper.shared().back(ReportActivity.this);
    }
}