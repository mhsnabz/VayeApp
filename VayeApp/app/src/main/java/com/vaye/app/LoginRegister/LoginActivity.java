package com.vaye.app.LoginRegister;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kongzue.dialog.interfaces.OnDismissListener;
import com.kongzue.dialog.v3.CustomDialog;
import com.kongzue.dialog.v3.TipDialog;
import com.kongzue.dialog.v3.WaitDialog;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.vaye.app.Controller.HomeController.SettingController.Settings.GizlilikActivity;
import com.vaye.app.Controller.HomeController.SettingController.Settings.HizmetActivity;
import com.vaye.app.Interfaces.CurrentUserService;
import com.vaye.app.Interfaces.TaskUserHandler;
import com.vaye.app.Interfaces.TrueFalse;
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

                                    UserService.shared().checkEmailVerfied(task.getResult().getUser(), new TrueFalse<Boolean>() {
                                        @Override
                                        public void callBack(Boolean _value) {
                                            if (_value){
                                                Intent i = new Intent(LoginActivity.this , SplashScreen.class);
                                                startActivity(i);
                                                WaitDialog.dismiss();
                                                finish();
                                            }else{
                                                showDialog(task.getResult().getUser());
                                            }
                                        }
                                    });

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
                                                            UserService.shared().getTaskTeacher(task.getResult().getUser().getUid(), new TaskUserHandler() {
                                                                @Override
                                                                public void onCallback(TaskUser user) {
                                                                   DocumentReference ref = FirebaseFirestore.getInstance().collection("task-teacher")
                                                                           .document(task.getResult().getUser().getUid());
                                                                   ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                       @Override
                                                                       public void onComplete(@NonNull Task<DocumentSnapshot> taskk) {
                                                                           if (task.isSuccessful()){

                                                                               if (taskk.getResult().getBoolean("isValid")){

                                                                                   UserService.shared().checkEmailVerfied(task.getResult().getUser(), new TrueFalse<Boolean>() {
                                                                                       @Override
                                                                                       public void callBack(Boolean _value) {
                                                                                           if (_value){
                                                                                               //TODO: is verifty email
                                                                                               Intent i = new Intent(LoginActivity.this , SplashScreen.class);
                                                                                               startActivity(i);
                                                                                               WaitDialog.dismiss();
                                                                                               finish();
                                                                                           }else{
                                                                                               showDialog(task.getResult().getUser());
                                                                                           }
                                                                                       }
                                                                                   });
                                                                               }else{
                                                                                   CustomDialog.show(LoginActivity.this, R.layout.auth_dialog, new CustomDialog.OnBindView() {
                                                                                       @Override
                                                                                       public void onBind(CustomDialog dialog, View v) {
                                                                                           TextView headerTitle = (TextView)v.findViewById(R.id.headerTitle);
                                                                                           TextView mainText = (TextView)v.findViewById(R.id.mainText);
                                                                                           Button okey = (Button)v.findViewById(R.id.okey);

                                                                                           headerTitle.setText("Sayın "+ user.getUnvan() + user.getName());


                                                                                           SpannableStringBuilder builder = new SpannableStringBuilder();

                                                                                           String text1 = "Üniversitenizin kurumsal web sitesinden ";
                                                                                           SpannableString redSpannable= new SpannableString(text1);
                                                                                           redSpannable.setSpan(new ForegroundColorSpan(Color.GRAY), 0, text1.length(), 0);
                                                                                           builder.append(redSpannable);

                                                                                           String textName = "'"+user.getUnvan() + user.getName() +"'";
                                                                                           SpannableString textNameSpannable= new SpannableString(textName);
                                                                                           textNameSpannable.setSpan(new ForegroundColorSpan(Color.BLUE), 0, textName.length(), 0);
                                                                                           builder.append(textNameSpannable);


                                                                                           String text3 = " araştıracağız. Size ";
                                                                                           SpannableString text3Spanable= new SpannableString(text3);
                                                                                           text3Spanable.setSpan(new ForegroundColorSpan(Color.GRAY), 0, text3.length(), 0);
                                                                                           builder.append(text3Spanable);

                                                                                           String text4 =  "destek@vaye.app" ;
                                                                                           SpannableString tex4Span= new SpannableString(text4);
                                                                                           tex4Span.setSpan(new ForegroundColorSpan(Color.BLUE), 0, text4.length(), 0);
                                                                                           builder.append(tex4Span);



                                                                                           String text5 = "'den en geç 24 saat içinde onay mesajı göndereceğiz. ";
                                                                                           SpannableString text5Spanable= new SpannableString(text5);
                                                                                           text5Spanable.setSpan(new ForegroundColorSpan(Color.GRAY), 0, text5.length(), 0);
                                                                                           builder.append(text5Spanable);


                                                                                           String text6 = "Bize ";
                                                                                           SpannableString text6Spanable= new SpannableString(text6);
                                                                                           text6Spanable.setSpan(new ForegroundColorSpan(Color.GRAY), 0, text6.length(), 0);
                                                                                           builder.append(text6Spanable);


                                                                                           String text7 =  "destek@vaye.app" ;
                                                                                           SpannableString tex7Span= new SpannableString(text7);
                                                                                           tex7Span.setSpan(new ForegroundColorSpan(Color.BLUE), 0, text7.length(), 0);
                                                                                           builder.append(tex7Span);


                                                                                           String text8 = "'den ulaşabilirsiniz";
                                                                                           SpannableString text8Spanable= new SpannableString(text8);
                                                                                           text8Spanable.setSpan(new ForegroundColorSpan(Color.GRAY), 0, text8.length(), 0);
                                                                                           builder.append(text8Spanable);


                                                                                           mainText.setText(builder,TextView.BufferType.SPANNABLE);


                                                                                           okey.setOnClickListener(new View.OnClickListener() {
                                                                                               @Override
                                                                                               public void onClick(View view) {
                                                                                                   dialog.doDismiss();

                                                                                               }
                                                                                           });
                                                                                       }
                                                                                   }).setOnDismissListener(new OnDismissListener() {
                                                                                       @Override
                                                                                       public void onDismiss() {
                                                                                           FirebaseAuth.getInstance().signOut();
                                                                                            WaitDialog.dismiss();
                                                                                       }
                                                                                   });
                                                                               }
                                                                           }
                                                                       }
                                                                   });



                                                                }
                                                            });
                                                        }else if (_task.getResult().getString("priority").equals("student")){
                                                            UserService.shared().getTaskUser(task.getResult().getUser().getUid(), new TaskUserHandler() {
                                                                @Override
                                                                public void onCallback(TaskUser user) {
                                                                    UserService.shared().checkEmailVerfied(task.getResult().getUser(), new TrueFalse<Boolean>() {
                                                                        @Override
                                                                        public void callBack(Boolean _value) {
                                                                            if (_value){
                                                                                Intent i = new Intent(LoginActivity.this , SplashScreen.class);
                                                                                startActivity(i);
                                                                                WaitDialog.dismiss();
                                                                                finish();
                                                                            }else{
                                                                                showDialog(task.getResult().getUser());
                                                                            }
                                                                        }
                                                                    });

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


    public void showDialog(FirebaseUser user){
        FirebaseAuth.getInstance().signOut();
        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(LoginActivity.this)
                .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                .setTitle(user.getEmail()+ " Adresini Doğrulayamadık")
                .setMessage("Tekrar Doğrulama Bağlantısı Göndermemizi İstermisiniz?")
                .addButton("Evet", -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {
                    WaitDialog.show(LoginActivity.this,"Gönderiliyor");
                    user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                WaitDialog.dismiss();
                                TipDialog.show(LoginActivity.this,"Doğrulama Linki Gönderildi", TipDialog.TYPE.SUCCESS);
                                TipDialog.dismiss(1500);
                                dialog.dismiss();
                            }else {
                                WaitDialog.dismiss();
                                dialog.dismiss();
                            }
                        }
                    });


                    dialog.dismiss();

                }).addButton("Vazgeç", -1, -1, CFAlertDialog.CFAlertActionStyle.DEFAULT, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {
                    WaitDialog.dismiss();
                    dialog.dismiss();

                });
        builder.show();
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