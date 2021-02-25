package com.vaye.app.Services;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.MainPostModel;

public class VayeAppPostService {
    private static final VayeAppPostService instance = new VayeAppPostService();
    public static VayeAppPostService shared() {
        return instance;
    }

    public void deletePost(MainPostModel post , TrueFalse<Boolean> callback){

        DocumentReference ref = FirebaseFirestore.getInstance().collection("main-post")
                .document("post")
                .collection("post")
                .document(post.getPostId());
        ref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                deleteAllComment(post);
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


    private void deleteAllComment(MainPostModel postModel){

            CollectionReference refComment = FirebaseFirestore.getInstance().collection("main-post")
                    .document("post")
                    .collection("post")
                    .document(postModel.getPostId())
                    .collection("comment");

            CollectionReference refCommentReplied = FirebaseFirestore.getInstance().collection("main-post")
                    .document("post")
                    .collection("post")
                    .document(postModel.getPostId())
                    .collection("comment-replied");
            refComment.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if (queryDocumentSnapshots.isEmpty()){

                    }else{
                        for (DocumentSnapshot item : queryDocumentSnapshots.getDocuments()){
                            DocumentReference ref =FirebaseFirestore.getInstance().collection("main-post")
                                    .document("post")
                                    .collection("post")
                                    .document(postModel.getPostId())
                                    .collection("comment").document(item.getId());
                            ref.delete();
                        }
                    }
                }
            });
        refCommentReplied.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()){
                    for (DocumentSnapshot item : queryDocumentSnapshots.getDocuments()){
                        DocumentReference ref =FirebaseFirestore.getInstance().collection("main-post")
                                .document("post")
                                .collection("post")
                                .document(postModel.getPostId())
                                .collection("comment-replied").document(item.getId());
                        ref.delete();
                    }
                }
            }
        });

    }



}
