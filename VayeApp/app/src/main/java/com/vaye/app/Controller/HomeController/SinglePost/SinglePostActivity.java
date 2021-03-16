package com.vaye.app.Controller.HomeController.SinglePost;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.vaye.app.Controller.NotificationController.NotificationActivity;
import com.vaye.app.Controller.VayeAppController.VayeAppNewPostActivity;
import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.LessonPostModel;
import com.vaye.app.Model.MainPostModel;
import com.vaye.app.Model.NoticesMainModel;
import com.vaye.app.R;
import com.vaye.app.Util.Helper;

public class SinglePostActivity extends AppCompatActivity {


    LessonPostModel lessonPostModel;
    NoticesMainModel noticesMainModel;
    MainPostModel mainPostModel;
    CurrentUser currentUser;
    Toolbar toolbar;
    TextView toolbarTitle;
    RecyclerView list;
    SinglePostAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_post);
        Bundle extras = getIntent().getExtras();
        Intent intentIncoming = getIntent();
        if (extras != null){
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


    private void configureUI(){
        list = (RecyclerView)findViewById(R.id.list);
        list.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        list.setHasFixedSize(true);
        if (lessonPostModel != null){
            adapter = new SinglePostAdapter(lessonPostModel,currentUser,this);
            list.setAdapter(adapter);
        }else if (noticesMainModel != null){
            adapter = new SinglePostAdapter(noticesMainModel,currentUser,this);
            list.setAdapter(adapter);
        }else if (mainPostModel!=null){
            adapter = new SinglePostAdapter(mainPostModel,currentUser,this);
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
}