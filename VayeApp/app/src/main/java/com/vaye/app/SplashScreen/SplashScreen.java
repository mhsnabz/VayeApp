package com.vaye.app.SplashScreen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kongzue.dialog.interfaces.OnDismissListener;
import com.kongzue.dialog.v3.CustomDialog;
import com.vaye.app.Application.OnClearFromRecentService;
import com.vaye.app.Controller.HomeController.HomeActivity;
import com.vaye.app.Controller.NotificationService.PushNotificationType;
import com.vaye.app.Interfaces.CurrentUserService;
import com.vaye.app.Interfaces.TaskUserHandler;
import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.LoginRegister.LoginActivity;
import com.vaye.app.LoginRegister.SetStudentNumber;
import com.vaye.app.LoginRegister.SetTeacherActivity;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.TaskUser;
import com.vaye.app.R;
import com.vaye.app.Services.UserService;

import java.util.ArrayList;

import static com.vaye.app.Application.VayeApp.SHARED_PREFS;

public class SplashScreen extends AppCompatActivity {
    ImageView logo;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    String currentUserUid = auth.getUid();
    CurrentUser currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        startService(new Intent(SplashScreen.this, OnClearFromRecentService.class));
        logo = (ImageView)findViewById(R.id.logo);
        Animation animation = AnimationUtils.loadAnimation(this,R.anim.mytransistion);
        logo.startAnimation(animation);
        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    sleep(2000);
                }catch (InterruptedException ex){
                    ex.printStackTrace();
                }finally {
                    if (currentUserUid != null){

                        DocumentReference reference = FirebaseFirestore.getInstance().collection("status")
                                .document(currentUserUid);
                                reference.get().addOnCompleteListener(SplashScreen.this, new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()){
                                            if (task.getResult().exists()){
                                                if (task.getResult().getBoolean("status")){
                                                    UserService.shared().getCurrentUser(currentUserUid, new CurrentUserService() {
                                                        @Override
                                                        public void onCallback(CurrentUser user) {
                                                            SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                                                            SharedPreferences.Editor editor = sharedPreferences.edit();

                                                            editor.putBoolean(PushNotificationType.lessonNotices,currentUser.getLessonNotices());
                                                            editor.putBoolean(PushNotificationType.like,currentUser.getLike());
                                                            editor.putBoolean(PushNotificationType.follow,currentUser.getFollow());
                                                            editor.putBoolean(PushNotificationType.comment,currentUser.getComment());
                                                            editor.apply();
                                                            Log.d("CurrentUserName", "onCallback: " + user.getName());
                                                            Intent i = new Intent(SplashScreen.this , HomeActivity.class);
                                                            i.putExtra("currentUser",user);
                                                            startActivity(i);
                                                            finish();
                                                        }
                                                    });
                                                }else{
                                                    checkIsCompelte(currentUserUid);
                                                }
                                            }else{
                                                Intent i = new Intent(SplashScreen.this , LoginActivity.class);
                                                startActivity(i);
                                                finish();
                                            }
                                        }
                                    }
                                }).addOnFailureListener(SplashScreen.this, new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });



                    }else{
                        Intent i = new Intent(SplashScreen.this , LoginActivity.class);
                        startActivity(i);
                        finish();
                    }


                }
            }
        };
        thread.start();
    }
    public void setNotificaitonPref(ArrayList<String> type, Boolean val ) {


    }
    private void checkIsCompelte(String uid){
        DocumentReference ref = FirebaseFirestore.getInstance().collection("priority")
                .document(uid);
        ref.get().addOnCompleteListener(SplashScreen.this, new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().getString("priority").equals("student")){
                        UserService.shared().checkUserIsComplete(uid, new TrueFalse() {
                            @Override
                            public void callBack(Boolean _value) {
                                if (_value){
                                    UserService.shared().getTaskUser(uid, new TaskUserHandler() {
                                        @Override
                                        public void onCallback(TaskUser user) {
                                            //TODO: student registiration
                                            if (user!=null){
                                                Intent i = new Intent(SplashScreen.this, SetStudentNumber.class);
                                                i.putExtra("taskUser",user);
                                                startActivity(i);
                                                finish();
                                            }else{

                                            }
                                        }
                                    });
                                }else{

                                }
                            }
                        });
                    }else if (task.getResult().getString("priority").equals("teacher")){
                        UserService.shared().getTaskTeacher(uid, new TaskUserHandler() {
                            @Override
                            public void onCallback(TaskUser user) {

                                DocumentReference ref = FirebaseFirestore.getInstance().collection("task-teacher")
                                        .document(uid);
                                ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()){
                                            if (task.getResult().getBoolean("isValid")){
                                                Intent i = new Intent(SplashScreen.this, SetTeacherActivity.class);
                                                i.putExtra("taskUser",user);
                                                startActivity(i);
                                                finish();
                                            }else{
                                                CustomDialog.show(SplashScreen.this, R.layout.auth_dialog, new CustomDialog.OnBindView() {
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
                                                        Intent i = new Intent(SplashScreen.this,LoginActivity.class);
                                                        startActivity(i);
                                                        finish();
                                                    }
                                                });

                                            }
                                        }
                                    }
                                });




                            }
                        });
                    }
                }
            }
        }).addOnFailureListener(SplashScreen.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
}