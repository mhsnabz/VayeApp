package com.vaye.app.Controller.NotificationController;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;

import com.kongzue.dialog.v3.TipDialog;
import com.kongzue.dialog.v3.WaitDialog;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.vaye.app.Controller.CommentController.CommentActivity;
import com.vaye.app.Controller.CommentController.ReplyActivity;
import com.vaye.app.Controller.HomeController.SinglePost.SinglePostActivity;
import com.vaye.app.Controller.NotificationService.MainPostNotification;
import com.vaye.app.Controller.NotificationService.MajorPostNotification;
import com.vaye.app.Controller.NotificationService.NoticesPostNotification;
import com.vaye.app.Controller.NotificationService.NotificationPostType;
import com.vaye.app.Controller.NotificationService.PushNotificationService;
import com.vaye.app.Interfaces.RepliedCommentModel;
import com.vaye.app.Interfaces.SingleLessonPost;
import com.vaye.app.Interfaces.SingleMainPost;
import com.vaye.app.Interfaces.SingleNoticesPost;
import com.vaye.app.Model.CommentModel;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.LessonPostModel;
import com.vaye.app.Model.MainPostModel;
import com.vaye.app.Model.NoticesMainModel;
import com.vaye.app.Model.NotificationModel;
import com.vaye.app.R;
import com.vaye.app.Services.CommentServis;
import com.vaye.app.Services.MainPostService;
import com.vaye.app.Services.MajorPostService;
import com.vaye.app.Services.SchoolPostService;
import com.vaye.app.Util.Helper;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    String TAG = "NotificationAdapter";
    ArrayList<NotificationModel> models;
    CurrentUser currentUser;
    Context context;

    public NotificationAdapter(ArrayList<NotificationModel> models, CurrentUser currentUser, Context context) {
        this.models = models;
        this.currentUser = currentUser;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_notification_item, parent, false);

        return new NotificaitonViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            NotificaitonViewHolder viewHolder = (NotificaitonViewHolder)holder;
            NotificationModel model = models.get(position);
            viewHolder.setName(model.getSenderName());
            viewHolder.setUsername(model.getUsername());
            viewHolder.setTime(model.getTime());
            viewHolder.setProfileImage(model.getSenderImage());
            viewHolder.setCard(model.getIsRead());
            viewHolder.setMainText(model);
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    WaitDialog.show((AppCompatActivity)context , "");
                    if (model.getPostType().equals(NotificationPostType.name.lessonPost)){

                        if (model.getType().equals(MajorPostNotification.type.comment_like) ||
                                model.getType().equals(MajorPostNotification.type.new_comment)||
                            model.getType().equals(MajorPostNotification.type.new_mentioned_comment))
                        {
                            viewHolder.showComment(model);
                        }
                        else if (model.getType().equals(MajorPostNotification.type.new_post)||
                        model.getType().equals(MajorPostNotification.type.new_mentioned_post)||
                        model.getType().equals(MajorPostNotification.type.post_like)){
                            viewHolder.showPost(model);
                        }else if (model.getType().equals(MajorPostNotification.type.new_replied_comment)||
                        model.getType().equals(MajorPostNotification.type.new_replied_mentioned_comment)||
                        model.getType().equals(MajorPostNotification.type.replied_comment_like)){
                            viewHolder.showRepliedComment(model);
                        }


                    }
                    else if (model.getPostType().equals(NotificationPostType.name.mainPost)){


                        if (model.getType().equals(MainPostNotification.type.comment_like) ||
                                model.getType().equals(MainPostNotification.type.new_comment)||
                                model.getType().equals(MainPostNotification.type.new_mentioned_comment))
                        {

                            viewHolder.showComment(model);
                        }
                        else if (model.getType().equals(MainPostNotification.type.new_post)||
                                model.getType().equals(MainPostNotification.type.new_mentioned_post)||
                                model.getType().equals(MainPostNotification.type.post_like))
                        {

                            viewHolder.showPost(model);

                        }else if (model.getType().equals(MainPostNotification.type.new_replied_comment)||
                                model.getType().equals(MainPostNotification.type.new_replied_mentioned_comment)||
                                model.getType().equals(MainPostNotification.type.replied_comment_like)){
                            viewHolder.showRepliedComment(model);
                        }


                    }
                    else if (model.getPostType().equals(NotificationPostType.name.notices)){


                        if (model.getType().equals(NoticesPostNotification.type.comment_like) ||
                                model.getType().equals(NoticesPostNotification.type.new_comment)||
                                model.getType().equals(NoticesPostNotification.type.new_mentioned_comment))
                        {
                            viewHolder.showComment(model);
                        }
                        else if (model.getType().equals(NoticesPostNotification.type.new_post)||
                                model.getType().equals(NoticesPostNotification.type.new_mentioned_post)||
                                model.getType().equals(NoticesPostNotification.type.post_like))
                        {
                           viewHolder.showPost(model);


                        }else if (model.getType().equals(NoticesPostNotification.type.new_replied_comment)||
                                model.getType().equals(NoticesPostNotification.type.new_replied_mentioned_comment)||
                                model.getType().equals(NoticesPostNotification.type.replied_comment_like)){
                           viewHolder.showRepliedComment(model);
                        }


                    }
                }
            });

    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    class NotificaitonViewHolder extends  RecyclerView.ViewHolder{

        public NotificaitonViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public CircleImageView profileImage = (CircleImageView)itemView.findViewById(R.id.profileImage);
        public ProgressBar progressBar = (ProgressBar)itemView.findViewById(R.id.progress);
        public TextView name = (TextView)itemView.findViewById(R.id.name);
        public TextView username = (TextView)itemView.findViewById(R.id.username);
        public TextView time = (TextView)itemView.findViewById(R.id.time);
        public TextView mainText = (TextView)itemView.findViewById(R.id.mainText);
        public RelativeLayout relView = (RelativeLayout)itemView.findViewById(R.id.relView);

        public void setCard(String _isSeen){

            if (_isSeen.equals("true")){

                relView.setBackgroundColor(context.getResources().getColor(R.color.white));

            }else{
                relView.setBackgroundColor(context.getResources().getColor(R.color.mainColorTransparent));


            }
        }
        public void setName(String _text){
            name.setText(_text);
        }
        public void setUsername(String _text){
            username.setText(_text);
        }

        public void setTime(Timestamp _time){
            time.setText(Html.fromHtml("&#8226;")+ Helper.shared().setTimeAgo(_time));
        }
        public void setProfileImage(String url){
            if (url !=null && !url.isEmpty()){
                Picasso.get().load(url)
                        .centerCrop()
                        .resize(128,128)
                        .into(profileImage, new Callback() {
                            @Override
                            public void onSuccess() {
                                progressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError(Exception e) {
                                profileImage.setImageResource(android.R.color.darker_gray);
                                progressBar.setVisibility(View.GONE);
                            }
                        });
            }else{
                profileImage.setImageResource(android.R.color.darker_gray);
                progressBar.setVisibility(View.GONE);
            }
        }
        public void setMainText(NotificationModel model){
            String text = Helper.shared().getMainText(model) +" : ";
            String text2 = text + model.getText() ;

            Spannable spannable = new SpannableString(text2);

            spannable.setSpan(new ForegroundColorSpan(Color.BLACK), text.length(), (text + model.getText()).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            mainText.setText(spannable, TextView.BufferType.SPANNABLE);

        }

        public void showPost(NotificationModel model){
            if (model.getPostType().equals(NotificationPostType.name.notices))
            {
                SchoolPostService.shared().getPost(context, currentUser, model.getPostId(), new SingleNoticesPost() {
                    @Override
                    public void getPost(NoticesMainModel post) {
                        if (post!=null){
                            Intent i = new Intent(context, SinglePostActivity.class);
                            i.putExtra("currentUser",currentUser);
                            i.putExtra("noticesPost",post);
                            context.startActivity(i);
                            Helper.shared().go((Activity)context);
                            PushNotificationService.shared().makeReadLocalNotification(currentUser,model.getNot_id());
                            model.setIsRead("true");
                            notifyDataSetChanged();
                            WaitDialog.dismiss();
                        }
                    }
                });
            }
            else if (model.getPostType().equals(NotificationPostType.name.lessonPost)){
                MajorPostService.shared().getPost(context, currentUser, model.getPostId(), new SingleLessonPost() {
                    @Override
                    public void getPost(LessonPostModel postModel) {
                        if (postModel != null){
                            Intent i = new Intent(context, SinglePostActivity.class);
                            i.putExtra("currentUser",currentUser);
                            i.putExtra("lessonPost",postModel);
                            context.startActivity(i);
                            Helper.shared().go((Activity)context);
                            PushNotificationService.shared().makeReadLocalNotification(currentUser,model.getNot_id());
                            model.setIsRead("true");
                            notifyDataSetChanged();
                            WaitDialog.dismiss();
                        }

                    }
                });
            }else if (model.getPostType().equals(NotificationPostType.name.mainPost))
            {
                MainPostService.shared().getMainPost(context, currentUser, model.getPostId(), new SingleMainPost() {
                    @Override
                    public void getPost(MainPostModel post) {
                        if (post!=null){
                            Intent i = new Intent(context, SinglePostActivity.class);
                            i.putExtra("currentUser",currentUser);
                            i.putExtra("mainPost",post);
                            context.startActivity(i);
                            Helper.shared().go((Activity)context);
                            PushNotificationService.shared().makeReadLocalNotification(currentUser,model.getNot_id());
                            model.setIsRead("true");
                            notifyDataSetChanged();
                            WaitDialog.dismiss();
                        }
                    }
                });
            }
            else{
                WaitDialog.dismiss();
                return;
            }
        }

        public void showComment(NotificationModel model){
            if (model.getPostType().equals(NotificationPostType.name.notices))
            {
                SchoolPostService.shared().getPost(context, currentUser, model.getPostId(), new SingleNoticesPost() {
                    @Override
                    public void getPost(NoticesMainModel post) {
                        if (post!=null){
                            Intent i = new Intent(context, CommentActivity.class);
                            i.putExtra("noticesPost",post);
                            i.putExtra("currentUser",currentUser);
                            context.startActivity(i);
                            Helper.shared().go((Activity)context);
                            WaitDialog.dismiss();
                        }
                    }
                });
            }
            else if (model.getPostType().equals(NotificationPostType.name.lessonPost)){
                MajorPostService.shared().getPost(context, currentUser, model.getPostId(), new SingleLessonPost() {
                    @Override
                    public void getPost(LessonPostModel post) {
                        if (post!=null){
                            Intent i = new Intent(context, CommentActivity.class);
                            i.putExtra("lessonPost",post);
                            i.putExtra("currentUser",currentUser);
                            context.startActivity(i);
                            Helper.shared().go((Activity)context);
                            WaitDialog.dismiss();
                        }
                    }
                });
            }else if (model.getPostType().equals(NotificationPostType.name.mainPost))
            {
                MainPostService.shared().getMainPost(context, currentUser, model.getPostId(), new SingleMainPost() {
                    @Override
                    public void getPost(MainPostModel post) {
                        if (post!=null){
                            Intent i = new Intent(context, CommentActivity.class);
                            i.putExtra("mainPost",post);
                            i.putExtra("currentUser",currentUser);
                            context.startActivity(i);
                            Helper.shared().go((Activity)context);
                            WaitDialog.dismiss();
                        }
                    }
                });
            }
            else{
                WaitDialog.dismiss();
                return;
            }
        }
        public void showRepliedComment(NotificationModel model ){
            if (model.getTargetCommentId()==null&&model.getTargetCommentId().isEmpty()){
                WaitDialog.dismiss();
                TipDialog.show((AppCompatActivity)context,"Gönderiye Ulaşılamıyor", TipDialog.TYPE.ERROR);
                TipDialog.dismiss(1000);
                return;
            }
            if (model.getPostType().equals(NotificationPostType.name.notices))
            {
                SchoolPostService.shared().getPost(context, currentUser, model.getPostId(), new SingleNoticesPost() {
                    @Override
                    public void getPost(NoticesMainModel post) {
                      CommentServis.shared().getRepliedComment(context, model.getTargetCommentId(), model.getPostId(), new RepliedCommentModel() {
                          @Override
                          public void getComment(CommentModel comment) {
                              if (comment!=null){
                                  Intent i = new Intent(context,ReplyActivity.class);
                                  i.putExtra("currentUser",currentUser);
                                  i.putExtra("targetComment",comment);
                                  i.putExtra("noticesPost",post);
                                  context.startActivity(i);
                                  Helper.shared().go((Activity)context);
                                  PushNotificationService.shared().makeReadLocalNotification(currentUser,model.getNot_id());
                                  model.setIsRead("true");
                                  notifyDataSetChanged();
                                  WaitDialog.dismiss();
                              }
                          }
                      });
                    }
                });
            }else if (model.getPostType().equals(NotificationPostType.name.lessonPost)){
                MajorPostService.shared().getPost(context, currentUser, model.getPostId(), new SingleLessonPost() {
                    @Override
                    public void getPost(LessonPostModel post) {
                        if (post!=null){
                            CommentServis.shared().getRepliedComment(context, model.getTargetCommentId(), model.getPostId(), new RepliedCommentModel() {
                                @Override
                                public void getComment(CommentModel comment) {
                                    if (comment!=null){
                                        Intent i = new Intent(context,ReplyActivity.class);
                                        i.putExtra("currentUser",currentUser);
                                        i.putExtra("targetComment",comment);
                                        i.putExtra("lessonPost",post);
                                        context.startActivity(i);
                                        Helper.shared().go((Activity)context);
                                        PushNotificationService.shared().makeReadLocalNotification(currentUser,model.getNot_id());
                                        model.setIsRead("true");
                                        notifyDataSetChanged();;
                                        WaitDialog.dismiss();
                                    }
                                }
                            });
                        }

                    }
                });
            }else if (model.getPostType().equals(NotificationPostType.name.mainPost)){
                MainPostService.shared().getMainPost(context, currentUser, model.getPostId(), new SingleMainPost() {
                    @Override
                    public void getPost(MainPostModel post) {
                        CommentServis.shared().getRepliedComment(context, model.getTargetCommentId(), model.getPostId(), new RepliedCommentModel() {
                            @Override
                            public void getComment(CommentModel comment) {
                                if (comment!=null){
                                    Intent i = new Intent(context,ReplyActivity.class);
                                    i.putExtra("currentUser",currentUser);
                                    i.putExtra("targetComment",comment);
                                    i.putExtra("mainPost",post);
                                    context.startActivity(i);
                                    Helper.shared().go((Activity)context);
                                    PushNotificationService.shared().makeReadLocalNotification(currentUser,model.getNot_id());
                                    model.setIsRead("true");
                                    notifyDataSetChanged();
                                    WaitDialog.dismiss();
                                }
                            }
                        });
                    }
                });
            }else{
                WaitDialog.dismiss();
            }
        }
    }




}
