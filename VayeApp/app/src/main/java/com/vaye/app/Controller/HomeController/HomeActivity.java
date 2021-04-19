package com.vaye.app.Controller.HomeController;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.DexterActivity;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.kongzue.dialog.v3.WaitDialog;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.vaye.app.Controller.ChatController.Conservation.ConservationController;
import com.vaye.app.Controller.HomeController.Bolum.BolumFragment;
import com.vaye.app.Controller.HomeController.PagerAdapter.AllDatasActivity;
import com.vaye.app.Controller.HomeController.PagerAdapter.PagerViewApadater;
import com.vaye.app.Controller.HomeController.School.SchoolFragment;
import com.vaye.app.Controller.HomeController.School.SchoolPostNotificationActivity;
import com.vaye.app.Controller.HomeController.SetLessons.StudentSetLessonActivity;
import com.vaye.app.Controller.HomeController.SetLessons.TeacherSetLessonActivity;
import com.vaye.app.Controller.HomeController.SettingController.SettingActivity;
import com.vaye.app.Controller.HomeController.StudentSetNewPost.StudentNewPostActivity;
import com.vaye.app.Controller.MapsController.LocationPermissionActivity;
import com.vaye.app.Controller.MapsController.VayeAppPlacePickerActivity;
import com.vaye.app.Controller.NotificationController.NotificationSetting.NotificationSettingActivity;
import com.vaye.app.Controller.Profile.CurrentUserProfile;
import com.vaye.app.Controller.Profile.EditProfileActivity;
import com.vaye.app.Interfaces.CompletionWithValue;
import com.vaye.app.Interfaces.CurrentUserService;
import com.vaye.app.Interfaces.DataTypes;
import com.vaye.app.Interfaces.OnOptionSelect;
import com.vaye.app.Interfaces.StringCompletion;
import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.R;
import com.vaye.app.Services.SchoolPostService;
import com.vaye.app.Services.UserService;
import com.vaye.app.SplashScreen.SplashScreen;
import com.vaye.app.Util.BottomNavHelper;
import com.vaye.app.Util.BottomSheetHelper.ProfileImageSettingAdapter;
import com.vaye.app.Util.Helper;
import com.vaye.app.Util.RunTimePermissionHelper;
import com.vincent.filepicker.Constant;
import com.vincent.filepicker.activity.ImagePickActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;
import q.rorbin.badgeview.QBadgeView;

import static com.vincent.filepicker.activity.ImagePickActivity.IS_NEED_CAMERA;

public class HomeActivity extends AppCompatActivity implements CompletionWithValue , OnOptionSelect {
    String TAG = "HomeActivity";
    private DrawerLayout drawer;
    Toolbar toolbar;
    Button showProflie ,notButton, settingButton,exit ,homeButton2;
    ImageButton edit;
    CircleImageView profileIamge;
    CurrentUser currentUser;
    TextView name , username;
    TextView title;
    TextView bolumLbl , schoolLbl;
    RelativeLayout line1,line2;
    ViewPager viewPager;
    Uri image;
    KProgressHUD hud;
    StorageTask<UploadTask.TaskSnapshot> uploadTask;
    private StorageReference imageStorage;
    ImageButton addLesson , notificationSetting , profileImageSetting;
    private int STORAGE_PERMISSION_CODE = 1;
    private int STOREGE_READ_WRİTE_CODE = 2;
    private int STROGE_MANAGE_CODE = 3;

    private static final int image_pick_request =600;
    private static final int camera_pick_request =800;
    private static final int CAMERA_REQUEST = 1888;
    OnOptionSelect optionSelect;

    private PagerViewApadater pagerViewApadater;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        optionSelect = this::onChoose;
        setAllPermissinon();
        Bundle extras = getIntent().getExtras();
        Intent intentIncoming = getIntent();
        if (extras != null){
            currentUser = intentIncoming.getParcelableExtra("currentUser");
            setUserProile(currentUser);
            Bundle bundle = new Bundle();
            bundle.putParcelable("currentUser",intentIncoming.getParcelableExtra("currentUser"));

            BolumFragment fragobj = new BolumFragment();
            fragobj.setArguments(bundle);
            SchoolFragment schoolFragment = new SchoolFragment();
            schoolFragment.setArguments(bundle);

        }

        hud = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.ANNULAR_DETERMINATE)
                .setLabel("Dosya Yükleniyor")
                .setMaxProgress(100);
        bolumLbl = (TextView)findViewById(R.id.text1);
        schoolLbl = (TextView)findViewById(R.id.text2);
        line1  = (RelativeLayout)findViewById(R.id.line1);
        line2  = (RelativeLayout)findViewById(R.id.line2);
        viewPager = (ViewPager)findViewById(R.id.mainPager);
        pagerViewApadater = new PagerViewApadater(getSupportFragmentManager());
        viewPager.setAdapter(pagerViewApadater);

        bolumLbl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(0);
            }
        });

        schoolLbl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(1);
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                changeTabs(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }




    //TODO:--functions
    private void setUserProile(CurrentUser currentUser){

        setupBottomNavBar(currentUser);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        title = toolbar.findViewById(R.id.toolbar_title);
        addLesson = (ImageButton)toolbar.findViewById(R.id.addLesson);
        notificationSetting = (ImageButton)toolbar.findViewById(R.id.notificationSetting);
        notificationSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(HomeActivity.this , SchoolPostNotificationActivity.class);
                i.putExtra("currentUser",currentUser);
                startActivity(i);
                Helper.shared().go(HomeActivity.this);
            }
        });
        setSupportActionBar(toolbar);

        toolbar.setTitle("");
        toolbar.setSubtitle("");
        title.setText("Vaye App");
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerview = navigationView.getHeaderView(0);
        profileIamge = (CircleImageView)headerview.findViewById(R.id.profileImage);
        profileImageSetting = ( ImageButton)headerview.findViewById(R.id.profileImageSetting);
        homeButton2 = (Button)headerview.findViewById(R.id.homeButton2);
        homeButton2.setText(currentUser.getBolum());;
        homeButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                }
            }
        });
        edit = (ImageButton)headerview.findViewById(R.id.edit);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WaitDialog.show(HomeActivity.this,"");
                UserService.shared().getCurrentUser(currentUser.getUid(), new CurrentUserService() {
                    @Override
                    public void onCallback(CurrentUser user) {
                        Intent i = new Intent(HomeActivity.this , EditProfileActivity.class);
                        i.putExtra("currentUser",user);
                        startActivity(i);
                        Helper.shared().go(HomeActivity.this);
                        WaitDialog.dismiss();
                    }
                });

            }
        });
        name = (TextView)headerview.findViewById(R.id.name);
        username = (TextView)headerview.findViewById(R.id.username);
        Log.d(TAG, "setUserProile: " + currentUser.getThumb_image());
        name.setText(currentUser.getName());
        username.setText(currentUser.getUsername());
        if (!currentUser.getThumb_image().isEmpty() && currentUser.getThumb_image()!=null){
            Picasso.get().load(currentUser.getThumb_image()).resize(256,256).centerCrop().into(profileIamge);

        }else{
            profileIamge.setImageResource(android.R.color.darker_gray);
        }

        showProflie = (Button)headerview.findViewById(R.id.showProfile);
        showProflie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(HomeActivity.this , CurrentUserProfile.class);
                i.putExtra("currentUser",currentUser);
                startActivity(i);
                Helper.shared().go(HomeActivity.this);
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                }
            }
        });
        exit = (Button)headerview.findViewById(R.id.exit2);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WaitDialog.show(HomeActivity.this,null);
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(HomeActivity.this , SplashScreen.class);
                startActivity(i);
                WaitDialog.dismiss();
                finish();

            }
        });
        notButton = (Button)headerview.findViewById(R.id.notButton2);
        settingButton = (Button)headerview.findViewById(R.id.settin2);
        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i =  new Intent(HomeActivity.this , SettingActivity.class);
                i.putExtra("currentUser",currentUser);
                startActivity(i);
                Helper.shared().go(HomeActivity.this);
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                }

            }
        });
        notButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(HomeActivity.this, NotificationSettingActivity.class);
                i.putExtra("currentUser",currentUser);
                startActivity(i);
                Helper.shared().go(HomeActivity.this);
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                }

            }
        });
        showProflie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(HomeActivity.this, CurrentUserProfile.class);
                i.putExtra("currentUser",currentUser);
                startActivity(i);
                Helper.shared().go(HomeActivity.this);
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                }
            }
        });

        addLesson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addLesson();
            }
        });
        profileImageSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Helper.shared().ProfileImageSetting(HomeActivity.this,optionSelect, currentUser, new TrueFalse<Boolean>() {
                    @Override
                    public void callBack(Boolean _value) {
                        if (_value){
                            if (!currentUser.getThumb_image().isEmpty() && currentUser.getThumb_image()!=null){
                                Picasso.get().load(currentUser.getThumb_image()).resize(256,256).centerCrop().into(profileIamge);

                            }else{
                                profileIamge.setImageResource(android.R.color.darker_gray);

                            }
                        }
                    }
                });
            }


        });
    }

    @Override
    public void onChoose(String value) {
        Log.d(TAG, "onChoose: " + value);
        if (value.equals(CompletionWithValue.chooseImage)){
            pickGallery();
        }else if (value.equals(CompletionWithValue.takePicture)){
            takeImage();
        }else if (value.equals(CompletionWithValue.showImage)){

        }
    }
    private void setupBottomNavBar(CurrentUser currentUser){
        BottomNavigationView navBar = (BottomNavigationView)findViewById(R.id.bottom_nav_bar);
        navBar.setElevation(5);
        BottomNavHelper.enableNavigation(this,navBar,currentUser);
        navBar.setElevation(4);
        Menu menu = navBar.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);
        overridePendingTransition(0, 0);
        getBadgeCount();
    }
    private void getBadgeCount(){
        BottomNavigationView view;
        view=(BottomNavigationView)findViewById(R.id.bottom_nav_bar);
        BottomNavigationMenuView bottomNavigationMenuView =
                (BottomNavigationMenuView) view.getChildAt(0);
        final QBadgeView badge = new QBadgeView(this);
        final View v = bottomNavigationMenuView.getChildAt(2);

        Query ref =  FirebaseFirestore.getInstance().collection("user")
                .document(currentUser.getUid())
                .collection("notification").whereEqualTo("isRead","false");

        ref.addSnapshotListener(this,new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot.isEmpty()){
                    badge.hide(true);
                }else{
                    badge.bindTarget(v).setBadgeTextSize(14,true).setBadgePadding(7,true)
                            .setBadgeBackgroundColor(Color.RED).setBadgeNumber(documentSnapshot.getDocuments().size());;
                    if (documentSnapshot.getDocuments().size() < 1){
                        badge.hide(true);
                    }
                }
            }
        });

    }

    private void addLesson(){
        if (currentUser.getPriority().equals("teacher")){
            Intent i = new Intent(HomeActivity.this , TeacherSetLessonActivity.class);
            i.putExtra("currentUser",currentUser);
            startActivity(i);
            Helper.shared().go(HomeActivity.this);
        }else if (currentUser.getPriority().equals("student")){
            Intent i = new Intent(HomeActivity.this , StudentSetLessonActivity.class);
            i.putExtra("currentUser",currentUser);
            startActivity(i);
            Helper.shared().go(HomeActivity.this);
        }
    }

    private void changeTabs(int positon){
        if (positon == 0){
            addLesson.setVisibility(View.VISIBLE);
            notificationSetting.setVisibility(View.GONE);
            title.setText(currentUser.getBolum());
            line1.setVisibility(View.VISIBLE);
            line2.setVisibility(View.GONE);
            bolumLbl.setTextColor(getColor(R.color.black));
            schoolLbl.setTextColor(getColor(R.color.gray));
            bolumLbl.setTextSize(16);
            schoolLbl.setTextSize(12);

        }else if (positon == 1){
            addLesson.setVisibility(View.GONE);
            notificationSetting.setVisibility(View.VISIBLE);
            title.setText(currentUser.getShort_school() + " Kulüpleri");
            line1.setVisibility(View.GONE);
            line2.setVisibility(View.VISIBLE);
            bolumLbl.setTextColor(getColor(R.color.gray));
            schoolLbl.setTextColor(getColor(R.color.black));
            bolumLbl.setTextSize(12);
            schoolLbl.setTextSize(16);
        }
    }


    private void pickGallery()
    {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(intent.CATEGORY_OPENABLE);
        startActivityForResult(intent,image_pick_request);
    }

    private void takeImage(){
        if (!RunTimePermissionHelper.shared().checkGalleryPermission(this)){
            RunTimePermissionHelper.shared().requestGalleryCameraPermission(this, new TrueFalse<Boolean>() {
                @Override
                public void callBack(Boolean _value) {
                    if (_value){
                        CropImage.activity()
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setCropShape(CropImageView.CropShape.OVAL)
                                .setActivityTitle("Konumlandır")
                                .setCropMenuCropButtonTitle("Seç")
                                .setAllowFlipping(false)
                                .setAllowRotation(false)
                                .setAllowCounterRotation(false)
                                .setAspectRatio(1, 1)
                                .setMinCropWindowSize(500, 500)
                                .start(HomeActivity.this);
                    }

                }
            });
        }else{

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setCropShape(CropImageView.CropShape.OVAL)
                    .setActivityTitle("Konumlandır")
                    .setCropMenuCropButtonTitle("Seç")
                    .setAllowFlipping(false)
                    .setAllowRotation(false)
                    .setAllowCounterRotation(false)
                    .setAspectRatio(1, 1)
                    .setMinCropWindowSize(500, 500)
                    .start(this);

        }

    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStart() {
        super.onStart();

        RunTimePermissionHelper.shared().locationPermission(HomeActivity.this, new TrueFalse<Boolean>() {
            @Override
            public void callBack(Boolean _value) {
                if (!_value){

                }
            }
        });

        FirebaseFirestore.getInstance().collection("user").orderBy("username").whereGreaterThanOrEqualTo("username","@a").whereLessThanOrEqualTo("username","@a"+'\uf8ff').get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().getDocuments().isEmpty()){
                        Log.d(TAG, "onComplete: null" );
                    }else{
                        for (DocumentSnapshot item : task.getResult().getDocuments()){
                            Log.d(TAG, "onComplete: " + item.getString("name"));
                        }
                    }
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void completion(Boolean bool, String val) {
        Log.d(TAG, "completion: " + val);
        Log.d(TAG, "completion: " + bool);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == image_pick_request) {
                CropImage.activity(data.getData())
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .setActivityTitle("Konumlandır")
                        .setCropMenuCropButtonTitle("Seç")
                        .setAllowFlipping(false)
                        .setAllowRotation(false)
                        .setAllowCounterRotation(false)
                        .setAspectRatio(1, 1)
                        .setMinCropWindowSize(500, 500)
                        .start(this);
            }

            if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
                CropImage.ActivityResult result =CropImage.getActivityResult(data);
                if(resultCode==RESULT_OK){
                    WaitDialog.show(HomeActivity.this,"Resim Yükleniyor");
                    Uri resultUri = result.getUri();
                    String mimeType = DataTypes.mimeType.image;
                    String  contentType = DataTypes.contentType.image;
                    saveToDatabase(contentType, mimeType, resultUri, new StringCompletion() {
                        @Override
                        public void getString(String url) {
                            try {
                                setThumbImage(contentType, mimeType, currentUser, resultUri, new StringCompletion() {
                                    @Override
                                    public void getString(String thumb_url) {
                                        currentUser.setProfileImage(url);
                                        currentUser.setThumb_image(thumb_url);
                                        DocumentReference reference = FirebaseFirestore.getInstance().collection("user")
                                                .document(currentUser.getUid());
                                        Map<String , Object> map = new HashMap<>();
                                        map.put("profileImage",url);
                                        map.put("thumb_image",thumb_url);
                                        reference.set(map , SetOptions.merge());
                                        UserService.shared().updateAllPost(currentUser);
                                        WaitDialog.dismiss();
                                        Picasso.get().load(thumb_url).resize(256,256).centerCrop().placeholder(android.R.color.darker_gray).into(profileIamge, new Callback() {
                                            @Override
                                            public void onSuccess() {

                                            }

                                            @Override
                                            public void onError(Exception e) {

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


    private void saveToDatabase(String contentType, String mimeType,  Uri data,
                                StringCompletion completion){
        WaitDialog.dismiss();
        hud.show();
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType(contentType)
                .build();
        final StorageReference ref = FirebaseStorage.getInstance().getReference().child("profileImage").child("profileImage"+currentUser.getUid()+mimeType);
        uploadTask =  ref.putFile(data , metadata).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()){

                    task.getResult().getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String  url = uri.toString();
                            Log.d(TAG, "onComplete: "+ url);
                            hud.dismiss();
                            WaitDialog.show(HomeActivity.this , "Dosya Yükleniyor");
                            completion.getString(url);


                        }
                    });

                }
            }
        });
        uploadTask.addOnProgressListener(this, new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {

                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                hud.setProgress((int) progress);
                Log.d(TAG, "onProgress: " + progress);
            }
        });
    }

    private void setThumbImage(String contentType,String mimeType, CurrentUser currentUser  , Uri data,
                               StringCompletion completion) throws IOException {
        WaitDialog.show(HomeActivity.this ,"Lütfen Bekleyin...");
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

        StorageReference ref = FirebaseStorage.getInstance().getReference().child("thumb_image").child("thumb_image"+currentUser.getUid()+mimeType);
        ref.putBytes(thumb_byte, metadata).addOnCompleteListener(this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()){
                    task.getResult().getMetadata().getReference().getDownloadUrl().addOnSuccessListener(HomeActivity.this, new OnSuccessListener<Uri>() {
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


    private void setAllPermissinon(){
       // galleryPermission();
       // locationPermission();
       // galleryPermission();
    }

    private void galleryPermission(){
        Dexter.withContext(HomeActivity.this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE , Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        if (multiplePermissionsReport.areAllPermissionsGranted()){

                        }else {

                           for (PermissionDeniedResponse item : multiplePermissionsReport.getDeniedPermissionResponses()){
                               Log.d(TAG, "onPermissionsChecked: "+ item.getPermissionName());

                           }
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }

    private void locationPermission(){
        Dexter.withActivity(HomeActivity.this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        if (permissionDeniedResponse.isPermanentlyDenied()){
                            AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                            builder.setTitle("İzin Vermediniz")
                                    .setMessage("Konumunuza Erişebilmemiz için konum servislerine izin vermeniz gerekiyor")
                                    .setNegativeButton("Vazgeç",null)
                                    .setPositiveButton("İzin Ver", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Intent intent = new Intent();
                                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            intent.setData(Uri.fromParts("package",getPackageName(),null));

                                        }
                                    }).show();
                        }else{
                            Toast.makeText(HomeActivity.this,"Izın Verildi",Toast.LENGTH_SHORT).show();;
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
        Dexter.withActivity(HomeActivity.this)
                .withPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        Intent i = new Intent(HomeActivity.this,VayeAppPlacePickerActivity.class);
                        startActivity(i);
                        finish();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        if (permissionDeniedResponse.isPermanentlyDenied()){
                            AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                            builder.setTitle("İzin Vermediniz")
                                    .setMessage("Konumunuza Erişebilmemiz için konum servislerine izin vermeniz gerekiyor")
                                    .setNegativeButton("Vazgeç",null)
                                    .setPositiveButton("TAMAM", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Intent intent = new Intent();
                                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            intent.setData(Uri.fromParts("package",getPackageName(),null));

                                        }
                                    }).show();
                        }else{
                            Toast.makeText(HomeActivity.this,"Izın Verildi",Toast.LENGTH_SHORT).show();;
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }



}