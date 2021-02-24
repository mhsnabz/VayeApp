package com.vaye.app.Controller.VayeAppController;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
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
import com.vaye.app.Controller.HomeController.Bolum.BolumFragment;
import com.vaye.app.Controller.HomeController.HomeActivity;
import com.vaye.app.Controller.HomeController.School.SchoolFragment;
import com.vaye.app.Controller.HomeController.SinglePost.ReplyActivity;
import com.vaye.app.Controller.VayeAppController.BuySell.BuySellFragment;
import com.vaye.app.Controller.VayeAppController.Camping.CampingFragment;
import com.vaye.app.Controller.VayeAppController.Followers.FollowersFragment;
import com.vaye.app.Controller.VayeAppController.FoodMe.FoodMeFragment;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.R;
import com.vaye.app.Util.BottomNavHelper;
import com.vaye.app.Util.Helper;

public class VayeAppActivity extends AppCompatActivity {
    CurrentUser currentUser;
    String TAG = "VayeAppActivity";
    Toolbar toolbar;
    TextView title;
    ImageButton followers , foodme , camping , buySell , setting;
    RelativeLayout followesLine , foodmeLine , campingLine , buySellLine;
    ViewPager mainViewPager;
    VayeAppPager pagerViewAdapter;
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
}