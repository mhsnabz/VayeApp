package com.vaye.app.Services;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
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

}
