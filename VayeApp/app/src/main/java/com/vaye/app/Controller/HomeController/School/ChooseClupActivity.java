package com.vaye.app.Controller.HomeController.School;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;
import android.widget.TextView;

import com.vaye.app.Controller.HomeController.SchoolPostAdapter.ChooseClupAdapter;
import com.vaye.app.Controller.HomeController.StudentSetNewPost.ChooseLessonAdapter;
import com.vaye.app.Controller.HomeController.StudentSetNewPost.StudentChooseLessonActivity;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.LessonModel;
import com.vaye.app.R;
import com.vaye.app.Services.SchoolPostService;
import com.vaye.app.Util.Helper;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ChooseClupActivity extends AppCompatActivity {

    CurrentUser currentUser;
    Toolbar toolbar;
    TextView title;
    RecyclerView lessonList;
    ChooseClupAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_clup);
        Bundle extras = getIntent().getExtras();
        Intent intentIncoming = getIntent();
        if (extras != null){
            currentUser = intentIncoming.getParcelableExtra("currentUser");
            setToolbar("Kulüp Seçiniz");
            setView(currentUser);

        }else{
            finish();
        }
    }
    private void setView(CurrentUser currentUser)
    {

        lessonList = (RecyclerView)findViewById(R.id.lessonList);
        adapter = new ChooseClupAdapter(SchoolPostService.shared().getHastah(currentUser.getShort_school()),ChooseClupActivity.this,currentUser);
        lessonList.setHasFixedSize(true);
        lessonList.setLayoutManager(new LinearLayoutManager(ChooseClupActivity.this));
        lessonList.setAdapter(adapter);

    }
    private void setToolbar(String  titleText)
    {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        title = toolbar.findViewById(R.id.toolbar_title);

        toolbar.setTitle("");
        toolbar.setSubtitle("");
        title.setText(titleText);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Helper.shared().back(ChooseClupActivity.this);
            }
        });
    }
    @SuppressLint("WrongConstant")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_bar_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        searchView.setQueryHint("Kulüp Adı Giriniz");
        ArrayList<String> hastag = SchoolPostService.shared().getHastah(currentUser.getShort_school());
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("TAG", "onQueryTextChange: " + newText);

                ArrayList<String> filtred = new ArrayList<>();
                for (String item : hastag ){
                    if (item.toLowerCase().contains(newText.toLowerCase())){
                        filtred.add(item);
                    }
                }
                adapter.setClupName(filtred);
                adapter.notifyDataSetChanged();

                return true;
            }
        });

        return true;

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Helper.shared().back(ChooseClupActivity.this);
    }
}