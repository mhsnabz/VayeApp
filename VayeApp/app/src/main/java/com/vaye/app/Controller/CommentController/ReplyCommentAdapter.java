package com.vaye.app.Controller.CommentController;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.hendraanggrian.appcompat.widget.SocialView;
import com.kongzue.dialog.v3.TipDialog;
import com.kongzue.dialog.v3.WaitDialog;
import com.vaye.app.Controller.NotificationService.CommentNotificationService;
import com.vaye.app.Controller.NotificationService.MainPostNotification;
import com.vaye.app.Controller.NotificationService.MajorPostNotification;
import com.vaye.app.Controller.NotificationService.NoticesPostNotification;
import com.vaye.app.Controller.Profile.CurrentUserProfile;
import com.vaye.app.Controller.Profile.OtherUserProfileActivity;
import com.vaye.app.Interfaces.OtherUserService;
import com.vaye.app.Interfaces.StringCompletion;
import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.Model.CommentModel;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.LessonPostModel;
import com.vaye.app.Model.MainPostModel;
import com.vaye.app.Model.NoticesMainModel;
import com.vaye.app.Model.OtherUser;
import com.vaye.app.R;
import com.vaye.app.Services.CommentServis;
import com.vaye.app.Services.UserService;
import com.vaye.app.Util.Helper;

import java.util.ArrayList;

public class ReplyCommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    String TAG = "ReplyCommentAdapter";

    ArrayList<CommentModel> comments;
    CurrentUser currentUser;
    Context context;
    CommentModel targetCommentModel;
    LessonPostModel postModel;
    NoticesMainModel noticesPostModel;
    MainPostModel mainPostModel;
    Boolean istanceOfCurrentUserProfile = false;
    Boolean istanceOfOtherUserProfile = false;

    public ReplyCommentAdapter(ArrayList<CommentModel> comments, CurrentUser currentUser, Context context, CommentModel targetCommentModel, LessonPostModel postModel) {
        this.comments = comments;
        this.currentUser = currentUser;
        this.context = context;
        this.targetCommentModel = targetCommentModel;
        this.postModel = postModel;
        setHasStableIds(true);
        if (context instanceof CurrentUserProfile){
            Log.d("FollowersAdapter", "FollowersAdapter: " + "instanceof CurrentUserProfile");
            istanceOfCurrentUserProfile = true;

        }else{

            Log.d("FollowersAdapter", "FollowersAdapter: " + "not instanceof CurrentUserProfile");
            istanceOfCurrentUserProfile = false;
        }

        if (context instanceof OtherUserProfileActivity){
            istanceOfOtherUserProfile= true;
        }else{
            istanceOfOtherUserProfile = false;
        }
    }

    public ReplyCommentAdapter(ArrayList<CommentModel> comments, CurrentUser currentUser, Context context, CommentModel targetCommentModel, MainPostModel mainPostModel) {
        this.comments = comments;
        this.currentUser = currentUser;
        this.context = context;
        this.targetCommentModel = targetCommentModel;
        this.mainPostModel = mainPostModel;
        setHasStableIds(true);
        if (context instanceof CurrentUserProfile){
            Log.d("FollowersAdapter", "FollowersAdapter: " + "instanceof CurrentUserProfile");
            istanceOfCurrentUserProfile = true;

        }else{

            Log.d("FollowersAdapter", "FollowersAdapter: " + "not instanceof CurrentUserProfile");
            istanceOfCurrentUserProfile = false;
        }

        if (context instanceof  OtherUserProfileActivity){
            istanceOfOtherUserProfile= true;
        }else{
            istanceOfOtherUserProfile = false;
        }
    }

    public ReplyCommentAdapter(ArrayList<CommentModel> comments, CurrentUser currentUser, Context context, CommentModel targetCommentModel, NoticesMainModel noticesPostModel) {
        this.comments = comments;
        this.currentUser = currentUser;
        this.context = context;
        this.targetCommentModel = targetCommentModel;
        this.noticesPostModel = noticesPostModel;
        setHasStableIds(true);
        if (context instanceof CurrentUserProfile){
            Log.d("FollowersAdapter", "FollowersAdapter: " + "instanceof CurrentUserProfile");
            istanceOfCurrentUserProfile = true;

        }else{

            Log.d("FollowersAdapter", "FollowersAdapter: " + "not instanceof CurrentUserProfile");
            istanceOfCurrentUserProfile = false;
        }

        if (context instanceof  OtherUserProfileActivity){
            istanceOfOtherUserProfile= true;
        }else{
            istanceOfOtherUserProfile = false;
        }
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.commen_cell, parent, false);

        return new CommentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        CommentViewHolder viewHolder =(CommentViewHolder) holder;
        CommentModel model = comments.get(position);

        viewHolder.setMsgText(model.getComment());
        viewHolder.setName(model.getSenderName(),model.getUsername());
        viewHolder.setProfileImage(model.getSenderImage());
        viewHolder.setMsgText(model.getComment());
        viewHolder.setTime(model.getTime());
        viewHolder.setLikeBtn(model.getLikes(),currentUser);
        viewHolder.setLikeCount(String.valueOf(model.getLikes().size()));
        viewHolder.setReplyCount(model.getReplies().size());
        viewHolder.msgText.setOnMentionClickListener(new SocialView.OnClickListener() {
            @Override
            public void onClick(@NonNull SocialView view, @NonNull CharSequence username) {

                String _username = "@"+username.toString();
                if (_username.equals(currentUser.getUsername())){
                    if (!istanceOfCurrentUserProfile){
                        Intent i = new Intent(context , CurrentUserProfile.class);
                        i.putExtra("currentUser",currentUser);
                        context.startActivity(i);
                        Helper.shared().go((Activity) context);
                    }

                }else{
                    UserService.shared().getOthUserIdByMention("@"+username.toString(), new StringCompletion() {
                        @Override
                        public void getString(String otherUserId) {
                            if (otherUserId!=null){
                                if (!istanceOfOtherUserProfile){
                                    UserService.shared().getOtherUser((Activity) context, otherUserId, new OtherUserService() {
                                        @Override
                                        public void callback(OtherUser user) {
                                            if (user!=null){
                                                WaitDialog.dismiss();
                                                Intent i = new Intent(context , OtherUserProfileActivity.class);
                                                i.putExtra("currentUser",currentUser);
                                                i.putExtra("otherUser",user);
                                                context.startActivity(i);
                                                Helper.shared().go((Activity) context);
                                            }
                                        }
                                    });
                                }

                            }else{
                                TipDialog.show((AppCompatActivity) context, "Böyle Bir Kullanıcı Bulunmuyor", TipDialog.TYPE.ERROR);
                                TipDialog.dismiss(1000);
                            }

                        }
                    });

                }
            }
        });
        viewHolder.msgText.setOnHyperlinkClickListener(new SocialView.OnClickListener() {
            @Override
            public void onClick(@NonNull SocialView view, @NonNull CharSequence text) {
                String url = text.toString();
                if (!url.startsWith("http://") && !url.startsWith("https://"))
                    url = "http://" + url;
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                context.startActivity(browserIntent);
            }
        });
        viewHolder.likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewHolder.like_dislike_click(model, currentUser, new TrueFalse<Boolean>() {
                    @Override
                    public void callBack(Boolean _value) {
                        if (_value){
                            viewHolder.setLikeBtn(model.getLikes(),currentUser);
                            notifyDataSetChanged();
                            CommentServis.shared().setRepliedCommentLike(model,targetCommentModel.getCommentId(), currentUser, new TrueFalse<Boolean>() {
                                @Override
                                public void callBack(Boolean _value) {
                                    if (mainPostModel != null){
                                        CommentNotificationService.shared().setMainPostRepliedCommentLike(model,targetCommentModel,mainPostModel,currentUser,model.getComment(),MainPostNotification.type.replied_comment_like);
                                    }else if (noticesPostModel != null){
                                        CommentNotificationService.shared().setLessonPostRepliedCommentLike(model,targetCommentModel,postModel,currentUser,model.getComment(),MajorPostNotification.type.replied_comment_like);

                                    }else if (postModel != null){
                                        CommentNotificationService.shared().setNoticesPostRepliedCommentLike(model,targetCommentModel,noticesPostModel,currentUser,model.getComment(),NoticesPostNotification.type.replied_comment_like);

                                    }
                                }
                            });

                        }else{
                            viewHolder.setLikeBtn(model.getLikes(),currentUser);
                            notifyDataSetChanged();
                        }
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(hasStableIds);
    }
}
