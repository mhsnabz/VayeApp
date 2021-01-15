package com.vaye.app.Controller.HomeController.LessonPostEdit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.loader.content.CursorLoader;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.features.ReturnMode;
import com.esafirm.imagepicker.model.Image;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.kongzue.dialog.v3.WaitDialog;



import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.vaye.app.Interfaces.DataTypes;
import com.vaye.app.Interfaces.DriveLinkNames;
import com.vaye.app.Interfaces.StringArrayListInterface;
import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.LessonPostModel;
import com.vaye.app.Model.UploadFiles;
import com.vaye.app.R;
import com.vaye.app.Services.MajorPostService;
import com.vaye.app.Services.MajorPostUploadService;
import com.vaye.app.Util.BottomSheetHelper.BottomSheetActionTarget;
import com.vaye.app.Util.BottomSheetHelper.BottomSheetModel;
import com.vaye.app.Util.BottomSheetHelper.BottomSheetTarget;
import com.vaye.app.Util.Helper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class EditPostActivity extends AppCompatActivity {
    private static final String TAG ="EditPostActivity" ;
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


    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int gallery_request =400;
    private static final int image_pick_request =600;
    private static final int camera_pick_request =800;
    String storagePermission[];
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
                    Log.d(TAG, "detectLink: " + MajorPostService.shared().getLink(model.getLink()));
                    driveIcon.setImageResource(R.drawable.google_drive);
                    linkName.setText(DriveLinkNames.googleDrive);
                }else if ( MajorPostService.shared().getLink(model.getLink()).equals("onedrive.live.com" )
                        || MajorPostService.shared().getLink(model.getLink()).equals("www.onedrive.live.com")|| model.getLink().equals("1drv.ms")){
                    driveIcon.setImageResource(R.drawable.onedrive);
                    linkName.setText(DriveLinkNames.onedrive);
                    Log.d(TAG, "detectLink: " + MajorPostService.shared().getLink(model.getLink()));

                }else if ( MajorPostService.shared().getLink(model.getLink()).equals("dropbox.com")
                        ||  MajorPostService.shared().getLink(model.getLink()).equals("www.dropbox.com")){
                    driveIcon.setImageResource(R.drawable.dropbox);
                    linkName.setText(DriveLinkNames.dropbox);
                    Log.d(TAG, "detectLink: " + MajorPostService.shared().getLink(model.getLink()));

                }else if ( MajorPostService.shared().getLink(model.getLink()).equals("icloud.com")
                        ||  MajorPostService.shared().getLink(model.getLink()).equals("www.icloud.com")){
                    driveIcon.setImageResource(R.drawable.icloud);
                    linkName.setText(DriveLinkNames.icloud);
                    Log.d(TAG, "detectLink: " + MajorPostService.shared().getLink(model.getLink()));

                }else if ( MajorPostService.shared().getLink(model.getLink()).equals("disk.yandex.com.tr")
                        ||  MajorPostService.shared().getLink(model.getLink()).equals("disk.yandex.com") || model.getLink().equals("yadi.sk")){
                    driveIcon.setImageResource(R.drawable.yandex);
                    linkName.setText(DriveLinkNames.yandex);
                    Log.d(TAG, "detectLink: " + MajorPostService.shared().getLink(model.getLink()));

                }else if ( MajorPostService.shared().getLink(model.getLink()).equals("mega.nz")
                        ||  MajorPostService.shared().getLink(model.getLink()).equals("www.mega.nz")){
                    driveIcon.setImageResource(R.drawable.mega);
                    linkName.setText(DriveLinkNames.mega);
                    Log.d(TAG, "detectLink: " + MajorPostService.shared().getLink(model.getLink()));
                }
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            deleteClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MajorPostService.shared().deleteLink(post, currentUser, EditPostActivity.this, new TrueFalse<Boolean>() {
                        @Override
                        public void callBack(Boolean _value) {
                            if(_value){
                                post.setLink("");
                                detectLink(post);
                            }
                        }
                    });
                }
            });
        }
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }
    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
    @Override
    protected void onActivityResult(int requestCode, final int resultCode, Intent data) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            Image image = ImagePicker.getFirstImageOrNull(data);
            ArrayList<UploadFiles> files = new ArrayList<>();
            files.add(new UploadFiles(image.getUri() , "image"));
            MajorPostUploadService.shared().uploadToDatebase(this, post.getLesson_key(), String.valueOf(Calendar.getInstance().getTimeInMillis()), currentUser, files, new StringArrayListInterface() {
                @Override
                public void getArrayList(ArrayList<String> list) {

                }
            });
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Helper.shared().back(EditPostActivity.this);
    }


    //TODO-permission
    private void uploadImage() {
        if (!checkGalleryPermissions()){
            requestStoragePermission();
        }
        else{ pickGallery();}
    }
    private boolean checkGalleryPermissions()
    {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }
    private void requestStoragePermission()
    {
        ActivityCompat.requestPermissions(this,storagePermission,gallery_request);

    }
    private void pickGallery()
    {

        ArrayList<Image> images = new ArrayList<>();
      ImagePicker.create(this)
              .language("tr")
              .folderMode(true)
              .toolbarFolderTitle("Resim Seç")
              .toolbarImageTitle("Seçmek İçin Dokun")
              .includeVideo(false)
              .single()
              .limit(1)
              .showCamera(false)
              .origin(images)
              .exclude(images)
              .enableLog(false)
              .start();


    }





}