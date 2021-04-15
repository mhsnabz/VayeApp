package com.vaye.app.Controller.HomeController.SetLessons;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.kongzue.dialog.v3.WaitDialog;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.vaye.app.Controller.HomeController.TeacherNewPost.UserList;
import com.vaye.app.Controller.HomeController.TeacherNewPost.UserListAdapter;
import com.vaye.app.Interfaces.OtherUserService;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.LessonFallowerUser;
import com.vaye.app.Model.OtherUser;
import com.vaye.app.R;
import com.vaye.app.Services.UserService;
import com.vaye.app.Util.Helper;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AboutLessonActivity extends AppCompatActivity {
    String TAG = "AboutLessonActivity";
    Toolbar toolbar;
    TextView title;

    CurrentUser currentUser;
    RecyclerView list;
    LessonFollowerAdapter adapter;
    ArrayList<LessonFallowerUser> lessonFollower = new ArrayList<>();
    String lessonName;
    RelativeLayout mainHeader;
    TextView noTeacher, name , username;
    CircleImageView profileImage;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_lesson);
        Bundle extras = getIntent().getExtras();
        Intent intentIncoming = getIntent();
        if (extras != null){
            currentUser = intentIncoming.getParcelableExtra("currentUser");

            lessonName = intentIncoming.getStringExtra("lessonName");
            setToolbar(intentIncoming.getStringExtra("lessonName"));
            setView(currentUser);
            getList(lessonName);
        }else {
            finish();
        }
    }

    private void getList( String lessonName){
        CollectionReference ref = FirebaseFirestore.getInstance().collection(currentUser.getShort_school())
                .document("lesson")
                .collection(currentUser.getBolum()).document(lessonName).collection("fallowers");
            ref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()){
                        if (task.getResult().getDocuments().isEmpty()){
                            WaitDialog.dismiss();
                        }else {
                            for (DocumentSnapshot user : task.getResult().getDocuments()){
                                lessonFollower.add(user.toObject(LessonFallowerUser.class));
                                adapter.notifyDataSetChanged();
                            }
                            WaitDialog.dismiss();
                        }
                    }
                }
            });

    }

    private void setView(CurrentUser currentUser) {
        mainHeader = (RelativeLayout)findViewById(R.id.mainHeader);
        noTeacher =(TextView)findViewById(R.id.noTeacher);
        profileImage = (CircleImageView)findViewById(R.id.profileImage);
        progressBar = (ProgressBar)findViewById(R.id.progress);
        name = (TextView)findViewById(R.id.name);
        username = (TextView)findViewById(R.id.username);
        list = (RecyclerView)findViewById(R.id.studentList);
        list.setHasFixedSize(true);
        list.setLayoutManager(new LinearLayoutManager(this));
        adapter = new LessonFollowerAdapter(lessonFollower,this,currentUser);
        list.setAdapter(adapter);
        DocumentReference ref = FirebaseFirestore.getInstance().collection(currentUser.getShort_school())
                .document("lesson")
                .collection(currentUser.getBolum()).document(lessonName);
        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().getString("teacherId").equals("empty")){
                        mainHeader.setVisibility(View.GONE);
                        noTeacher.setVisibility(View.VISIBLE);
                    }else{
                        mainHeader.setVisibility(View.VISIBLE);
                        UserService.shared().getOtherUserById(task.getResult().getString("teacherId"), new OtherUserService() {
                            @Override
                            public void callback(OtherUser user) {
                                if (user!=null){
                                    if (user.getThumb_image()!=null && !user.getThumb_image().isEmpty()){
                                        Picasso.get().load(user.getThumb_image())
                                                .centerCrop().resize(256,256)
                                                .into(profileImage, new Callback() {
                                                    @Override
                                                    public void onSuccess() {

                                                        progressBar.setVisibility(View.GONE);
                                                    }

                                                    @Override
                                                    public void onError(Exception e) {
                                                        profileImage.setImageResource(android.R.color.darker_gray);
                                                        progressBar.setVisibility(View.GONE);
                                                    }
                                                });
                                    }else{
                                        profileImage.setImageResource(android.R.color.darker_gray);
                                        progressBar.setVisibility(View.GONE);
                                    }
                                    username.setText(user.getUsername());
                                    name.setText(user.getName());
                                }
                            }
                        });
                    }
                }
            }
        });
    }
    private void setToolbar(String lessonName)
    {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        title = toolbar.findViewById(R.id.toolbar_title);

        toolbar.setTitle("");
        toolbar.setSubtitle("");
        title.setText(lessonName);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Helper.shared().back(AboutLessonActivity.this);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Helper.shared().back(AboutLessonActivity.this);
    }
}