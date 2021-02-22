package com.vaye.app.Controller.Profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.vaye.app.Controller.Profile.ProfileFragments.CurrentUserFragment.FavFragment;
import com.vaye.app.Controller.Profile.ProfileFragments.CurrentUserFragment.MajorPostFragment;
import com.vaye.app.Controller.Profile.ProfileFragments.CurrentUserFragment.SchoolFragment;
import com.vaye.app.Controller.Profile.ProfileFragments.CurrentUserFragment.VayeAppFragment;
import com.vaye.app.Interfaces.CallBackCount;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.OtherUser;
import com.vaye.app.R;
import com.vaye.app.Services.FollowService;
import com.vaye.app.Util.Helper;

import de.hdodenhof.circleimageview.CircleImageView;

public class CurrentUserProfile extends AppCompatActivity {
    Toolbar toolbar;
    String TAG = "CurrentUserProfile";
    TextView toobarTitle;
    CurrentUser currentUser ;

    ViewPager viewPager;
    TabLayout tabLayout;
    CollapsingToolbarLayout collapsingToolbarLayout;
    ProfileViewPager adapter;
    Boolean isFollowing = false;

    CircleImageView profileImage;
    ProgressBar progressBar;
    TextView name , school , major , followingCount,followingLbl , followerCount , followerLbl;
    Button followButton;
    LinearLayout socialLayout;
    ImageButton github , linkedin , insta , twitter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_user_profile);
        Bundle extras = getIntent().getExtras();
        Intent intentIncoming = getIntent();
        if (extras==null){
            finish();
        }else{
            currentUser = intentIncoming.getParcelableExtra("currentUser");
            setToolbar(currentUser);
            setView(currentUser);

        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Helper.shared().back(CurrentUserProfile.this);
    }

    public void editProfile(View view) {
    }

    private void setView(CurrentUser currentUser ) {

        followerCount = (TextView)findViewById(R.id.followerCount);
        followingCount = (TextView)findViewById(R.id.followingCount);
        followerLbl = (TextView)findViewById(R.id.followerLbl);
        followingLbl = (TextView)findViewById(R.id.followingLbl);

        FollowService.shared().getCurrentUserFollowersCount(currentUser, new CallBackCount() {
            @Override
            public void callBackCount(long count) {
                followerCount.setText(String.valueOf(count));
            }
        });
        FollowService.shared().getCurrentUserFollowingCount(currentUser, new CallBackCount() {
            @Override
            public void callBackCount(long count) {
                followingCount.setText(String.valueOf(count));
            }
        });


        profileImage = (CircleImageView)findViewById(R.id.profileImage);
        progressBar = (ProgressBar)findViewById(R.id.progress);
        name = (TextView)findViewById(R.id.name);
        school = (TextView)findViewById(R.id.school);
        major = (TextView)findViewById(R.id.bolum);
        socialLayout = (LinearLayout)findViewById(R.id.socialLayot);

        github = (ImageButton)findViewById(R.id.github);
        insta = (ImageButton)findViewById(R.id.insta);
        twitter = (ImageButton)findViewById(R.id.twitter);
        linkedin = (ImageButton)findViewById(R.id.linkedin);

        if (currentUser.getThumb_image()!=null && !currentUser.getThumb_image().isEmpty()){
            Picasso.get().load(currentUser.getThumb_image())
                    .resize(256,256)
                    .centerCrop()
                    .placeholder(R.color.gray).into(profileImage, new Callback() {
                @Override
                public void onSuccess() {
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onError(Exception e) {
                    progressBar.setVisibility(View.GONE);
                }
            });
        }else {
            progressBar.setVisibility(View.GONE);
        }

        name.setText(currentUser.getName());
        school.setText(currentUser.getSchoolName());
        major.setText(currentUser.getBolum());


        if (currentUser.getGithub() != null && !currentUser.getGithub().isEmpty() &&
                currentUser.getLinkedin() != null && !currentUser.getLinkedin().isEmpty() &&
                currentUser.getInstagram() != null && !currentUser.getInstagram().isEmpty() &&
                currentUser.getTwitter() != null && !currentUser.getTwitter().isEmpty() ){
            socialLayout.setVisibility(View.VISIBLE);
            if (currentUser.getGithub() != null && !currentUser.getGithub().isEmpty()){
                github.setVisibility(View.VISIBLE);
            }else {
                github.setVisibility(View.GONE);
            }
            if (currentUser.getLinkedin() != null && !currentUser.getLinkedin().isEmpty()){
                linkedin.setVisibility(View.VISIBLE);
            }else {
                linkedin.setVisibility(View.GONE);
            }
            if (currentUser.getTwitter() != null && !currentUser.getTwitter().isEmpty()){
                twitter.setVisibility(View.VISIBLE);
            }else {
                twitter.setVisibility(View.GONE);
            }
            if (currentUser.getInstagram() != null && !currentUser.getInstagram().isEmpty()){
                insta.setVisibility(View.VISIBLE);
            }else {
                insta.setVisibility(View.GONE);
            }
        }else{
            socialLayout.setVisibility(View.GONE);
        }

        viewPager = findViewById(R.id.htab_viewpager);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.htab_collapse_toolbar);

        tabLayout = findViewById(R.id.htab_tabs);
        tabLayout.setupWithViewPager(viewPager);

        adapter = new ProfileViewPager(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        String initials = "";
        for (String s : currentUser.getBolum().split(" ")) {
            initials+=s.charAt(0);
        }
        adapter.addFrag(new MajorPostFragment(
                ContextCompat.getColor(this, R.color.mainColor),currentUser), initials);
        adapter.addFrag(new SchoolFragment(
                ContextCompat.getColor(this, R.color.red),currentUser), currentUser.getShort_school());
        adapter.addFrag(new VayeAppFragment(
                ContextCompat.getColor(this, R.color.black),currentUser), "Vaye.app");
        adapter.addFrag(new FavFragment(currentUser), "Favoriler");
        viewPager.setAdapter(adapter);
    }
    private void setToolbar(CurrentUser currentUser) {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toobarTitle = toolbar.findViewById(R.id.toolbar_title);

        toolbar.setTitle("");
        toolbar.setSubtitle("");
        toobarTitle.setText(currentUser.getUsername());

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Helper.shared().back(CurrentUserProfile.this);
            }
        });
    }
}