package com.vaye.app.Services;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.vaye.app.Interfaces.LessonPostModelCompletion;
import com.vaye.app.Interfaces.StringArrayListInterface;
import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.LessonPostModel;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

public class MajorPostService {
    ArrayList<LessonPostModel> models;


    private static final MajorPostService instance = new MajorPostService();
    public static MajorPostService shared() {
        return instance;
    }

    public void setLike(CurrentUser currentUser , LessonPostModel post , TrueFalse<Boolean> result ){
        DocumentReference ref = FirebaseFirestore.getInstance().collection(currentUser.getShort_school())
                .document("lesson-post")
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
        }
        else{
            post.getLikes().remove(currentUser.getUid());
            result.callBack(true);
            mapLike.put("likes",FieldValue.arrayRemove(currentUser.getUid()));
            ref.update(mapLike);

        }
    }

    public void setDislike(CurrentUser currentUser , LessonPostModel post , TrueFalse<Boolean> result){
        DocumentReference ref = FirebaseFirestore.getInstance().collection(currentUser.getShort_school())
                .document("lesson-post")
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

    public void setFav(CurrentUser currentUser , LessonPostModel post , TrueFalse<Boolean> result){
        DocumentReference ref = FirebaseFirestore.getInstance().collection(currentUser.getShort_school())
                .document("lesson-post")
                .collection("post")
                .document(post.getPostId());
        DocumentReference refUser =  FirebaseFirestore.getInstance().collection("user")
                .document(currentUser.getUid())
                .collection("fav-post")
                .document(post.getPostId());
        Map<String, Object> mapFav = new HashMap<>();
        Map<String , Object> mapUser = new HashMap<>();
        if (!post.getFavori().contains(currentUser.getUid())){
            post.getFavori().add(currentUser.getUid());
            result.callBack(true);
            mapFav.put("favori", FieldValue.arrayUnion(currentUser.getUid()));
            ref.update(mapFav).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isComplete()){
                        mapUser.put("postId",post.getPostId());
                        refUser.set(mapUser, SetOptions.merge());
                    }
                }
            });
        }else{
            post.getFavori().remove(currentUser.getUid());
            result.callBack(true);
            mapFav.put("favori",FieldValue.arrayRemove(currentUser.getUid()));
            ref.update(mapFav).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isComplete()){
                        refUser.delete();
                    }
                }
            });
        }
    }
}
