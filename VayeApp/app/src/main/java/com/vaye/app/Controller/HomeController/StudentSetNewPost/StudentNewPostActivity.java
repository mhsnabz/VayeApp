package com.vaye.app.Controller.HomeController.StudentSetNewPost;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.vaye.app.Controller.HomeController.LessonPostEdit.EditPostActivity;
import com.vaye.app.Controller.HomeController.LessonPostEdit.PostDataAdaptar;
import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.LessonPostModel;
import com.vaye.app.R;
import com.vaye.app.Util.BottomSheetHelper.BottomSheetActionTarget;
import com.vaye.app.Util.BottomSheetHelper.BottomSheetModel;
import com.vaye.app.Util.BottomSheetHelper.BottomSheetTarget;
import com.vaye.app.Util.Helper;
import com.vincent.filepicker.Constant;
import com.vincent.filepicker.activity.ImagePickActivity;
import com.vincent.filepicker.activity.NormalFilePickActivity;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.vincent.filepicker.activity.ImagePickActivity.IS_NEED_CAMERA;

public class StudentNewPostActivity extends AppCompatActivity {
    Toolbar toolbar;

    TextView title;
    ImageButton rigthBarButton;
    CurrentUser currentUser;
    StorageTask<UploadTask.TaskSnapshot> uploadTask;
    CircleImageView profileImage;
    TextView name , username , lessonName;
    ProgressBar progressBar;
    EditText text;
    RelativeLayout driveLayout;
    ImageButton driveIcon , deleteClick;
    TextView linkName;
    RecyclerView datas;
    String selectedLessonName;
    KProgressHUD hud;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int gallery_request =400;
    private static final int image_pick_request =600;
    private static final int camera_pick_request =800;

    private int MAX_ATTACHMENT_COUNT = 10;
    private ArrayList<String> photoPaths = new ArrayList<>();
    private ArrayList<Uri> docPaths = new ArrayList<>();

    String storagePermission[];
    //Stackview
    ImageButton addImage, addDoc , addPdf , addLink;
    String contentType = "";
    String mimeType = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_new_post);
        Bundle extras = getIntent().getExtras();
        Intent intentIncoming = getIntent();


        if (extras != null){
            currentUser = intentIncoming.getParcelableExtra("currentUser");
            selectedLessonName = intentIncoming.getStringExtra("lessonName");
            setToolbar(selectedLessonName);
            setView(currentUser,selectedLessonName);

        }else {
            finish();
        }

        hud = KProgressHUD.create(StudentNewPostActivity.this)
                .setStyle(KProgressHUD.Style.ANNULAR_DETERMINATE)
                .setLabel("Dosya Yükleniyor")
                .setMaxProgress(100);
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
        title.setText(lessonName);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Helper.shared().back(StudentNewPostActivity.this);
            }
        });
        rigthBarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
    private void setView(CurrentUser currentUser , String selectedLessonName){
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
        lessonName.setText(selectedLessonName);



        setRecylerView(currentUser);
        setStackView();
       // detectLink(post);
    }


    private void setRecylerView(CurrentUser currentUser ){
        datas = (RecyclerView)findViewById(R.id.datasRec);
        datas.setLayoutManager(new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL));

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
                UploadDoc();
            }
        });


        addPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickPdf();
            }

        });

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();
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
      /*  Helper.shared().BottomSheetAddLink(StudentNewPostActivity.this, BottomSheetTarget.post_add_link_target, currentUser, model, post, new TrueFalse<Boolean>() {
            @Override
            public void callBack(Boolean _value) {

              //  detectLink(post);
            }
        });*/
    }

    //TODO-permission
    private void uploadImage() {
        if (!checkGalleryPermissions()){
            requestStoragePermission();
        }
        else{ pickGallery();}
    }
    private boolean checkGalleryPermissions() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }
    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this,storagePermission,gallery_request);

    }
    private void pickGallery() {
        Intent intent1 = new Intent(this, ImagePickActivity.class);
        intent1.putExtra(IS_NEED_CAMERA, false);
        intent1.putExtra(Constant.MAX_NUMBER, 1);
        startActivityForResult(intent1, Constant.REQUEST_CODE_PICK_IMAGE);

    }
    private void picDoc(){
        Intent intent4 = new Intent(this, NormalFilePickActivity.class);
        intent4.putExtra(Constant.MAX_NUMBER, 1);
        intent4.putExtra(NormalFilePickActivity.SUFFIX, new String[] {"doc", "docx"});
        startActivityForResult(intent4, Constant.REQUEST_CODE_PICK_FILE);
    }
    private void UploadDoc() {
        if (!checkGalleryPermissions()){
            requestStoragePermission();
        }
        else{ picDoc();}
    }
    private void pickPdf(){
        Intent intent4 = new Intent(this, NormalFilePickActivity.class);
        intent4.putExtra(Constant.MAX_NUMBER, 1);
        intent4.putExtra(NormalFilePickActivity.SUFFIX, new String[] {"pdf"});
        startActivityForResult(intent4, Constant.REQUEST_CODE_PICK_FILE);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Helper.shared().back(StudentNewPostActivity.this);
    }
}