package com.vaye.app.Controller.HomeController.TeacherNewPost;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.kongzue.dialog.v3.TipDialog;
import com.kongzue.dialog.v3.WaitDialog;
import com.vaye.app.Interfaces.LessonFallowersCallback;
import com.vaye.app.Interfaces.StringArrayListInterface;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.LessonFallowerUser;
import com.vaye.app.Model.LessonModel;

import com.vaye.app.R;
import com.vaye.app.Services.MajorPostService;
import com.vaye.app.Util.Helper;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

public class TeacherChooseLesson extends AppCompatActivity implements StringArrayListInterface {
    String TAG = "TeacherChooseLesson";
    Toolbar toolbar;
    TextView title;
    CurrentUser currentUser;
    RecyclerView lessonList;
    Button devamEt;
    String lesson_key;
    int size = 0;
    TeacherChooseLessonAdapter adapter;
    String selectedLesson;
    ArrayList<LessonModel> lessons  = new ArrayList<>();
    ArrayList<LessonFallowerUser> lstContacts ;
    ArrayList<String > userUidList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_choose_lesson);
        Bundle extras = getIntent().getExtras();
        Intent intentIncoming = getIntent();
        if (extras != null){
            currentUser = intentIncoming.getParcelableExtra("currentUser");
            setToolbar("Ders Seçin");
            setView(currentUser);
            getMyLesson(currentUser);
            LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                    new IntentFilter("users"));
        }
    }
    private void setView(CurrentUser currentUser)
    {
        lessonList = (RecyclerView)findViewById(R.id.lessonList);
        adapter = new TeacherChooseLessonAdapter(lessons,currentUser,this,this);
        lessonList.setHasFixedSize(true);
        lessonList.setLayoutManager(new LinearLayoutManager(TeacherChooseLesson.this));
        lessonList.setAdapter(adapter);

    }
    private void getMyLesson(CurrentUser currentUser){

        WaitDialog.show(TeacherChooseLesson.this , "");
        CollectionReference ref = FirebaseFirestore.getInstance().collection("user")
                .document(currentUser.getUid())
                .collection("lesson");
        ref.get().addOnCompleteListener(TeacherChooseLesson.this, new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().isEmpty()){
                        WaitDialog.dismiss();
                        TipDialog.show(TeacherChooseLesson.this , "Hiç Ders Takip Etmiyorsunuz", TipDialog.TYPE.ERROR);
                        TipDialog.dismiss(1500);
                    }else{
                        for (DocumentSnapshot doc : task.getResult().getDocuments()){
                            lessons.add(doc.toObject(LessonModel.class));

                        }
                        adapter.notifyDataSetChanged();
                        WaitDialog.dismiss();
                    }
                }
            }
        });


    }

    private void setToolbar(String  titleText)
    {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        title = toolbar.findViewById(R.id.toolbar_title);

        toolbar.setTitle("");
        toolbar.setSubtitle("");
        title.setText(titleText);
        devamEt = (Button)toolbar.findViewById(R.id.devamEt);
        devamEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(TeacherChooseLesson.this, TeacherNewPostActivity.class);
                i.putExtra("userList",userUidList);
                i.putExtra("currentUser",currentUser);
                i.putExtra("selectedLesson",selectedLesson);
                i.putExtra("lesson_key",lesson_key);
                startActivity(i);
                Helper.shared().go(TeacherChooseLesson.this);
                Log.d(TAG, "onClick: list " + userUidList.size());



            }
        });
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Helper.shared().back(TeacherChooseLesson.this);
            }
        });
    }
    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<LessonFallowerUser> target = intent.getParcelableArrayListExtra("list");
            Set<LessonFallowerUser> unique = new LinkedHashSet<LessonFallowerUser>(target);
            lstContacts = new ArrayList<LessonFallowerUser>(unique);

            if (intent.getIntExtra("times",0) == 1){
                title.setText(intent.getStringExtra("lessonname"));
                selectedLesson = intent.getStringExtra("lessonname");
                lesson_key = intent.getStringExtra("lesson_key");
            }else{
                selectedLesson = "Genel Duyuru";
                title.setText("Genel Duyuru");
                lesson_key = "genel_duyuru";
            }
            size = lstContacts.size();
            WaitDialog.dismiss();
            TipDialog.show(TeacherChooseLesson.this,lstContacts.size()+" Öğrenci Seçildi", TipDialog.TYPE.SUCCESS);
            TipDialog.dismiss(1000);
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Helper.shared().back(TeacherChooseLesson.this);

    }

    @Override
    public void getArrayList(ArrayList<String> list) {
        Log.d(TAG, "getArrayList: " + list.size());
        for (String item : list){
            if (!userUidList.contains(item)){
                userUidList.add(item);
            }
        }
    }
}