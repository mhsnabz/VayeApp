package com.vaye.app.Controller.VayeAppController.CommentController;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vaye.app.Controller.HomeController.SinglePost.CommentAdapter;
import com.vaye.app.Interfaces.Notifications;
import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.Model.CommentModel;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.LessonPostModel;
import com.vaye.app.Model.MainPostModel;
import com.vaye.app.R;
import com.vaye.app.Services.CommentService;
import com.vaye.app.Services.MainPostService;

import java.util.ArrayList;

public class MainPostCommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<CommentModel> comments;
    CurrentUser currentUser;
    Context context;
    MainPostModel postModel;

    public MainPostCommentAdapter(ArrayList<CommentModel> comments, CurrentUser currentUser, Context context, MainPostModel postModel) {
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
        viewHolder.likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MainPostService.shared().setCommentLike(model, Notifications.NotificationType.comment_like, Notifications.NotificationDescription.comment_like, postModel, currentUser, new TrueFalse<Boolean>() {
                    @Override
                    public void callBack(Boolean _value) {
                        if (_value){
                            viewHolder.setLikeBtn(currentUser,model.getLikes());
                            notifyDataSetChanged();
                        }else{
                            viewHolder.setLikeBtn(currentUser,model.getLikes());
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
}
