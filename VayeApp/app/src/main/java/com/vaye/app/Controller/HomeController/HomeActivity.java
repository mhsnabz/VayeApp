package com.vaye.app.Controller.HomeController;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.R;
import com.vaye.app.Util.BottomNavHelper;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity {
    String TAG = "HomeActivity";
    private DrawerLayout drawer;
    Toolbar toolbar;
    Button showProflie;
    CircleImageView profileIamge;
    CurrentUser currentUser;
    TextView name , username;
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
        setupBottomNavBar();
         toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();




    }


    private void setUserProile(CurrentUser currentUser){
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
                Toast.makeText(HomeActivity.this,"Show Proilfe",Toast.LENGTH_LONG).show();
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
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}