package com.vaye.app.Controller.VayeAppController;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
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
import com.kongzue.dialog.v3.CustomDialog;
import com.kongzue.dialog.v3.TipDialog;
import com.kongzue.dialog.v3.WaitDialog;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.vaye.app.Controller.HomeController.StudentSetNewPost.NewPostAdapter;
import com.vaye.app.Controller.MapsController.LocationPermissionActivity;
import com.vaye.app.Interfaces.CompletionWithValue;
import com.vaye.app.Interfaces.DataTypes;
import com.vaye.app.Interfaces.Notifications;
import com.vaye.app.Interfaces.StringArrayListInterface;
import com.vaye.app.Interfaces.StringCompletion;
import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.MainPostModel;
import com.vaye.app.Model.MainPostTopicFollower;
import com.vaye.app.Model.NewPostDataModel;
import com.vaye.app.R;
import com.vaye.app.Services.MainPostNS;
import com.vaye.app.Util.BottomSheetHelper.BottomSheetActionTarget;
import com.vaye.app.Util.Helper;
import com.vincent.filepicker.Constant;
import com.vincent.filepicker.activity.ImagePickActivity;
import com.vincent.filepicker.filter.entity.ImageFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

import static com.vincent.filepicker.activity.ImagePickActivity.IS_NEED_CAMERA;

public class VayeAppNewPostActivity extends AppCompatActivity {
    private static final String TAG = "StudentNewPostActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;
    Toolbar toolbar;
    Double lat = null , longLat = null ;
    String locationName = "";
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
    KProgressHUD hud;
    MainPostModel mainPostModel;
    ArrayList<MainPostTopicFollower>  followers;
    String postType;
    String postName = "";
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int gallery_request =400;
    private static final int image_pick_request =600;
    private static final int camera_pick_request =800;
    String link = "";
    private int MAX_ATTACHMENT_COUNT = 10;
    private ArrayList<String> photoPaths = new ArrayList<>();
    private ArrayList<Uri> docPaths = new ArrayList<>();
    String value = "";
    String storagePermission[];
    //Stackview
    ImageButton addImage, map_pin , price , cancelPrice , cancelLacation;
    String contentType = "";
    String mimeType = "";
    long postDate = Calendar.getInstance().getTimeInMillis();
    ArrayList<NewPostDataModel> dataModel = new ArrayList<>();
    String notificationType = "";
    NewPostAdapter adapter ;
    RelativeLayout priceLayout , locationLayout;
    TextView totalPrice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vaye_app_new_post);
        Bundle extras = getIntent().getExtras();
        Intent intentIncoming = getIntent();


        if (extras != null){
            currentUser = intentIncoming.getParcelableExtra("currentUser");
            postType = intentIncoming.getStringExtra("postType");

            followers = intentIncoming.getParcelableExtra("followers");
            setToolbar(postType);
            setView(currentUser,postType);
            setStackView(postType);
            setRecylerView(currentUser);
            priceLayout = (RelativeLayout)findViewById(R.id.priceLayout);
            cancelPrice = (ImageButton)findViewById(R.id.cancelPriceButton) ;
            totalPrice = (TextView)findViewById(R.id.totalPrice);

            LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                    new IntentFilter("target_location"));

            cancelPrice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    value = "";
                    setPriceLayout(value);
                }
            });

        }else {
            finish();
        }

        hud = KProgressHUD.create(VayeAppNewPostActivity.this)
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
                Helper.shared().back(VayeAppNewPostActivity.this);
            }
        });
        rigthBarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                 String  msgText = text.getText().toString();
                if (msgText.isEmpty()){
                    TipDialog.show(VayeAppNewPostActivity.this , "Gönderiniz Boş Olamaz", TipDialog.TYPE.ERROR);
                    return;
                }else {
                    WaitDialog.show(VayeAppNewPostActivity.this , "Gönderiniz Paylaşılıyor...");
                    if (postType.equals(BottomSheetActionTarget.al_sat)){
                        notificationType = Notifications.NotificationType.new_ad;
                        postName = BottomSheetActionTarget.sell_buy;
                    }else if (postType.equals(BottomSheetActionTarget.yemek)){
                        notificationType = Notifications.NotificationType.new_food_me;
                        postName = BottomSheetActionTarget.food_me;
                    }
                    else if (postType.equals(BottomSheetActionTarget.kamp)){
                        notificationType = Notifications.NotificationType.new_camping;
                        postName = BottomSheetActionTarget.camping;

                    }
                    MainPostNS.shared().setNewPostNotification(followers,currentUser,String.valueOf(Calendar.getInstance().getTimeInMillis()),postType,msgText, notificationType,String.valueOf(postDate));
                    for (String username : Helper.shared().getMentionedUser(msgText)){
                        MainPostNS.shared().setMentionedPost(username,currentUser , String.valueOf(postDate), Notifications.NotificationType.home_new_mentions_post, Notifications.NotificationDescription.home_new_mentions_post );
                    }
                    MainPostNS.shared().getMyFollowers(currentUser.getUid(), new StringArrayListInterface() {
                        @Override
                        public void getArrayList(ArrayList<String> list) {

                                MainPostNS.shared().setNewPost(postName, "post", "", value, currentUser, null, postDate, list, msgText, dataModel, new TrueFalse<Boolean>() {
                                    @Override
                                    public void callBack(Boolean _value) {
                                        if (_value){
                                            DocumentReference ref = FirebaseFirestore.getInstance().collection("main-post")
                                                    .document("post")
                                                    .collection("post")
                                                    .document(String.valueOf(postDate));

                                            Map<String , Object> mapObj = new HashMap<>();
                                            if (!dataModel.isEmpty()){
                                                mapObj.put("type","data");

                                                for (int i = 0 ; i < dataModel.size() ; i ++){
                                                    mapObj.put("data",FieldValue.arrayUnion(dataModel.get(i).getFileUrl()));
                                                    mapObj.put("thumbData",FieldValue.arrayUnion(dataModel.get(i).getThumb_url()));
                                                    ref.set(mapObj, SetOptions.merge());

                                                }

                                            }

                                            WaitDialog.dismiss();
                                            TipDialog.show(VayeAppNewPostActivity.this , "Gönderiniz Paylaşıldı", TipDialog.TYPE.SUCCESS);
                                            TipDialog.dismiss(1000);
                                            finish();
                                            Helper.shared().back(VayeAppNewPostActivity.this);
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
    }
    private void setRecylerView(CurrentUser currentUser ){
        adapter = new NewPostAdapter(dataModel,VayeAppNewPostActivity.this,currentUser);
        datas = (RecyclerView)findViewById(R.id.datasRec);
        datas.setLayoutManager(new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL));
        datas.setAdapter(adapter);


    }

    public boolean isServicesOk(){
        Log.d(TAG, "isServicesOk: " +"check google service version");
        int avabile = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(VayeAppNewPostActivity.this);
        if (avabile == ConnectionResult.SUCCESS){
            return true;
        }else if (GoogleApiAvailability.getInstance().isUserResolvableError(avabile))
        {
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(VayeAppNewPostActivity.this ,avabile,ERROR_DIALOG_REQUEST);
            dialog.show();
            return  false;
        }else{

        }
        return  false;
    }
    private void setStackView(String postType){

        addImage = (ImageButton)findViewById(R.id.gallery);
        price = (ImageButton)findViewById(R.id.price);
        map_pin = (ImageButton)findViewById(R.id.map_pin);
        map_pin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isServicesOk()){
                    Intent i = new Intent(VayeAppNewPostActivity.this , LocationPermissionActivity.class);
                    startActivity(i);
                }
            }
        });

        if (postType.equals(BottomSheetActionTarget.al_sat)){
            addImage.setVisibility(View.VISIBLE);
            price.setVisibility(View.VISIBLE);
            map_pin.setVisibility(View.VISIBLE);

        }else if (postType.equals(BottomSheetActionTarget.yemek)){
            addImage.setVisibility(View.VISIBLE);
            price.setVisibility(View.GONE);
            map_pin.setVisibility(View.VISIBLE);
        }else if (postType.equals(BottomSheetActionTarget.kamp)){
            addImage.setVisibility(View.VISIBLE);
            price.setVisibility(View.GONE);
            map_pin.setVisibility(View.VISIBLE);
        }
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();
            }
        });

        price.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CustomDialog.show(VayeAppNewPostActivity.this, R.layout.add_price_layout, new CustomDialog.OnBindView() {
                    @Override
                    public void onBind(CustomDialog dialog, View v) {
                        Button addPrice = (Button)v.findViewById(R.id.addPrice);
                        EditText price = (EditText)v.findViewById(R.id.price);
                        Button cancel = (Button)v.findViewById(R.id.cancel);
                        ImageButton dismiss = ( ImageButton)v.findViewById(R.id.dismis);
                        dismiss.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.doDismiss();
                            }
                        });

                        addPrice.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String priceValue = price.getText().toString();
                                if (!priceValue.isEmpty()){
                                    value = priceValue;
                                    setPriceLayout(value);
                                }
                                dialog.doDismiss();
                            }
                        });
                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.doDismiss();
                            }
                        });


                    }
                });
            }
        });
    }

    private void setPriceLayout(String value){

        if (value.isEmpty() && value.equals("")){

            priceLayout.setVisibility(View.GONE);
            totalPrice.setText("");
        }else{
            priceLayout.setVisibility(View.VISIBLE);
            totalPrice.setText(value + " ₺");
        }

    }

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
        switch (requestCode){
            case Constant.REQUEST_CODE_PICK_IMAGE:
                if (getTotalSize(VayeAppNewPostActivity.this , dataModel) < 15){
                    if (resultCode == RESULT_OK){
                        ArrayList<ImageFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_IMAGE);
                        Uri file = Uri.fromFile(new File(list.get(0).getPath()));
                        String fileName = list.get(0).getName();
                        Uri fileUri = Uri.fromFile(new File(list.get(0).getPath()));
                        String mimeType = DataTypes.mimeType.image;
                        String  contentType = DataTypes.contentType.image;
                        dataModel.add(new NewPostDataModel(fileName , fileUri,null,null,mimeType,contentType));
                        saveDatasToDataBase(DataTypes.contentType.image, DataTypes.mimeType.image, this, String.valueOf(postDate), currentUser, file, new StringCompletion() {
                            @Override
                            public void getString(String url) {
                                try {
                                    setThumbData(DataTypes.contentType.image, VayeAppNewPostActivity.this, String.valueOf(postDate), DataTypes.mimeType.image, currentUser, "image", file, new StringCompletion() {
                                        @Override
                                        public void getString(String thumb_url) {
                                            updateImages(VayeAppNewPostActivity.this,  currentUser, url, thumb_url, new TrueFalse<Boolean>() {
                                                @Override
                                                public void callBack(Boolean _value) {
                                                    WaitDialog.dismiss();
                                                    TipDialog.show(VayeAppNewPostActivity.this , "Dosya Yüklendi", TipDialog.TYPE.SUCCESS);
                                                    TipDialog.dismiss(1500);
                                                    for (int i = 0 ; i< dataModel.size() ; i++){
                                                        if (dataModel.get(i).getFile() == fileUri){
                                                            dataModel.get(i).setFileUrl(url);
                                                            dataModel.get(i).setThumb_url(thumb_url);
                                                            adapter.notifyDataSetChanged();
                                                        }
                                                    }
                                                    title.setText(String.valueOf(getTotalSize(VayeAppNewPostActivity.this , dataModel) +"mb"));
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
        }
    }
    //TODO:: upload images
    private void saveDatasToDataBase(String contentType, String mimeType, Activity activity , String date , CurrentUser currentUser , Uri data,
                                     StringCompletion completion ){
        hud.show();

        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType(contentType)
                .build();
            StorageReference ref = FirebaseStorage.getInstance().getReference().child("main-post")
                .child(currentUser.getShort_school())
                .child(postType)
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
    private void setThumbData(String contentType, Activity activity ,String date ,String mimeType, CurrentUser currentUser , String type , Uri data,
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

            StorageReference ref = FirebaseStorage.getInstance().getReference().child("main-post-thumb")
                    .child(currentUser.getShort_school())
                    .child(postType)
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
    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String target = intent.getStringExtra("target");
            if (target.equals(CompletionWithValue.location)){
                 lat = intent.getDoubleExtra("lat",-45);
                 longLat = intent.getDoubleExtra("longLat",45);
                 locationName = intent.getStringExtra("locationName");
                Log.d(TAG, "onReceive: locaiton " + lat + " " + longLat + " " + locationName );
                setLocationLayout();
            }
        }
    };
    private void setLocationLayout(){
        locationLayout = (RelativeLayout)findViewById(R.id.locationLayout);
        locationLayout.setVisibility(View.VISIBLE);

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Helper.shared().back(VayeAppNewPostActivity.this);
    }

    public void hideLocationLayout(View view) {
        locationLayout.setVisibility(View.GONE);
        lat = null;
        locationName = "";
        longLat = null;
    }
}