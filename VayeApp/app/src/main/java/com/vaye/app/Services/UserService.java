package com.vaye.app.Services;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.rpc.Help;
import com.kongzue.dialog.v3.TipDialog;
import com.kongzue.dialog.v3.WaitDialog;
import com.vaye.app.Interfaces.CompletionWithValue;
import com.vaye.app.Interfaces.CurrentUserService;
import com.vaye.app.Interfaces.OtherUserOptionsCompletion;
import com.vaye.app.Interfaces.OtherUserService;
import com.vaye.app.Interfaces.StringCompletion;
import com.vaye.app.Interfaces.TaskUserHandler;
import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.OtherUser;
import com.vaye.app.Model.TaskUser;
import com.vaye.app.Util.Helper;

import java.util.HashMap;
import java.util.Map;

public class UserService {

    private static final UserService instance = new UserService();
    public static UserService shared() {
        return instance;
    }
    String TAG = "UserService";
    public void getCurrentUser(String uid , CurrentUserService result ){
        DocumentReference ref = FirebaseFirestore.getInstance().collection("user")
                .document(uid);
        ref.get().addOnCompleteListener( new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().exists()){
                        result.onCallback(task.getResult().toObject(CurrentUser.class));
                    }else{
                        result.onCallback(null);
                    }
                }
            }
        }).addOnFailureListener( new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }
    public void getOthUserIdByMention(String username , StringCompletion uid){
        ///username/@mhsnabz
        DocumentReference ref = FirebaseFirestore.getInstance().collection("username")
                .document(username);
        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
            if (task.isSuccessful()){
                if (task.getResult().exists()){
                    uid.getString(task.getResult().getString("uid"));
                }else{
                    uid.getString(null);
                }
            }
            }
        });
    }
    public void getUserByMention(Activity activity,String username , OtherUserService otherUser){
        getUidByMention(activity, username, new StringCompletion() {
            @Override
            public void getString(String string) {
                getOtherUserWihtoutProgres(activity, string, new OtherUserService() {
                    @Override
                    public void callback(OtherUser user) {
                        otherUser.callback(user);
                    }
                });
            }
        });
    }
    private void getUidByMention(Activity activity, String username , StringCompletion uid){
        WaitDialog.show((AppCompatActivity) activity, null);
        DocumentReference ref = FirebaseFirestore.getInstance().collection("username")
                .document(username);
        ref.get().addOnCompleteListener(activity, new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().exists()){
                        if (!task.getResult().getString("uid").isEmpty()){
                            uid.getString(task.getResult().getString("uid"));
                            WaitDialog.dismiss();
                        }
                    }else{
                        WaitDialog.dismiss();
                        TipDialog.show((AppCompatActivity) activity, "Böyle Bir Kullanıcı Bulunmuyor", TipDialog.TYPE.ERROR);
                        TipDialog.dismiss(1000);
                    }
                }
            }
        });
    }


    public void getTaskTeacher(String uid , TaskUserHandler result){
        DocumentReference ref = FirebaseFirestore.getInstance().collection("task-teacher")
                .document(uid);
        ref.get().addOnCompleteListener( new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().exists()){
                        result.onCallback(task.getResult().toObject(TaskUser.class));
                    }else{
                        result.onCallback(null);
                    }
                }
            }
        }).addOnFailureListener( new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    public void getTaskUser(String uid , TaskUserHandler result){
        DocumentReference ref = FirebaseFirestore.getInstance().collection("task-user")
                .document(uid);
        ref.get().addOnCompleteListener( new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().exists()){
                        result.onCallback(task.getResult().toObject(TaskUser.class));
                    }else{
                        result.onCallback(null);
                    }
                }
            }
        }).addOnFailureListener( new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    public void checkUserIsComplete(String uid , TrueFalse result){
        DocumentReference ref = FirebaseFirestore.getInstance().collection("task-user")
                .document(uid);
        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()){
                    result.callBack(true);
                }else{
                    result.callBack(false);
                }
            }
        });
    }

    public void checkIsFallowing(CurrentUser currentUser  , OtherUser otherUser , TrueFalse<Boolean> val){
        ///user/t01RVvdauThanTbmpmmsLMgiJGx1/following/VUSU6uA0odX7vuF5giXWbOUYzni1

        DocumentReference ref = FirebaseFirestore.getInstance().collection("user")
                .document(currentUser.getUid())
                .collection("following").document(otherUser.getUid());
        ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()){
                        val.callBack(true);
                    }else{
                        val.callBack(false);
                    }
            }
        });
    }

    public void getOtherUser_Mentioned(String username , OtherUserService user){
        Log.d(TAG, "getOtherUser_Mentioned: " + username);
        DocumentReference ref = FirebaseFirestore.getInstance().collection("username")
                .document(username);
        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().exists()){
                        if (task.getResult().getString("uid") != null){
                            DocumentReference ref = FirebaseFirestore.getInstance().collection("user")
                                    .document(task.getResult().getString("uid"));
                            ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()){
                                        if (task.getResult().exists()){
                                            user.callback(task.getResult().toObject(OtherUser.class));
                                        }
                                    }else{
                                        user.callback(null );
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    user.callback(null);
                                }
                            });
                        }

                    }
                }else{
                    user.callback(null);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                    user.callback(null);
            }
        });

    }
    public void getOtherUserWihtoutProgres(Activity activity, String otherUserUid , OtherUserService user){
        WaitDialog.show((AppCompatActivity) activity, null);
        DocumentReference ref = FirebaseFirestore.getInstance()
                .collection("user")
                .document(otherUserUid);
        ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    user.callback(documentSnapshot.toObject(OtherUser.class));
                }
            }
        });
    }

    public void otherUser(String otherUserUid , OtherUserService user){

        DocumentReference ref = FirebaseFirestore.getInstance()
                .collection("user")
                .document(otherUserUid);
        ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    user.callback(documentSnapshot.toObject(OtherUser.class));
                }else{

                    user.callback(null);
                }
            }
        });
    }
    public void getOtherUser(Activity activity, String otherUserUid , OtherUserService user){
        WaitDialog.show((AppCompatActivity) activity, null);
        DocumentReference ref = FirebaseFirestore.getInstance()
                .collection("user")
                .document(otherUserUid);
        ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    user.callback(documentSnapshot.toObject(OtherUser.class));
                }else{
                    WaitDialog.dismiss();

                    TipDialog.show((AppCompatActivity) activity, "Böyle Bir Kullanıcı Bulunmuyor", TipDialog.TYPE.ERROR);
                    TipDialog.dismiss(1000);
                    user.callback(null);
                }
            }
        });

    }
    public void getOtherUserList(String otherUserUid , OtherUserService user){

        DocumentReference ref = FirebaseFirestore.getInstance()
                .collection("user")
                .document(otherUserUid);
        ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    user.callback(documentSnapshot.toObject(OtherUser.class));
                }
            }
        });

    }
    public void isUserExist(String uid , TrueFalse<Boolean> callback){
        DocumentReference ref = FirebaseFirestore.getInstance().collection("user")
                .document(uid);
        ref.get().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callback.callBack(false);
            }
        }).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().exists()){
                        callback.callBack(true);
                    }else{
                        callback.callBack(false);
                    }
                }
            }
        });
    }

    public void getOtherUserById(String otherUserUid , OtherUserService user){

        DocumentReference ref = FirebaseFirestore.getInstance()
                .collection("user")
                .document(otherUserUid);
        ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    user.callback(documentSnapshot.toObject(OtherUser.class));
                }
            }
        });
    }

    public void setUnFollow(String currentUserId , String otherUserId , TrueFalse<Boolean> completion){

        //      let db = Firestore.firestore().collection("user")
        //                .document(user.uid).collection("fallowers").document(currentUser.uid)
        DocumentReference dbRef = FirebaseFirestore.getInstance().collection("user"
        ).document(currentUserId).collection("fallowers")
                .document(otherUserId);
        DocumentReference ref = FirebaseFirestore.getInstance().collection("user"
        ).document(otherUserId).collection("fallowers")
                .document(currentUserId);
        ref.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                   dbRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                       @Override
                       public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            removeFromFirendList(currentUserId, otherUserId, new TrueFalse<Boolean>() {
                                @Override
                                public void callBack(Boolean _value) {
                                    completion.callBack(true);
                                }
                            });
                        }
                       }
                   }) ;
                }
            }
        });
    }



    public void removeFromFirendList(String currentUser , String  otherUser , TrueFalse<Boolean> completion){
        //  let db = Firestore.firestore().collection("user")
        //            .document(currentUserUid.uid)
        DocumentReference ref = FirebaseFirestore.getInstance().collection("user")
                .document(currentUser);
        DocumentReference dbref = FirebaseFirestore.getInstance().collection("user")
                .document(otherUser);
        Map<String , Object> map = new HashMap<>();
        map.put("friendList", FieldValue.arrayRemove(otherUser));
        ref.set(map , SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Map<String  , Object> map1 =  new HashMap<>();
                        map1.put("friendList",FieldValue.arrayRemove(currentUser));
                        dbref.set(map1,SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    DocumentReference reference = FirebaseFirestore.getInstance()
                                            .collection("user")
                                            .document(currentUser)
                                            .collection("friend-list")
                                            .document(otherUser);
                                    reference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                DocumentReference documentReference = FirebaseFirestore.getInstance().collection("user")
                                                        .document(otherUser)
                                                        .collection("friend-list")
                                                        .document(currentUser);
                                                documentReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()){
                                                            removeFromMsgList(currentUser, otherUser, new TrueFalse<Boolean>() {
                                                                @Override
                                                                public void callBack(Boolean _value) {
                                                                    completion.callBack(true);
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
                        });
                    }
            }
        });
    }
    public void removeFromMsgList(String  currentUser , String  otherUser , TrueFalse<Boolean> completion){

        DocumentReference reference = FirebaseFirestore.getInstance().collection("user")
                .document(currentUser).collection("msg-list")
                .document(otherUser);
        reference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    completion.callBack(true);
                }
            }
        });
    }

    private void checkIsMutual(String  currentUserId , String otherUserId , TrueFalse<Boolean> completion){
        DocumentReference db= FirebaseFirestore.getInstance().collection("user")
                .document(otherUserId).collection("following")
                .document(currentUserId);
        DocumentReference dbb= FirebaseFirestore.getInstance().collection("user")
                .document(currentUserId).collection("following")
                .document(otherUserId);
        db.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().exists())
                    {
                        dbb.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task1) {
                                     if (task1.isSuccessful()){
                                         if (task1.getResult().exists()){
                                             completion.callBack(true);
                                         }else{
                                             completion.callBack(false);
                                         }
                                     }else{
                                         completion.callBack(false);
                                     }
                            }
                        });
                    }else {
                        completion.callBack(false);
                    }
                }
            }
        });
    }

    public void addAsMessegesFriend(CurrentUser currentUser , OtherUser  otherUser , TrueFalse<Boolean> completion){
        checkIsMutual(currentUser.getUid(), otherUser.getUid(), new TrueFalse<Boolean>() {
            @Override
            public void callBack(Boolean _value) {
                if (_value){
                    addOnFreindArray(currentUser, otherUser, new TrueFalse<Boolean>() {
                        @Override
                        public void callBack(Boolean _value) {
                            if (_value){
                                addOtherUserFriendList(currentUser, otherUser, new TrueFalse<Boolean>() {
                                    @Override
                                    public void callBack(Boolean _value) {
                                        completion.callBack(true);
                                    }
                                });
                            }else{
                                completion.callBack(true);
                            }
                        }
                    });
                }else{
                    completion.callBack(true);
                }
            }
        });
    }

    public void acceptRequest(CurrentUser currentUser , OtherUser otherUser , TrueFalse<Boolean> callback){
        addOnFreindArray(currentUser, otherUser, new TrueFalse<Boolean>() {
            @Override
            public void callBack(Boolean _value) {
                if (_value){
                    addOtherUserFriendList(currentUser, otherUser, new TrueFalse<Boolean>() {
                        @Override
                        public void callBack(Boolean _value) {
                            if(_value){
                                MessageService.shared().removeRequest(currentUser, otherUser, new TrueFalse<Boolean>() {
                                    @Override
                                    public void callBack(Boolean _value) {
                                        callback.callBack(_value);
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }

    private void addOnFreindArray(CurrentUser currentUser , OtherUser otherUserr , TrueFalse<Boolean> completion){
        DocumentReference ref = FirebaseFirestore.getInstance().collection("user")
                .document(otherUserr.getUid());
        Map<String , Object> map = new HashMap<>();
        map.put("friendList",FieldValue.arrayUnion(currentUser.getUid()));
        ref.set(map , SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        DocumentReference reff = FirebaseFirestore.getInstance().collection("user")
                                .document(currentUser.getUid());
                        Map<String , Object> map1 = new HashMap<>();
                        map1.put("friendList",FieldValue.arrayUnion(otherUserr.getUid()));
                        reff.set(map1 , SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    completion.callBack(true);
                                }
                            }
                        });
                    }
            }
        });
    }

    private void addOtherUserFriendList(CurrentUser currentUser , OtherUser otherUser , TrueFalse<Boolean> completion){
        DocumentReference db = FirebaseFirestore.getInstance()
                .collection("user")
                .document(currentUser.getUid())
                .collection("friend-list")
                .document(otherUser.getUid());
        Map<String , Object> map = new HashMap<>();

        map.put("userName",otherUser.getUsername());
        map.put("uid",otherUser.getUid());
        map.put("name",otherUser.getName());
        map.put("short_school",otherUser.getShort_school());
        map.put("thumb_image",otherUser.getThumb_image());
        map.put("bolum",otherUser.getBolum());
        map.put("tarih",FieldValue.serverTimestamp());
        db.set(map ,SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        addCurrenUserOnFriendList(currentUser, otherUser, new TrueFalse<Boolean>() {
                            @Override
                            public void callBack(Boolean _value) {
                                completion.callBack(true);
                            }
                        });
                    }
            }
        });
    }
    private void addCurrenUserOnFriendList(CurrentUser currentUser , OtherUser otherUser , TrueFalse<Boolean> completion){
        DocumentReference db = FirebaseFirestore.getInstance()
                .collection("user")
                .document(otherUser.getUid())
                .collection("friend-list")
                .document(currentUser.getUid());
        Map<String , Object> map = new HashMap<>();
        // let dic = ["userName":otherUser.username as Any ,"uid":otherUser.uid as Any,
        // "name":otherUser.name as Any , "short_school" : otherUser.short_school as Any ,
        // "thumb_image":otherUser.thumb_image as Any,"tarih":FieldValue.serverTimestamp(),
        // "bolum":otherUser.bolum as Any]  as [String : Any]
        map.put("userName",currentUser.getUsername());
        map.put("uid",currentUser.getUid());
        map.put("name",currentUser.getName());
        map.put("short_school",currentUser.getShort_school());
        map.put("thumb_image",currentUser.getThumb_image());
        map.put("bolum",currentUser.getBolum());
        map.put("tarih",FieldValue.serverTimestamp());
        db.set(map ,SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    completion.callBack(true);
                }
            }
        });
    }


    public void deleteProfileImage(Activity activity,CurrentUser currentUser , CompletionWithValue callback){
        WaitDialog.show((AppCompatActivity) activity, "Resminiz Siliniyor...");
        Log.d("UserService", "onFailure: " +currentUser.getProfileImage());
        Log.d("UserService", "onFailure: " +currentUser.getThumb_image());
        if (currentUser.getProfileImage()!=null && !currentUser.getProfileImage().isEmpty()){
            StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(currentUser.getProfileImage());
            ref.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        if (currentUser.getThumb_image()!=null && !currentUser.getThumb_image().isEmpty()){
                            StorageReference ref1 = FirebaseStorage.getInstance().getReferenceFromUrl(currentUser.getThumb_image());
                            ref1.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        currentUser.setProfileImage("");
                                        currentUser.setThumb_image("");
                                        callback.completion(true, CompletionWithValue.deleted);
                                        WaitDialog.dismiss();
                                        DocumentReference ref = FirebaseFirestore.getInstance().collection("user")
                                                .document(currentUser.getUid());
                                        Map<String , String> map = new HashMap<>();
                                        map.put("profileImage","");
                                        map.put("thumb_image","");
                                        ref.set(map , SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    currentUser.setThumb_image("");
                                                    currentUser.setProfileImage("");
                                                    updateAllPost(currentUser);
                                                    WaitDialog.dismiss();
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    }
                }
            }).addOnFailureListener(activity, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    callback.completion(false, CompletionWithValue.deleted);
                    Log.d("UserService", "onFailure: " +e.getLocalizedMessage());
                }
            });
        }else{
            callback.completion(true, CompletionWithValue.deleted);
            WaitDialog.dismiss();
        }
    }

    public void updateAllPost(CurrentUser currentUser ){
        Query mainPostRef = FirebaseFirestore.getInstance().collection("main-post")
                .document("post").collection("post").whereEqualTo("senderUid",currentUser.getUid());

        mainPostRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    if (!task.getResult().isEmpty()){
                        for (DocumentSnapshot doc : task.getResult().getDocuments()){
                            updateMainPost(currentUser , doc.getId());
                        }
                    }
                }
            }
        });

        Query majorPostRef = FirebaseFirestore.getInstance().collection(currentUser.getShort_school())
                .document("lesson-post").collection("post").whereEqualTo("senderUid",currentUser.getUid());
        majorPostRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    if (!task.getResult().isEmpty()){
                        for (DocumentSnapshot doc : task.getResult().getDocuments()){
                            updateMajorPost(currentUser , doc.getId());
                        }
                    }
                }
            }
        });

        Query noticesPostRef = FirebaseFirestore.getInstance().collection(currentUser.getShort_school())
                .document("notices").collection("post").whereEqualTo("senderUid",currentUser.getUid());
        noticesPostRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    if (!task.getResult().isEmpty()){
                        for (DocumentSnapshot doc : task.getResult().getDocuments()){
                            updateNoticesPost(currentUser , doc.getId());
                        }
                    }
                }
            }
        });
    }
    private void updateMajorPost(CurrentUser currentUser , String postId){
        DocumentReference ref = FirebaseFirestore.getInstance().collection(currentUser.getShort_school())
                .document("lesson-post").collection("post").document(postId);
        Map<String , String> map = new HashMap<>();
        map.put("thumb_image",currentUser.getThumb_image());
        map.put("username",currentUser.getUsername());
        ref.set(map , SetOptions.merge());

    }
    private void updateNoticesPost(CurrentUser currentUser , String postId){
        DocumentReference ref = FirebaseFirestore.getInstance().collection(currentUser.getShort_school())
                .document("notices").collection("post").document(postId);
        Map<String , String> map = new HashMap<>();
        map.put("thumb_image",currentUser.getThumb_image());
        map.put("username",currentUser.getUsername());
        ref.set(map , SetOptions.merge());

    }
    private void updateMainPost(CurrentUser currentUser , String postId){
        DocumentReference ref = FirebaseFirestore.getInstance().collection("main-post")
                .document("post").collection("post").document(postId);
        Map<String , String> map = new HashMap<>();
        map.put("thumb_image",currentUser.getThumb_image());
        map.put("username",currentUser.getUsername());
        ref.set(map , SetOptions.merge());
    }

   public void  removeRequestBadgeCount (CurrentUser currentUser , OtherUser otherUser , TrueFalse<Boolean> callback){
       CollectionReference db = FirebaseFirestore.getInstance().collection("user")
               .document(currentUser.getUid())
               .collection("msg-request")
               .document(otherUser.getUid())
               .collection("badgeCount");
       db.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
           @Override
           public void onComplete(@NonNull Task<QuerySnapshot> task) {
               if (task.isSuccessful()){
                   if (task.getResult().isEmpty()){
                       callback.callBack(true);
                       return;
                   }else{
                       if (task.getResult()!=null){
                            for (DocumentSnapshot item : task.getResult().getDocuments()){
                                db.document(item.getId()).delete();
                            }
                            callback.callBack(true);
                       }else{
                           callback.callBack(true);
                           return;
                       }
                   }
               }
           }
       });
    }


    public void checkEmailVerfied(FirebaseUser user, TrueFalse<Boolean> callback){
        if (user.isEmailVerified()){
            callback.callBack(true);
        }else{
            callback.callBack(false);
        }
    }
    public void canSendNotificaiton(CurrentUser currentUser , OtherUser otherUser , TrueFalse<Boolean> callback){
        if (currentUser.getBlockList().contains(otherUser.getUid()) || currentUser.getBlockByOtherUser().contains(otherUser.getUid()) || otherUser.getBlockList().contains(currentUser.getUid()) ||otherUser.getBlockByOtherUser().contains(currentUser.getUid())){
            callback.callBack(false);
        }else{
            callback.callBack(true);
        }
    }
    public void checkBlock(String uid , CurrentUser currentUser , TrueFalse<Boolean> callback){
        if (currentUser.getBlockByOtherUser().contains(uid) || currentUser.getBlockList().contains(uid)){
            callback.callBack(false);
        }else{
            callback.callBack(true);
        }
    }
}
