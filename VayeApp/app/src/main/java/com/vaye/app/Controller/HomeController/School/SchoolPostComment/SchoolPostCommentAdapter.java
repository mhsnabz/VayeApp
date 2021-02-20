package com.vaye.app.Controller.HomeController.School.SchoolPostComment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.hendraanggrian.appcompat.widget.SocialView;
import com.kongzue.dialog.v3.TipDialog;
import com.kongzue.dialog.v3.WaitDialog;
import com.vaye.app.Controller.Profile.CurrentUserProfile;
import com.vaye.app.Controller.Profile.OtherUserProfileActivity;
import com.vaye.app.Controller.VayeAppController.CommentController.MainPostCommentViewHolder;
import com.vaye.app.Interfaces.Notifications;
import com.vaye.app.Interfaces.OtherUserService;
import com.vaye.app.Interfaces.StringCompletion;
import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.Model.CommentModel;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.MainPostModel;
import com.vaye.app.Model.NoticesMainModel;
import com.vaye.app.Model.OtherUser;
import com.vaye.app.R;
import com.vaye.app.Services.MainPostService;
import com.vaye.app.Services.UserService;
import com.vaye.app.Util.Helper;

import java.util.ArrayList;

public class SchoolPostCommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<CommentModel> comments;
    CurrentUser currentUser;
    Context context;
    NoticesMainModel postModel;

    public SchoolPostCommentAdapter(ArrayList<CommentModel> comments, CurrentUser currentUser, Context context, NoticesMainModel postModel) {
        this.comments = comments;
        this.currentUser = currentUser;
        this.context = context;
        this.postModel = postModel;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.commen_cell, parent, false);

        return new MainPostCommentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MainPostCommentViewHolder viewHolder = (MainPostCommentViewHolder) holder;
        CommentModel model = comments.get(position);

        viewHolder.setMsgText(model.getComment());
        viewHolder.setName(model.getSenderName(),model.getUsername());
        viewHolder.setProfileImage(model.getSenderImage());
        viewHolder.setMsgText(model.getComment());
        viewHolder.setTime(model.getTime());
        viewHolder.setLikeBtn(currentUser,model.getLikes());
        viewHolder.setLikeCount(String.valueOf(model.getLikes().size()));
        viewHolder.setReplyCount(model.getReplies().size());
        viewHolder.msgText.setOnMentionClickListener(new SocialView.OnClickListener() {
            @Override
            public void onClick(@NonNull SocialView view, @NonNull CharSequence username) {

                String _username = "@"+username.toString();
                if (_username.equals(currentUser.getUsername())){
                    Intent i = new Intent(context , CurrentUserProfile.class);
                    i.putExtra("currentUser",currentUser);
                    context.startActivity(i);
                    Helper.shared().go((Activity) context);
                }else{
                    UserService.shared().getOthUserIdByMention("@"+username.toString(), new StringCompletion() {
                        @Override
                        public void getString(String otherUserId) {
                            if (otherUserId!=null){
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
                            }else{
                                TipDialog.show((AppCompatActivity) context, "Böyle Bir Kullanıcı Bulunmuyor", TipDialog.TYPE.ERROR);
                                TipDialog.dismiss(1000);
                            }

                        }
                    });

                }
            }
        });

        viewHolder.likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        viewHolder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (model.getSenderUid().equals(currentUser.getUid())){
                    Intent i = new Intent(context , CurrentUserProfile.class);
                    i.putExtra("currentUser",currentUser);
                    context.startActivity(i);
                    Helper.shared().go((Activity) context);
                }else{
                    UserService.shared().getOtherUser((Activity) context, model.getSenderUid(), new OtherUserService() {
                        @Override
                        public void callback(OtherUser user) {
                            if (user!=null){
                                Intent i = new Intent(context , OtherUserProfileActivity.class);
                                i.putExtra("otherUser",user);
                                i.putExtra("currentUser",currentUser);
                                context.startActivity(i);
                                Helper.shared().go((Activity) context);
                            }
                        }
                    });
                }
            }
        });

        viewHolder.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (model.getSenderUid().equals(currentUser.getUid())){
                    Intent i = new Intent(context , CurrentUserProfile.class);
                    i.putExtra("currentUser",currentUser);
                    context.startActivity(i);
                    Helper.shared().go((Activity) context);
                }else{
                    UserService.shared().getOtherUser((Activity) context, model.getSenderUid(), new OtherUserService() {
                        @Override
                        public void callback(OtherUser user) {
                            if (user!=null){
                                Intent i = new Intent(context , OtherUserProfileActivity.class);
                                i.putExtra("otherUser",user);
                                i.putExtra("currentUser",currentUser);
                                context.startActivity(i);
                                Helper.shared().go((Activity) context);
                            }
                        }
                    });
                }

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
}
