package com.vaye.app.Application;

import android.app.Application;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;
import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.Services.UserService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class VayeApp  extends Application {
    ArrayList<String> dersler = new ArrayList<>();
    @Override
    public void onCreate() {
        super.onCreate();

        dersler.add("Fizik II");
        dersler.add("Matematik II");
        dersler.add("Bilgisayar Programlama");
        dersler.add("Devre Elemanları");
        dersler.add("Sayısal Tasarım II");
        dersler.add("Lineer Cebir");
        dersler.add("İş sağlığı ve Güvenliği");
        dersler.add("Olasılık ve İstatistik");
        dersler.add("Veri Taban destekli Görsel programlama");
        dersler.add("İşletim Sistemleri");
        dersler.add("Sayısal Tasarım Lab");
        dersler.add("Üniversite Etkinliklerine Katılım ");
        dersler.add("Yazılım Müh. Giriş");
        dersler.add("Mikroişlemciler");
        dersler.add("Web Tabanlı Programlama ");
        dersler.add("Nesne Tabanlı Programlama Uyg");
        dersler.add("Kablosuz Sensör Ağlar");
        dersler.add("Bilgisayar Ağları ");
        dersler.add("Yapay Zeka Uygulamaları");
        dersler.add("Mühendislikte Bilgisayar uyg. II");
        dersler.add("İnsan Bilgisayar Etkileşimi");
        dersler.add("Robotik uygulamaları");
        dersler.add("Sayısal Görüntü İşleme");
        dersler.add("Bilişim hukuku");

        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttp3Downloader(this,Integer.MAX_VALUE));
        Picasso built = builder.build();
        built.setIndicatorsEnabled(true);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        FirebaseAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user!=null){
                    UserService.shared().isUserExist(user.getUid(), new TrueFalse<Boolean>() {
                        @Override
                        public void callBack(Boolean _value) {
                            if (_value){
                                String tokenID = FirebaseInstanceId.getInstance().getToken();
                                final Map<String,Object> map=new HashMap<>();
                                map.put("tokenID",tokenID);
                                DocumentReference db = FirebaseFirestore.getInstance().collection("user")
                                        .document(user.getUid());
                                db.get().addOnSuccessListener( new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if (documentSnapshot.exists()){
                                            db.set(map , SetOptions.merge());
                                        }
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
