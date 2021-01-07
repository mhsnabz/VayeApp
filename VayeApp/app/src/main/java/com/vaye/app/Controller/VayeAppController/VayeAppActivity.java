package com.vaye.app.Controller.VayeAppController;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.vaye.app.Controller.HomeController.HomeActivity;
import com.vaye.app.R;
import com.vaye.app.Util.BottomNavHelper;

public class VayeAppActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vaye_app);
        setupBottomNavBar();

    }
    private void setupBottomNavBar(){
        BottomNavigationView navBar = (BottomNavigationView)findViewById(R.id.bottom_nav_bar);
        BottomNavHelper.enableNavigation(this,navBar);
        Menu menu = navBar.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);
        overridePendingTransition(0, 0);


    }
}