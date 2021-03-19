package com.vaye.app.LoginRegister;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.kongzue.dialog.v3.TipDialog;
import com.kongzue.dialog.v3.WaitDialog;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.vaye.app.Controller.Profile.EditProfileActivity;
import com.vaye.app.Interfaces.TaskUserHandler;
import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.Model.SchoolModel;
import com.vaye.app.Model.TaskUser;
import com.vaye.app.R;
import com.vaye.app.Services.StudentSignUpService;
import com.vaye.app.Services.UserService;
import com.vaye.app.Util.Helper;

import java.util.HashMap;
import java.util.Map;

public class SetStudentNumber extends AppCompatActivity {
    Toolbar toolbar;
    TaskUser taskUser;
    TextView toolbarTitle;
    String _char = "@";
    Button register;
    MaterialEditText number , name ,username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_student_number);
        Bundle extras = getIntent().getExtras();
        Intent intentIncoming = getIntent();
        if (extras !=null){
            taskUser = intentIncoming.getParcelableExtra("taskUser");
        }

        name = (MaterialEditText)findViewById(R.id.name);
        username = (MaterialEditText)findViewById(R.id.username);
        number = (MaterialEditText)findViewById(R.id.number);

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
        toolbarTitle.setText("Kaydını Tamamla");

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

    }

    private void setTaskUser(){
        WaitDialog.show(SetStudentNumber.this,"Lütfen Bekleyin");
        String _name = name.getText().toString();
        String _number = number.getText().toString();
        String _username =  _char+username.getText().toString().replace("@","");

        if (_name.isEmpty()){
            WaitDialog.dismiss();
            name.setError("Lütfen Adınızı ve Soyadınız Giriniz");
            name.requestFocus();
            return;
        }
        if (_name.length()<6){
            WaitDialog.dismiss();
            name.setError("Lütfen Adınızı ve Soyadınız Giriniz");
            name.requestFocus();
            return;
        }
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
        if (_number.isEmpty()){
            WaitDialog.dismiss();
            number.setError("Lütfen Okul Numaranızı Giriniz");
            number.requestFocus();
            return;
        }
        if (_number.length() != 9){
            WaitDialog.dismiss();
            number.setError("Lütfen Geçerli Bir Numara Giriniz");
            number.requestFocus();
            return;
        }

        StudentSignUpService.shared().checkIsUserNameValid(_username, new TrueFalse<Boolean>() {
            @Override
            public void callBack(Boolean _value) {
                if (_value){
                    StudentSignUpService.shared().checkNumberIsExist(SetStudentNumber.this, new SchoolModel(taskUser.getSchoolName(), taskUser.getShort_school(), null), _number, new TrueFalse<Boolean>() {
                        @Override
                        public void callBack(Boolean _value) {
                            if (_value){
                                WaitDialog.dismiss();
                                number.setError("Bu Numaraya Kayıtlı Bir Kullanıcı Bulunuyor");
                                number.requestFocus();
                                return;
                            }else{
                                DocumentReference ref = FirebaseFirestore.getInstance().collection("task-user")
                                .document(taskUser.getUid());
                                Map<String,Object> map = new HashMap<>();
                                map.put("username",_username);
                                map.put("number",_number);
                                map.put("name",_name);
                                ref.set(map, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            UserService.shared().getTaskUser(taskUser.getUid(), new TaskUserHandler() {
                                                @Override
                                                public void onCallback(TaskUser user) {
                                                    Intent i = new Intent(SetStudentNumber.this,SetFakulteActivity.class);
                                                    i.putExtra("taskUser",taskUser);
                                                    startActivity(i);
                                                    Helper.shared().go(SetStudentNumber.this);
                                                    WaitDialog.dismiss();
                                                }
                                            });
                                        }else{
                                            WaitDialog.dismiss();
                                            TipDialog.show(SetStudentNumber.this,"Hata Oluştu\nLütfen Tekrar Deneyiniz", TipDialog.TYPE.ERROR);
                                            TipDialog.dismiss(1000);
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        WaitDialog.dismiss();
                                        TipDialog.show(SetStudentNumber.this,"Lütfen İnternet Bağlantınızı Kontrol Ediniz", TipDialog.TYPE.ERROR);

                                        TipDialog.dismiss(1000);

                                        return;
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}