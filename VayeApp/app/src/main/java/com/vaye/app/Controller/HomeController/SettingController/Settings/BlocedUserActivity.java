package com.vaye.app.Controller.HomeController.SettingController.Settings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.kongzue.dialog.v3.TipDialog;
import com.kongzue.dialog.v3.WaitDialog;
import com.vaye.app.Controller.HomeController.SettingController.SettingActivity;
import com.vaye.app.Interfaces.OtherUserService;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.OtherUser;
import com.vaye.app.R;
import com.vaye.app.Services.UserService;
import com.vaye.app.Util.Helper;

import java.util.ArrayList;

public class BlocedUserActivity extends AppCompatActivity {
    TextView toolbarTitle;
    Toolbar toolbar;
    CurrentUser currentUser;
    RecyclerView list;
    BlockListAdapter adapter;
    ArrayList<OtherUser> userList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bloced_user);
        Bundle extras = getIntent().getExtras();
        Intent intentIncoming = getIntent();
        if (extras != null){
            currentUser = intentIncoming.getParcelableExtra("currentUser");
            setToolbar();
            configureUI(currentUser);

        }
    }

    private void configureUI(CurrentUser currentUser) {
        userList = new ArrayList<>();
        list = (RecyclerView)findViewById(R.id.list);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setHasFixedSize(true);
        adapter = new BlockListAdapter(currentUser,userList,BlocedUserActivity.this);
        list.setAdapter(adapter);
        getBlockList();
    }

    private void getBlockList(){
        WaitDialog.show(BlocedUserActivity.this,null);
        if (currentUser.getBlockList().isEmpty()){
            TipDialog.show(BlocedUserActivity.this,"Hiç Engellenen Kullanıcı Yok", TipDialog.TYPE.ERROR);
            TipDialog.dismiss(1500);
        }else{
            for (String uid : currentUser.getBlockList()){
                UserService.shared().getOtherUserById(uid, new OtherUserService() {
                    @Override
                    public void callback(OtherUser user) {
                        if (user!=null){
                            userList.add(user);
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
                WaitDialog.dismiss();
            }
        }

    }

    private void setToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setSupportActionBar(toolbar);

        toolbar.setTitle("");
        toolbar.setSubtitle("");
        toolbarTitle = (TextView)toolbar.findViewById(R.id.toolbar_title);
        toolbarTitle.setText("Engellenen Kullanıcılar");
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Helper.shared().back(BlocedUserActivity.this);
            }
        });
    }
}