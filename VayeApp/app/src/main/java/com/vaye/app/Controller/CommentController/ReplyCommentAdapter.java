package com.vaye.app.Controller.CommentController;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.Model.CommentModel;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.LessonPostModel;
import com.vaye.app.Model.MainPostModel;
import com.vaye.app.Model.NoticesMainModel;
import com.vaye.app.R;

import java.util.ArrayList;

public class ReplyCommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {



    ArrayList<CommentModel> comments;
    CurrentUser currentUser;
    Context context;
    CommentModel targetCommentModel;
    LessonPostModel postModel;
    NoticesMainModel noticesPostModel;
    MainPostModel mainPostModel;


    public ReplyCommentAdapter(ArrayList<CommentModel> comments, CurrentUser currentUser, Context context, CommentModel targetCommentModel, LessonPostModel postModel) {
        this.comments = comments;
        this.currentUser = currentUser;
        this.context = context;
        this.targetCommentModel = targetCommentModel;
        this.postModel = postModel;
        setHasStableIds(true);
    }

    public ReplyCommentAdapter(ArrayList<CommentModel> comments, CurrentUser currentUser, Context context, CommentModel targetCommentModel, MainPostModel mainPostModel) {
        this.comments = comments;
        this.currentUser = currentUser;
        this.context = context;
        this.targetCommentModel = targetCommentModel;
        this.mainPostModel = mainPostModel;
        setHasStableIds(true);
    }

    public ReplyCommentAdapter(ArrayList<CommentModel> comments, CurrentUser currentUser, Context context, CommentModel targetCommentModel, NoticesMainModel noticesPostModel) {
        this.comments = comments;
        this.currentUser = currentUser;
        this.context = context;
        this.targetCommentModel = targetCommentModel;
        this.noticesPostModel = noticesPostModel;
        setHasStableIds(true);
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
        viewHolder.likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewHolder.like_dislike_click(model, currentUser, new TrueFalse<Boolean>() {
                    @Override
                    public void callBack(Boolean _value) {
                        if (_value){
                            viewHolder.setLikeBtn(model.getLikes(),currentUser);
                            notifyDataSetChanged();
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
