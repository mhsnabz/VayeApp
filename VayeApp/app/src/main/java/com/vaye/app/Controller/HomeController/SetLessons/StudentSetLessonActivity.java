package com.vaye.app.Controller.HomeController.SetLessons;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.Layout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.kongzue.dialog.v3.TipDialog;
import com.kongzue.dialog.v3.WaitDialog;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.LessonModel;
import com.vaye.app.Model.LessonPostModel;
import com.vaye.app.R;
import com.vaye.app.Util.Helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StudentSetLessonActivity extends AppCompatActivity {
    CurrentUser currentUser;
    Toolbar toolbar;
    TextView title;
    RecyclerView lessonList;
    String TAG = "StudentSetLessonActivity";
    ArrayList <LessonModel> model = new ArrayList<>();
    StudentLessonAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_set_lessom);

        lessonList  = (RecyclerView)findViewById(R.id.lessonList);
        lessonList.setHasFixedSize(true);
        lessonList.setLayoutManager(new LinearLayoutManager(this));

        Bundle extras = getIntent().getExtras();
        Intent intentIncoming = getIntent();

        if (extras != null){
            currentUser = intentIncoming.getParcelableExtra("currentUser");
            setToolbar();

            getLessons(currentUser);

            adapter = new StudentLessonAdapter(model , currentUser , this);
            lessonList.setAdapter(adapter);
        }
    }

    private void getLessons(CurrentUser currentUser){

        WaitDialog.show(StudentSetLessonActivity.this , "Lütfen Bekleyin");
        // let db = Firestore.firestore().collection(user.short_school)
           //     .document("lesson").collection(user.bolum)
        CollectionReference ref = FirebaseFirestore.getInstance().collection(currentUser.getShort_school())
        .document("lesson").collection(currentUser.getBolum());
        ref.get().addOnCompleteListener(this, new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task)
            {
                if (task.isSuccessful()){
                    if (!task.getResult().isEmpty()){
                        for (DocumentSnapshot item : task.getResult().getDocuments()){
                            if (item.exists()){
                                LessonModel value = item.toObject(LessonModel.class);
                                model.add(value);
                                adapter.notifyDataSetChanged();

                            }
                        }
                        WaitDialog.dismiss();
                    }else {
                        WaitDialog.dismiss();
                        TipDialog.show(StudentSetLessonActivity.this,"Seçebilceğiniz Ders Yok", TipDialog.TYPE.ERROR);
                    }
                }
            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: "+e.getLocalizedMessage());
            }
        });
    }

    private void setToolbar(){
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        title = toolbar.findViewById(R.id.toolbar_title);
        toolbar.setTitle("");
        toolbar.setSubtitle("");
        title.setText("Ders Ekle-Çıkar");
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Helper.shared().back(StudentSetLessonActivity.this);
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
        searchView.setQueryHint("Ders Adı Giriniz");
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

                ArrayList<LessonModel> filtred = new ArrayList<>();
                for (LessonModel item : model){
                    if (item.getLessonName().toLowerCase().contains(newText.toLowerCase())){
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
        Helper.shared().back(StudentSetLessonActivity.this);
    }
}