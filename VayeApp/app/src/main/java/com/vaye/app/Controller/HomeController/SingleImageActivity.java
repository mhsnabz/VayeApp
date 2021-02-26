package com.vaye.app.Controller.HomeController;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.vaye.app.R;

public class SingleImageActivity extends AppCompatActivity {
    String profileImage;
    ImageView imageView;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_image);
        Bundle extras = getIntent().getExtras();
        Intent intentIncoming = getIntent();
        if (extras != null){
            profileImage = intentIncoming.getStringExtra("profileImage");
            imageView = (ImageView) findViewById(R.id.imageView);
            progressBar = (ProgressBar)findViewById(R.id.progress);
            if (profileImage!=null && !profileImage.isEmpty()){
                Picasso.get().load(profileImage).resize(1024,1024).centerCrop()
                        .placeholder(android.R.color.darker_gray).into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }else{
                finish();
            }

        }
    }

    public void dismiss(View view) {
        finish();
    }
}