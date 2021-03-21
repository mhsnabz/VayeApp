package com.vaye.app.Controller.HomeController.TeacherNewPost;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.vaye.app.Model.CurrentUser;

import com.vaye.app.Model.LessonFallowerUser;
import com.vaye.app.R;
import com.vaye.app.Util.Helper;

import java.util.ArrayList;

public class UserList extends AppCompatActivity {
        String TAG = "UserList";
    Toolbar toolbar;
    TextView title;
    ArrayList<LessonFallowerUser> userLists;
    CurrentUser currentUser;
    RecyclerView list;
    UserListAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        Bundle extras = getIntent().getExtras();
        Intent intentIncoming = getIntent();
        if (extras != null){
            currentUser = intentIncoming.getParcelableExtra("currentUser");
           // userLists = intentIncoming.getParcelableArrayListExtra("userList");
            setToolbar();
            setView(currentUser,userLists);
        }else {
            finish();
        }
    }

    private void setView(CurrentUser currentUser, ArrayList<LessonFallowerUser> userLists) {
        list = (RecyclerView)findViewById(R.id.userList);
        list.setHasFixedSize(true);
        list.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UserListAdapter(userLists,this,currentUser);
        list.setAdapter(adapter);

    }

    private void setToolbar()
    {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        title = toolbar.findViewById(R.id.toolbar_title);

        toolbar.setTitle("");
        toolbar.setSubtitle("");
        title.setText("Öğrenci Listesi");

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Helper.shared().back(UserList.this);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Helper.shared().back(UserList.this);
    }
}