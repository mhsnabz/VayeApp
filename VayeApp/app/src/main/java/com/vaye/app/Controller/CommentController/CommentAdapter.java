package com.vaye.app.Controller.CommentController;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.ServerTimestamp;
import com.hendraanggrian.appcompat.widget.SocialTextView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.vaye.app.Controller.HomeController.LessonPostAdapter.MajorPostViewHolder;
import com.vaye.app.Controller.NotificationService.CommentNotificationService;
import com.vaye.app.Controller.NotificationService.MainPostNotification;
import com.vaye.app.Controller.NotificationService.MajorPostNotification;
import com.vaye.app.Controller.NotificationService.NoticesPostNotification;
import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.Model.CommentModel;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.LessonPostModel;
import com.vaye.app.Model.MainPostModel;
import com.vaye.app.Model.NoticesMainModel;
import com.vaye.app.R;
import com.vaye.app.Services.CommentService;
import com.vaye.app.Services.CommentServis;
import com.vaye.app.Util.Helper;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<CommentModel> comments;
    CurrentUser currentUser;
    Context context;
    LessonPostModel postModel;

    NoticesMainModel noticesPostModel;

    MainPostModel mainPostModel;


    public CommentAdapter(ArrayList<CommentModel> comments, CurrentUser currentUser, Context context, MainPostModel mainPostModel) {
        this.comments = comments;
        this.currentUser = currentUser;
        this.context = context;
        this.mainPostModel = mainPostModel;
        setHasStableIds(true);
    }

    public CommentAdapter(ArrayList<CommentModel> comments, CurrentUser currentUser, Context context, NoticesMainModel noticesPostModel) {
        this.comments = comments;
        this.currentUser = currentUser;
        this.context = context;
        this.noticesPostModel = noticesPostModel;
        setHasStableIds(true);
    }

    public CommentAdapter(ArrayList<CommentModel> comments, CurrentUser currentUser, Context context, LessonPostModel postModel) {
        this.comments = comments;
        this.currentUser = currentUser;
        this.context = context;
        this.postModel = postModel;
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
            viewHolder.like_dislike_click(model,currentUser, new TrueFalse<Boolean>() {
                @Override
                public void callBack(Boolean _value) {
                    if (_value){
                        viewHolder.setLikeBtn(model.getLikes(),currentUser);
                        notifyDataSetChanged();

                        CommentServis.shared().setCommentLike(model, currentUser, new TrueFalse<Boolean>() {
                            @Override
                            public void callBack(Boolean _value) {
                                if (mainPostModel != null){
                                    CommentNotificationService.shared().sendMainPostCommentLike(mainPostModel,model,currentUser,model.getComment(), MainPostNotification.type.comment_like);
                                }else if (noticesPostModel != null){
                                    CommentNotificationService.shared().sendNoticesPostCommentLike(noticesPostModel,model,currentUser,model.getComment(), NoticesPostNotification.type.comment_like);

                                }else if (postModel != null){
                                    CommentNotificationService.shared().sendLessonPostCommentLike(postModel,model,currentUser,model.getComment(), MajorPostNotification.type.comment_like);

                                }
                            }
                        });
                    }else{
                        viewHolder.setLikeBtn(model.getLikes(),currentUser);
                        notifyDataSetChanged();
                        CommentServis.shared().removeCommentLike(model,currentUser);
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
