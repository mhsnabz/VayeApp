package com.vaye.app.LoginRegister;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.kongzue.dialog.v3.WaitDialog;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.vaye.app.Interfaces.TaskUserHandler;
import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.Model.TaskUser;
import com.vaye.app.R;
import com.vaye.app.Services.StudentSignUpService;
import com.vaye.app.Services.UserService;
import com.vaye.app.Util.Helper;

import java.util.HashMap;
import java.util.Map;

public class SetTeacherActivity extends AppCompatActivity {
    String TAG = "SetStudentNumber";
    Toolbar toolbar;
    TaskUser taskUser;
    TextView toolbarTitle , name , schoolName;
    String _char = "@";
    Button register;
    MaterialEditText username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_teacher);
        Bundle extras = getIntent().getExtras();
        Intent intentIncoming = getIntent();
        if (extras !=null){
            taskUser = intentIncoming.getParcelableExtra("taskUser");
            name = (TextView)findViewById(R.id.name);
            schoolName = (TextView)findViewById(R.id.schoolName);
            name.setText(taskUser.getUnvan() + taskUser.getName());
            schoolName.setText(taskUser.getSchoolName());
        }
        username = (MaterialEditText)findViewById(R.id.username);

        register = (Button)findViewById(R.id.signUp);
        setToolbar();
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTaskUser();
            }
        });
    }
    private void setToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setSupportActionBar(toolbar);

        toolbar.setTitle("");
        toolbar.setSubtitle("");
        toolbarTitle = (TextView)toolbar.findViewById(R.id.toolbar_title);
        toolbarTitle.setText("Kaydınızı Tamamlayın");

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

    }

    private void setTaskUser(){
        WaitDialog.show(SetTeacherActivity.this,"Lütfen Bekleyin");

        String _username =  _char+username.getText().toString().replace("@","");


        if (_username.isEmpty()){
            WaitDialog.dismiss();
            username.setError("Lütfen Kullanızı Adı Belirleyin");
            username.requestFocus();
            return;
        }
        if (_username.length()<4 ){
            WaitDialog.dismiss();
            username.setError("Kullanıcı Adınız En Az 4 Karakterden Oluşmalıdır");
            username.requestFocus();
            return;
        }

        StudentSignUpService.shared().checkIsUserNameValid(_username, new TrueFalse<Boolean>() {
            @Override
            public void callBack(Boolean _value) {
                if (_value){
                    DocumentReference ref = FirebaseFirestore.getInstance().collection("task-teacher")
                            .document(taskUser.getUid());
                    Map<String,Object> map = new HashMap<>();
                    map.put("username",_username);
                   ref.set(map , SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                       @Override
                       public void onComplete(@NonNull Task<Void> task) {
                           if (task.isSuccessful()){
                               UserService.shared().getTaskTeacher(taskUser.getUid(), new TaskUserHandler() {
                                   @Override
                                   public void onCallback(TaskUser user) {
                                       if (user!=null){
                                           Intent i = new Intent(SetTeacherActivity.this,SetFakulteActivity.class);
                                           taskUser.setUsername(_username);
                                           Log.d(TAG, "onCallback: " + user.getUsername());
                                           Log.d(TAG, "onCallback: " + user.getName());
                                           Log.d(TAG, "onCallback: " + user.getNumber());
                                           i.putExtra("taskUser",user);
                                           startActivity(i);
                                           Helper.shared().go(SetTeacherActivity.this);
                                       }
                                   }
                               });
                           }
                       }
                   });
                }else{
                    WaitDialog.dismiss();
                    username.setError("Bu Kullanıcı Adı Zaten Kullanılıyor");
                    username.requestFocus();
                    return;
                }
            }
        });
    }
}