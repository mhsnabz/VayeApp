package com.vaye.app.Controller.ChatController;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.R;
import com.vaye.app.Util.BottomNavHelper;

public class ChatActivity extends AppCompatActivity {
    CurrentUser currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Bundle extras = getIntent().getExtras();
        Intent intentIncoming = getIntent();
        if (extras != null){
            currentUser = intentIncoming.getParcelableExtra("currentUser");

            Bundle bundle = new Bundle();
            bundle.putParcelable("currentUser",intentIncoming.getParcelableExtra("currentUser"));
            setupBottomNavBar(currentUser);

        }

    }
    private void setupBottomNavBar(CurrentUser currentUser){
        BottomNavigationView navBar = (BottomNavigationView)findViewById(R.id.bottom_nav_bar);
        BottomNavHelper.enableNavigation(this,navBar,currentUser);
        Menu menu = navBar.getMenu();
        MenuItem menuItem = menu.getItem(3);
        menuItem.setChecked(true);
        overridePendingTransition(0, 0);
    }
}