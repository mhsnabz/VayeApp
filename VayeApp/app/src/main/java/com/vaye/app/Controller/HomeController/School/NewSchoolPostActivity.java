package com.vaye.app.Controller.HomeController.School;

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
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
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
import com.vaye.app.Controller.HomeController.StudentSetNewPost.NewPostAdapter;
import com.vaye.app.Controller.HomeController.StudentSetNewPost.StudentNewPostActivity;
import com.vaye.app.Interfaces.DataTypes;
import com.vaye.app.Interfaces.Notifications;
import com.vaye.app.Interfaces.StringCompletion;
import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.LessonModel;
import com.vaye.app.Model.NewPostDataModel;
import com.vaye.app.Model.NoticesMainModel;
import com.vaye.app.R;
import com.vaye.app.Services.SchoolPostNS;
import com.vaye.app.Services.SchoolPostService;
import com.vaye.app.Util.Helper;
import com.vaye.app.Util.RunTimePermissionHelper;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;



public class NewSchoolPostActivity extends AppCompatActivity {
    private static final String TAG = "NewSchoolPostActivity";
    Toolbar toolbar;

    TextView title;
    ImageButton rigthBarButton;
    CurrentUser currentUser;
    StorageTask<UploadTask.TaskSnapshot> uploadTask;
    CircleImageView profileImage;
    TextView name , username , clupNameText;
    ProgressBar progressBar;
    EditText text;
    String clupName;
    RecyclerView datas;
    KProgressHUD hud;
    NoticesMainModel noticesMainModel;
    private ArrayList<String> photoPaths = new ArrayList<>();
    String storagePermission[];
    String contentType = "";
    String mimeType = "";
    long postDate = Calendar.getInstance().getTimeInMillis();
    ImageButton addImage;
    ImageView sampleImage;
    private static final int gallery_request =400;
    ArrayList<NewPostDataModel> dataModel = new ArrayList<>();
    NewPostAdapter adapter ;
    ArrayList<String> followers = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_school_post);
        Bundle extras = getIntent().getExtras();
        Intent intentIncoming = getIntent();
        sampleImage = (ImageView)findViewById(R.id.sampleImage);

        if (extras != null){
            currentUser = intentIncoming.getParcelableExtra("currentUser");

            clupName = intentIncoming.getStringExtra("clupName");
            followers = intentIncoming.getStringArrayListExtra("followers");
            setToolbar(clupName);
             setView(currentUser,clupName);


        }else {
            finish();
        }

        hud = KProgressHUD.create(NewSchoolPostActivity.this)
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
                Helper.shared().back(NewSchoolPostActivity.this);
            }
        });
        rigthBarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WaitDialog.show(NewSchoolPostActivity.this,"Gönderiniz Paylaşılıyor");
                String  msgText = text.getText().toString();
                if (msgText.isEmpty()){
                    WaitDialog.dismiss();
                    TipDialog.show(NewSchoolPostActivity.this , "Gönderiniz Boş Olamaz", TipDialog.TYPE.ERROR);
                    TipDialog.dismiss(1000);
                    return;
                }else {

                    SchoolPostNS.shared().setNewNoticesNotification(followers , currentUser , clupName , String.valueOf(Calendar.getInstance().getTimeInMillis()),String.valueOf(postDate), Notifications.NotificationDescription.notices_new_post, Notifications.NotificationType.notices_new_post);
                    for (String item : Helper.shared().getMentionedUser(msgText)){
                        Log.d("SchoolPostNS", "onClick: " + item);
                        SchoolPostNS.shared().setMentionedNotification(item , currentUser ,String.valueOf(postDate), Notifications.NotificationType.home_new_mentions_post, Notifications.NotificationDescription.home_new_mentions_post,clupName);
                    }
                    SchoolPostService.shared().setNewNotice(currentUser, clupName, msgText, postDate, dataModel, new TrueFalse<Boolean>() {
                        @Override
                        public void callBack(Boolean _value) {
                            if (_value){
                                DocumentReference ref = FirebaseFirestore.getInstance().collection(currentUser.getShort_school())
                                        .document("notices").collection("post").document(String.valueOf(postDate));
                                Map<String , Object> mapObj = new HashMap<>();
                                if (!dataModel.isEmpty()){
                                    mapObj.put("type","data");

                                    for (int i = 0 ; i < dataModel.size() ; i ++){
                                        mapObj.put("data",FieldValue.arrayUnion(dataModel.get(i).getFileUrl()));
                                        mapObj.put("thumbData",FieldValue.arrayUnion(dataModel.get(i).getThumb_url()));
                                        ref.set(mapObj,SetOptions.merge());

                                    }

                                }

                                WaitDialog.dismiss();
                                TipDialog.show(NewSchoolPostActivity.this , "Gönderiniz Paylaşıldı", TipDialog.TYPE.SUCCESS);
                                TipDialog.dismiss(1000);
                                finish();
                                Helper.shared().back(NewSchoolPostActivity.this);
                            }
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
        clupNameText = (TextView)findViewById(R.id.lessonName);
        progressBar = (ProgressBar)findViewById(R.id.progress);
        text = (EditText)findViewById(R.id.text);

        String thumbImage ;
        if (currentUser.getThumb_image()!=null && !currentUser.getThumb_image().isEmpty()){
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
            profileImage.setImageResource(android.R.color.darker_gray);
           progressBar.setVisibility(View.GONE);
        }


        name.setText(currentUser.getName());
        username.setText(currentUser.getUsername());
        clupNameText.setText(selectedLessonName);



        setRecylerView(currentUser);
        setStackView();

    }

    private void setStackView() {
        addImage = (ImageButton)findViewById(R.id.gallery);
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();
            }
        });
    }

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

    private void setRecylerView(CurrentUser currentUser) {
        adapter = new NewPostAdapter(dataModel,NewSchoolPostActivity.this,currentUser);
        datas = (RecyclerView)findViewById(R.id.datasRec);
        datas.setLayoutManager(new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL));
        datas.setAdapter(adapter);
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == gallery_request){
            Uri file = data.getData();
            decodeUri(data.getData());
            String fileName = String.valueOf(Calendar.getInstance().getTimeInMillis());
            String mimeType = "."+getMimeType(file);
            String contentType = getMContentType(file);
            Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(decodeUri(data.getData()), 300, 300);

            dataModel.add(new NewPostDataModel(fileName, file, null, null, mimeType, contentType));
            saveDatasToDataBase(contentType, mimeType, NewSchoolPostActivity.this, clupName, String.valueOf(postDate), currentUser, "image", file, new StringCompletion() {
                @Override
                public void getString(String url) {
                    setThumbData(contentType, NewSchoolPostActivity.this, clupName, String.valueOf(postDate), mimeType, currentUser, "image", ThumbImage, new StringCompletion() {
                        @Override
                        public void getString(String thumb_url) {
                            updateImages(NewSchoolPostActivity.this,  currentUser, url, thumb_url, new TrueFalse<Boolean>() {
                                @Override
                                public void callBack(Boolean _value) {
                                    WaitDialog.dismiss();
                                    TipDialog.show(NewSchoolPostActivity.this , "Dosya Yüklendi", TipDialog.TYPE.SUCCESS);
                                    TipDialog.dismiss(1500);
                                    for (int i = 0 ; i< dataModel.size() ; i++){
                                        if (dataModel.get(i).getFile() == file){
                                            dataModel.get(i).setFileUrl(url);
                                            dataModel.get(i).setThumb_url(thumb_url);
                                            adapter.notifyDataSetChanged();
                                        }
                                    }
                                    title.setText(String.valueOf(getTotalSize(NewSchoolPostActivity.this , dataModel) +"mb"));

                                }
                            });
                        }
                    });
                }
            });
        }

    }

    //TODO:: upload images
    private void saveDatasToDataBase(String contentType, String mimeType, Activity activity , String clup_name , String date , CurrentUser currentUser , String type , Uri data,
                                     StringCompletion completion ){
        hud.show();

        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType(contentType)
                .build();
        StorageReference ref = FirebaseStorage.getInstance().getReference().child(currentUser.getShort_school())
                .child("clup")
                .child(clup_name)
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
    private void setThumbData(String contentType, Activity activity ,String clup_name , String date ,String mimeType, CurrentUser currentUser , String type , Bitmap data,
                              StringCompletion completion)  {
        WaitDialog.show((AppCompatActivity) activity ,null);


        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType(contentType)
                .build();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        data.compress(Bitmap.CompressFormat.JPEG, 25, baos);
        final byte[] thumb_byte = baos.toByteArray();


            StorageReference ref = FirebaseStorage.getInstance().getReference().child(currentUser.getShort_school() +"thumb")
                    .child("clup")
                    .child(clup_name)
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
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Helper.shared().back(NewSchoolPostActivity.this);
    }
}