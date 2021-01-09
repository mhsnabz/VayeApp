package com.vaye.app.Controller.HomeController.PagerAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.vaye.app.Model.CurrentUser;
import com.vaye.app.R;
import com.vaye.app.Util.Helper;

import java.util.ArrayList;

public class AllDatasActivity extends AppCompatActivity {
    Toolbar toolbar;
    ViewPager2 viewPager;
    AllDatasAdapter adapter ;
    ArrayList<String> url;
    CurrentUser currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_datas);
        Bundle extras = getIntent().getExtras();
        Intent intentIncoming = getIntent();
        if (extras != null){
            url = intentIncoming.getStringArrayListExtra("url");
            currentUser = intentIncoming.getParcelableExtra("currentUser");
            adapter = new AllDatasAdapter(url,this,currentUser);
            viewPager = (ViewPager2)findViewById(R.id.viewPager);
            viewPager.setAdapter(adapter);

        }
        setToolbar();

    }


    private void setToolbar(){
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        TextView title = toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        toolbar.setSubtitle("");
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        title.setText("Bütün Dosyalar");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Helper.shared().back(AllDatasActivity.this);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Helper.shared().back(AllDatasActivity.this);
    }
}