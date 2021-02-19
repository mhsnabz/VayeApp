package com.vaye.app.Services;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.vaye.app.Interfaces.StringArrayListInterface;
import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.NewPostDataModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SchoolPostService {
    private static final SchoolPostService instance = new SchoolPostService();
    public static SchoolPostService shared() {
        return instance;
    }

   public String iste_hastag[] = {"Atatürkçü Düşünce Öğrenci Topluluğu","Bağımlılıkla Mücadele Öğrenci Topluluğu","Bilim Kadınları Öğrenci Topluluğu",
            "Bilim Ve Çocuk Öğrenci Topluluğu","Bilimsel Araştırmalar Öğrenci Topluluğu","Bireysel Sporlar Öğrenci Topluluğu"
            ,"Bisiklet Öğrenci Topluluğu","Çevre Öğrenci Topluluğu","Doğa Sporları Öğrenci Topluluğu","Edebiyat Öğrenci Topluluğu",
            "Ekonomi Öğrenci Topluluğu","Fotoğrafçılık Öğrenci Topluluğu","Gezi Öğrenci Topluluğu","Gönüllü Genç Sağlık Liderleri Öğrenci Topluluğu",
            "Güzel Sanatlar Öğrenci Topluluğu","Halk Dansları Öğrenci Topluluğu",
            "Havacılık Öğrenci Topluluğu","Hayvanları Koruma Öğrenci Topluluğu","Hidrojen Teknolojileri Öğrenci Topluluğu","İSTE-IEEE Öğrenci Topluluğu",
            "İSTE Endüstri Öğrenci Topluluğu","İSTE E-Spor Öğrenci Topluluğu",
            "İSTE Genç Tema Öğrenci Topluluğu","İSTE İzcilik Öğrenci Topluluğu","İSTE-Spe Öğrenci Topluluğu","İSTE-Engelsiz Öğrenci Topluluğu",
            "Karikatür Ve Mizah Öğrenci Topluluğu","Kültür Ve Sanat Öğrenci Topluluğu","Matematik Öğrenci Topluluğu","Mekatronik Öğrenci Topluluğu",
            "Metalurji Öğrenci Topluluğu","Müzik Öğrenci Topluluğu","Ombudsmanlık Öğrenci Topluluğu","Radyo Ve Televizyon Öğrenci Topluluğu",
            "Resim Öğrenci Topluluğu", "Robotik Öğrenci Topluluğu","Satranç Öğrenci Topluluğu","Savunma SanayiTeknolojileri Öğrenci Topluluğu",
            "Sinema Öğrenci Topluluğu","Sosyal Sorumluluk Öğrenci Topluluğu","Sualtı Öğrenci Topluluğ","Takım Sporları Öğrenci Topluluğu"," Tasarım Öğrenci Topluluğu"
            ,"Teknoloji Öğrenci Topluluğu","Tiyatro Öğrenci Topluluğu","Turizm Öğrenci Topluluğu","Türk Tarihi Araştırma Öğrenci Topluluğu","Uluslararası İlişkiler Öğrenci Topluluğu",
            "Uluslararası İlişkiler Öğrenci Topluluğu","Üniversite-Sanayi İşbirliği Öğrenci Topluluğu"
            ,"Üniversite-Sanayi İşbirliği Öğrenci Topluluğu","Yelken Öğrenci Topluluğu","Yenilikçilik Ve Girişimcilik Öğrenci Topluluğu"};


    public ArrayList<String> getHastah(String shortSchool){
        ArrayList<String> list = new ArrayList<>();
        if (short_school.İSTE.equals(shortSchool)){
            for (String i : iste_hastag){
                list.add(i);
            }
            return  list;
        }
        else {
            return  null;
        }
    }


    interface  short_school{
        String İSTE = "İSTE";
    }


    public void setClupNames(CurrentUser currentUser , String[] list){
        for ( String name :list){
            DocumentReference reference = FirebaseFirestore.getInstance().collection(currentUser.getShort_school())
                    .document("clup").collection("name").document(name);
            Map<String , Object> map = new HashMap<>();
            map.put("followers", FieldValue.arrayUnion());
            map.put("name",name);
            reference.set(map , SetOptions.merge());
        }

    }

    public void followClup(CurrentUser currentUser,String name , TrueFalse<Boolean> callback){
        DocumentReference reference = FirebaseFirestore.getInstance().collection(currentUser.getShort_school())
                .document("clup").collection("name").document(name);
        Map<String,Object> map = new HashMap<>();
        map.put("followers", FieldValue.arrayUnion(currentUser.getUid()));
        reference.set(map , SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    callback.callBack(true);
                }else{
                    callback.callBack(false);
                }
            }
        });
    }
    public void unFollowClup(CurrentUser currentUser,String name , TrueFalse<Boolean> callback){
        DocumentReference reference = FirebaseFirestore.getInstance().collection(currentUser.getShort_school())
                .document("clup").collection("name").document(name);
        Map<String,Object> map = new HashMap<>();
        map.put("followers", FieldValue.arrayRemove(currentUser.getUid()));
        reference.set(map , SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    callback.callBack(true);
                }else{
                    callback.callBack(false);
                }
            }
        });
    }

    public void getFollowers(CurrentUser currentUser , String  name , StringArrayListInterface list){
        DocumentReference ref = FirebaseFirestore.getInstance().collection(currentUser.getShort_school())
                .document("clup").collection("name").document(name);
        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
            if (task.isSuccessful()){
                if (task.getResult().exists()){
                    list.getArrayList((ArrayList<String>) task.getResult().get("followers"));
                }
            }
            }
        });
    }

    public void setNewNotice(CurrentUser currentUser , String clupName , String msgText , long postId , ArrayList<NewPostDataModel> datas , TrueFalse<Boolean> callback ){

        Map<String , Object> map = new HashMap<>();
        if (datas.isEmpty()){
            map.put("type","post");
            map.put("data",FieldValue.arrayUnion());
            map.put("thumb_data",FieldValue.arrayUnion());
        }else{
            map.put("type","data");
        }
        map.put("postTime", FieldValue.serverTimestamp());
        map.put("senderName", currentUser.getName());
        map.put("text", msgText);
        map.put("likes", FieldValue.arrayUnion());
        map.put("comment", 0);
        map.put("clupName",  clupName);
        map.put("senderUid", currentUser.getUid());
        map.put("postId", String.valueOf(postId));
        map.put("post_ID", postId);
        map.put("username", currentUser.getUsername());
        map.put("thumb_image", currentUser.getThumb_image());
        map.put("silent", FieldValue.arrayUnion());
        map.put("postType", "notice");
        setPostForCurrentUser(String.valueOf(postId),currentUser);
        setNotice(map, currentUser, String.valueOf(postId), new TrueFalse<Boolean>() {
            @Override
            public void callBack(Boolean _value) {
                if (_value)
                callback.callBack(true);
            }
        });
    }
    private void setPostForCurrentUser(String postId , CurrentUser currentUser){
        DocumentReference ref = FirebaseFirestore.getInstance().collection("user")
                .document(currentUser.getUid()).collection(currentUser.getShort_school()).document(postId);
        Map<String , Object> map = new HashMap<>();
        map.put("postId",postId);
        ref.set(map , SetOptions.merge());
    }

    private void setNotice(Map<String , Object> map , CurrentUser currentUser , String postId , TrueFalse<Boolean> callback){

        DocumentReference ref = FirebaseFirestore.getInstance().collection(currentUser.getShort_school())
                .document("notices").collection("post").document(postId);
        ref.set(map , SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    callback.callBack(true);
                }
            }
        });
    }
}
