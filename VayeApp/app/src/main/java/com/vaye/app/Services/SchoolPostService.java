package com.vaye.app.Services;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kongzue.dialog.v3.WaitDialog;
import com.vaye.app.Interfaces.CallBackCount;
import com.vaye.app.Interfaces.Notifications;
import com.vaye.app.Interfaces.StringArrayListInterface;
import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.LessonPostModel;
import com.vaye.app.Model.MainPostModel;
import com.vaye.app.Model.NewPostDataModel;
import com.vaye.app.Model.NoticesMainModel;

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
        map.put("dislike",FieldValue.arrayUnion());
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

    public void setLike(CurrentUser currentUser , NoticesMainModel post , TrueFalse<Boolean> result){
        ///İSTE/notices/post/1613514956598
        DocumentReference ref = FirebaseFirestore.getInstance().collection(currentUser.getShort_school())
                .document("notices")
                .collection("post")
                .document(post.getPostId());
        Map<String , Object> mapLike = new HashMap<>();
        Map<String , Object> mapDisike = new HashMap<>();
        if (!post.getLikes().contains(currentUser.getUid())){
            post.getLikes().add(currentUser.getUid());
            post.getDislike().remove(currentUser.getUid());
            result.callBack(true);
            mapLike.put("likes",FieldValue.arrayUnion(currentUser.getUid()));
            mapDisike.put("dislike",FieldValue.arrayRemove(currentUser.getUid()));
            ref.update(mapLike);
            ref.update(mapDisike);
            SchoolPostNS.shared().sendLikeNotification(post,currentUser, Notifications.NotificationType.notices_post_like, Notifications.NotificationDescription.notices_post_like);
        }
        else{
            post.getLikes().remove(currentUser.getUid());
            result.callBack(true);
            mapLike.put("likes",FieldValue.arrayRemove(currentUser.getUid()));
            ref.update(mapLike);

        }
    }


    public void setDislike(CurrentUser currentUser , NoticesMainModel post , TrueFalse<Boolean> result){
        DocumentReference ref = FirebaseFirestore.getInstance().collection(currentUser.getShort_school())
                .document("notices")
                .collection("post")
                .document(post.getPostId());
        Map<String , Object> mapLike = new HashMap<>();


        if (!post.getDislike().contains(currentUser.getUid())){
            post.getLikes().remove(currentUser.getUid());
            post.getDislike().add(currentUser.getUid());
            result.callBack(true);
            mapLike.put("likes",FieldValue.arrayRemove(currentUser.getUid()));
            mapLike.put("dislike",FieldValue.arrayUnion(currentUser.getUid()));
            ref.update(mapLike);
        }else{
            post.getDislike().remove(currentUser.getUid());
            result.callBack(true);
            mapLike.put("dislike",FieldValue.arrayRemove(currentUser.getUid()));
            ref.update(mapLike);
        }
    }

    public void deletePost(CurrentUser currentUser,NoticesMainModel post , TrueFalse<Boolean> callback){
        DocumentReference ref = FirebaseFirestore.getInstance().collection(currentUser.getShort_school())
                .document("notices")
                .collection("post")
                .document(post.getPostId());
        ref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                if (post.getData().size()<=0){
                    callback.callBack(true);
                }else{
                    for (String url : post.getData()){
                        StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(url);
                        ref.delete();
                    }
                    for (String thumbUrl : post.getThumbData()){
                        StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(thumbUrl);
                        reference.delete();
                    }

                    callback.callBack(true);
                }

            }
        });
    }
    public void deleteAllComment(String postID){
        ///comment/1613861865647/comment
        CollectionReference ref = FirebaseFirestore.getInstance().collection("comment").document(postID).collection("comment");
        ref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    if (!task.getResult().isEmpty()){
                        for (DocumentSnapshot item : task.getResult().getDocuments()){
                            DocumentReference ref = FirebaseFirestore.getInstance().collection("comment").document(postID).collection("comment").document(item.getId());
                            ref.delete();
                        }
                    }
                }
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("deleteAllComment", "onComplete: " + e.getLocalizedMessage());
            }
        });
    }
    public void SetPostSlient(Activity activity, NoticesMainModel postModel , CurrentUser currentUser , TrueFalse<Boolean> callback){
        WaitDialog.show((AppCompatActivity) activity ,null);

        DocumentReference ref = FirebaseFirestore.getInstance().collection(currentUser.getShort_school())
                .document("notices").collection("post").document(postModel.getPostId());
        Map<String , Object> map = new HashMap<>();
        map.put("silent", FieldValue.arrayUnion(currentUser.getUid()));
        ref.set(map , SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    postModel.getSilent().add(currentUser.getUid());
                    callback.callBack(true);
                    WaitDialog.dismiss();
                }
            }
        });
    }
    public void RemoveSlientFromPost(Activity activity, NoticesMainModel postModel , CurrentUser currentUser, TrueFalse<Boolean> callback){
        WaitDialog.show((AppCompatActivity) activity ,null);
        DocumentReference ref = FirebaseFirestore.getInstance().collection(currentUser.getShort_school())
                .document("notices").collection("post").document(postModel.getPostId());
        Map<String , Object> map = new HashMap<>();
        map.put("silent", FieldValue.arrayRemove(currentUser.getUid()));
        ref.set(map , SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    postModel.getSilent().remove(currentUser.getUid());
                    callback.callBack(true);
                    WaitDialog.dismiss();
                }
            }
        });
    }

    public void setNewComment(CurrentUser currentUser , String commentText , String commentId , String postId , TrueFalse<Boolean> callback){
        DocumentReference ref  = FirebaseFirestore.getInstance()
                .collection("comment")
                .document(postId).collection("comment").document(commentId);

        Map<String , Object> map = new HashMap<>();
        map.put("senderName",currentUser.getName());
        map.put("senderUid",currentUser.getUid());
        map.put("username",currentUser.getUsername());
        map.put("time",FieldValue.serverTimestamp());
        map.put("comment",commentText);
        map.put("commentId",commentId);
        map.put("postId",postId);
        map.put("likes",FieldValue.arrayUnion());
        map.put("replies",FieldValue.arrayUnion());
        map.put("senderImage",currentUser.getThumb_image());
        ref.set(map , SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    callback.callBack(true);
                    getTotalCommentCount(postId, new CallBackCount() {
                        @Override
                        public void callBackCount(long count) {
                            DocumentReference commentCountRef  = FirebaseFirestore.getInstance().collection(currentUser.getShort_school())
                                    .document("notices")
                                    .collection("post")
                                    .document(postId);
                            Map<String , Object> map1 = new HashMap<>();
                            map1.put("comment", count);
                            commentCountRef.set(map1 , SetOptions.merge());
                        }
                    });
                }
            }
        });
    }

    private void getTotalCommentCount(String postId , CallBackCount count){
        CollectionReference ref  = FirebaseFirestore.getInstance()
                .collection("comment")
                .document(postId).collection("comment");
        ref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().isEmpty()){
                        count.callBackCount(0);
                    }else{
                        count.callBackCount(task.getResult().getDocuments().size());
                    }
                }
            }
        });
    }
}
