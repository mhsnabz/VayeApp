package com.vaye.app.Controller.VayeAppController;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
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

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
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
import com.vaye.app.Interfaces.StringArrayListInterface;
import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.LessonFallowerUser;
import com.vaye.app.Model.LessonModel;
import com.vaye.app.Model.MainPostModel;
import com.vaye.app.Model.MainPostTopicFollower;
import com.vaye.app.Model.NewPostDataModel;
import com.vaye.app.R;
import com.vaye.app.Services.MainPostNS;
import com.vaye.app.Services.MajorPostNS;
import com.vaye.app.Util.BottomSheetHelper.BottomSheetActionTarget;
import com.vaye.app.Util.Helper;
import com.vincent.filepicker.Constant;
import com.vincent.filepicker.activity.ImagePickActivity;
import com.vincent.filepicker.filter.entity.ImageFile;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.vincent.filepicker.activity.ImagePickActivity.IS_NEED_CAMERA;

public class VayeAppNewPostActivity extends AppCompatActivity {
    private static final String TAG = "StudentNewPostActivity";
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

    String storagePermission[];
    //Stackview
    ImageButton addImage, map_pin , price;
    String contentType = "";
    String mimeType = "";
    long postDate = Calendar.getInstance().getTimeInMillis();
    ArrayList<NewPostDataModel> dataModel = new ArrayList<>();
    String notificationType = "";
    NewPostAdapter adapter ;
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
                    else if (postType.equals(BottomSheetActionTarget.yemek)){
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
                            if (list!=null && !list.isEmpty()){
                                MainPostNS.shared().setNewPost(postName, "post", "", "", currentUser, null, postDate, list, msgText, dataModel, new TrueFalse<Boolean>() {
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


    private void setStackView(String postType){

        addImage = (ImageButton)findViewById(R.id.gallery);
        price = (ImageButton)findViewById(R.id.price);
        map_pin = (ImageButton)findViewById(R.id.map_pin);


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
                    }
                }
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Helper.shared().back(VayeAppNewPostActivity.this);
    }
}