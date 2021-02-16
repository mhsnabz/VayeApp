package com.vaye.app.Controller.Profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;
import com.vaye.app.Controller.HomeController.StudentSetNewPost.StudentNewPostActivity;
import com.vaye.app.Controller.Profile.ProfileFragments.MajorPostFragment;
import com.vaye.app.Controller.Profile.ProfileFragments.SchoolFragment;
import com.vaye.app.Controller.Profile.ProfileFragments.VayeAppFragment;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.OtherUser;
import com.vaye.app.R;
import com.vaye.app.Util.Helper;

public class OtherUserProfileActivity extends AppCompatActivity {
    Toolbar toolbar;
    TextView toobarTitle;
    CurrentUser currentUser ;
    OtherUser otherUser;
    ViewPager viewPager;
    TabLayout tabLayout;
    CollapsingToolbarLayout collapsingToolbarLayout;
    ProfileViewPager adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_user_profile);
        Bundle extras = getIntent().getExtras();
        Intent intentIncoming = getIntent();
        currentUser = intentIncoming.getParcelableExtra("currentUser");
        otherUser = intentIncoming.getParcelableExtra("otherUser");
        
        setToolbar(otherUser);
        setView(currentUser,otherUser);
    }

    private void setView(CurrentUser currentUser , OtherUser otherUser) {
        viewPager = findViewById(R.id.htab_viewpager);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.htab_collapse_toolbar);

        tabLayout = findViewById(R.id.htab_tabs);
        tabLayout.setupWithViewPager(viewPager);

        if (currentUser.getShort_school().equals(otherUser.getShort_school())){
            if (currentUser.getBolum().equals(otherUser.getBolum())){
               adapter = new ProfileViewPager(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
                adapter.addFrag(new MajorPostFragment(
                        ContextCompat.getColor(this, R.color.mainColor)), "BM");
                adapter.addFrag(new SchoolFragment(
                        ContextCompat.getColor(this, R.color.red)), "İSTE");
                adapter.addFrag(new VayeAppFragment(
                        ContextCompat.getColor(this, R.color.black)), "Vaye.app");
                viewPager.setAdapter(adapter);
            }else {
                //iste , vaye.app
            }
        }else{
            //vayeApp
        }

    }

    private void setToolbar(OtherUser otherUser) {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toobarTitle = toolbar.findViewById(R.id.toolbar_title);
     
        toolbar.setTitle("");
        toolbar.setSubtitle("");
        toobarTitle.setText(otherUser.getUsername());

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Helper.shared().back(OtherUserProfileActivity.this);
            }
        });
    }
}