package com.vaye.app.Controller.HomeController.SettingController.Settings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.vaye.app.R;
import com.vaye.app.Util.Helper;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class HizmetActivity extends AppCompatActivity {
    TextView toolbarTitle;
    Toolbar toolbar;
    WebView web;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hizmet);
        setToolbar();
        web = (WebView)findViewById(R.id.web);

        try {
            AssetManager assetManager = HizmetActivity.this.getAssets();
            InputStream stream = assetManager.open("kullanim_kosullari.html");
            BufferedReader r = new BufferedReader(new InputStreamReader(stream));
            StringBuilder total = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                total.append(line).append("\n");
            }
            web.loadDataWithBaseURL(null, total.toString(), "text/html", "UTF-8", null);
        } catch (Exception xxx) {
            Log.e("tag", "Load assets/page.html", xxx);
        }
    }
    private void setToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setSupportActionBar(toolbar);

        toolbar.setTitle("");
        toolbar.setSubtitle("");
        toolbarTitle = (TextView)toolbar.findViewById(R.id.toolbar_title);
        toolbarTitle.setText("Kullanım Koşulları");
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Helper.shared().back(HizmetActivity.this);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Helper.shared().back(HizmetActivity.this);
    }
}