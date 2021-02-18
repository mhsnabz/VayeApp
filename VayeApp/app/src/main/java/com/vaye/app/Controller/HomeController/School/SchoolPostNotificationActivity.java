package com.vaye.app.Controller.HomeController.School;

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
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.vaye.app.Controller.HomeController.SchoolPostAdapter.ChooseClupAdapter;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.SchoolPostNotificationModel;
import com.vaye.app.R;
import com.vaye.app.Services.SchoolPostService;
import com.vaye.app.Util.Helper;

import java.util.ArrayList;

public class SchoolPostNotificationActivity extends AppCompatActivity {

    CurrentUser currentUser;
    Toolbar toolbar;
    TextView title;
    RecyclerView lessonList;
    SchoolPostNotificaitonAdapter adapter;
    ArrayList<SchoolPostNotificationModel> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_post_notification);
        Bundle extras = getIntent().getExtras();
        Intent intentIncoming = getIntent();
        if (extras != null){
            currentUser = intentIncoming.getParcelableExtra("currentUser");
            setToolbar("Kulüp Seçiniz");
            setView(currentUser);
            getNotificaitonSetting(currentUser);

        }else{
            finish();
        }
    }

    public void getNotificaitonSetting(CurrentUser currentUser){
        ///İSTE/clup/name/Atatürkçü Düşünce Öğrenci Topluluğu
        CollectionReference ref = FirebaseFirestore.getInstance().collection(currentUser.getShort_school())
                .document("clup").collection("name");
        ref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().isEmpty()){

                    }else{
                        for (DocumentSnapshot item : task.getResult().getDocuments()){
                            list.add(item.toObject(SchoolPostNotificationModel.class));
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });
    }

    private void setView(CurrentUser currentUser)
    {
        list = new ArrayList<>();
        lessonList = (RecyclerView)findViewById(R.id.lessonList);
        adapter = new SchoolPostNotificaitonAdapter(currentUser , SchoolPostNotificationActivity.this,list);
        lessonList.setHasFixedSize(true);
        lessonList.setLayoutManager(new LinearLayoutManager(SchoolPostNotificationActivity.this));
        lessonList.setAdapter(adapter);


    }
    private void setToolbar(String  titleText)
    {
        toolbar = findViewById(R.id.toolbar);

        title = toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
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
                Helper.shared().back(SchoolPostNotificationActivity.this);
            }
        });
    }
}