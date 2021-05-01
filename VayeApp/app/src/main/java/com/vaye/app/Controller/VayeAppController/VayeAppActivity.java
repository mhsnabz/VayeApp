package com.vaye.app.Controller.VayeAppController;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.vaye.app.Controller.HomeController.HomeActivity;
import com.vaye.app.Controller.VayeAppController.BuySell.BuySellFragment;
import com.vaye.app.Controller.VayeAppController.Camping.CampingFragment;
import com.vaye.app.Controller.VayeAppController.Followers.FollowersFragment;
import com.vaye.app.Controller.VayeAppController.FoodMe.FoodMeFragment;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.R;
import com.vaye.app.Util.BottomNavHelper;
import com.vaye.app.Util.Helper;

import java.util.HashMap;
import java.util.Map;

import q.rorbin.badgeview.QBadgeView;

public class VayeAppActivity extends AppCompatActivity {
    CurrentUser currentUser;
    String TAG = "VayeAppActivity";
    Toolbar toolbar;
    TextView title;
    ImageButton followers , foodme , camping , buySell , setting;
    RelativeLayout followesLine , foodmeLine , campingLine , buySellLine;
    ViewPager mainViewPager;
    VayeAppPager pagerViewAdapter;
    int requestSize = 0 , chatSize = 0 , friendSize = 0;
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

            confiureUI();


            FollowersFragment fragobj = new FollowersFragment();
            fragobj.setArguments(bundle);

            CampingFragment campingFragment = new CampingFragment();
            campingFragment.setArguments(bundle);

            FoodMeFragment FoodMeFragment = new FoodMeFragment();
            FoodMeFragment.setArguments(bundle);

            BuySellFragment BuySellFragment = new BuySellFragment();
            BuySellFragment.setArguments(bundle);




        }


    }
    private void confiureUI() {
        followers = (ImageButton)findViewById(R.id.followers);
        foodme = (ImageButton)findViewById(R.id.foodMe);
        camping = (ImageButton)findViewById(R.id.camping);
        buySell = (ImageButton)findViewById(R.id.buy_sell);

        followesLine = (RelativeLayout)findViewById(R.id.followersLine);
        foodmeLine = (RelativeLayout)findViewById(R.id.foodMeLine);
        campingLine = (RelativeLayout)findViewById(R.id.campingLine);
        buySellLine = (RelativeLayout)findViewById(R.id.buy_sell_line);


        mainViewPager = (ViewPager)findViewById(R.id.mainPager);
        pagerViewAdapter = new VayeAppPager(getSupportFragmentManager());
        mainViewPager.setAdapter(pagerViewAdapter);


        mainViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                changeTabs(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        foodme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               mainViewPager.setCurrentItem(2);
            }
        });

        followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               mainViewPager.setCurrentItem(0);
            }
        });

        camping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainViewPager.setCurrentItem(3);
            }
        });
        buySell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               mainViewPager.setCurrentItem(1);
            }
        });
    }
    private void setupBottomNavBar(CurrentUser currentUser){
        BottomNavigationView navBar = (BottomNavigationView)findViewById(R.id.bottom_nav_bar);
        navBar.setElevation(5);
        BottomNavHelper.enableNavigation(this,navBar,currentUser);
        Menu menu = navBar.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);
        overridePendingTransition(0, 0);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        title = toolbar.findViewById(R.id.toolbar_title);
        setting = toolbar.findViewById(R.id.notificationSetting);
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Intent i = new Intent(VayeAppActivity.this , VayeAppnotificationSettingActivity.class);
            i.putExtra("currentUser",currentUser);
            startActivity(i);
            Helper.shared().go(VayeAppActivity.this);
            }
        });
        toolbar.setTitle("");
        toolbar.setSubtitle("");
        title.setText("Takip Ettiklerin");
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

    }

    private void changeTabs(int postion){
        if (postion == 0){

            followesLine.setVisibility(View.VISIBLE);
            followers.setImageResource(R.drawable.follower_selected);

            buySellLine.setVisibility(View.GONE);
            buySell.setImageResource(R.drawable.buy_sell);

            foodmeLine.setVisibility(View.GONE);
            foodme.setImageResource(R.drawable.food_me);

            campingLine.setVisibility(View.GONE);
            camping.setImageResource(R.drawable.camping);

            title.setText("Takip Ettiklerin");

        }else if (postion == 1){
            followesLine.setVisibility(View.GONE);
            followers.setImageResource(R.drawable.follower);

            buySellLine.setVisibility(View.VISIBLE);
            buySell.setImageResource(R.drawable.buy_sell_selected);

            foodmeLine.setVisibility(View.GONE);
            foodme.setImageResource(R.drawable.food_me);

            campingLine.setVisibility(View.GONE);
            camping.setImageResource(R.drawable.camping);
            title.setText("Al-Sat");
        }else if (postion == 2){
            followesLine.setVisibility(View.GONE);
            followers.setImageResource(R.drawable.follower);

            buySellLine.setVisibility(View.GONE);
            buySell.setImageResource(R.drawable.buy_sell);

            foodmeLine.setVisibility(View.VISIBLE);
            foodme.setImageResource(R.drawable.food_me_selected);

            campingLine.setVisibility(View.GONE);
            camping.setImageResource(R.drawable.camping);
            title.setText("Yemek");
        }else if (postion == 3){
            followesLine.setVisibility(View.GONE);
            followers.setImageResource(R.drawable.follower);

            buySellLine.setVisibility(View.GONE);
            buySell.setImageResource(R.drawable.buy_sell);

            foodmeLine.setVisibility(View.GONE);
            foodme.setImageResource(R.drawable.food_me);

            campingLine.setVisibility(View.VISIBLE);
            camping.setImageResource(R.drawable.camping_selected);
            title.setText("Kamp");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        getMessagesCount();
        getBadgeCount();
        setChatBadgeCount();
    }

    private void getBadgeCount(){
        BottomNavigationView view;
        view=(BottomNavigationView)findViewById(R.id.bottom_nav_bar);
        BottomNavigationMenuView bottomNavigationMenuView =
                (BottomNavigationMenuView) view.getChildAt(0);
        final QBadgeView badge = new QBadgeView(this);
        final View v = bottomNavigationMenuView.getChildAt(2);

        Query ref =  FirebaseFirestore.getInstance().collection("user")
                .document(currentUser.getUid())
                .collection("notification").whereEqualTo("isRead","false");

        ref.addSnapshotListener(this,new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot.isEmpty()){
                    badge.hide(true);
                }else{
                    badge.bindTarget(v).setBadgeTextSize(14,true).setBadgePadding(7,true)
                            .setBadgeBackgroundColor(Color.RED).setBadgeNumber(documentSnapshot.getDocuments().size());;
                    if (documentSnapshot.getDocuments().size() < 1){
                        badge.hide(true);
                    }
                }
            }
        });

    }
    private void getMessagesCount(){
        BottomNavigationView view;
        view=(BottomNavigationView)findViewById(R.id.bottom_nav_bar);
        BottomNavigationMenuView bottomNavigationMenuView =
                (BottomNavigationMenuView) view.getChildAt(0);
        final QBadgeView badge = new QBadgeView(this);
        final View v = bottomNavigationMenuView.getChildAt(3);

        DocumentReference ref = FirebaseFirestore.getInstance().collection("user").document(currentUser.getUid());
        ref.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value!=null){
                    if (value.getLong("totalBadge") != null){
                        if (value.getLong("totalBadge").intValue() == 0){
                            badge.hide(true);
                        }else{

                            badge.bindTarget(v).setBadgeTextSize(14,true).setBadgePadding(7,true)
                                    .setBadgeBackgroundColor(Color.RED).setBadgeNumber(value.getLong("totalBadge").intValue());
                        }
                    }
                }
            }
        });


    }
    private void setChatBadgeCount(){
        Query ref_msg_list = FirebaseFirestore.getInstance().collection("user").document(currentUser.getUid())
                .collection("msg-list").whereGreaterThan("badgeCount",0);
        Query ref_req_list = FirebaseFirestore.getInstance().collection("user").document(currentUser.getUid())
                .collection("msg-request").whereGreaterThan("badgeCount",0);

        ref_req_list.addSnapshotListener( VayeAppActivity.this,new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value.isEmpty()){

                    requestSize = 0;
                    updateTotalBadgeCount(chatSize + requestSize);
                    Log.d(TAG, "sohbetBadge: empty ");
                }else{
                    if (value.getDocuments() != null){
                        requestSize = value.getDocuments().size();
                        updateTotalBadgeCount(chatSize + requestSize);

                        Log.d(TAG, "sohbetBadge: is " + value.getDocuments().size());
                    }
                }
            }
        });

        ref_msg_list.addSnapshotListener(VayeAppActivity.this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value.isEmpty()){

                    chatSize = 0;
                    updateTotalBadgeCount(chatSize + requestSize);

                    Log.d(TAG, "sohbetBadge: empty ");
                }else{
                    if (value.getDocuments() != null){
                        chatSize = value.getDocuments().size();
                        updateTotalBadgeCount(chatSize + requestSize);

                        Log.d(TAG, "sohbetBadge: is " + value.getDocuments().size());
                    }
                }
            }
        });
    }
    private void updateTotalBadgeCount(int total){
        DocumentReference ref = FirebaseFirestore.getInstance().collection("user")
                .document(currentUser.getUid());
        Map<String , Object> map = new HashMap<>();
        map.put("totalBadge",total);
        ref.set(map , SetOptions.merge());
    }

}