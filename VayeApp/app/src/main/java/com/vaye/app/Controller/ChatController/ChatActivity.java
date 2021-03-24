package com.vaye.app.Controller.ChatController;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.vaye.app.Controller.ChatController.ChatPagerAdapter.ChatViewPagerAdapter;
import com.vaye.app.Controller.HomeController.HomeActivity;
import com.vaye.app.Controller.HomeController.PagerAdapter.PagerViewApadater;
import com.vaye.app.Controller.HomeController.School.SchoolPostNotificationActivity;
import com.vaye.app.Controller.Profile.ProfileViewPager;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.R;
import com.vaye.app.Util.BottomNavHelper;
import com.vaye.app.Util.Helper;

import q.rorbin.badgeview.QBadgeView;

public class ChatActivity extends AppCompatActivity {
    CurrentUser currentUser;
    ViewPager viewPager;
    Toolbar toolbar;
    TextView title;
    ImageButton notificationSetting;
    TextView sohbetlerLbl , arkadaslarLbl , isteklerLbl;
    RelativeLayout line1,line2,line3;
    ChatViewPagerAdapter adapter;

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
            setToolbar();
            setView(currentUser);

        }

    }
    private void setupBottomNavBar(CurrentUser currentUser){
        BottomNavigationView navBar = (BottomNavigationView)findViewById(R.id.bottom_nav_bar);
        navBar.setElevation(5);
        BottomNavHelper.enableNavigation(this,navBar,currentUser);
        Menu menu = navBar.getMenu();
        MenuItem menuItem = menu.getItem(3);
        menuItem.setChecked(true);
        overridePendingTransition(0, 0);
    }
    private void setToolbar(){
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        title = toolbar.findViewById(R.id.toolbar_title);

        notificationSetting = (ImageButton)toolbar.findViewById(R.id.notificationSetting);
        notificationSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        setSupportActionBar(toolbar);

        toolbar.setTitle("");
        toolbar.setSubtitle("");
        title.setText("Sohbetler");
    }
    private void setView(CurrentUser currentUser){
        sohbetlerLbl = (TextView)findViewById(R.id.text1);
        arkadaslarLbl = (TextView)findViewById(R.id.text2);
        isteklerLbl = (TextView)findViewById(R.id.text3);
        line1  = (RelativeLayout)findViewById(R.id.line1);
        line2  = (RelativeLayout)findViewById(R.id.line2);
        line3  = (RelativeLayout)findViewById(R.id.line3);
        viewPager = (ViewPager)findViewById(R.id.mainPager);
        adapter = new ChatViewPagerAdapter(getSupportFragmentManager(),currentUser);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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

        sohbetlerLbl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeTabs(0);
            }
        });
        arkadaslarLbl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeTabs(1);
            }
        });
        isteklerLbl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeTabs(2);
            }
        });
        setBadgeCount();
    }



    private void setBadgeCount(){
        new QBadgeView(ChatActivity.this).bindTarget(sohbetlerLbl).setBadgeGravity(Gravity.CENTER | Gravity.START).setBadgeNumber(5);

        Query db = FirebaseFirestore.getInstance().collection("user")
                .document(currentUser.getUid())
                .collection("msg-request").whereGreaterThan("badgeCount",0);

        db.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (value.isEmpty()){
                      //  requestCount.hide(true);
                    }else{
                        //requestCount.bindTarget(isteklerLbl).setBadgeGravity(Gravity.CENTER | Gravity.START).setBadgeNumber(value.getDocuments().size());
                    }
             }
        });

    }


    private void changeTabs(int positon){
        if (positon == 0){
            title.setText("Sohbetler");
            line1.setVisibility(View.VISIBLE);
            line2.setVisibility(View.GONE);
            line3.setVisibility(View.GONE);
            sohbetlerLbl.setTextColor(getColor(R.color.black));
            arkadaslarLbl.setTextColor(getColor(R.color.gray));
            isteklerLbl.setTextColor(getColor(R.color.gray));
            sohbetlerLbl.setTextSize(16);
            arkadaslarLbl.setTextSize(12);
            isteklerLbl.setTextSize(12);
        }else if (positon == 1){
            title.setText("Arkadaşlar");
            line1.setVisibility(View.GONE);
            line3.setVisibility(View.GONE);
            line2.setVisibility(View.VISIBLE);
            sohbetlerLbl.setTextColor(getColor(R.color.gray));
            isteklerLbl.setTextColor(getColor(R.color.gray));
            arkadaslarLbl.setTextColor(getColor(R.color.black));
            sohbetlerLbl.setTextSize(12);
            isteklerLbl.setTextSize(12);
            arkadaslarLbl.setTextSize(16);
        }else if (positon == 2){
            title.setText("Sohbet İstekleri");
            line1.setVisibility(View.GONE);
            line2.setVisibility(View.GONE);
            line3.setVisibility(View.VISIBLE);
            sohbetlerLbl.setTextColor(getColor(R.color.gray));
            arkadaslarLbl.setTextColor(getColor(R.color.gray));
            isteklerLbl.setTextColor(getColor(R.color.black));
            sohbetlerLbl.setTextSize(12);
            arkadaslarLbl.setTextSize(12);
            isteklerLbl.setTextSize(16);
        }
    }
}