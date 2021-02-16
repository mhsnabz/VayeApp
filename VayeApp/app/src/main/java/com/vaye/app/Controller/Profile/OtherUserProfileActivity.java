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
import com.google.firebase.firestore.auth.User;
import com.kongzue.dialog.v3.TipDialog;
import com.kongzue.dialog.v3.WaitDialog;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.vaye.app.Controller.Profile.ProfileFragments.MajorPostFragment;
import com.vaye.app.Controller.Profile.ProfileFragments.SchoolFragment;
import com.vaye.app.Controller.Profile.ProfileFragments.VayeAppFragment;
import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.OtherUser;
import com.vaye.app.R;
import com.vaye.app.Services.FollowService;
import com.vaye.app.Services.UserService;
import com.vaye.app.Util.Helper;

import de.hdodenhof.circleimageview.CircleImageView;

public class OtherUserProfileActivity extends AppCompatActivity {
    Toolbar toolbar;
    TextView toobarTitle;
    CurrentUser currentUser ;
    OtherUser otherUser;
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
        setContentView(R.layout.activity_other_user_profile);
        Bundle extras = getIntent().getExtras();
        Intent intentIncoming = getIntent();
        currentUser = intentIncoming.getParcelableExtra("currentUser");
        otherUser = intentIncoming.getParcelableExtra("otherUser");
        
        setToolbar(otherUser);
        setView(currentUser,otherUser);
    }

    private void setView(CurrentUser currentUser , OtherUser otherUser) {

        profileImage = (CircleImageView)findViewById(R.id.profileImage);
        progressBar = (ProgressBar)findViewById(R.id.progress);
        name = (TextView)findViewById(R.id.name);
        school = (TextView)findViewById(R.id.school);
        major = (TextView)findViewById(R.id.bolum);
        followButton = (Button) findViewById(R.id.followButton);
        socialLayout = (LinearLayout)findViewById(R.id.socialLayot);

        github = (ImageButton)findViewById(R.id.github);
        insta = (ImageButton)findViewById(R.id.insta);
        twitter = (ImageButton)findViewById(R.id.twitter);
        linkedin = (ImageButton)findViewById(R.id.linkedin);

        if (otherUser.getThumb_image()!=null && !otherUser.getThumb_image().isEmpty()){
            Picasso.get().load(otherUser.getThumb_image())
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

        name.setText(otherUser.getName());
        school.setText(otherUser.getSchoolName());
        major.setText(otherUser.getBolum());

        if (otherUser.getGithub() != null && !otherUser.getGithub().isEmpty() &&
                otherUser.getLinkedin() != null && !otherUser.getLinkedin().isEmpty() &&
                otherUser.getInstagram() != null && !otherUser.getInstagram().isEmpty() &&
                otherUser.getTwitter() != null && !otherUser.getTwitter().isEmpty() ){
            socialLayout.setVisibility(View.VISIBLE);
            if (otherUser.getGithub() != null && !otherUser.getGithub().isEmpty()){
                github.setVisibility(View.VISIBLE);
            }else {
                github.setVisibility(View.GONE);
            }
            if (otherUser.getLinkedin() != null && !otherUser.getLinkedin().isEmpty()){
                linkedin.setVisibility(View.VISIBLE);
            }else {
                linkedin.setVisibility(View.GONE);
            }
            if (otherUser.getTwitter() != null && !otherUser.getTwitter().isEmpty()){
                twitter.setVisibility(View.VISIBLE);
            }else {
                twitter.setVisibility(View.GONE);
            }
            if (otherUser.getInstagram() != null && !otherUser.getInstagram().isEmpty()){
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

        if (currentUser.getShort_school().equals(otherUser.getShort_school())){
            if (currentUser.getBolum().equals(otherUser.getBolum())){
               adapter = new ProfileViewPager(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
                String initials = "";
                for (String s : otherUser.getBolum().split(" ")) {
                    initials+=s.charAt(0);
                }
                adapter.addFrag(new MajorPostFragment(
                        ContextCompat.getColor(this, R.color.mainColor),otherUser), initials);
                adapter.addFrag(new SchoolFragment(
                        ContextCompat.getColor(this, R.color.red),otherUser), otherUser.getShort_school());
                adapter.addFrag(new VayeAppFragment(
                        ContextCompat.getColor(this, R.color.black),otherUser), "Vaye.app");
                viewPager.setAdapter(adapter);
            }else {
                adapter = new ProfileViewPager(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
                adapter.addFrag(new SchoolFragment(
                        ContextCompat.getColor(this, R.color.red),otherUser), otherUser.getShort_school());
                adapter.addFrag(new VayeAppFragment(
                        ContextCompat.getColor(this, R.color.black),otherUser), "Vaye.app");
                viewPager.setAdapter(adapter);
            }
        }else{
            adapter = new ProfileViewPager(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            adapter.addFrag(new VayeAppFragment(
                    ContextCompat.getColor(this, R.color.black),otherUser), "Vaye.app");
            viewPager.setAdapter(adapter);
        }


        UserService.shared().checkIsFallowing(currentUser, otherUser, new TrueFalse<Boolean>() {
            @Override
            public void callBack(Boolean _value) {
                if (_value){
                    isFollowing = false;
                    followButton.setBackground(getApplicationContext().getDrawable(R.drawable.button_fallow_back));
                    followButton.setText("Takip Et");
                }else{
                    followButton.setBackground(getApplicationContext().getDrawable(R.drawable.button_unfollow_back));
                    followButton.setText("Takip Etmeyi Bırak");
                    isFollowing = true;
                }
            }
        });


        followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFollowing){
                    WaitDialog.show(OtherUserProfileActivity.this , "Lütfen Bekleyin");
                    UserService.shared().setUnFollow(currentUser.getUid(), otherUser.getUid(), new TrueFalse<Boolean>() {
                        @Override
                        public void callBack(Boolean _value) {

                            FollowService.shared().unFollowUser(otherUser, currentUser, new TrueFalse<Boolean>() {
                                @Override
                                public void callBack(Boolean _value) {
                                    if (_value){
                                        WaitDialog.dismiss();
                                        TipDialog.show(OtherUserProfileActivity.this, "Takip Etmeyi Bıraktınız", TipDialog.TYPE.SUCCESS);
                                        TipDialog.dismiss(1000);

                                        isFollowing = false;
                                        followButton.setBackground(getApplicationContext().getDrawable(R.drawable.button_fallow_back));
                                        followButton.setText("Takip Et");
                                    }else{
                                        WaitDialog.dismiss();
                                        TipDialog.show(OtherUserProfileActivity.this , "Sorun Oluştu Lütfen Tekrar Deneyin", TipDialog.TYPE.WARNING);
                                        TipDialog.dismiss(1000);

                                    }
                                }
                            });


                        }
                    });
                }else{
                    WaitDialog.show(OtherUserProfileActivity.this , "Lütfen Bekleyin");
                    FollowService.shared().followUser(otherUser, currentUser, new TrueFalse<Boolean>() {
                        @Override
                        public void callBack(Boolean _value) {
                            if (_value){
                                WaitDialog.dismiss();
                                TipDialog.show(OtherUserProfileActivity.this, "Takip Ediliyor", TipDialog.TYPE.SUCCESS);
                                TipDialog.dismiss(1000);
                                followButton.setBackground(getApplicationContext().getDrawable(R.drawable.button_unfollow_back));
                                followButton.setText("Takip Etmeyi Bırak");
                                isFollowing = true;
                            }else{
                                WaitDialog.dismiss();
                                TipDialog.show(OtherUserProfileActivity.this , "Sorun Oluştu Lütfen Tekrar Deneyin", TipDialog.TYPE.WARNING);
                                TipDialog.dismiss(1000);

                            }
                        }
                    });
                }
            }
        });
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

    public void sendMsg(View view) {
    }
}