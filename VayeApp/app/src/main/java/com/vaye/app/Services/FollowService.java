package com.vaye.app.Services;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.OtherUser;

import java.util.HashMap;
import java.util.Map;

public class FollowService {
    private static final FollowService instance = new FollowService();
    public static FollowService shared() {
        return instance;
    }


    public void followUser(OtherUser otherUser, CurrentUser currentUser, TrueFalse<Boolean> callback){
        DocumentReference followUser = FirebaseFirestore.getInstance().collection("user")
                .document(otherUser.getUid()).collection("fallowers")
                .document(currentUser.getUid());
        Map<String,Object> map = new HashMap<>();
        map.put("user",currentUser.getUid());
        followUser.set(map , SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                DocumentReference followingUser = FirebaseFirestore.getInstance().collection("user")
                        .document(currentUser.getUid()).collection("following")
                        .document(otherUser.getUid());
                Map<String,Object> map1 = new HashMap<>();
                map1.put("user",otherUser.getUid());
                followingUser.set(map1 , SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        checkIsMutual(currentUser, otherUser, new TrueFalse<Boolean>() {
                            @Override
                            public void callBack(Boolean _value) {
                                if (_value){
                                        addOnFriendArray(currentUser, otherUser, new TrueFalse<Boolean>() {
                                            @Override
                                            public void callBack(Boolean _value) {
                                                if (_value){
                                                    addOnFriendList(currentUser, otherUser, new TrueFalse<Boolean>() {
                                                        @Override
                                                        public void callBack(Boolean _value) {
                                                            callback.callBack(true);
                                                        }
                                                    });
                                                }
                                            }
                                        });
                                }else{
                                    callback.callBack(true);
                                }
                            }
                        });
                    }
                });
            }
        });
    }

    private void checkIsMutual(CurrentUser currentUser , OtherUser otherUser , TrueFalse<Boolean> callback){

        DocumentReference db = FirebaseFirestore.getInstance().collection("user")
                .document(otherUser.getUid()).collection("following").document(currentUser.getUid());
        DocumentReference dbb = FirebaseFirestore.getInstance().collection("user").document(currentUser.getUid()).collection("following").document(otherUser.getUid());
        db.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    dbb.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()){
                                    callback.callBack(true);
                            }else{
                                callback.callBack(false);
                            }
                        }
                    });

                }else{
                    callback.callBack(false);
                }
            }
        });
    }

    private void addOnFriendArray(CurrentUser  currentUser,  OtherUser otherUser , TrueFalse<Boolean> callback){

        DocumentReference dbc = FirebaseFirestore.getInstance().collection("user")
                .document(otherUser.getUid());
        Map<String , Object> map = new HashMap<>();
        map.put("friendList", FieldValue.arrayUnion(currentUser.getUid()));
        dbc.set(map , SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                DocumentReference dbb = FirebaseFirestore.getInstance().collection("user")
                        .document(currentUser.getUid());
                Map<String , Object> map1 = new HashMap<>();
                map1.put("friendList", FieldValue.arrayUnion(otherUser.getUid()));
                dbb.set(map , SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        callback.callBack(true);
                    }
                });

            }
        });
    }

    private void addOnFriendList(CurrentUser currentUser , OtherUser otherUser , TrueFalse<Boolean> callback){


        DocumentReference db = FirebaseFirestore.getInstance().collection("user")
                .document(currentUser.getUid()).collection("friend-list").document(otherUser.getUid());

        Map<String , Object> map = new HashMap<>();
        map.put("userName",otherUser.getUsername());
        map.put("uid",otherUser.getUid());
        map.put("name",otherUser.getName());
        map.put("short_school",otherUser.getShort_school());
        map.put("thumb_image",otherUser.getThumb_image());
        map.put("tarih",FieldValue.serverTimestamp());
        map.put("bolum",otherUser.getBolum());
        db.set(map , SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                DocumentReference dbc = FirebaseFirestore.getInstance().collection("user")
                        .document(otherUser.getUid()).collection("friend-list").document(currentUser.getUid());


                Map<String , Object> map1 = new HashMap<>();
                map1.put("userName",currentUser.getUsername());
                map1.put("uid",currentUser.getUid());
                map1.put("name",currentUser.getName());
                map1.put("short_school",currentUser.getShort_school());
                map1.put("thumb_image",currentUser.getThumb_image());
                map1.put("tarih",FieldValue.serverTimestamp());
                map1.put("bolum",currentUser.getBolum());
                dbc.set(map1,SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        callback.callBack(true);
                    }
                });

            }
        });
    }

    public void unFollowUser(OtherUser otherUser , CurrentUser currentUser , TrueFalse<Boolean> callback){
        //let db = Firestore.firestore().collection("user")
        //                        .document(sself.currentUser.uid)
        //                        .collection("following").document(sself.otherUser!.uid)
        DocumentReference ref = FirebaseFirestore.getInstance().collection("user")
                .document(currentUser.getUid()).collection("following").document(otherUser.getUid());
        ref.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    removeOtherUserMainPost(currentUser , otherUser);
                    removeFromFriendList(currentUser, otherUser, new TrueFalse<Boolean>() {
                        @Override
                        public void callBack(Boolean _value) {
                            callback.callBack(_value);
                        }
                    });
                }else{
                    callback.callBack(false);
                }
            }
        });
    }
    private void removeOtherUserMainPost(CurrentUser currentUser , OtherUser otherUser ){
       //    let db = Firestore.firestore().collection("user").document(currentUser.uid)
        //            .collection("main-post").whereField("senderUid", isEqualTo:otherUserId)
        Query db = FirebaseFirestore.getInstance().collection("user")
                .document(currentUser.getUid()).collection("main-post").whereEqualTo("senderUid",otherUser.getUid());
        db.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().isEmpty()){
                        return;
                    }else{
                        for (DocumentSnapshot item : task.getResult().getDocuments()){
                            DocumentReference reference =  FirebaseFirestore.getInstance().collection("user")
                                    .document(currentUser.getUid()).collection("main-post").document(item.getId());
                            reference.delete();
                        }
                    }
                }
            }
        });
    }


    private void removeFromFriendList(CurrentUser currentUser , OtherUser otherUser, TrueFalse<Boolean> callback){
        //    let db = Firestore.firestore().collection("user")
        //            .document(currentUserUid.uid)
        DocumentReference ref = FirebaseFirestore.getInstance().collection("user").document(currentUser.getUid());
        Map<String , Object> map = new HashMap<>();
        map.put("friendList",FieldValue.arrayRemove(otherUser.getUid()));
        ref.set(map , SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    //let db = Firestore.firestore().collection("user")
                    //                            .document(currentUserUid.uid).collection("friend-list").document(otherUserUid.uid)
                    DocumentReference reference = FirebaseFirestore.getInstance().collection("user").document(currentUser.getUid())
                            .collection("friend-list").document(otherUser.getUid());
                    reference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                //  let db = Firestore.firestore().collection("user")
                                //                                .document(otherUserUid.uid).collection("friend-list").document(currentUserUid.uid)
                                DocumentReference db = FirebaseFirestore.getInstance().collection("user").document(otherUser.getUid())
                                        .collection("friend-list").document(currentUser.getUid());
                                db.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                           // let db = Firestore.firestore().collection("user")
                                            //            .document(currentUser.uid)
                                            //            .collection("msg-list")
                                            //            .document(otherUser)
                                            DocumentReference msgListDelete = FirebaseFirestore.getInstance().collection("user")
                                                    .document(currentUser.getUid()).collection("msg-list").document(otherUser.getUid());
                                            msgListDelete.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()){
                                                        callback.callBack(true);
                                                    }else{
                                                        callback.callBack(false);
                                                    }
                                                }
                                            });
                                        }else{
                                            callback.callBack(false);
                                        }
                                    }
                                });
                            }else{
                                callback.callBack(false);
                            }
                        }
                    });
                    callback.callBack(true);
                }else{
                    callback.callBack(false);
                }
            }
        });
    }
}
