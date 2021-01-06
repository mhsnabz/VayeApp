package com.vaye.app.SplashScreen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.vaye.app.Interfaces.FirestoreService;
import com.vaye.app.LoginRegister.LoginActivity;
import com.vaye.app.MainActivity;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.R;
import com.vaye.app.Services.UserService;

public class SplashScreen extends AppCompatActivity {
    ImageView logo;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    String currentUser = auth.getUid();
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

                    if (currentUser != null){

                        UserService.shared().getCurrentUser(currentUser, new FirestoreService() {
                            @Override
                            public void onCallback(CurrentUser user) {
                                Log.d("CurrentUserName", "onCallback: " + user.getName());
                                Intent i = new Intent(SplashScreen.this , MainActivity.class);
                                i.putExtra("currentUser",user);
                                startActivity(i);
                                finish();
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
}