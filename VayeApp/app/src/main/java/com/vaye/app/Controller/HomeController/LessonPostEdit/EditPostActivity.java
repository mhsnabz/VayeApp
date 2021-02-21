package com.vaye.app.Controller.HomeController.LessonPostEdit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.kongzue.dialog.v3.TipDialog;
import com.kongzue.dialog.v3.WaitDialog;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.vaye.app.Interfaces.DataTypes;
import com.vaye.app.Interfaces.DriveLinkNames;
import com.vaye.app.Interfaces.StringArrayListInterface;
import com.vaye.app.Interfaces.StringCompletion;
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
import com.vincent.filepicker.Constant;
import com.vincent.filepicker.activity.ImagePickActivity;
import com.vincent.filepicker.activity.NormalFilePickActivity;
import com.vincent.filepicker.filter.entity.AudioFile;
import com.vincent.filepicker.filter.entity.ImageFile;
import com.vincent.filepicker.filter.entity.NormalFile;
import com.vincent.filepicker.filter.entity.VideoFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

import static com.vincent.filepicker.activity.ImagePickActivity.IS_NEED_CAMERA;

public class EditPostActivity extends AppCompatActivity {
    private static final String TAG ="EditPostActivity" ;
    Toolbar toolbar;
    LessonPostModel post;
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
    PostDataAdaptar adaptar;

    RecyclerView datas;

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

        hud = KProgressHUD.create(EditPostActivity.this)
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
                updatePost(post);
            }
        });
    }

    private void updatePost(LessonPostModel post){
        String _text = text.getText().toString();
        if (_text.equals(post.getText())){
            TipDialog.show(EditPostActivity.this, "Hiç Değişiklik Yapmadınız" , TipDialog.TYPE.ERROR);
            TipDialog.dismiss(1500);
            return;
        }else if (_text.isEmpty()){
            TipDialog.show(EditPostActivity.this, "Gönderiniz Boş Olamaz" , TipDialog.TYPE.ERROR);
            TipDialog.dismiss(1500);
            return;
        }else {
            MajorPostService.shared().updatePost(EditPostActivity.this, _text, post.getPostId(), currentUser, new TrueFalse<Boolean>() {
                @Override
                public void callBack(Boolean _value) {
                    post.setText(_text);
                    finish();
                    Helper.shared().back(EditPostActivity.this);
                }
            });
        }
    }

    private void setView(CurrentUser currentUser , LessonPostModel model){
        profileImage = (CircleImageView)findViewById(R.id.profileImage);
        name = (TextView)findViewById(R.id.name);
        username = (TextView)findViewById(R.id.username);
        lessonName = (TextView)findViewById(R.id.lessonName);
        progressBar = (ProgressBar)findViewById(R.id.progress);
        text = (EditText)findViewById(R.id.text);


        if (currentUser.getThumb_image()!=null && !currentUser.getThumb_image().isEmpty()) {
            Picasso.get().load(currentUser.getThumb_image()).placeholder(android.R.color.darker_gray)
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
        }else{
            progressBar.setVisibility(View.GONE);
            profileImage.setImageResource(android.R.color.darker_gray);
        }


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



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case Constant.REQUEST_CODE_PICK_IMAGE:
                if (resultCode == RESULT_OK) {

                    ArrayList<ImageFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_IMAGE);
                    Uri file = Uri.fromFile(new File(list.get(0).getPath()));
                    saveDatasToDataBase(DataTypes.contentType.image,DataTypes.mimeType.image,this, post.getLesson_key(), String.valueOf(Calendar.getInstance().getTimeInMillis()), currentUser, "image", file,
                            new StringCompletion() {
                                @Override
                                public void getString(String url) {
                                    try {
                                        setThumbData(DataTypes.contentType.image,EditPostActivity.this, post.getLesson_key(), String.valueOf(Calendar.getInstance().getTimeInMillis()),DataTypes.mimeType.image, currentUser, "image", file, new StringCompletion() {
                                            @Override
                                            public void getString(String thumb_url) {
                                                updateImages(EditPostActivity.this, post, currentUser, url, thumb_url, new TrueFalse<Boolean>() {
                                                    @Override
                                                    public void callBack(Boolean _value) {
                                                        WaitDialog.dismiss();
                                                        TipDialog.show(EditPostActivity.this , "Dosya Yüklendi", TipDialog.TYPE.SUCCESS);
                                                        TipDialog.dismiss(1500);
                                                        adaptar.notifyDataSetChanged();
                                                    }
                                                });
                                            }
                                        });
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                }
                break;
            case Constant.REQUEST_CODE_PICK_VIDEO:
                if (resultCode == RESULT_OK) {
                    ArrayList<VideoFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_VIDEO);
                }
                break;
            case Constant.REQUEST_CODE_PICK_AUDIO:
                if (resultCode == RESULT_OK) {
                    ArrayList<AudioFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_AUDIO);
                }
                break;
            case Constant.REQUEST_CODE_PICK_FILE:
                if (resultCode == RESULT_OK) {
                    ArrayList<NormalFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_FILE);
                    Uri file = Uri.fromFile(new File(list.get(0).getPath()));
                    Log.d(TAG, "onActivityResult: " + list.get(0).getMimeType());

                    if (list.get(0).getMimeType().equals(DataTypes.contentType.doc )){
                        contentType = DataTypes.contentType.doc;
                        mimeType = DataTypes.mimeType.doc;
                    }else if(list.get(0).getMimeType().equals(DataTypes.contentType.docx)){
                        contentType = DataTypes.contentType.docx;
                        mimeType = DataTypes.mimeType.docx;
                    }else if (list.get(0).getMimeType().equals(DataTypes.contentType.pdf)){
                        contentType = DataTypes.contentType.pdf;
                        mimeType = DataTypes.mimeType.pdf;
                    }

                    saveDatasToDataBase(contentType, mimeType, this, post.getLesson_key(), String.valueOf(Calendar.getInstance().getTimeInMillis()),
                            currentUser, "file", file, new StringCompletion() {
                                @Override
                                public void getString(String url) {
                                    try {
                                        setThumbData(contentType, EditPostActivity.this, post.getLesson_key(), String.valueOf(Calendar.getInstance().getTimeInMillis()), mimeType, currentUser,
                                                "file", file, new StringCompletion() {
                                                    @Override
                                                    public void getString(String thumb_url) {
                                                        updateImages(EditPostActivity.this, post, currentUser, url, thumb_url, new TrueFalse<Boolean>() {
                                                            @Override
                                                            public void callBack(Boolean _value) {
                                                                WaitDialog.dismiss();
                                                                TipDialog.show(EditPostActivity.this , "Dosya Yüklendi", TipDialog.TYPE.SUCCESS);
                                                                TipDialog.dismiss(1500);
                                                                adaptar.notifyDataSetChanged();
                                                            }
                                                        });
                                                    }
                                                });
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                }
                break;
        }


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


    //TODO:: upload images
    private void saveDatasToDataBase(String contentType,String mimeType,Activity activity ,String lesson_key , String date , CurrentUser currentUser , String type , Uri data,
                                     StringCompletion completion ){
        hud.show();

            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setContentType(contentType)
                    .build();
            StorageReference ref = FirebaseStorage.getInstance().getReference().child(currentUser.getShort_school())
                    .child(currentUser.getBolum_key())
                    .child(lesson_key)
                    .child(currentUser.getUsername())
                    .child(date)
                    .child(String.valueOf(Calendar.getInstance().getTimeInMillis()) +mimeType);
            uploadTask =  ref.putFile(data , metadata).addOnCompleteListener(activity, new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()){

                            task.getResult().getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                        String  url = uri.toString();
                                    Log.d(TAG, "onComplete: "+ url);
                                    hud.dismiss();
                                    WaitDialog.show((AppCompatActivity) activity , "Dosya Yükleniyor");
                                    completion.getString(url);


                                }
                            });

                    }

                }

            });

        uploadTask.addOnProgressListener(activity, new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {

                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                hud.setProgress((int) progress);
                Log.d(TAG, "onProgress: " + progress);
            }
        });

    }
    private void setThumbData(String contentType, Activity activity ,String lesson_key , String date ,String mimeType, CurrentUser currentUser , String type , Uri data,
                              StringCompletion completion) throws IOException {
        WaitDialog.show((AppCompatActivity) activity ,null);
        if (type == "image"){

            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setContentType(contentType)
                    .build();

            File thumb_file = new File(data.getPath());
            Bitmap thumb_bitmap = new Compressor(this)
                    .setMaxHeight(300)
                    .setMaxWidth(300).setQuality(100)
                    .compressToBitmap(thumb_file);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            final byte[] thumb_byte = baos.toByteArray();


            StorageReference ref = FirebaseStorage.getInstance().getReference().child(currentUser.getShort_school() +"thumb")
                    .child(currentUser.getBolum_key())
                    .child(lesson_key)
                    .child(currentUser.getUsername())
                    .child(date)
                    .child(String.valueOf(Calendar.getInstance().getTimeInMillis()) +mimeType);

            ref.putBytes(thumb_byte, metadata).addOnCompleteListener(activity, new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()){
                            task.getResult().getMetadata().getReference().getDownloadUrl().addOnSuccessListener(activity, new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String thumb_url = uri.toString();
                                    completion.getString(thumb_url);

                                }
                            });
                        }
                }
            });
        }else {

            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setContentType(DataTypes.contentType.thumb)
                    .build();


            StorageReference ref = FirebaseStorage.getInstance().getReference().child(currentUser.getShort_school() +"thumb")
                    .child(currentUser.getBolum_key())
                    .child(lesson_key)
                    .child(currentUser.getUsername())
                    .child(date)
                    .child(String.valueOf(Calendar.getInstance().getTimeInMillis()) +DataTypes.mimeType.thumb);
            Uri imageUri = null;
            if (contentType.equals(DataTypes.contentType.pdf)){
                 imageUri = Uri.parse("android.resource://"+ R.class.getPackage().getName()+"/"+R.drawable.pdf_holder);
            }else if (contentType.equals(DataTypes.contentType.docx) || contentType.equals(DataTypes.contentType.doc)){
                 imageUri = Uri.parse("android.resource://"+ R.class.getPackage().getName()+"/"+R.drawable.doc_holder);
            }

            if (imageUri!=null){
                ref.putFile(imageUri , metadata) .addOnCompleteListener(activity, new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()){
                            task.getResult().getMetadata().getReference().getDownloadUrl().addOnSuccessListener(activity, new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String thumb_url = uri.toString();
                                    completion.getString(thumb_url);

                                }
                            });
                        }
                    }
                });
            }



        }

    }
    private void  updateImages(Activity activity , LessonPostModel postModel ,CurrentUser currentUser ,String url , String  thumb_url, TrueFalse<Boolean> callback ){
        DocumentReference ref = FirebaseFirestore.getInstance().collection(currentUser.getShort_school())
                .document("lesson-post")
                .collection("post")
                .document(postModel.getPostId());
        Map<String , Object> map = new HashMap<>();
        map.put("data", FieldValue.arrayUnion(url));
        map.put("thumbData",FieldValue.arrayUnion(thumb_url));
        map.put("type","data");
        ref.set(map , SetOptions.merge()).addOnCompleteListener(activity, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    postModel.getData().add(url);
                    postModel.getThumbData().add(thumb_url);
                    callback.callBack(true);
                }
            }
        });

    }

}