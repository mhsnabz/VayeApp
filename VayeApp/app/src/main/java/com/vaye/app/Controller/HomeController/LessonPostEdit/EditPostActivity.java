package com.vaye.app.Controller.HomeController.LessonPostEdit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
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
import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.LessonPostModel;
import com.vaye.app.R;
import com.vaye.app.Services.MajorPostService;
import com.vaye.app.Util.BottomSheetHelper.BottomSheetActionTarget;
import com.vaye.app.Util.BottomSheetHelper.BottomSheetModel;
import com.vaye.app.Util.BottomSheetHelper.BottomSheetTarget;
import com.vaye.app.Util.Helper;

import java.net.URISyntaxException;
import java.util.ArrayList;

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
    PostDataAdaptar adaptar;

    RecyclerView datas;


    //Stackview
    ImageButton addImage, addDoc , addPdf , addLink;

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
        title.setText("Gönderiyi Düzenle");

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
        text.setText(model.getText());


        setRecylerView(currentUser , model);
        setStackView();
        detectLink(post);
    }

    private void setRecylerView(CurrentUser currentUser , LessonPostModel model){
        datas = (RecyclerView)findViewById(R.id.datasRec);
        datas.setLayoutManager(new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL));
        adaptar = new PostDataAdaptar(model.getData() ,model.getThumbData(), this , currentUser , model);
        datas.setAdapter(adaptar);
        adaptar.notifyDataSetChanged();
    }


    private void setStackView(){
        addImage = (ImageButton)findViewById(R.id.gallery);
        addDoc = (ImageButton)findViewById(R.id.doc);
        addPdf = (ImageButton)findViewById(R.id.pdf);
        addLink = (ImageButton)findViewById(R.id.link);


        addLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLinkSheet();
            }
        });

        addDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        addPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }

        });

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }


    private void showLinkSheet(){


        ArrayList<String > items = new ArrayList<>();
        items.add(BottomSheetActionTarget.google_drive);
        items.add(BottomSheetActionTarget.one_drive);
        items.add(BottomSheetActionTarget.dropbox);
        items.add(BottomSheetActionTarget.yandex_disk);
        items.add(BottomSheetActionTarget.mega);
        items.add(BottomSheetActionTarget.iClould);
        ArrayList<Integer> res = new ArrayList<>();
        res.add(R.drawable.google_drive);
        res.add(R.drawable.onedrive);
        res.add(R.drawable.dropbox);
        res.add(R.drawable.yandex);
        res.add(R.drawable.mega);
        res.add(R.drawable.icloud);
        BottomSheetModel model = new BottomSheetModel(items , BottomSheetTarget.post_add_link_target , res);
        Helper.shared().BottomSheetAddLink(EditPostActivity.this, BottomSheetTarget.post_add_link_target, currentUser, model, post, new TrueFalse<Boolean>() {
            @Override
            public void callBack(Boolean _value) {

                detectLink(post);
            }
        });
    }

    private void detectLink(LessonPostModel model){
        driveLayout = (RelativeLayout)findViewById(R.id.driveLayout);
        driveIcon = (ImageButton)findViewById(R.id.driveIcon);
        deleteClick = (ImageButton)findViewById(R.id.cancelButton);
        linkName = (TextView)findViewById(R.id.linkName);

        if (model.getLink() == null || model.getLink().equals("")){
            driveLayout.setVisibility(View.GONE);
        }else{
            driveLayout.setVisibility(View.VISIBLE);
            try {
                if(MajorPostService.shared().getLink(model.getLink()) .equals("drive.google.com")
                        || MajorPostService.shared().getLink(model.getLink()).equals("www.drive.google.com")){
                    driveIcon.setImageResource(R.drawable.google_drive);
                    linkName.setText(DriveLinkNames.googleDrive);
                }else if ( MajorPostService.shared().getLink(model.getLink()).equals("onedrive.live.com" )
                        || MajorPostService.shared().getLink(model.getLink()).equals("www.onedrive.live.com")|| model.getLink().equals("1drv.ms")){
                    driveIcon.setImageResource(R.drawable.onedrive);
                    linkName.setText(DriveLinkNames.onedrive);

                }else if ( MajorPostService.shared().getLink(model.getLink()).equals("dropbox.com")
                        ||  MajorPostService.shared().getLink(model.getLink()).equals("www.dropbox.com")){
                    driveIcon.setImageResource(R.drawable.dropbox);
                    linkName.setText(DriveLinkNames.dropbox);

                }else if ( MajorPostService.shared().getLink(model.getLink()).equals("icloud.com")
                        ||  MajorPostService.shared().getLink(model.getLink()).equals("www.icloud.com")){
                    driveIcon.setImageResource(R.drawable.icloud);
                    linkName.setText(DriveLinkNames.icloud);

                }else if ( MajorPostService.shared().getLink(model.getLink()).equals("disk.yandex.com.tr")
                        ||  MajorPostService.shared().getLink(model.getLink()).equals("disk.yandex.com") || model.getLink().equals("yadi.sk")){
                    driveIcon.setImageResource(R.drawable.yandex);
                    linkName.setText(DriveLinkNames.yandex);

                }else if ( MajorPostService.shared().getLink(model.getLink()).equals("mega.nz")
                        ||  MajorPostService.shared().getLink(model.getLink()).equals("www.mega.nz")){
                    driveIcon.setImageResource(R.drawable.mega);
                    linkName.setText(DriveLinkNames.mega);
                }
            } catch (URISyntaxException e) {
                e.printStackTrace();
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