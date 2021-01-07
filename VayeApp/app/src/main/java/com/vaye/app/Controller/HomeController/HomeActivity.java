package com.vaye.app.Controller.HomeController;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;
import com.vaye.app.Controller.HomeController.PagerAdapter.PagerViewApadater;
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

    TextView bolumLbl , schoolLbl;
    RelativeLayout line1,line2;
    ViewPager viewPager;

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

        setupBottomNavBar();
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
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
    }

    private void setupBottomNavBar(){
        BottomNavigationView navBar = (BottomNavigationView)findViewById(R.id.bottom_nav_bar);
        BottomNavHelper.enableNavigation(this,navBar);
        Menu menu = navBar.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);
        overridePendingTransition(0, 0);
    }


    private void changeTabs(int positon){
        if (positon == 0){
            line1.setVisibility(View.VISIBLE);
            line2.setVisibility(View.GONE);
            bolumLbl.setTextColor(getColor(R.color.black));
            schoolLbl.setTextColor(getColor(R.color.gray));
            bolumLbl.setTextSize(16);
            schoolLbl.setTextSize(12);

        }else if (positon == 1){
            line1.setVisibility(View.GONE);
            line2.setVisibility(View.VISIBLE);
            bolumLbl.setTextColor(getColor(R.color.gray));
            schoolLbl.setTextColor(getColor(R.color.black));
            bolumLbl.setTextSize(12);
            schoolLbl.setTextSize(16);
        }
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