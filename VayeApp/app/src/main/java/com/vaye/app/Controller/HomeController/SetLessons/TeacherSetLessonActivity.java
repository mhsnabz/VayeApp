package com.vaye.app.Controller.HomeController.SetLessons;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.vaye.app.R;
import com.vaye.app.Util.Helper;

import java.util.ArrayList;

public class TeacherSetLessonActivity extends AppCompatActivity {
    CurrentUser currentUser;
    Toolbar toolbar;
    TextView title;
    RecyclerView lessonList;
    String TAG = "TeacherSetLessonActivity";
    TeacherSetLessonAdapter adapter;
    ArrayList<LessonModel> model = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_set_lesson);
        lessonList  = (RecyclerView)findViewById(R.id.lessonList);
        lessonList.setHasFixedSize(true);
        lessonList.setLayoutManager(new LinearLayoutManager(this));

        Bundle extras = getIntent().getExtras();
        Intent intentIncoming = getIntent();

        if (extras != null){
            currentUser = intentIncoming.getParcelableExtra("currentUser");
            setToolbar();

            getLessons(currentUser);

            adapter = new TeacherSetLessonAdapter(model , currentUser , this);
            lessonList.setAdapter(adapter);
        }
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
                Helper.shared().back(TeacherSetLessonActivity.this);
            }
        });
    }
    private void getLessons(CurrentUser currentUser){

        WaitDialog.show(TeacherSetLessonActivity.this , "Lütfen Bekleyin");
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
                        TipDialog.show(TeacherSetLessonActivity.this,"Seçebilceğiniz Ders Yok", TipDialog.TYPE.ERROR);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Helper.shared().back(TeacherSetLessonActivity.this);
    }
}