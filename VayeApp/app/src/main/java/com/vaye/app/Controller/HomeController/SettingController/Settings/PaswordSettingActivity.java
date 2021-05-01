package com.vaye.app.Controller.HomeController.SettingController.Settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kongzue.dialog.v3.TipDialog;
import com.kongzue.dialog.v3.WaitDialog;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.vaye.app.Controller.HomeController.SettingController.SettingActivity;
import com.vaye.app.R;
import com.vaye.app.Util.Helper;

public class PaswordSettingActivity extends AppCompatActivity {
    FirebaseUser user;
    MaterialEditText oldPass,newPass,newPassAgain;
    TextView toolbarTitle;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pasword_setting);
        setToolbar();
        user= FirebaseAuth.getInstance().getCurrentUser();
        oldPass=(MaterialEditText)findViewById(R.id.currentPass);
        newPass=(MaterialEditText)findViewById(R.id.newPassword);
        newPassAgain=(MaterialEditText)findViewById(R.id.newPasswordAgain);
    }
    private void setToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setSupportActionBar(toolbar);

        toolbar.setTitle("");
        toolbar.setSubtitle("");
        toolbarTitle = (TextView)toolbar.findViewById(R.id.toolbar_title);
        toolbarTitle.setText("Şifreni Değiştir");
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Helper.shared().back(PaswordSettingActivity.this);
            }
        });
    }
    private void changePasswordFunk(String _oldPass, final String _newPass, String _newPassAgain)
    {
        WaitDialog.show(this,"");
        if (_oldPass.isEmpty())
        {
            oldPass.setError("Lütfen Şifrenizi Giriniz ");
            oldPass.requestFocus();
            WaitDialog.dismiss();
            return;
        }
        if (_newPass.isEmpty())
        {
            newPass.setError("Lütfen Yeni Şifrenizi Giriniz ");
            newPass.requestFocus();
            WaitDialog.dismiss();

            return;
        }
        if (_newPass.length()< 5){
            newPass.setError("Lütfen Daha Güçlü Bir Şifre Giriniz ");
            newPass.requestFocus();
            WaitDialog.dismiss();

            return;
        }
        if (_newPassAgain.isEmpty())
        {
            newPassAgain.setError("Şifrenizi Tekrar Giriniz");
            newPassAgain.requestFocus();
            WaitDialog.dismiss();

            return;
        }
        if (!_newPass.equals(_newPassAgain))
        {
            newPass.setError("Şifreleriniz Aynı Değil");
            newPassAgain.setError("Şifreleriniz Aynı Değil");
            newPass.requestFocus();
            newPassAgain.requestFocus();
            WaitDialog.dismiss();

            return;
        }
        user=FirebaseAuth.getInstance().getCurrentUser();
        final String email = user.getEmail();
        AuthCredential credential = EmailAuthProvider.getCredential(email,_oldPass);
        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if (task.isSuccessful())
                {
                    user.updatePassword(_newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if (task.isSuccessful())
                            {
                                WaitDialog.dismiss();
                                TipDialog.show(PaswordSettingActivity.this,"Şifreniz Değiştirildi", TipDialog.TYPE.SUCCESS);
                                TipDialog.dismiss(1000);
                            }else {
                                WaitDialog.dismiss();
                                newPass.setError("Şifreniz Doğru Değil");
                                newPass.requestFocus();
                                newPassAgain.setError("Şifreniz Doğru Değil");
                                newPassAgain.requestFocus();
                            }


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            System.out.println("err->"+e.getMessage());
                        }
                    });;
                }
                else
                {
                    WaitDialog.dismiss();
                    oldPass.setError("Şifreniz Doğru Değil");
                    oldPass.requestFocus();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("err->"+e.getMessage());
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        Helper.shared().back(PaswordSettingActivity.this);
    }
    public void resetPassword(View view) {
        changePasswordFunk(oldPass.getText().toString(),newPass.getText().toString(),newPassAgain.getText().toString());
    }
}