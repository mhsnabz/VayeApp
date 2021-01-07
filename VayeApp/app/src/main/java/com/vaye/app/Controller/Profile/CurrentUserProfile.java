package com.vaye.app.Controller.Profile;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.vaye.app.Model.CurrentUser;
import com.vaye.app.R;
import com.vaye.app.Util.Helper;

public class CurrentUserProfile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_user_profile);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Helper.shared().back(CurrentUserProfile.this);
    }
}