package com.vaye.app.Controller.HomeController.StudentSetNewPost;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class StudentChooseLessonActivity extends AppCompatActivity {

    Toolbar toolbar;

    TextView title;
    CurrentUser currentUser;
    RecyclerView lessonList;
    ChooseLessonAdapter adapter;
    ArrayList<LessonModel> lessons  = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_choose_lesson);
        Bundle extras = getIntent().getExtras();
        Intent intentIncoming = getIntent();
        if (extras != null){
            currentUser = intentIncoming.getParcelableExtra("currentUser");
            setToolbar("Ders Seçin");
            setView(currentUser);
            getMyLesson(currentUser);
        }else{
            finish();
        }
    }

    private void setView(CurrentUser currentUser)
    {
        lessonList = (RecyclerView)findViewById(R.id.lessonList);
        adapter = new ChooseLessonAdapter(lessons,StudentChooseLessonActivity.this,currentUser);
        lessonList.setHasFixedSize(true);
        lessonList.setLayoutManager(new LinearLayoutManager(StudentChooseLessonActivity.this));
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
                Helper.shared().back(StudentChooseLessonActivity.this);
            }
        });
    }

    private void getMyLesson(CurrentUser currentUser){

        WaitDialog.show(StudentChooseLessonActivity.this , "");
        //let db = Firestore.firestore().collection("user")
        //            .document(currentUser.uid).collection("lesson")
        CollectionReference ref = FirebaseFirestore.getInstance().collection("user")
                .document(currentUser.getUid())
                .collection("lesson");

        ref.get().addOnSuccessListener(StudentChooseLessonActivity.this, new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if (queryDocumentSnapshots.isEmpty()){
                        WaitDialog.dismiss();
                        TipDialog.show(StudentChooseLessonActivity.this , "Hiç Ders Takip Etmiyorsonuz", TipDialog.TYPE.ERROR);
                        TipDialog.dismiss(1500);
                    }else {
                        for (DocumentSnapshot item : queryDocumentSnapshots.getDocuments()){
                            lessons.add(item.toObject(LessonModel.class));
                            adapter.notifyDataSetChanged();
                        }


                        WaitDialog.dismiss();
                    }
            }
        });

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Helper.shared().back(StudentChooseLessonActivity.this);
    }
}