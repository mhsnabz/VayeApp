package com.vaye.app.Controller.NotificationService;

import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.protobuf.StringValue;
import com.vaye.app.Interfaces.OtherUserService;
import com.vaye.app.Model.CommentModel;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.LessonPostModel;
import com.vaye.app.Model.MainPostModel;
import com.vaye.app.Model.NoticesMainModel;
import com.vaye.app.Model.OtherUser;
import com.vaye.app.Services.NotificaitonService;
import com.vaye.app.Services.UserService;
import com.vaye.app.Util.Helper;

import java.util.Calendar;

public class CommentNotificationService {
    String TAG = "CommentNotificationService";
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
                PushNotificationService.shared().sendPushNotification(String.valueOf(Calendar.getInstance().getTimeInMillis()),commentModel.getSenderUid(),null,PushNotificationTarget.comment,currentUser.getName(),text,MainPostNotification.descp.comment_like,currentUser.getUid());
            }
        }
    }



    //TODO:-lesson post comment

    public void sendNewLessonPostCommentNotification(LessonPostModel post , CurrentUser currentUser ,
                                                     String text , String type){
        String notificaitonId = String.valueOf(Calendar.getInstance().getTimeInMillis());
        if (post.getSenderUid().equals(currentUser.getUid())){
            return;
        }else{
            if (!currentUser.getSlient().contains(post.getSenderUid())){
                DocumentReference db = FirebaseFirestore.getInstance().collection("user")
                        .document(post.getSenderUid())
                    .collection("notification")
                        .document(notificaitonId);
                db.set(Helper.shared().getDictionary(NotificationPostType.name.lessonPost,type,text,currentUser,notificaitonId,null,post.getPostId(),post.getLessonName(),null,null),SetOptions.merge());
                PushNotificationService.shared().sendPushNotification(String.valueOf(Calendar.getInstance().getTimeInMillis()),post.getSenderUid(),null,PushNotificationTarget.comment,currentUser.getName(),text,MajorPostNotification.descp.new_comment,currentUser.getUid());
            }
        }
    }

    public void sendNewLessonPostMentionedComment(LessonPostModel post , CurrentUser currentUser ,
                                                  String text , String type){
        String notificaitonId = String.valueOf(Calendar.getInstance().getTimeInMillis());
        for (String item : Helper.shared().getMentionedUser(text)){
            String notId = String.valueOf(Calendar.getInstance().getTimeInMillis());
            UserService.shared().getOtherUser_Mentioned(item, new OtherUserService() {
                @Override
                public void callback(OtherUser otherUser) {
                    if (otherUser != null){
                        if (otherUser.getShort_school().equals(currentUser.getShort_school()))
                        {
                            if (otherUser.getBolum().equals(currentUser.getBolum())){
                                if (!currentUser.getUid().equals(otherUser.getUid())){
                                    DocumentReference db = FirebaseFirestore.getInstance().collection("user")
                                            .document(otherUser.getUid())
                                            .collection("notification")
                                            .document(notificaitonId);

                                    db.set(Helper.shared().getDictionary(NotificationPostType.name.lessonPost,type,text,currentUser,notId,null,post.getPostId(),post.getLessonName(),null,null));
                                    PushNotificationService.shared().sendPushNotification(String.valueOf(Calendar.getInstance().getTimeInMillis()),otherUser.getUid(),otherUser,PushNotificationTarget.comment,currentUser.getName(),text,MajorPostNotification.descp.new_mentioned_comment,currentUser.getUid());


                                }

                            }
                        }
                    }
                }
            });
        }
    }

    public void sendLessonPostRepliedComment(CommentModel targetCommentModel, LessonPostModel post , CurrentUser currentUser , String  text , String type){
        String notificaitonId = String.valueOf(Calendar.getInstance().getTimeInMillis());
        if (targetCommentModel.getSenderUid().equals(currentUser.getUid())){
            return;
        }else{
            if (!currentUser.getSlient().contains(targetCommentModel.getSenderUid())){
                DocumentReference db = FirebaseFirestore.getInstance().collection("user")
                        .document(targetCommentModel.getSenderUid())
                    .collection("notification")
                        .document(notificaitonId);
                Log.d(TAG, "sendLessonPostRepliedComment: "+targetCommentModel.getCommentId());
                db.set(Helper.shared().getDictionary(NotificationPostType.name.lessonPost,type,text,currentUser,notificaitonId,targetCommentModel.getCommentId(),targetCommentModel.getPostId(),post.getLessonName(),null,null),SetOptions.merge());
                PushNotificationService.shared().sendPushNotification(String.valueOf(Calendar.getInstance().getTimeInMillis()),targetCommentModel.getSenderUid(),null,PushNotificationTarget.comment,currentUser.getName(),text,MajorPostNotification.descp.new_replied_comment,currentUser.getUid());
            }
        }
    }

    public void sendLessonPostRepliedMentionComment(CommentModel targetCommentModel, LessonPostModel post , CurrentUser currentUser , String  text , String type){
        String notificaitonId = String.valueOf(Calendar.getInstance().getTimeInMillis());

        for (String  item : Helper.shared().getMentionedUser(text)){
            String notId = String.valueOf(Calendar.getInstance().getTimeInMillis());

            if (item.equals(currentUser.getUsername())){
                return;
            }else{
                UserService.shared().getOtherUser_Mentioned(item, new OtherUserService() {
                    @Override
                    public void callback(OtherUser otherUser) {
                        if (otherUser!=null){
                            if(otherUser.getShort_school().equals(currentUser.getShort_school())){
                                if (otherUser.getBolum().equals(currentUser.getBolum())){
                                    DocumentReference db = FirebaseFirestore.getInstance().collection("user")
                                            .document(otherUser.getUid())
                                            .collection("notification")
                                            .document(notificaitonId);

                                    db.set(Helper.shared().getDictionary(NotificationPostType.name.lessonPost,type,text,currentUser,notId,targetCommentModel.getCommentId(),post.getPostId(),post.getLessonName(),null,null));
                                    PushNotificationService.shared().sendPushNotification(String.valueOf(Calendar.getInstance().getTimeInMillis()),otherUser.getUid(),otherUser,PushNotificationTarget.comment,currentUser.getName(),text,MajorPostNotification.descp.new_replied_mentioned_comment,currentUser.getUid());
                                }
                            }
                        }
                    }
                });

            }
        }
    }

    public void sendLessonPostCommentLike(LessonPostModel post , CommentModel commentModel , CurrentUser currentUser , String text , String type){
        String  notificaitonId = String.valueOf(Calendar.getInstance().getTimeInMillis());
        if (commentModel.getSenderUid().equals(currentUser.getUid())){
            return;
        }else{
            if (!currentUser.getSlient().contains(commentModel.getSenderUid())){
                DocumentReference db = FirebaseFirestore.getInstance().collection("user")
                        .document(commentModel.getSenderUid())
                    .collection("notification")
                        .document(notificaitonId);
                db.set(Helper.shared().getDictionary(NotificationPostType.name.lessonPost,type,text,currentUser,notificaitonId,null,post.getPostId(),post.getLessonName(),null,null),SetOptions.merge());
                PushNotificationService.shared().sendPushNotification(String.valueOf(Calendar.getInstance().getTimeInMillis()),commentModel.getSenderUid(),null,PushNotificationTarget.comment,currentUser.getName(),text,MajorPostNotification.descp.comment_like,currentUser.getUid());
            }
        }
    }
    public void setLessonPostRepliedCommentLike(CommentModel comment , CommentModel targetCommentModel,LessonPostModel post , CurrentUser currentUser,
                                                  String text , String  type){
        String  notificaitonId = String.valueOf(Calendar.getInstance().getTimeInMillis());
        if (comment.getSenderUid().equals(currentUser.getUid())){
            return;
        }else{
            if (!currentUser.getSlient().contains(comment.getSenderUid())){
                DocumentReference db = FirebaseFirestore.getInstance().collection("user")
                        .document(comment.getSenderUid())
                        .collection("notification")
                        .document(notificaitonId);
                db.update(Helper.shared().getDictionary(NotificationPostType.name.mainPost,type,text,currentUser,notificaitonId,targetCommentModel.getCommentId(),targetCommentModel.getPostId(),post.getLessonName(),null,null));
                PushNotificationService.shared().sendPushNotification(String.valueOf(Calendar.getInstance().getTimeInMillis()),comment.getSenderUid(),null,PushNotificationTarget.comment,currentUser.getName(),text,MajorPostNotification.descp.replied_comment_like,currentUser.getUid());

            }
        }
    }


    //TODO:-main post comment
    public void sendNewMainPostCommentNotification(MainPostModel post , CurrentUser currentUser ,
                                                     String text , String type){
        String notificaitonId = String.valueOf(Calendar.getInstance().getTimeInMillis());
        if (post.getSenderUid().equals(currentUser.getUid())){
            return;
        }else{
            if (!currentUser.getSlient().contains(post.getSenderUid())){
                DocumentReference db = FirebaseFirestore.getInstance().collection("user")
                        .document(post.getSenderUid())
                        .collection("notification")
                        .document(notificaitonId);
                db.set(Helper.shared().getDictionary(NotificationPostType.name.mainPost,type,text,currentUser,notificaitonId,null,post.getPostId(),null,null,post.getPostType()),SetOptions.merge());
                PushNotificationService.shared().sendPushNotification(String.valueOf(Calendar.getInstance().getTimeInMillis()),post.getSenderUid(),null,PushNotificationTarget.comment,currentUser.getName(),text,MainPostNotification.descp.new_comment,currentUser.getUid());
            }
        }
    }

    public void sendNewMainPostMentionedComment(MainPostModel post , CurrentUser currentUser ,
                                                  String text , String type){
        String notificaitonId = String.valueOf(Calendar.getInstance().getTimeInMillis());
        for (String item : Helper.shared().getMentionedUser(text)){
            String notId = String.valueOf(Calendar.getInstance().getTimeInMillis());
            UserService.shared().getOtherUser_Mentioned(item, new OtherUserService() {
                @Override
                public void callback(OtherUser otherUser) {
                    if (otherUser != null){

                                if (!currentUser.getUid().equals(otherUser.getUid())){
                                    DocumentReference db = FirebaseFirestore.getInstance().collection("user")
                                            .document(otherUser.getUid())
                                            .collection("notification")
                                            .document(notificaitonId);

                                    db.set(Helper.shared().getDictionary(NotificationPostType.name.mainPost,type,text,currentUser,notId,null,post.getPostId(),null,null,post.getPostType()),SetOptions.merge());
                                    PushNotificationService.shared().sendPushNotification(String.valueOf(Calendar.getInstance().getTimeInMillis()),otherUser.getUid(),otherUser,PushNotificationTarget.comment,currentUser.getName(),text,MainPostNotification.descp.new_mentioned_comment,currentUser.getUid());


                                }

                            }
                        }

            });
        }
    }
    public void sendMainPostCommentLike(MainPostModel post , CommentModel commentModel , CurrentUser currentUser , String text , String type){
        String  notificaitonId = String.valueOf(Calendar.getInstance().getTimeInMillis());
        if (commentModel.getSenderUid().equals(currentUser.getUid())){
            return;
        }else{
            if (!currentUser.getSlient().contains(commentModel.getSenderUid())){
                DocumentReference db = FirebaseFirestore.getInstance().collection("user")
                        .document(commentModel.getSenderUid())
                        .collection("notification")
                        .document(notificaitonId);
                db.set(Helper.shared().getDictionary(NotificationPostType.name.mainPost,type,text,currentUser,notificaitonId,null,post.getPostId(),null,null,post.getPostType()),SetOptions.merge());
                PushNotificationService.shared().sendPushNotification(String.valueOf(Calendar.getInstance().getTimeInMillis()),commentModel.getSenderUid(),null,PushNotificationTarget.comment,currentUser.getName(),text,MainPostNotification.descp.comment_like,currentUser.getUid());
            }
        }
    }

    public void setMainPostRepliedCommentLike(CommentModel comment , CommentModel targetCommentModel,MainPostModel post , CurrentUser currentUser,
                                              String text , String  type){
        String  notificaitonId = String.valueOf(Calendar.getInstance().getTimeInMillis());
        if (comment.getSenderUid().equals(currentUser.getUid())){
            return;
        }else{
            if (!currentUser.getSlient().contains(comment.getSenderUid())){
                DocumentReference db = FirebaseFirestore.getInstance().collection("user")
                        .document(comment.getSenderUid())
                    .collection("notification")
                        .document(notificaitonId);
                db.set(Helper.shared().getDictionary(NotificationPostType.name.mainPost,type,text,currentUser,notificaitonId,targetCommentModel.getCommentId(),targetCommentModel.getPostId(),null,null,post.getPostType()),SetOptions.merge());
                PushNotificationService.shared().sendPushNotification(String.valueOf(Calendar.getInstance().getTimeInMillis()),comment.getSenderUid(),null,PushNotificationTarget.comment,currentUser.getName(),text,MainPostNotification.descp.replied_comment_like,currentUser.getUid());

            }
        }
    }


    public void sendMainPostRepliedComment(CommentModel targetCommentModel, MainPostModel post , CurrentUser currentUser , String  text , String type){
        String notificaitonId = String.valueOf(Calendar.getInstance().getTimeInMillis());
        if (targetCommentModel.getSenderUid().equals(currentUser.getUid())){
            return;
        }else{
            if (!currentUser.getSlient().contains(targetCommentModel.getSenderUid())){
                DocumentReference db = FirebaseFirestore.getInstance().collection("user")
                        .document(targetCommentModel.getSenderUid())
                        .collection("notification")
                        .document(notificaitonId);
                db.set(Helper.shared().getDictionary(NotificationPostType.name.mainPost,type,text,currentUser,notificaitonId,targetCommentModel.getCommentId(),targetCommentModel.getPostId(),null,null,post.getPostType()),SetOptions.merge());
                PushNotificationService.shared().sendPushNotification(String.valueOf(Calendar.getInstance().getTimeInMillis()),targetCommentModel.getSenderUid(),null,PushNotificationTarget.comment,currentUser.getName(),text,MainPostNotification.descp.new_replied_comment,currentUser.getUid());
            }
        }
    }

    public void sendMainPostRepliedMentionComment(CommentModel targetCommentModel, MainPostModel post , CurrentUser currentUser , String  text , String type){
        String notificaitonId = String.valueOf(Calendar.getInstance().getTimeInMillis());

        for (String  item : Helper.shared().getMentionedUser(text)){
            String notId = String.valueOf(Calendar.getInstance().getTimeInMillis());

            if (item.equals(currentUser.getUsername())){
                return;
            }else{
                UserService.shared().getOtherUser_Mentioned(item, new OtherUserService() {
                    @Override
                    public void callback(OtherUser otherUser) {
                        if (otherUser!=null){

                                    DocumentReference db = FirebaseFirestore.getInstance().collection("user")
                                            .document(otherUser.getUid())
                                            .collection("notification")
                                            .document(notificaitonId);

                                    db.set(Helper.shared().getDictionary(NotificationPostType.name.mainPost,type,text,currentUser,notId,targetCommentModel.getCommentId(),post.getPostId(),null,null,post.getPostType()),SetOptions.merge());
                                    PushNotificationService.shared().sendPushNotification(String.valueOf(Calendar.getInstance().getTimeInMillis()),otherUser.getUid(),otherUser,PushNotificationTarget.comment,currentUser.getName(),text,MainPostNotification.descp.new_replied_mentioned_comment,currentUser.getUid());

                        }
                    }
                });

            }
        }
    }


    //TODO:-notices post comment
    public void sendNewNatoicesPostCommentNotification(NoticesMainModel post , CurrentUser currentUser ,
                                                       String text , String type){
        String notificaitonId = String.valueOf(Calendar.getInstance().getTimeInMillis());
        if (post.getSenderUid().equals(currentUser.getUid())){
            return;
        }else{
            if (!currentUser.getSlient().contains(post.getSenderUid())){
                DocumentReference db = FirebaseFirestore.getInstance().collection("user")
                        .document(post.getSenderUid())
                        .collection("notification")
                        .document(notificaitonId);
                db.set(Helper.shared().getDictionary(NotificationPostType.name.notices,type,text,currentUser,notificaitonId,null,post.getPostId(),null,post.getClupName(),null),SetOptions.merge());
                PushNotificationService.shared().sendPushNotification(String.valueOf(Calendar.getInstance().getTimeInMillis()),post.getSenderUid(),null,PushNotificationTarget.comment,currentUser.getName(),text,NoticesPostNotification.descp.new_comment,currentUser.getUid());
            }
        }
    }

    public void sendNewNoticesPostMentionedComment(NoticesMainModel post , CurrentUser currentUser ,
                                                String text , String type){
        String notificaitonId = String.valueOf(Calendar.getInstance().getTimeInMillis());
        for (String item : Helper.shared().getMentionedUser(text)){
            String notId = String.valueOf(Calendar.getInstance().getTimeInMillis());
            UserService.shared().getOtherUser_Mentioned(item, new OtherUserService() {
                @Override
                public void callback(OtherUser otherUser) {
                    if (otherUser != null){
                        if (otherUser.getShort_school().equals(currentUser.getShort_school()))
                        {
                            if (otherUser.getBolum().equals(currentUser.getBolum())){
                                if (!currentUser.getUid().equals(otherUser.getUid())){
                                    DocumentReference db = FirebaseFirestore.getInstance().collection("user")
                                            .document(otherUser.getUid())
                                            .collection("notification")
                                            .document(notificaitonId);

                                    db.set(Helper.shared().getDictionary(NotificationPostType.name.notices,type,text,currentUser,notId,null,post.getPostId(),null,post.getClupName(),null));
                                    PushNotificationService.shared().sendPushNotification(String.valueOf(Calendar.getInstance().getTimeInMillis()),otherUser.getUid(),otherUser,PushNotificationTarget.comment,currentUser.getName(),text,NoticesPostNotification.descp.new_mentioned_comment,currentUser.getUid());


                                }

                            }
                        }
                    }
                }
            });
        }
    }

    public void sendNoticesPostRepliedComment(CommentModel targetCommentModel, NoticesMainModel post , CurrentUser currentUser , String  text , String type){
        String notificaitonId = String.valueOf(Calendar.getInstance().getTimeInMillis());
        if (targetCommentModel.getSenderUid().equals(currentUser.getUid())){
            return;
        }else{
            if (!currentUser.getSlient().contains(targetCommentModel.getSenderUid())){
                DocumentReference db = FirebaseFirestore.getInstance().collection("user")
                        .document(targetCommentModel.getSenderUid())
                        .collection("notification")
                        .document(notificaitonId);
                db.set(Helper.shared().getDictionary(NotificationPostType.name.notices,type,text,currentUser,notificaitonId,targetCommentModel.getCommentId(),targetCommentModel.getPostId(),null,post.getClupName(),null),SetOptions.merge());
                PushNotificationService.shared().sendPushNotification(String.valueOf(Calendar.getInstance().getTimeInMillis()),targetCommentModel.getSenderUid(),null,PushNotificationTarget.comment,currentUser.getName(),text,NoticesPostNotification.descp.new_replied_comment,currentUser.getUid());
            }
        }
    }

    public void sendNoticesPostRepliedMentionComment(CommentModel targetCommentModel, NoticesMainModel post , CurrentUser currentUser , String  text , String type){
        String notificaitonId = String.valueOf(Calendar.getInstance().getTimeInMillis());

        for (String  item : Helper.shared().getMentionedUser(text)){
            String notId = String.valueOf(Calendar.getInstance().getTimeInMillis());

            if (item.equals(currentUser.getUsername())){
                return;
            }else{
                UserService.shared().getOtherUser_Mentioned(item, new OtherUserService() {
                    @Override
                    public void callback(OtherUser otherUser) {
                        if (otherUser!=null){
                            if(otherUser.getShort_school().equals(currentUser.getShort_school())){
                                if (otherUser.getBolum().equals(currentUser.getBolum())){
                                    DocumentReference db = FirebaseFirestore.getInstance().collection("user")
                                            .document(otherUser.getUid())
                                            .collection("notification")
                                            .document(notificaitonId);

                                    db.set(Helper.shared().getDictionary(NotificationPostType.name.notices,type,text,currentUser,notId,targetCommentModel.getCommentId(),post.getPostId(),null,post.getClupName(),null));
                                    PushNotificationService.shared().sendPushNotification(String.valueOf(Calendar.getInstance().getTimeInMillis()),otherUser.getUid(),otherUser,PushNotificationTarget.comment,currentUser.getName(),text,NoticesPostNotification.descp.new_replied_mentioned_comment,currentUser.getUid());
                                }
                            }
                        }
                    }
                });

            }
        }
    }


    public void sendNoticesPostCommentLike(NoticesMainModel post , CommentModel commentModel , CurrentUser currentUser , String text , String type){
        String  notificaitonId = String.valueOf(Calendar.getInstance().getTimeInMillis());
        if (commentModel.getSenderUid().equals(currentUser.getUid())){
            return;
        }else{
            if (!currentUser.getSlient().contains(commentModel.getSenderUid())){
                DocumentReference db = FirebaseFirestore.getInstance().collection("user")
                        .document(commentModel.getSenderUid())
                        .collection("notification")
                        .document(notificaitonId);
                db.set(Helper.shared().getDictionary(NotificationPostType.name.notices,type,text,currentUser,notificaitonId,null,post.getPostId(),null,post.getClupName(),null),SetOptions.merge());
                PushNotificationService.shared().sendPushNotification(String.valueOf(Calendar.getInstance().getTimeInMillis()),commentModel.getSenderUid(),null,PushNotificationTarget.comment,currentUser.getName(),text,NoticesPostNotification.descp.comment_like,currentUser.getUid());
            }
        }
    }
    public void setNoticesPostRepliedCommentLike(CommentModel comment , CommentModel targetCommentModel,NoticesMainModel post , CurrentUser currentUser,
                                              String text , String  type){
        String  notificaitonId = String.valueOf(Calendar.getInstance().getTimeInMillis());
        if (comment.getSenderUid().equals(currentUser.getUid())){
            return;
        }else{
            if (!currentUser.getSlient().contains(comment.getSenderUid())){
                DocumentReference db = FirebaseFirestore.getInstance().collection("user")
                        .document(comment.getSenderUid())
                        .collection("notification")
                        .document(notificaitonId);
                db.update(Helper.shared().getDictionary(NotificationPostType.name.mainPost,type,text,currentUser,notificaitonId,targetCommentModel.getCommentId(),targetCommentModel.getPostId(),null,post.getClupName(),null));
                PushNotificationService.shared().sendPushNotification(String.valueOf(Calendar.getInstance().getTimeInMillis()),comment.getSenderUid(),null,PushNotificationTarget.comment,currentUser.getName(),text,NoticesPostNotification.descp.replied_comment_like,currentUser.getUid());

            }
        }
    }

}
