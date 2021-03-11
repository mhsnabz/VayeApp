package com.vaye.app.Controller.NotificationService;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.protobuf.StringValue;
import com.vaye.app.Model.CommentModel;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.MainPostModel;
import com.vaye.app.Util.Helper;

import java.util.Calendar;

public class CommentNotificationService {
    private static final CommentNotificationService instance = new CommentNotificationService();

    public static CommentNotificationService shared() {
        return instance;
    }

    //TODO:main post comment
    public void likeMainPostRepliedComment(CommentModel targetCommentModel , MainPostModel post , CurrentUser currentUser , String text , String type){
        String notificaitonId = String.valueOf(Calendar.getInstance().getTimeInMillis());
        if (targetCommentModel.getSenderUid().equals(currentUser.getUid())){
            return;
        }else{
            if (!currentUser.getSlient().contains(post.getSenderUid())){
                DocumentReference db = FirebaseFirestore.getInstance().collection("user")
                        .document(targetCommentModel.getSenderUid())
                        .collection("notification")
                        .document(notificaitonId);
                db.set(Helper.shared().getDictionary(NotificationPostType.name.mainPost,type,text,currentUser,notificaitonId,targetCommentModel.getCommentId(),post.getPostId(),null,null,post.getPostType()), SetOptions.merge());
                PushNotificationService.shared().sendPushNotification(String.valueOf(Calendar.getInstance().getTimeInMillis()),targetCommentModel.getSenderUid(),null,PushNotificationTarget.comment,currentUser.getName(),text,MainPostNotification.descp.replied_comment_like,currentUser.getUid());
            }
        }
    }
//post : MainPostModel ,
// commentModel : CommentModel, currentUser : CurrentUser , text : String , type : String
    public void likeMainPostComment(MainPostModel post,CommentModel commentModel , CurrentUser currentUser , String text , String type){
        String notificaitonId = String.valueOf(Calendar.getInstance().getTimeInMillis());
        if (commentModel.getSenderUid().equals(currentUser.getUid()))
            return;
        else{
            if (!currentUser.getSlient().contains(commentModel.getSenderUid())){
                DocumentReference db = FirebaseFirestore.getInstance().collection("user")
                        .document(commentModel.getSenderUid())
                        .collection("notification")
                        .document(notificaitonId);
                db.set(Helper.shared().getDictionary(NotificationPostType.name.mainPost,type,text,currentUser,notificaitonId,null,post.getPostId(),null,null,post.getPostType()),SetOptions.merge());
                PushNotificationService.shared().sendPushNotification(String.valueOf(Calendar.getInstance().getTimeInMillis()),null,null,PushNotificationTarget.comment,currentUser.getName(),text,MainPostNotification.descp.comment_like,currentUser.getUid());
            }
        }
    }

}
