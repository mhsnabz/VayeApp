package com.vaye.app.Util;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.vaye.app.Controller.ChatController.ChatActivity;
import com.vaye.app.Controller.HomeController.HomeActivity;
import com.vaye.app.Controller.NotificationController.NotificationActivity;
import com.vaye.app.Controller.VayeAppController.VayeAppActivity;
import com.vaye.app.R;

public class BottomNavHelper
{

    public static void enableNavigation(final Context context , BottomNavigationView view){
        view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home:
                        Intent i = new Intent(context , HomeActivity.class);
                        context.startActivity(i);
                        break;
                    case R.id.vaye:
                        Intent i1 = new Intent(context , VayeAppActivity.class);
                        context.startActivity(i1);
                        break;
                    case R.id.notificaiton:
                        Intent i2 = new Intent(context , NotificationActivity.class);
                        context.startActivity(i2);
                        break;
                    case R.id.chat:
                        Intent i3 = new Intent(context , ChatActivity.class);
                        context.startActivity(i3);
                        break;
                }

                return false;
            }
        });
    }
}
