package com.vaye.app.Controller.ChatController;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firestore.v1.Document;
import com.vaye.app.Controller.ChatController.ChatList.ChatListFragment;
import com.vaye.app.Controller.ChatController.ChatPagerAdapter.ChatViewPagerAdapter;
import com.vaye.app.Controller.ChatController.FriendList.FriendListFragment;
import com.vaye.app.Controller.ChatController.RequestList.RequestListFragment;
import com.vaye.app.Interfaces.CurrentUserService;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.R;
import com.vaye.app.Services.UserService;
import com.vaye.app.Util.BottomNavHelper;

import java.util.HashMap;
import java.util.Map;

import q.rorbin.badgeview.QBadgeView;

public class ChatActivity extends AppCompatActivity {
    String TAG = "ChatActivity";
    CurrentUser currentUser;
    ViewPager viewPager;
    Toolbar toolbar;
    TextView title;
    ImageButton notificationSetting;
    TextView sohbetlerLbl , arkadaslarLbl , isteklerLbl;
    RelativeLayout line1,line2,line3;
    ChatViewPagerAdapter adapter;
    QBadgeView sohbetBadge =null;
    QBadgeView arkadasBadge = null;
    QBadgeView istekBadge = null;

    int requestSize = 0 , chatSize = 0 , friendSize = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Bundle extras = getIntent().getExtras();
        Intent intentIncoming = getIntent();
        if (extras != null){
            currentUser = intentIncoming.getParcelableExtra("currentUser");

            setupBottomNavBar(currentUser);
            setToolbar();
            setView(currentUser);
            setTabBar();


            Bundle bundle = new Bundle();
            bundle.putParcelable("currentUser",intentIncoming.getParcelableExtra("currentUser"));

            ChatListFragment chatListFragment = new ChatListFragment();
            chatListFragment.setArguments(bundle);
            FriendListFragment friendListFragment = new FriendListFragment();
            friendListFragment.setArguments(bundle);
            RequestListFragment requestListFragment = new RequestListFragment();
            requestListFragment.setArguments(bundle);

          sohbetBadge = (QBadgeView) new QBadgeView(ChatActivity.this);
          sohbetBadge.bindTarget(findViewById(R.id.text1));
          arkadasBadge = (QBadgeView) new QBadgeView(ChatActivity.this);
          arkadasBadge.bindTarget(findViewById(R.id.text2));
          istekBadge = (QBadgeView) new QBadgeView(ChatActivity.this);
          istekBadge.bindTarget(findViewById(R.id.text3));

        }

    }

    private void getBadgeCount(){
        BottomNavigationView view;
        view=(BottomNavigationView)findViewById(R.id.bottom_nav_bar);
        BottomNavigationMenuView bottomNavigationMenuView =
                (BottomNavigationMenuView) view.getChildAt(0);
        final QBadgeView badge = new QBadgeView(this);
        final View v = bottomNavigationMenuView.getChildAt(3);

        DocumentReference ref = FirebaseFirestore.getInstance().collection("user").document(currentUser.getUid());
              ref.addSnapshotListener(ChatActivity.this,new EventListener<DocumentSnapshot>() {
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

    private void updateTotalBadgeCount(int total){
        DocumentReference ref = FirebaseFirestore.getInstance().collection("user")
                .document(currentUser.getUid());
        Map<String , Object> map = new HashMap<>();
        map.put("totalBadge",total);
        ref.set(map , SetOptions.merge());
    }

    private void setupBottomNavBar(CurrentUser currentUser){
        BottomNavigationView navBar = (BottomNavigationView)findViewById(R.id.bottom_nav_bar);
        navBar.setElevation(5);
        BottomNavHelper.enableNavigation(this,navBar,currentUser);
        Menu menu = navBar.getMenu();
        MenuItem menuItem = menu.getItem(3);
        menuItem.setChecked(true);
        overridePendingTransition(0, 0);
        getBadgeCount();
        getNotificationBadgeCount();
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

        viewPager = (ViewPager)findViewById(R.id.mainPager);
        adapter = new ChatViewPagerAdapter(getSupportFragmentManager());
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



    }
    private void getMessagesBadgeCount(){
        Query ref = FirebaseFirestore.getInstance().collection("user").document(currentUser.getUid())
                .collection("msg-list").whereGreaterThan("badgeCount",0);
        ref.addSnapshotListener( ChatActivity.this,new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value.isEmpty()){
                    sohbetBadge.hide(true);
                    chatSize = 0;
                    updateTotalBadgeCount(chatSize + requestSize);

                    Log.d(TAG, "sohbetBadge: empty ");
                }else{
                    if (value.getDocuments() != null){
                            chatSize = value.getDocuments().size();
                        updateTotalBadgeCount(chatSize + requestSize);
                        sohbetBadge.setBadgeTextSize(8,true).setBadgePadding(7,true).setBadgeGravity(Gravity.CENTER | Gravity.START)
                                .setBadgeBackgroundColor(Color.RED).setBadgeNumber(value.getDocuments().size());
                        Log.d(TAG, "sohbetBadge: is " + value.getDocuments().size());
                    }
                }
            }
        });
    }
    private void getRequestBadgeCount(){
        Query ref = FirebaseFirestore.getInstance().collection("user").document(currentUser.getUid())
                .collection("msg-request").whereGreaterThan("badgeCount",0);
      ref.addSnapshotListener(ChatActivity.this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value.isEmpty()){
                    istekBadge.hide(true);
                    requestSize = 0;
                    updateTotalBadgeCount(chatSize + requestSize);
                    Log.d(TAG, "sohbetBadge: empty ");
                }else{
                    if (value.getDocuments() != null){
                        requestSize = value.getDocuments().size();
                        updateTotalBadgeCount(chatSize + requestSize);
                        istekBadge.setBadgeTextSize(8,true).setBadgePadding(7,true).setBadgeGravity(Gravity.CENTER | Gravity.START)
                                .setBadgeBackgroundColor(Color.RED).setBadgeNumber(value.getDocuments().size());
                        Log.d(TAG, "sohbetBadge: is " + value.getDocuments().size());
                    }
                }
            }
        });
    }
    private void getNotificationBadgeCount(){
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
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: destroy" );

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


    private void setTabBar(){
        sohbetlerLbl = (TextView)findViewById(R.id.text1);
        arkadaslarLbl = (TextView)findViewById(R.id.text2);
        isteklerLbl = (TextView)findViewById(R.id.text3);
        line1  = (RelativeLayout)findViewById(R.id.line1);
        line2  = (RelativeLayout)findViewById(R.id.line2);
        line3  = (RelativeLayout)findViewById(R.id.line3);
        sohbetlerLbl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                viewPager.setCurrentItem(0);
                changeTabs(0);
            }
        });
        arkadaslarLbl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                changeTabs(1);
                viewPager.setCurrentItem(1);
            }
        });
        isteklerLbl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeTabs(2);
                viewPager.setCurrentItem(2);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        viewPager.setCurrentItem(0);
        getMessagesBadgeCount();
        getRequestBadgeCount();
        if (currentUser!=null){
            setView(currentUser);
        }else{
            if (FirebaseAuth.getInstance().getCurrentUser().getUid() != null){
                UserService.shared().getCurrentUser(FirebaseAuth.getInstance().getCurrentUser().getUid(), new CurrentUserService() {
                    @Override
                    public void onCallback(CurrentUser user) {
                        setView(user);
                    }
                });
            }

        }
    }
}