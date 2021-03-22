package com.vaye.app.Controller.Profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;
import com.kongzue.dialog.v3.TipDialog;
import com.kongzue.dialog.v3.WaitDialog;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.vaye.app.Controller.Profile.ProfileFragments.CurrentUserFragment.MajorPostFragment;
import com.vaye.app.Controller.Profile.ProfileFragments.CurrentUserFragment.SchoolFragment;
import com.vaye.app.Controller.Profile.ProfileFragments.CurrentUserFragment.VayeAppFragment;
import com.vaye.app.Controller.Profile.ProfileFragments.OtherUserFragment.OtherUserMajorFragment;
import com.vaye.app.Controller.Profile.ProfileFragments.OtherUserFragment.OtherUserSchoolFragment;
import com.vaye.app.Controller.Profile.ProfileFragments.OtherUserFragment.OtherUserVayeAppFragment;
import com.vaye.app.Interfaces.CallBackCount;
import com.vaye.app.Interfaces.Notifications;
import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.OtherUser;
import com.vaye.app.R;
import com.vaye.app.Services.FollowService;
import com.vaye.app.Services.NotificaitonService;
import com.vaye.app.Services.UserService;
import com.vaye.app.Util.Helper;

import java.util.Timer;
import java.util.TimerTask;

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
    private Handler mHandler = new Handler();


    CircleImageView profileImage;
    ProgressBar progressBar;
    TextView name , school , major , followingCount,followingLbl , followerCount , followerLbl;
    Button followButton;
    LinearLayout socialLayout;
    ImageButton github , linkedin , insta , twitter;

    private InterstitialAd mInterstitialAd , githubAds , instaAds , twitterAds , linkedInAds;
    private Timer myTimer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_user_profile);

        Bundle extras = getIntent().getExtras();
        Intent intentIncoming = getIntent();
        if (extras==null){
            finish();
        }else{
            currentUser = intentIncoming.getParcelableExtra("currentUser");
            otherUser = intentIncoming.getParcelableExtra("otherUser");

            setToolbar(otherUser);
            setView(currentUser,otherUser);
            AdRequest adRequest = new AdRequest.Builder().build();
            MobileAds.initialize(this, new OnInitializationCompleteListener() {
                @Override
                public void onInitializationComplete(InitializationStatus initializationStatus) {
                    Log.d("---adMob", "onInitializationComplete: " + initializationStatus.getAdapterStatusMap());
                }
            });
            mainAds(adRequest);
            setTwitterAds(adRequest);
            setInstaAds(adRequest);
            setGithubAds(adRequest);
            setLinkedInAds(adRequest);
        }


    }

    private void setLinkedInAds(AdRequest adRequest) {
        com.google.android.gms.ads.interstitial.InterstitialAd.load(this,getResources().getString(R.string.gecis_unit_id),adRequest,new InterstitialAdLoadCallback(){
            @Override
            public void onAdLoaded(@NonNull com.google.android.gms.ads.interstitial.InterstitialAd interstitialAd) {
                linkedInAds =  interstitialAd;

                linkedInAds.setFullScreenContentCallback(new FullScreenContentCallback(){
                    @Override
                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        Log.d("---adMob", "onAdFailedToLoad: " + adError.getMessage());
                        Toast.makeText(OtherUserProfileActivity.this , "linkedInAds ads failed to load" ,Toast.LENGTH_SHORT).show();
                        showUrl(otherUser.getLinkedin());
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        Log.d("---adMob", "onAdLoaded: " + "add loaded");
                    }

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent();
                        showUrl(otherUser.getLinkedin());
                    }
                });

            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                Log.d("---adMob", loadAdError.getMessage());
                linkedInAds = null;
            }
        });

    }

    private void setGithubAds(AdRequest adRequest) {
        com.google.android.gms.ads.interstitial.InterstitialAd.load(this,getResources().getString(R.string.gecis_unit_id),adRequest,new InterstitialAdLoadCallback(){
            @Override
            public void onAdLoaded(@NonNull com.google.android.gms.ads.interstitial.InterstitialAd interstitialAd) {
                githubAds =  interstitialAd;

                githubAds.setFullScreenContentCallback(new FullScreenContentCallback(){
                    @Override
                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        Log.d("---adMob", "onAdFailedToLoad: " + adError.getMessage());
                        showUrl(otherUser.getGithub(),"github.com");
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        Log.d("---adMob", "onAdLoaded: " + "add loaded");
                    }

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent();
                        showUrl(otherUser.getGithub(),"github.com");

                    }
                });

            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                Log.d("---adMob", loadAdError.getMessage());
                githubAds = null;
            }
        });
    }

    private void mainAds(AdRequest adRequest){

        com.google.android.gms.ads.interstitial.InterstitialAd.load(this,getResources().getString(R.string.gecis_unit_id),adRequest,new InterstitialAdLoadCallback(){
            @Override
            public void onAdLoaded(@NonNull com.google.android.gms.ads.interstitial.InterstitialAd interstitialAd) {
                mInterstitialAd =  interstitialAd;

                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                    @Override
                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        Log.d("---adMob", "onAdFailedToLoad: " + adError.getMessage());

                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        Log.d("---adMob", "onAdLoaded: " + "add loaded");
                    }

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent();
                    }
                });

            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                Log.d("---adMob", loadAdError.getMessage());
                mInterstitialAd = null;
            }
        });
    }
    private void setTwitterAds(AdRequest adRequest){

        com.google.android.gms.ads.interstitial.InterstitialAd.load(this,getResources().getString(R.string.gecis_unit_id),adRequest,new InterstitialAdLoadCallback(){
            @Override
            public void onAdLoaded(@NonNull com.google.android.gms.ads.interstitial.InterstitialAd interstitialAd) {
                twitterAds =  interstitialAd;

                twitterAds.setFullScreenContentCallback(new FullScreenContentCallback(){
                    @Override
                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        Log.d("---adMob", "onAdFailedToLoad: " + adError.getMessage());
                        Toast.makeText(OtherUserProfileActivity.this , "twitter ads failed to load" ,Toast.LENGTH_SHORT).show();
                        showUrl(currentUser.getTwitter(),"twitter.com");
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        Log.d("---adMob", "onAdLoaded: " + "add loaded");
                    }

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent();
                        showUrl(otherUser.getTwitter(),"twitter.com");
                    }
                });

            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                Log.d("---adMob", loadAdError.getMessage());
                twitterAds = null;
            }
        });
    }
    private void setInstaAds(AdRequest adRequest){

        com.google.android.gms.ads.interstitial.InterstitialAd.load(this,getResources().getString(R.string.gecis_unit_id),adRequest,new InterstitialAdLoadCallback(){
            @Override
            public void onAdLoaded(@NonNull com.google.android.gms.ads.interstitial.InterstitialAd interstitialAd) {
                instaAds =  interstitialAd;

                instaAds.setFullScreenContentCallback(new FullScreenContentCallback(){
                    @Override
                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        Log.d("---adMob", "onAdFailedToLoad: " + adError.getMessage());
                        showUrl(otherUser.getInstagram(),"instagram.com");
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        Log.d("---adMob", "onAdLoaded: " + "add loaded");
                    }

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent();
                        showUrl(otherUser.getInstagram(),"instagram.com");
                    }
                });

            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                Log.d("---adMob", loadAdError.getMessage());
                instaAds = null;
            }
        });
    }
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {

            // Wait 60 seconds
            mHandler.postDelayed(this, 1000);

            // Show Ad
            showInterstitial();

        }
    };

    private void showInterstitial() {
        if (mInterstitialAd != null){
            mInterstitialAd.show(OtherUserProfileActivity.this);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        mHandler = new Handler();

        mHandler.postDelayed(mRunnable,2*1000);

        if (followButton == null){
            followButton = (Button) findViewById(R.id.followButton);
            UserService.shared().checkIsFallowing(currentUser, otherUser, new TrueFalse<Boolean>() {
                @Override
                public void callBack(Boolean _value) {
                    if (_value){
                        followButton.setText("Takibi Bırak");
                        followButton.setBackgroundResource(R.drawable.button_unfollow_back);
                        isFollowing = _value;
                    }else{
                        followButton.setBackgroundResource(R.drawable.button_fallow_back);
                        followButton.setText("Takip Et");
                        isFollowing = _value;
                    }
                }
            });

        }else{
            UserService.shared().checkIsFallowing(currentUser, otherUser, new TrueFalse<Boolean>() {
                @Override
                public void callBack(Boolean _value) {
                    if (_value){
                        followButton.setText("Takibi Bırak");
                        followButton.setBackgroundResource(R.drawable.button_unfollow_back);
                        isFollowing = _value;
                    }else{
                        followButton.setBackgroundResource(R.drawable.button_fallow_back);
                        followButton.setText("Takip Et");
                        isFollowing = _value;
                    }
                }
            });

        }

    }

    private void setView(CurrentUser currentUser , OtherUser otherUser) {
        followerCount = (TextView)findViewById(R.id.followerCount);
        followingCount = (TextView)findViewById(R.id.followingCount);
        followerLbl = (TextView)findViewById(R.id.followerLbl);
        followingLbl = (TextView)findViewById(R.id.followingLbl);


        FollowService.shared().getOtherUserUserFollowersCount(otherUser, new CallBackCount() {
            @Override
            public void callBackCount(long count) {
                followerCount.setText(String.valueOf(count));
            }
        });
        FollowService.shared().getOthertUserFollowingCount(otherUser, new CallBackCount() {
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
                adapter.addFrag(new OtherUserMajorFragment(
                            currentUser,otherUser), initials);
                adapter.addFrag(new OtherUserSchoolFragment(
                        currentUser,otherUser), otherUser.getShort_school());
                adapter.addFrag(new OtherUserVayeAppFragment(
                        currentUser,otherUser), "Vaye.app");
                viewPager.setAdapter(adapter);
            }else {
                adapter = new ProfileViewPager(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
                adapter.addFrag(new OtherUserSchoolFragment(
                        currentUser,otherUser), otherUser.getShort_school());
                adapter.addFrag(new OtherUserVayeAppFragment(
                        currentUser,otherUser), "Vaye.app");
                viewPager.setAdapter(adapter);
            }
        }else{
            adapter = new ProfileViewPager(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            adapter.addFrag(new OtherUserVayeAppFragment(
                    currentUser,otherUser), "Vaye.app");;
            viewPager.setAdapter(adapter);
        }

        UserService.shared().checkIsFallowing(currentUser, otherUser, new TrueFalse<Boolean>() {
            @Override
            public void callBack(Boolean _value) {
                if (_value){
                    followButton.setText("Takibi Bırak");
                    followButton.setBackgroundResource(R.drawable.button_unfollow_back);
                    isFollowing = _value;
                }else{
                    followButton.setBackgroundResource(R.drawable.button_fallow_back);
                    followButton.setText("Takip Et");
                    isFollowing = _value;
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
                                NotificaitonService.shared().start_following_you(currentUser, otherUser, Notifications.NotificationDescription.following_you, Notifications.NotificationType.following_you, new TrueFalse<Boolean>() {
                                    @Override
                                    public void callBack(Boolean _value) {

                                    }
                                });
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

        github.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (githubAds != null){
                    githubAds.show(OtherUserProfileActivity.this);
                }else{
                    showUrl(otherUser.getGithub() , "github.com");
                }
            }
        });


        twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (twitterAds != null){
                    twitterAds.show(OtherUserProfileActivity.this);
                }else{
                    showUrl(otherUser.getTwitter() , "twitter.com");
                }
            }
        });
        insta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (instaAds!=null){
                    instaAds.show(OtherUserProfileActivity.this);
                }else{
                    showUrl(otherUser.getInstagram(),"instagram.com");
                }
            }
        });

        linkedin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (linkedInAds!=null){
                    linkedInAds.show(OtherUserProfileActivity.this);
                }else{
                    showUrl(otherUser.getLinkedin());
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


    private void showUrl(String username,String url){
        String text = username.replace("@","");
        if (!url.startsWith("http://") && !url.startsWith("https://"))
            url = "http://" + url;
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url+"/"+text));
        startActivity(browserIntent);
    }

    private void showUrl(String url){

        if (!url.startsWith("http://") && !url.startsWith("https://"))
            url = "http://" + url;
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Helper.shared().back(OtherUserProfileActivity.this);
    }
}