package com.vaye.app.LoginRegister;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kongzue.dialog.v3.WaitDialog;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.vaye.app.Controller.HomeController.SettingController.Settings.GizlilikActivity;
import com.vaye.app.Controller.HomeController.SettingController.Settings.HizmetActivity;
import com.vaye.app.Interfaces.CurrentUserService;
import com.vaye.app.Interfaces.TaskUserHandler;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.TaskUser;
import com.vaye.app.R;
import com.vaye.app.Services.UserService;
import com.vaye.app.SplashScreen.SplashScreen;
import com.vaye.app.Util.Helper;

public class LoginActivity extends AppCompatActivity {
    Button login ;
    RelativeLayout register;

    MaterialEditText email , pass;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login = (Button) findViewById(R.id.login);
        register = (RelativeLayout)findViewById(R.id.register);
        email = (MaterialEditText)findViewById(R.id.email);
        pass = (MaterialEditText)findViewById(R.id.password);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                setLogin();
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i =new Intent(LoginActivity.this,ChooseSchoolActivity.class);
                startActivity(i);
                Helper.shared().go(LoginActivity.this);
            }
        });

    }

    private void setLogin(){
        WaitDialog.show(LoginActivity.this , "Lütfen Bekleyin");
        String _email = email.getText().toString();
        String _pass = pass.getText().toString();
        if (_email.isEmpty())
        {
            WaitDialog.dismiss();
            email.setError("Lütfen E-posta Adresinizi Giriniz");
            email.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(_email).matches())
        {
            WaitDialog.dismiss();
            email.setError("Geçerli Bir Eposta Adresi Giriniz");
            email.requestFocus();
            return;
        }
        if (_pass.isEmpty())
        {
            WaitDialog.dismiss();
            pass.setError("Lütfen Şifrenizi Giriniz");
            pass.requestFocus();
            return;
        }

        auth.signInWithEmailAndPassword(_email,_pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    if (task.getResult().getUser() != null){
                        UserService.shared().getCurrentUser(task.getResult().getUser().getUid(), new CurrentUserService() {
                            @Override
                            public void onCallback(CurrentUser user) {
                                if (user!=null){
                                    Intent i = new Intent(LoginActivity.this , SplashScreen.class);
                                    startActivity(i);
                                    WaitDialog.dismiss();
                                    finish();
                                }else{
                                    DocumentReference getPriority = FirebaseFirestore.getInstance().collection("priority")
                                            .document(task.getResult().getUser().getUid());
                                    getPriority.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> _task) {
                                            if (task.isSuccessful()){
                                                if (_task.getResult().exists()){
                                                    if (_task.getResult().getString("priority")!=null){
                                                        if (_task.getResult().getString("priority").equals("teacher")){

                                                        }else if (_task.getResult().getString("priority").equals("student")){
                                                            UserService.shared().getTaskUser(task.getResult().getUser().getUid(), new TaskUserHandler() {
                                                                @Override
                                                                public void onCallback(TaskUser user) {
                                                                    Intent i = new Intent(LoginActivity.this , SplashScreen.class);
                                                                    startActivity(i);
                                                                    WaitDialog.dismiss();
                                                                    finish();
                                                                }
                                                            });
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }


                }
            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("errr",e.getMessage());
                if (e.getMessage() == "There is no user record corresponding to this identifier. The user may have been deleted."){
                    email.setError("Sisteme Kayıtlı Böyle Bir E-posta Adresi Yok");
                    email.requestFocus();
                    WaitDialog.dismiss();
                    return;
                }
                else if (e.getMessage() == "The password is invalid or the user does not have a password."){
                    pass.setError("Şifreniz Yanlış!");
                    pass.requestFocus();
                    WaitDialog.dismiss();
                    return;
                }


            }
        });
    }

    public void gizlilik(View view) {
        Intent i = new Intent(LoginActivity.this , GizlilikActivity.class);
        startActivity(i);
        Helper.shared().go(LoginActivity.this);
    }

    public void hizmet_kosullari(View view) {
        Intent i = new Intent(LoginActivity.this , HizmetActivity.class);
        startActivity(i);
        Helper.shared().go(LoginActivity.this);
    }
}