package com.vaye.app.Controller.HomeController.LessonPostEdit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.rpc.Help;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.vaye.app.Controller.HomeController.SetLessons.StudentLessonAdapter;
import com.vaye.app.Controller.HomeController.SetLessons.StudentSetLessonActivity;
import com.vaye.app.Interfaces.DriveLinkNames;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.LessonPostModel;
import com.vaye.app.R;
import com.vaye.app.Services.MajorPostService;
import com.vaye.app.Util.Helper;

import java.net.URISyntaxException;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditPostActivity extends AppCompatActivity {
    Toolbar toolbar;
    LessonPostModel post;
    TextView title;
    ImageButton rigthBarButton;
    CurrentUser currentUser;

    CircleImageView profileImage;
    TextView name , username , lessonName;
    ProgressBar progressBar;
    EditText text;
    RelativeLayout driveLayout;
    ImageButton driveIcon , deleteClick;
    TextView linkName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);

        Bundle extras = getIntent().getExtras();
        Intent intentIncoming = getIntent();


        if (extras != null){
            currentUser = intentIncoming.getParcelableExtra("currentUser");
            post = intentIncoming.getParcelableExtra("post");
            setToolbar(post.getLessonName());
            setView(currentUser,post);

        }else {
            finish();
        }
    }

    private void setToolbar(String  lessonName)
    {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        title = toolbar.findViewById(R.id.toolbar_title);
        rigthBarButton = toolbar.findViewById(R.id.rigthButton);
        rigthBarButton.setImageResource(R.drawable.post_it);
        toolbar.setTitle("");
        toolbar.setSubtitle("");
        title.setText("Ders Ekle-Çıkar");

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Helper.shared().back(EditPostActivity.this);
            }
        });
        rigthBarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }


    private void setView(CurrentUser currentUser , LessonPostModel model){

        profileImage = (CircleImageView)findViewById(R.id.profileImage);
        name = (TextView)findViewById(R.id.name);
        username = (TextView)findViewById(R.id.username);
        lessonName = (TextView)findViewById(R.id.lessonName);
        progressBar = (ProgressBar)findViewById(R.id.progress);
        text = (EditText)findViewById(R.id.text);
        driveLayout = (RelativeLayout)findViewById(R.id.driveLayout);
        driveIcon = (ImageButton)findViewById(R.id.driveIcon);
        deleteClick = (ImageButton)findViewById(R.id.cancelButton);
        linkName = (TextView)findViewById(R.id.linkName);
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
        lessonName.setText(model.getLessonName());
        text.setText(lessonName.getText());

        if (model.getLink() == null || model.getLink().equals("")){
            driveLayout.setVisibility(View.GONE);
        }else{
            if( model.getLink().equals("drive.google.com")
                    ||  model.getLink().equals("www.drive.google.com")){
                driveIcon.setImageResource(R.drawable.google_drive);
                linkName.setText(DriveLinkNames.googleDrive);
            }else if ( model.getLink().equals("onedrive.live.com" )
                    || model.getLink().equals("www.onedrive.live.com")|| model.getLink().equals("1drv.ms")){
                driveIcon.setImageResource(R.drawable.onedrive);
                linkName.setText(DriveLinkNames.onedrive);

            }else if ( model.getLink().equals("dropbox.com")
                    ||  model.getLink().equals("www.dropbox.com")){
                driveIcon.setImageResource(R.drawable.dropbox);
                linkName.setText(DriveLinkNames.dropbox);

            }else if ( model.getLink().equals("icloud.com\"")
                    ||  model.getLink().equals("www.icloud.com\"")){
                driveIcon.setImageResource(R.drawable.icloud);
                linkName.setText(DriveLinkNames.icloud);

            }else if ( model.getLink().equals("disk.yandex.com.tr")
                    ||  model.getLink().equals("disk.yandex.com") || model.getLink().equals("yadi.sk")){
                driveIcon.setImageResource(R.drawable.yandex);
                linkName.setText(DriveLinkNames.yandex);

            }else if ( model.getLink().equals("mega.nz")
                    ||  model.getLink().equals("www.mega.nz")){
                driveIcon.setImageResource(R.drawable.mega);
                linkName.setText(DriveLinkNames.mega);
            }
            deleteClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Helper.shared().back(EditPostActivity.this);
    }
}