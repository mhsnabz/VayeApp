package com.vaye.app.LoginRegister;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
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

import com.vaye.app.Controller.HomeController.School.ChooseClupActivity;
import com.vaye.app.Controller.HomeController.SchoolPostAdapter.SchoolPostAdapter;
import com.vaye.app.Controller.HomeController.SetLessons.StudentSetLessonActivity;
import com.vaye.app.Controller.HomeController.StudentSetNewPost.ChooseLessonAdapter;
import com.vaye.app.LoginRegister.Adapter.ChooseSchoolAdapter;
import com.vaye.app.Model.LessonModel;
import com.vaye.app.Model.SchoolModel;
import com.vaye.app.R;
import com.vaye.app.Util.Helper;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ChooseSchoolActivity extends AppCompatActivity {

    ChooseSchoolAdapter adapter;
    RecyclerView list;
    ArrayList<SchoolModel> model;
    Toolbar toolbar;
    TextView title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_school);
        configureUI();
    }


    private void configureUI(){
        setToolbar();
        model = new ArrayList<>();
        model.add(new SchoolModel("İskenderun Teknik Üniversitesi","İSTE",getResources().getDrawable(R.drawable.iste_logo)));
        adapter  = new ChooseSchoolAdapter(model,this);

        list = (RecyclerView)findViewById(R.id.schoolList);
        list.setHasFixedSize(true);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);

    }
    private void setToolbar(){
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        title = toolbar.findViewById(R.id.toolbar_title);
        toolbar.setTitle("");
        toolbar.setSubtitle("");
        title.setText("Üniversiteni Seç");
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Helper.shared().back(ChooseSchoolActivity.this);
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
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        searchView.setQueryHint("Üniversite Adı Giriniz");
        searchView.setInputType(InputType.TYPE_CLASS_TEXT);
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("TAG", "onQueryTextChange: " + newText);

                ArrayList<SchoolModel> filtred = new ArrayList<>();

                for (SchoolModel item : model){
                    if (item.getName().toLowerCase().contains(newText.toLowerCase())){
                        filtred.add(item);
                    }
                }
                adapter.setList(filtred);
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
        Helper.shared().back(ChooseSchoolActivity.this);
    }
}