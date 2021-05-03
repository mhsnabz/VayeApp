package com.vaye.app.Controller.HomeController.SinglePost;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.vaye.app.Controller.ChatController.Conservation.ConservationController;
import com.vaye.app.Controller.NotificationController.NotificationActivity;
import com.vaye.app.Controller.VayeAppController.VayeAppNewPostActivity;
import com.vaye.app.Interfaces.BlockOptionSelect;
import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.LessonPostModel;
import com.vaye.app.Model.MainPostModel;
import com.vaye.app.Model.NoticesMainModel;
import com.vaye.app.Model.OtherUser;
import com.vaye.app.R;
import com.vaye.app.Util.Helper;

public class SinglePostActivity extends AppCompatActivity implements BlockOptionSelect {


    LessonPostModel lessonPostModel;
    NoticesMainModel noticesMainModel;
    MainPostModel mainPostModel;
    CurrentUser currentUser;
    Toolbar toolbar;
    TextView toolbarTitle;
    RecyclerView list;
    SinglePostAdapter adapter;
    BlockOptionSelect optionSelect;
    AdRequest adRequest;
    private InterstitialAd mInterstitialAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_post);
        optionSelect = this::onSelectOption;
        Bundle extras = getIntent().getExtras();
        Intent intentIncoming = getIntent();
        if (extras != null){
            adRequest = new AdRequest.Builder().build();
            MobileAds.initialize(this, new OnInitializationCompleteListener() {
                @Override
                public void onInitializationComplete(InitializationStatus initializationStatus) {
                    Log.d("---adMob", "onInitializationComplete: " + initializationStatus.getAdapterStatusMap());
                }
            });
            if (adRequest!=null){
                mainAds(adRequest);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showInterstitial();

                    }
                },3000);
            }
            lessonPostModel = intentIncoming.getParcelableExtra("lessonPost");
            noticesMainModel = intentIncoming.getParcelableExtra("noticesPost");
            mainPostModel = intentIncoming.getParcelableExtra("mainPost");
            currentUser = intentIncoming.getParcelableExtra("currentUser");
            configureUI();
            if (lessonPostModel!=null){
                setNavigationBar(lessonPostModel.getLessonName());
            }else if (noticesMainModel!=null){
                setNavigationBar(noticesMainModel.getClupName());
            }else if (mainPostModel!=null){
                setNavigationBar("VayeApp");
            }

        }
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
    private void showInterstitial() {
        if (mInterstitialAd != null){
            mInterstitialAd.show(SinglePostActivity.this);
        }
    }

    private void configureUI(){
        list = (RecyclerView)findViewById(R.id.list);
        list.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        list.setHasFixedSize(true);
        if (lessonPostModel != null){
            adapter = new SinglePostAdapter(lessonPostModel,currentUser,this,optionSelect);
            list.setAdapter(adapter);
        }else if (noticesMainModel != null){
            adapter = new SinglePostAdapter(noticesMainModel,currentUser,this,optionSelect);
            list.setAdapter(adapter);
        }else if (mainPostModel!=null){
            adapter = new SinglePostAdapter(mainPostModel,currentUser,this,optionSelect);
            list.setAdapter(adapter);
        }


    }

    private void setNavigationBar(String title){
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        toolbar.setSubtitle("");

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Helper.shared().back(SinglePostActivity.this);
            }
        });
        toolbarTitle = toolbar.findViewById(R.id.toolbar_title);
        toolbarTitle.setText(title);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Helper.shared().back(SinglePostActivity.this);
    }

    @Override
    public void onSelectOption(String target, OtherUser otherUser) {

    }
}