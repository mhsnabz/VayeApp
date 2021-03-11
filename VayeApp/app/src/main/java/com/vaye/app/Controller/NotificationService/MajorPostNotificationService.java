package com.vaye.app.Controller.NotificationService;

import com.google.firebase.database.core.utilities.Utilities;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.LessonPostModel;
import com.vaye.app.Services.CommentService;
import com.vaye.app.Util.Helper;

import java.util.Calendar;

import okhttp3.internal.Util;

public class MajorPostNotificationService {
    private static final MajorPostNotificationService instance = new MajorPostNotificationService();

    public static MajorPostNotificationService shared() {
        return instance;
    }

    void setPostLike(String postType , LessonPostModel post , CurrentUser currentUser , String text , String type){
        if (post.getSenderUid().equals(currentUser.getUid())){
            return;
        }else{
            if (!post.getSenderUid().contains(currentUser.getUid())){
                String notificationId = String.valueOf(Calendar.getInstance().getTimeInMillis());
                DocumentReference ref = FirebaseFirestore.getInstance().collection("user")
                        .document(post.getSenderUid()).collection("notification")
                        .document(notificationId);
                ref.set(Helper.shared().getDictionary(postType,type,text,currentUser,notificationId,null,post.getPostId(),post.getLessonName(),null,null), SetOptions.merge());
                
            }
        }
    }
}
