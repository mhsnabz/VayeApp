package com.vaye.app.Controller.HomeController;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;
import com.vaye.app.Controller.HomeController.Bolum.BolumFragment;
import com.vaye.app.Controller.HomeController.PagerAdapter.AllDatasActivity;
import com.vaye.app.Controller.HomeController.PagerAdapter.PagerViewApadater;
import com.vaye.app.Controller.HomeController.School.SchoolFragment;
import com.vaye.app.Controller.HomeController.SetLessons.StudentSetLessonActivity;
import com.vaye.app.Controller.Profile.CurrentUserProfile;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.R;
import com.vaye.app.Util.BottomNavHelper;
import com.vaye.app.Util.Helper;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity {
    String TAG = "HomeActivity";
    private DrawerLayout drawer;
    Toolbar toolbar;
    Button showProflie;
    CircleImageView profileIamge;
    CurrentUser currentUser;
    TextView name , username;
    TextView title;
    TextView bolumLbl , schoolLbl;
    RelativeLayout line1,line2;
    ViewPager viewPager;
    ImageButton addLesson , notificationSetting;
    private int STORAGE_PERMISSION_CODE = 1;
    private int STOREGE_READ_WRİTE_CODE = 2;
    private int STROGE_MANAGE_CODE = 3;

    private PagerViewApadater pagerViewApadater;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
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
        name = (TextView)headerview.findViewById(R.id.name);
        username = (TextView)headerview.findViewById(R.id.username);
        Log.d(TAG, "setUserProile: " + currentUser.getThumb_image());
        name.setText(currentUser.getName());
        username.setText(currentUser.getUsername());

        Picasso.get().load(currentUser.getThumb_image()).resize(256,256).centerCrop().into(profileIamge);

        showProflie = (Button)headerview.findViewById(R.id.showProfile);
        showProflie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(HomeActivity.this , CurrentUserProfile.class);
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
    }


    private void addLesson(){
        if (currentUser.getPriority().equals("teacher")){

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

    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this , Manifest.permission.CAMERA) +
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)+
                ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.CAMERA) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) ){
                AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                builder.setTitle("İzin Vermeniz Gerekmektedir");
                builder.setMessage("Kamera , Galeri ");
                builder.setPositiveButton("TAMAM", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityCompat.requestPermissions(HomeActivity.this , new String[] {
                                        Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE ,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                                },STORAGE_PERMISSION_CODE
                        );
                    }
                });
                builder.setNegativeButton("VAZGEÇ",null);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }else{
                ActivityCompat.requestPermissions(this , new String[] {
                                Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE ,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE

                        },STORAGE_PERMISSION_CODE
                );
            }
        }else{

        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

    }


    @Override
    protected void onStart() {
        super.onStart();
        requestStoragePermission();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}