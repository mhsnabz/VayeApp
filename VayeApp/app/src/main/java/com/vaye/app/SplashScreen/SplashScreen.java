package com.vaye.app.SplashScreen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.vaye.app.Controller.HomeController.HomeActivity;
import com.vaye.app.Interfaces.CurrentUserService;
import com.vaye.app.Interfaces.TaskUserHandler;
import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.LoginRegister.LoginActivity;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.TaskUser;
import com.vaye.app.R;
import com.vaye.app.Services.UserService;

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
                                        }
                                    });
                                }else{

                                }
                            }
                        });
                    }else if (task.getResult().getString("priority").equals("teacher")){
                        UserService.shared().getTaskUser(uid, new TaskUserHandler() {
                            @Override
                            public void onCallback(TaskUser user) {
                                //TODO: student registiration
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