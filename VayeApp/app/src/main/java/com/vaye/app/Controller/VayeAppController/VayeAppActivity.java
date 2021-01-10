package com.vaye.app.Controller.VayeAppController;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.vaye.app.Controller.HomeController.Bolum.BolumFragment;
import com.vaye.app.Controller.HomeController.HomeActivity;
import com.vaye.app.Controller.HomeController.School.SchoolFragment;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.R;
import com.vaye.app.Util.BottomNavHelper;

public class VayeAppActivity extends AppCompatActivity {
    CurrentUser currentUser;
    String TAG = "VayeAppActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vaye_app);
        Bundle extras = getIntent().getExtras();
        Intent intentIncoming = getIntent();
        if (extras != null){
            currentUser = intentIncoming.getParcelableExtra("currentUser");

            Bundle bundle = new Bundle();
            bundle.putParcelable("currentUser",intentIncoming.getParcelableExtra("currentUser"));
            setupBottomNavBar(currentUser);
            Log.d(TAG, "onCreate: "+currentUser.getUsername());

        }


    }
    private void setupBottomNavBar(CurrentUser currentUser){
        BottomNavigationView navBar = (BottomNavigationView)findViewById(R.id.bottom_nav_bar);
        BottomNavHelper.enableNavigation(this,navBar,currentUser);
        Menu menu = navBar.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);
        overridePendingTransition(0, 0);


    }
}