package com.vaye.app.Controller.Profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.os.Trace;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.auth.User;
import com.kongzue.dialog.v3.WaitDialog;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.vaye.app.Controller.HomeController.SettingController.SettingActivity;
import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.R;
import com.vaye.app.Services.UserService;
import com.vaye.app.Util.Helper;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditProfileActivity extends AppCompatActivity {
    TextView toolbarTitle;
    Toolbar toolbar;
    Button rigthBarButton;
    CurrentUser currentUser;
    MaterialEditText insta , linkedin , user_name , twitter , github;
    String _char = "@";
    String oldusername = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Bundle extras = getIntent().getExtras();
        Intent intentIncoming = getIntent();
        if (extras != null){
            currentUser = intentIncoming.getParcelableExtra("currentUser");
            setToolbar();
            configureUI(currentUser);
            oldusername = currentUser.getUsername();

        }

    }

    private void configureUI(CurrentUser currentUser) {
        insta = (MaterialEditText)findViewById(R.id.insta);
        user_name = (MaterialEditText)findViewById(R.id.username);
        twitter = (MaterialEditText)findViewById(R.id.twitter);
        linkedin = (MaterialEditText)findViewById(R.id.linkedin);
        github = (MaterialEditText)findViewById(R.id.github);
        github.setText(currentUser.getGithub());
        insta.setText(currentUser.getInstagram());
        user_name.setText(currentUser.getUsername());
        twitter.setText(currentUser.getTwitter());
        linkedin.setText(currentUser.getLinkedin());

    }

    private void setToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setSupportActionBar(toolbar);

        toolbar.setTitle("");
        toolbar.setSubtitle("");
        toolbarTitle = (TextView)toolbar.findViewById(R.id.toolbar_title);
        toolbarTitle.setText("Profilini Düzenle");
        rigthBarButton = (Button)toolbar.findViewById(R.id.rigthButton);
        rigthBarButton.setText("Kaydet");
        rigthBarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUsernames(currentUser);
                finish();
                WaitDialog.dismiss();
                Helper.shared().back(EditProfileActivity.this);

            }
        });
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Helper.shared().back(EditProfileActivity.this);
            }
        });
    }


    private void saveUsernames(CurrentUser currentUser){
        String _username = user_name.getText().toString();
        String _twitter = twitter.getText().toString();
        String _linkedind = linkedin.getText().toString();
        String _instagram = insta.getText().toString();
        String _github = github.getText().toString();

       if (!_instagram.isEmpty()) {
            String __insta = _instagram.replace("@","");
            setInstagram(currentUser , _char+__insta);
        }else {
            setInstagram(currentUser , null);
        }

        if (!_twitter.isEmpty()) {
            String __twitter = _twitter.replace("@","");
            setTwitter(currentUser , _char+__twitter);
        }else {
            setTwitter(currentUser , null);
        }
        if (!_github.isEmpty()) {
            String __github = _github.replace("@","");
            setGithub(currentUser , _char+__github);
        }else {
            setGithub(currentUser , null);
        }

        if (!_linkedind.isEmpty()){

            setLinkedin(currentUser , null);
        }else {
            setLinkedin(currentUser , _linkedind);
        }
        if (_username.isEmpty()){
            setUser_name(currentUser , currentUser.getUsername());
        }else{
            setUser_name(currentUser , _char+_username.replace("@",""));
        }


    }

    private void setInstagram(CurrentUser currentUser,String _insta){
        if (_insta == null){
            DocumentReference ref = FirebaseFirestore.getInstance().collection("user")
                    .document(currentUser.getUid());
            Map<String , String> map = new HashMap<>();
            map.put("instagram","");
            ref.set(map , SetOptions.merge());
            currentUser.setInstagram("");
        }else{
            if (currentUser.getInstagram().equals(_insta)){
                return;
            }else{

                DocumentReference ref = FirebaseFirestore.getInstance().collection("user")
                        .document(currentUser.getUid());
                Map<String , String> map = new HashMap<>();
                map.put("instagram",_insta);
                ref.set(map , SetOptions.merge());
                currentUser.setInstagram(_insta);
            }
        }

    }
    private void setTwitter(CurrentUser currentUser,String _twitter){
        if (_twitter == null){
            DocumentReference ref = FirebaseFirestore.getInstance().collection("user")
                    .document(currentUser.getUid());
            Map<String , String> map = new HashMap<>();
            map.put("twitter","");
            ref.set(map , SetOptions.merge());
            currentUser.setTwitter("");
        }else{
            if (currentUser.getTwitter().equals(_twitter)){
                return;
            }else{

                DocumentReference ref = FirebaseFirestore.getInstance().collection("user")
                        .document(currentUser.getUid());
                Map<String , String> map = new HashMap<>();
                map.put("twitter",_twitter);
                ref.set(map , SetOptions.merge());
                currentUser.setTwitter(_twitter);
            }
        }

    }
    private void setGithub(CurrentUser currentUser,String _github){
        if (_github == null){
            DocumentReference ref = FirebaseFirestore.getInstance().collection("user")
                    .document(currentUser.getUid());
            Map<String , String> map = new HashMap<>();
            map.put("github","");
            ref.set(map , SetOptions.merge());
            currentUser.setGithub("");
        }else{
            if (currentUser.getGithub().equals(_github)){
                return;
            }else{

                DocumentReference ref = FirebaseFirestore.getInstance().collection("user")
                        .document(currentUser.getUid());
                Map<String , String> map = new HashMap<>();
                map.put("github",_github);
                ref.set(map , SetOptions.merge());
                currentUser.setGithub(_github);
            }
        }

    }
    private void setLinkedin(CurrentUser currentUser ,String  _linkedin){
        if (_linkedin==null){
            DocumentReference ref = FirebaseFirestore.getInstance().collection("user")
                    .document(currentUser.getUid());
            Map<String , String> map = new HashMap<>();
            map.put("linkedin","");
            ref.set(map , SetOptions.merge());
            currentUser.setLinkedin("");
        }else{
            if (currentUser.getLinkedin().equals(_linkedin)){
                return;
            }else{
                DocumentReference ref = FirebaseFirestore.getInstance().collection("user")
                        .document(currentUser.getUid());
                Map<String , String> map = new HashMap<>();
                map.put("linkedin",_linkedin);
                ref.set(map , SetOptions.merge());
                currentUser.setLinkedin(_linkedin);
            }

        }
    }

    private void setUser_name(CurrentUser currentUser , String _username){
        WaitDialog.show(EditProfileActivity.this,"");
     if (currentUser.getUsername().equals(_username)){
         user_name.setText(currentUser.getUsername());
         WaitDialog.dismiss();
         return;
     }else{
         if (_username.isEmpty()){
             user_name.setText(currentUser.getUsername());
             WaitDialog.dismiss();
             return;
         }
         checkIsUserNameValid(_username, new TrueFalse<Boolean>() {
             @Override
             public void callBack(Boolean _value) {
                 if (_value){
                     updateUsername(_username, oldusername, new TrueFalse<Boolean>() {
                         @Override
                         public void callBack(Boolean _value) {
                             if (_value){

                             }
                         }
                     });
                     UserService.shared().updateAllPost(currentUser);
                 }else{
                     user_name.setError("Bu Kullanıcı Adı Zaten Kullanılıyor");
                     user_name.requestFocus();
                     return;
                 }
             }
         });

     }
    }

    private void checkIsUserNameValid(String _username , TrueFalse<Boolean> callback){

        CollectionReference ref = FirebaseFirestore.getInstance().collection("username");
        ref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (DocumentSnapshot doc : task.getResult().getDocuments()){
                        if (doc.getId().equals(_username)){
                            callback.callBack(false);
                        }else{
                            callback.callBack(true);
                        }
                    }
                }
            }
        });
    }

    private void updateUsername(String newUsername , String oldUserName , TrueFalse<Boolean> callback){
        currentUser.setUsername(newUsername);

        DocumentReference reference = FirebaseFirestore.getInstance().collection("username").document(oldUserName);
        reference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    DocumentReference reference = FirebaseFirestore.getInstance().collection("username").document(newUsername);
                    Map<String , Object> map = new HashMap<>();
                    map.put("email",currentUser.getEmail());
                    map.put("uid",currentUser.getUid());
                    map.put("username",currentUser.getUsername());
                    reference.set(map , SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                DocumentReference ref = FirebaseFirestore.getInstance().collection("user").document(currentUser.getUid());
                                Map<String , Object> map1 = new HashMap<>();

                                map1.put("username",newUsername);
                                ref.set(map1 , SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        callback.callBack(true);
                                    }
                                });

                            }
                        }
                    });
                }
            }
        });
    }

}