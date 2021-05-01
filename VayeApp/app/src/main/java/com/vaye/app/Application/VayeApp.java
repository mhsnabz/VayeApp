package com.vaye.app.Application;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;
import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.Services.MessageService;
import com.vaye.app.Services.UserService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.vaye.app.FCM.MessagingService.active_context_name;


public class VayeApp  extends Application {
    private static final String TAG =  "VayeApp App";
    public static final String SHARED_PREFS = "sharedPrefs";

    public static final boolean isOnlineChat = false;
    ArrayList<String> dersler = new ArrayList<>();
    @Override
    public void onCreate() {
        super.onCreate();

        dersler.add("PROGRAMLAMA DİLLERİ");
        dersler.add("FİZİK II");
        dersler.add("MATEMATİK II");
        dersler.add("TÜRK DİLİ II");
        dersler.add("VEKTÖRLER VE KOMPLEKS FONKSİYONLAR");
        dersler.add("İNGİLİZCE II");
        dersler.add("YENİLİKÇİLİK VE GİRİŞİMCİLİK");
        dersler.add("ATATÜRK İLKELERİ VE İNKILAP TARİHİ II");
        dersler.add("DEVRE ANALİZİ II");
        dersler.add("ELEKTROMANYETİK II");
        dersler.add("YARI İLETKEN ELEKTRONİĞİ");
        dersler.add("ELEKTROMANYETİK II");
        dersler.add("SAYISAL TASARIM VE UYGULAMALARI");
        dersler.add("İŞ SAĞLIĞI VE GÜVENLİĞİ");
        dersler.add("SAYISAL TASARIM II ");
        dersler.add("SİNYALLER VE SİSTEMLER");
        dersler.add("TEMEL DEVRE UYGULAMALARI II");
        dersler.add("SAYISAL HABERLEŞME");
        dersler.add("OTOMATİK KONTROL");
        dersler.add("GÜÇ ELEKTRONİĞİ");
        dersler.add("ELEKTRONİK DEVRE ELEMANLARI II");
        dersler.add("ÜNİVERSİTE ETKİNLİKLERİNE KATILIM");
        dersler.add("TEMEL ELEKTRONİK UYGULAMARI II");
        dersler.add("YÜKSEK GERİLİM TEKNİĞİ");

        dersler.add("ELEKTRİK MAKİNALARI II");
        dersler.add("İŞLETİM SİSTEMLERİ");
        dersler.add("ANALOG FİLTRELER");
        dersler.add("ELEKTRİK ENERJİSİ İLETİMİ VE DAĞITIMI");
        dersler.add("TIBBİ GÖRÜNTÜLEME TEMELLERİ");
        dersler.add("ELEKTRİK ENERJİSİ İLETİMİ VE DAĞITIMI");
        dersler.add("TIBBİ GÖRÜNTÜLEME TEMELLERİ");
        dersler.add("ELEKTRİK ENERJİSİ İLETİMİ VE DAĞITIMI");
        dersler.add("TIBBİ GÖRÜNTÜLEME TEMELLERİ");
        dersler.add("ENDÜSTRİYEL KUMANDA SİSTEMLERİ");

        dersler.add("ENDÜSTRİYEL KUMANDA SİSTEMLERİ");

        dersler.add("MERKEZİ DENETLEME KONTROL VE VERİ TOPLAMA TEKNİĞİ");
        dersler.add("FİBER OPTİK HABERLEŞME SİSTEMLERİ");
        dersler.add("HABERLEŞME AĞLARI");
        dersler.add("ANTEN TASARIMI");
        dersler.add("SENSÖRLER VE AKTÜATÖRLER");
        dersler.add("RADAR SİSTEMLERİ");
        dersler.add("BİLGİSAYAR DESTEKLİ MESLEKİ ÇİZİM");
        dersler.add("YAPAY ZEKA VE UYGULAMALARI");
        dersler.add("ANTENLER VE PROPAGASYON");

      //  setLesson();
        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttp3Downloader(this,Integer.MAX_VALUE));
        Picasso built = builder.build();
        built.setIndicatorsEnabled(true);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);
        setupActivityListener();
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
    private Activity activeActivity;
    private void setupActivityListener() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {

            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {
                saveActiceContext(activity.getLocalClassName());
            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {
                saveActiceContext(activity.getLocalClassName());

            }

            @Override
            public void onActivityResumed(Activity activity) {
                saveActiceContext(activity.getLocalClassName());
            }
            @Override
            public void onActivityPaused(Activity activity) {
                if (activity.getLocalClassName().equals(active_context_name)){
                    saveActiceContext("onActivityPaused");
                }
            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {
                if (activity.getLocalClassName().equals(active_context_name)){
                    saveActiceContext("onActivityStopped");
                }
            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {
                if (activity.getLocalClassName().equals(active_context_name)){
                    saveActiceContext("onActivityDestroyed");
                }
            }
        });
    }
    public void saveActiceContext(String activeContext) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("context",activeContext);

        editor.apply();

    }


    private void setLesson(){
        Map<String , Object > map = new HashMap<>();
        map.put("teacherEmail","empty");
        map.put( "teacherId","empty");
        map.put("teacherName","empty");
       //İSTE/lesson/Bilgisayar Mühendisliği/Bilgisayar Programlama
        CollectionReference ref = FirebaseFirestore.getInstance().collection("İSTE")
                .document("lesson").collection("Elektrik Elektronik Mühendisliği");
        for (String item : dersler){
            map.put("lesson_key","empty");
            map.put("lessonName",item);
            ref.document(item).set(map , SetOptions.merge());
        }
    }

}
