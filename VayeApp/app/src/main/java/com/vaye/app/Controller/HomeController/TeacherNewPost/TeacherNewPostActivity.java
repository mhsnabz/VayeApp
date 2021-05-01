package com.vaye.app.Controller.HomeController.TeacherNewPost;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
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
import com.vaye.app.Controller.HomeController.StudentSetNewPost.NewPostAdapter;
import com.vaye.app.Controller.HomeController.StudentSetNewPost.StudentNewPostActivity;
import com.vaye.app.Controller.NotificationService.MajorPostNotification;
import com.vaye.app.Controller.NotificationService.NotificationPostType;
import com.vaye.app.Controller.NotificationService.PushNotificationService;
import com.vaye.app.Controller.NotificationService.PushNotificationTarget;
import com.vaye.app.Controller.NotificationService.PushNotificationType;
import com.vaye.app.Interfaces.CompletionWithValue;
import com.vaye.app.Interfaces.DataTypes;
import com.vaye.app.Interfaces.DriveLinkNames;
import com.vaye.app.Interfaces.OtherUserService;
import com.vaye.app.Interfaces.StringCompletion;
import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.NewPostDataModel;
import com.vaye.app.Model.OtherUser;
import com.vaye.app.R;
import com.vaye.app.Services.MajorPostNS;
import com.vaye.app.Services.MajorPostService;
import com.vaye.app.Services.UserService;
import com.vaye.app.Util.BottomSheetHelper.BottomSheetActionTarget;
import com.vaye.app.Util.BottomSheetHelper.BottomSheetModel;
import com.vaye.app.Util.BottomSheetHelper.BottomSheetTarget;
import com.vaye.app.Util.Helper;
import com.vaye.app.Util.RunTimePermissionHelper;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;



public class TeacherNewPostActivity extends AppCompatActivity {
    String TAG = "TeacherNewPostActivity";
    private static final int PICK_PDF_CODE =800;
    private static final int PICK_DOC_CODE =801;
    CurrentUser currentUser;
    String selectedLesson ;
    TextView showList;
    Toolbar toolbar;
    ImageView sampleImage;
    int studentCount;
    TextView title;
    ImageButton rigthBarButton;
    StorageTask<UploadTask.TaskSnapshot> uploadTask;
    CircleImageView profileImage;
    TextView name , username , lessonName;
    ProgressBar progressBar;
    EditText text;
    RelativeLayout driveLayout;
    ImageButton driveIcon , deleteClick;
    TextView linkName;
    RecyclerView datas;
    KProgressHUD hud;

    String selectedLink = "";


    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int gallery_request =400;
    private static final int camera_pick_request =800;
    String link = "";
    private int MAX_ATTACHMENT_COUNT = 10;
    private ArrayList<String> photoPaths = new ArrayList<>();
    private ArrayList<Uri> docPaths = new ArrayList<>();

    String storagePermission[];
    //Stackview
    ImageButton addImage, addDoc , addPdf , addLink;
    String contentType = "";
    String mimeType = "";
    long postDate = Calendar.getInstance().getTimeInMillis();
    ArrayList<NewPostDataModel> dataModel = new ArrayList<>();
    ArrayList<String> lessonFallowerUsers = new ArrayList<>();
    NewPostAdapter adapter ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_new_post);
        sampleImage = (ImageView)findViewById(R.id.sampleImage);

        Bundle extras = getIntent().getExtras();
        Intent intentIncoming = getIntent();
        if (extras != null){
            currentUser = intentIncoming.getParcelableExtra("currentUser");
            selectedLesson = intentIncoming.getStringExtra("selectedLesson");
            lessonFallowerUsers = intentIncoming.getStringArrayListExtra("userList");
            showList = (TextView)findViewById(R.id.userList);
            showList.setText("("+lessonFallowerUsers.size() + ") Öğrenci Listesini Gör");
            showList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(TeacherNewPostActivity.this,UserList.class);
                    i.putExtra("currentUser",currentUser);
                    i.putExtra("userList",lessonFallowerUsers);
                    startActivity(i);
                    Helper.shared().go(TeacherNewPostActivity.this);

                }
            });
            setView(currentUser,selectedLesson);
            setToolbar(selectedLesson);
        }
        hud = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.ANNULAR_DETERMINATE)
                .setLabel("Dosya Yükleniyor")
                .setMaxProgress(100);
    }
    public Bitmap decodeUri(Uri uri) {
        ParcelFileDescriptor parcelFD = null;
        try {
            parcelFD = getContentResolver().openFileDescriptor(uri, "r");
            FileDescriptor imageSource = parcelFD.getFileDescriptor();

            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeFileDescriptor(imageSource, null, o);

            // the new size we want to scale to
            final int REQUIRED_SIZE = 1024;

            // Find the correct scale value. It should be the power of 2.
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE) {
                    break;
                }
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            // decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            Bitmap bitmap = BitmapFactory.decodeFileDescriptor(imageSource, null, o2);

            sampleImage.setImageBitmap(bitmap);
            return  BitmapFactory.decodeFileDescriptor(imageSource, null, o2);
        } catch (FileNotFoundException e) {
            // handle errors
        } finally {
            if (parcelFD != null)
                try {
                    parcelFD.close();
                } catch (IOException e) {
                    // ignored
                }
        }
        return  null;
    }
    private String getMimeType(Uri uri){
        ContentResolver cR = this.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        String type = mime.getExtensionFromMimeType(cR.getType(uri));
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
    private String getMContentType(Uri uri){
        ContentResolver cR = this.getContentResolver();
        return cR.getType(uri);
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
                Helper.shared().back(TeacherNewPostActivity.this);
            }
        });

        rigthBarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String  msgText = text.getText().toString();
                if (msgText.isEmpty()){
                    TipDialog.show(TeacherNewPostActivity.this , "Gönderiniz Boş Olamaz", TipDialog.TYPE.ERROR);
                    return;
                }else {
                    WaitDialog.show(TeacherNewPostActivity.this , "Gönderiniz Paylaşılıyor...");
                    Log.d(TAG, "onClick: " + lessonFallowerUsers.size());
                    for (String item : Helper.shared().getMentionedUser(text.getText().toString())){
                        String notId = String.valueOf(Calendar.getInstance().getTimeInMillis());
                        UserService.shared().getOtherUser_Mentioned(item, new OtherUserService() {
                            @Override
                            public void callback(OtherUser otherUser) {
                                if (otherUser != null){
                                    if (otherUser.getShort_school().equals(currentUser.getShort_school()))
                                    {
                                        if (otherUser.getBolum().equals(currentUser.getBolum())){
                                            if (!currentUser.getUid().equals(otherUser.getUid())){
                                                DocumentReference ref = FirebaseFirestore.getInstance().collection("user")
                                                        .document(otherUser.getUid())
                                                        .collection("notification")
                                                        .document(notId);
                                                ref.set(Helper.shared().getDictionary(NotificationPostType.name.lessonPost,MajorPostNotification.type.new_mentioned_post,text.getText().toString(),currentUser,notId,null,String.valueOf(postDate),lessonName,null,null));
                                                PushNotificationService.shared().sendPushNotification(currentUser,PushNotificationType.lessonNotices,notId, otherUser.getUid(), otherUser, PushNotificationTarget.newpost_lessonpost, currentUser.getName(), text.getText().toString(), MajorPostNotification.descp.new_mentioned_post, currentUser.getUid());

                                            }

                                        }
                                    }
                                }
                            }
                        });
                    }

                    MajorPostService.shared().teacherSetNewPost(selectedLesson, link, currentUser, postDate, lessonFallowerUsers
                            , msgText, dataModel, lessonName, new TrueFalse<Boolean>() {
                                @Override
                                public void callBack(Boolean _value) {
                                    if (_value){
                                        MajorPostNS.shared().teacherNewPostNotification(lessonFallowerUsers, NotificationPostType.name.lessonPost, currentUser, lessonName, text.getText().toString(), MajorPostNotification.type.new_post, String.valueOf(postDate));
                                        SystemClock.sleep(8000);

                                        WaitDialog.dismiss();
                                        TipDialog.show(TeacherNewPostActivity.this , "Gönderiniz Paylaşıldı", TipDialog.TYPE.SUCCESS);
                                        TipDialog.dismiss(1400);
                                        finish();
                                        Helper.shared().back(TeacherNewPostActivity.this);
                                    }

                                    MajorPostService.shared().moveSavedLinkOnpost(String.valueOf(postDate), currentUser, new TrueFalse<Boolean>() {
                                        @Override
                                        public void callBack(Boolean _value) {
                                            if (_value){

                                                DocumentReference ref = FirebaseFirestore.getInstance().collection(currentUser.getShort_school())
                                                        .document("lesson-post")
                                                        .collection("post")
                                                        .document(String.valueOf(postDate));

                                                Map<String , Object> mapObj = new HashMap<>();
                                                if (!dataModel.isEmpty()){
                                                    mapObj.put("type","data");

                                                    for (int i = 0 ; i < dataModel.size() ; i ++){
                                                        mapObj.put("data",FieldValue.arrayUnion(dataModel.get(i).getFileUrl()));
                                                        mapObj.put("thumbData",FieldValue.arrayUnion(dataModel.get(i).getThumb_url()));
                                                        ref.set(mapObj,SetOptions.merge());
                                                    }

                                                }
                                            }
                                        }
                                    });

                                }
                            });

                }
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
        lessonName.setText(selectedLessonName);



        setRecylerView(currentUser);
        setStackView();

    }


    private void setRecylerView(CurrentUser currentUser ){
        adapter = new NewPostAdapter(dataModel,TeacherNewPostActivity.this,currentUser);
        datas = (RecyclerView)findViewById(R.id.datasRec);
        datas.setLayoutManager(new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL));
        datas.setAdapter(adapter);


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
        BottomSheetModel model = new BottomSheetModel(items , BottomSheetTarget.new_post_add_link_target , res);
        Helper.shared().NewPostBottomSheetAddLink(TeacherNewPostActivity.this, BottomSheetTarget.new_post_add_link_target, link, currentUser, model, new CompletionWithValue() {
            @Override
            public void completion(Boolean bool, String val) {
                WaitDialog.show(TeacherNewPostActivity.this,null);
                getSavedTaskLink(currentUser, new StringCompletion() {
                    @Override
                    public void getString(String string) {
                        if (string != null){
                            detectLink(string);

                        }else{
                            WaitDialog.dismiss();
                        }
                    }
                });
            }
        });
        ;
    }

    private void getSavedTaskLink(CurrentUser currentUser , StringCompletion url){
        DocumentReference reference = FirebaseFirestore.getInstance().collection("user")
                .document(currentUser.getUid())
                .collection("saved-task")
                .document("task");
        reference.addSnapshotListener(TeacherNewPostActivity.this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error == null){
                    if (value.exists()){
                        url.getString(value.getString("link"));
                    }else{
                        url.getString("");
                    }

                }
            }
        });
    }

    private void detectLink(String link){

        driveLayout = (RelativeLayout)findViewById(R.id.driveLayout);
        driveIcon = (ImageButton)findViewById(R.id.driveIcon);
        deleteClick = (ImageButton)findViewById(R.id.cancelButton);
        linkName = (TextView)findViewById(R.id.linkName);

        if (link == null || link.equals("")){

            driveLayout.setVisibility(View.GONE);
        }else{
            driveLayout.setVisibility(View.VISIBLE);
            try {
                if(MajorPostService.shared().getLink(link) .equals("drive.google.com")
                        || MajorPostService.shared().getLink(link).equals("www.drive.google.com")){
                    WaitDialog.dismiss();
                    Log.d(TAG, "detectLink: " + MajorPostService.shared().getLink(link));
                    driveIcon.setImageResource(R.drawable.google_drive);
                    linkName.setText(DriveLinkNames.googleDrive);
                }else if ( MajorPostService.shared().getLink(link).equals("onedrive.live.com" )
                        || MajorPostService.shared().getLink(link).equals("www.onedrive.live.com")|| link.equals("1drv.ms")){
                    WaitDialog.dismiss();
                    driveIcon.setImageResource(R.drawable.onedrive);
                    linkName.setText(DriveLinkNames.onedrive);
                    Log.d(TAG, "detectLink: " + MajorPostService.shared().getLink(link));

                }else if ( MajorPostService.shared().getLink(link).equals("dropbox.com")
                        ||  MajorPostService.shared().getLink(link).equals("www.dropbox.com")){
                    WaitDialog.dismiss();
                    driveIcon.setImageResource(R.drawable.dropbox);
                    linkName.setText(DriveLinkNames.dropbox);
                    Log.d(TAG, "detectLink: " + MajorPostService.shared().getLink(link));

                }else if ( MajorPostService.shared().getLink(link).equals("icloud.com")
                        ||  MajorPostService.shared().getLink(link).equals("www.icloud.com")){
                    WaitDialog.dismiss();
                    driveIcon.setImageResource(R.drawable.icloud);
                    linkName.setText(DriveLinkNames.icloud);
                    Log.d(TAG, "detectLink: " + MajorPostService.shared().getLink(link));

                }else if ( MajorPostService.shared().getLink(link).equals("disk.yandex.com.tr")
                        ||  MajorPostService.shared().getLink(link).equals("disk.yandex.com") || MajorPostService.shared().getLink(link).equals("yadi.sk")){
                    WaitDialog.dismiss();
                    driveIcon.setImageResource(R.drawable.yandex);
                    linkName.setText(DriveLinkNames.yandex);
                    Log.d(TAG, "detectLink: " + MajorPostService.shared().getLink(link));

                }else if ( MajorPostService.shared().getLink(link).equals("mega.nz")
                        ||  MajorPostService.shared().getLink(link).equals("www.mega.nz")){
                    WaitDialog.dismiss();
                    driveIcon.setImageResource(R.drawable.mega);
                    linkName.setText(DriveLinkNames.mega);
                    Log.d(TAG, "detectLink: " + MajorPostService.shared().getLink(link));
                }else{
                    WaitDialog.dismiss();
                }
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            deleteClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    WaitDialog.show(TeacherNewPostActivity.this,null);
                    MajorPostService.shared().deleteSavedLink(currentUser, new TrueFalse<Boolean>() {
                        @Override
                        public void callBack(Boolean _value) {
                            if (_value){
                                detectLink("");
                                WaitDialog.dismiss();
                            }
                        }
                    });
                }
            });
        }
    }
    //TODO-permission
    private void uploadImage() {

        if (!RunTimePermissionHelper.shared().checkGalleryPermission(this)){
            RunTimePermissionHelper.shared().requestGalleryCameraPermission(this, new TrueFalse<Boolean>() {
                @Override
                public void callBack(Boolean _value) {
                    if (_value){
                        pickGallery();
                    }
                }
            });
        }else{
            pickGallery();

        }
    }
    private void pickGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(intent.CATEGORY_OPENABLE);
        startActivityForResult(intent,gallery_request);

    }
    private boolean checkGalleryPermissions() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }
    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this,storagePermission,gallery_request);

    }

    private void picDoc(){


        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("application/msword");
        startActivityForResult(intent,PICK_DOC_CODE);
    }


    private void UploadDoc() {

        if (!RunTimePermissionHelper.shared().checkGalleryPermission(this)){
            RunTimePermissionHelper.shared().requestGalleryCameraPermission(this, new TrueFalse<Boolean>() {
                @Override
                public void callBack(Boolean _value) {
                    if (_value){
                        picDoc();
                    }
                }
            });
        }else{
            picDoc();

        }
    }

    private void uploadPdf(){
        if (!RunTimePermissionHelper.shared().checkGalleryPermission(this)){
            RunTimePermissionHelper.shared().requestGalleryCameraPermission(this, new TrueFalse<Boolean>() {
                @Override
                public void callBack(Boolean _value) {
                    if (_value){
                        pickPdf();
                    }
                }
            });
        }else{
            pickPdf();

        }
    }

    private void pickPdf(){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        startActivityForResult(intent,PICK_PDF_CODE);

    }
    public  double getImageSizeFromUriInMegaByte(Context context, Uri uri) {
        String scheme = uri.getScheme();
        double dataSize = 0;
        if (scheme.equals(ContentResolver.SCHEME_CONTENT)) {
            try {
                InputStream fileInputStream = context.getContentResolver().openInputStream(uri);
                if (fileInputStream != null) {
                    dataSize = fileInputStream.available();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (scheme.equals(ContentResolver.SCHEME_FILE)) {
            String path = uri.getPath();
            File file = null;
            try {
                file = new File(path);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (file != null) {
                dataSize = file.length();
            }
        }
        return dataSize / (1024 * 1024);
    }

    private double getTotalSize(Context context , ArrayList<NewPostDataModel> dataModel){
        double totolVal = 0.0;
        for (NewPostDataModel model : dataModel){
            totolVal += getImageSizeFromUriInMegaByte(context,model.getFile());
        }
        return  totolVal;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            if (requestCode == gallery_request){
                Uri file = data.getData();
                decodeUri(data.getData());
                String fileName = String.valueOf(Calendar.getInstance().getTimeInMillis());
                String mimeType = "."+getMimeType(file);
                String contentType = getMContentType(file);
                Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(decodeUri(data.getData()), 300, 300);

                dataModel.add(new NewPostDataModel(fileName, file, null, null, mimeType, contentType));
                saveDatasToDataBase(contentType, mimeType, TeacherNewPostActivity.this, selectedLesson, String.valueOf(postDate), currentUser, "image", file, new StringCompletion() {
                    @Override
                    public void getString(String url) {
                        setThumbData(contentType, TeacherNewPostActivity.this, selectedLesson, String.valueOf(postDate), mimeType, currentUser, "image", ThumbImage, new StringCompletion() {
                            @Override
                            public void getString(String thumb_url) {
                                updateImages(TeacherNewPostActivity.this,  currentUser, url, thumb_url, new TrueFalse<Boolean>() {
                                    @Override
                                    public void callBack(Boolean _value) {
                                        WaitDialog.dismiss();
                                        TipDialog.show(TeacherNewPostActivity.this , "Dosya Yüklendi", TipDialog.TYPE.SUCCESS);
                                        TipDialog.dismiss(1500);
                                        for (int i = 0 ; i< dataModel.size() ; i++){
                                            if (dataModel.get(i).getFile() == file){
                                                dataModel.get(i).setFileUrl(url);
                                                dataModel.get(i).setThumb_url(thumb_url);
                                                adapter.notifyDataSetChanged();
                                            }
                                        }
                                        title.setText(String.valueOf(getTotalSize(TeacherNewPostActivity.this , dataModel) +"mb"));
                                    }
                                });
                            }
                        });
                    }
                });
            }else if (requestCode == PICK_DOC_CODE){
                Log.d(TAG, "onActivityResult: PICK_DOC_CODE" + data.getData().getPath());
                if (getTotalSize(TeacherNewPostActivity.this , dataModel) < 15){
                    Uri file = data.getData();
                    String fileName = String.valueOf(Calendar.getInstance().getTimeInMillis());
                    String mimeType = "."+getMimeType(file);
                    String contentType = getMContentType(file);

                    dataModel.add(new NewPostDataModel(fileName,file,null,null,mimeType,contentType));
                    title.setText(String.valueOf(getTotalSize(TeacherNewPostActivity.this , dataModel) +"mb"));
                    saveDatasToDataBase(contentType, mimeType, TeacherNewPostActivity.this, selectedLesson, String.valueOf(postDate), currentUser, "file", file, new StringCompletion() {
                        @Override
                        public void getString(String url) {
                            setThumbData(contentType, TeacherNewPostActivity.this, selectedLesson, String.valueOf(postDate), mimeType, currentUser, "file", null, new StringCompletion() {
                                @Override
                                public void getString(String thumb_url) {
                                    updateImages(TeacherNewPostActivity.this,  currentUser, url, thumb_url, new TrueFalse<Boolean>() {
                                        @Override
                                        public void callBack(Boolean _value) {
                                            WaitDialog.dismiss();
                                            TipDialog.show(TeacherNewPostActivity.this , "Dosya Yüklendi", TipDialog.TYPE.SUCCESS);
                                            TipDialog.dismiss(1500);
                                            for (int i = 0 ; i< dataModel.size() ; i++){
                                                if (dataModel.get(i).getFile() == file){
                                                    dataModel.get(i).setFileUrl(url);
                                                    dataModel.get(i).setThumb_url(thumb_url);
                                                    adapter.notifyDataSetChanged();
                                                }
                                            }
                                            title.setText(String.valueOf(getTotalSize(TeacherNewPostActivity.this , dataModel) +"mb"));

                                        }
                                    });
                                }
                            });

                        }
                    });
                }
            }
            else if (requestCode == PICK_PDF_CODE){
                Uri file = data.getData();
                String fileName = String.valueOf(Calendar.getInstance().getTimeInMillis());
                String mimeType = "."+getMimeType(file);
                String contentType = getMContentType(file);

                dataModel.add(new NewPostDataModel(fileName,file,null,null,mimeType,contentType));
                saveDatasToDataBase(contentType, mimeType, TeacherNewPostActivity.this, selectedLesson, String.valueOf(postDate), currentUser, "file", file, new StringCompletion() {
                    @Override
                    public void getString(String url) {

                        setThumbData(contentType, TeacherNewPostActivity.this, selectedLesson, String.valueOf(postDate), mimeType, currentUser, "file", null, new StringCompletion() {
                            @Override
                            public void getString(String thumb_url) {
                                updateImages(TeacherNewPostActivity.this,  currentUser, url, thumb_url, new TrueFalse<Boolean>() {
                                    @Override
                                    public void callBack(Boolean _value) {
                                        WaitDialog.dismiss();
                                        TipDialog.show(TeacherNewPostActivity.this , "Dosya Yüklendi", TipDialog.TYPE.SUCCESS);
                                        TipDialog.dismiss(1500);
                                        for (int i = 0 ; i< dataModel.size() ; i++){
                                            if (dataModel.get(i).getFile() == file){
                                                dataModel.get(i).setFileUrl(url);
                                                dataModel.get(i).setThumb_url(thumb_url);
                                                adapter.notifyDataSetChanged();
                                            }
                                        }
                                        title.setText(String.valueOf(getTotalSize(TeacherNewPostActivity.this , dataModel) +"mb"));

                                    }
                                });
                            }
                        });

                    }
                });
            }
        }

        /*switch (requestCode){
            case Constant.REQUEST_CODE_PICK_IMAGE:

                if (getTotalSize(TeacherNewPostActivity.this , dataModel) < 15){
                    if (resultCode == RESULT_OK){

                        ArrayList<ImageFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_IMAGE);
                        Uri file = Uri.fromFile(new File(list.get(0).getPath()));
                        String fileName = list.get(0).getName();
                        Uri fileUri = Uri.fromFile(new File(list.get(0).getPath()));
                        String mimeType = DataTypes.mimeType.image;
                        String  contentType = DataTypes.contentType.image;
                        dataModel.add(new NewPostDataModel(fileName , fileUri,null,null,mimeType,contentType));
                        saveDatasToDataBase(DataTypes.contentType.image,DataTypes.mimeType.image,this, selectedLesson, String.valueOf(postDate), currentUser, "image", file,
                                new StringCompletion() {
                                    @Override
                                    public void getString(String url) {
                                        try {
                                            setThumbData(DataTypes.contentType.image,TeacherNewPostActivity.this, selectedLesson, String.valueOf(postDate),DataTypes.mimeType.image, currentUser, "image", file, new StringCompletion() {
                                                @Override
                                                public void getString(String thumb_url) {
                                                    updateImages(TeacherNewPostActivity.this,  currentUser, url, thumb_url, new TrueFalse<Boolean>() {
                                                        @Override
                                                        public void callBack(Boolean _value) {
                                                            WaitDialog.dismiss();
                                                            TipDialog.show(TeacherNewPostActivity.this , "Dosya Yüklendi", TipDialog.TYPE.SUCCESS);
                                                            TipDialog.dismiss(1500);
                                                            for (int i = 0 ; i< dataModel.size() ; i++){
                                                                if (dataModel.get(i).getFile() == fileUri){
                                                                    dataModel.get(i).setFileUrl(url);
                                                                    dataModel.get(i).setThumb_url(thumb_url);
                                                                    adapter.notifyDataSetChanged();
                                                                }
                                                            }
                                                            title.setText(String.valueOf(getTotalSize(TeacherNewPostActivity.this , dataModel) +"mb"));
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
                }

                break;
            case Constant.REQUEST_CODE_PICK_FILE:

                if (getTotalSize(TeacherNewPostActivity.this , dataModel) < 15){
                    if (resultCode == RESULT_OK) {
                        ArrayList<NormalFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_FILE);
                        Uri file = Uri.fromFile(new File(list.get(0).getPath()));
                        String fileName = list.get(0).getName();
                        Uri fileUri = Uri.fromFile(new File(list.get(0).getPath()));


                        if (list.get(0).getMimeType().equals(DataTypes.contentType.doc)) {
                            contentType = DataTypes.contentType.doc;
                            mimeType = DataTypes.mimeType.doc;
                        } else if (list.get(0).getMimeType().equals(DataTypes.contentType.docx)) {
                            contentType = DataTypes.contentType.docx;
                            mimeType = DataTypes.mimeType.docx;
                        } else if (list.get(0).getMimeType().equals(DataTypes.contentType.pdf)) {
                            contentType = DataTypes.contentType.pdf;
                            mimeType = DataTypes.mimeType.pdf;
                        }

                        dataModel.add(new NewPostDataModel(fileName,fileUri,null,null,mimeType,contentType));
                        saveDatasToDataBase(contentType, mimeType, this, selectedLesson, String.valueOf(postDate),
                                currentUser, "file", file, new StringCompletion() {
                                    @Override
                                    public void getString(String url) {
                                        try {
                                            setThumbData(contentType, TeacherNewPostActivity.this, selectedLesson, String.valueOf(postDate), mimeType, currentUser,
                                                    "file", file, new StringCompletion() {
                                                        @Override
                                                        public void getString(String thumb_url) {
                                                            updateImages(TeacherNewPostActivity.this,  currentUser, url, thumb_url, new TrueFalse<Boolean>() {
                                                                @Override
                                                                public void callBack(Boolean _value) {
                                                                    WaitDialog.dismiss();
                                                                    TipDialog.show(TeacherNewPostActivity.this , "Dosya Yüklendi", TipDialog.TYPE.SUCCESS);
                                                                    TipDialog.dismiss(1500);
                                                                    for (int i = 0 ; i< dataModel.size() ; i++){
                                                                        if (dataModel.get(i).getFile() == fileUri){
                                                                            dataModel.get(i).setFileUrl(url);
                                                                            dataModel.get(i).setThumb_url(thumb_url);
                                                                            adapter.notifyDataSetChanged();
                                                                        }
                                                                    }
                                                                    title.setText(String.valueOf(getTotalSize(TeacherNewPostActivity.this , dataModel) +"mb"));

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
                }
                break;

        }*/
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Helper.shared().back(TeacherNewPostActivity.this);
    }




    //TODO:: upload images
    private void saveDatasToDataBase(String contentType, String mimeType, Activity activity , String lesson_key , String date , CurrentUser currentUser , String type , Uri data,
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
    private void setThumbData(String contentType, Activity activity ,String lesson_key , String date ,String mimeType, CurrentUser currentUser , String type , Bitmap data,
                              StringCompletion completion) {
        WaitDialog.show((AppCompatActivity) activity ,null);
        if (type == "image"){


            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setContentType(contentType)
                    .build();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            data.compress(Bitmap.CompressFormat.JPEG, 25, baos);
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
    private void  updateImages(Activity activity , CurrentUser currentUser ,String url , String  thumb_url, TrueFalse<Boolean> callback ){

        ///user/VUSU6uA0odX7vuF5giXWbOUYzni1/saved-task/task
        DocumentReference ref = FirebaseFirestore.getInstance().collection("user")
                .document(currentUser.getUid())
                .collection("saved-task")
                .document("task");
        Map<String , Object> map = new HashMap<>();
        map.put("data", FieldValue.arrayUnion(url));
        map.put("thumbData",FieldValue.arrayUnion(thumb_url));
        map.put("type","data");
        ref.set(map , SetOptions.merge()).addOnCompleteListener(activity, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){

                    callback.callBack(true);
                }
            }
        });

    }

    @Override
    public void finish() {
        super.finish();
        MajorPostService.shared().deleteSavedLink(currentUser, new TrueFalse<Boolean>() {
            @Override
            public void callBack(Boolean _value) {

            }
        });
    }
}