package com.vaye.app.Controller.HomeController.SinglePost;

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
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.vaye.app.Controller.HomeController.LessonPostAdapter.MajorPostViewHolder;
import com.vaye.app.Model.CommentModel;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.R;
import com.vaye.app.Util.Helper;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<CommentModel> comments;
    CurrentUser currentUser;
    Context context;

    public CommentAdapter(ArrayList<CommentModel> comments, CurrentUser currentUser, Context context) {
        this.comments = comments;
        this.currentUser = currentUser;
        this.context = context;
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

    }

    @Override
    public int getItemCount() {
        return comments.size();
    }


    class CommentViewHolder extends RecyclerView.ViewHolder{

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
        }
        CircleImageView profileImage = (CircleImageView)itemView.findViewById(R.id.profileImage);
        ProgressBar progressBar = (ProgressBar)itemView.findViewById(R.id.progress);
        TextView name = (TextView)itemView.findViewById(R.id.name);
        TextView username = (TextView)itemView.findViewById(R.id.username);
        TextView time = (TextView)itemView.findViewById(R.id.time);
        TextView likeCount = (TextView)itemView.findViewById(R.id.likeCount);
        TextView likeTextButton = (TextView)itemView.findViewById(R.id.likeTextButton);
        TextView replyTextBtn = (TextView)itemView.findViewById(R.id.replyTextButton);
        TextView replyCount = (TextView)itemView.findViewById(R.id.replyCount);
        TextView replyText = (TextView)itemView.findViewById(R.id.replyText);
        ImageButton likeBtn = (ImageButton)itemView.findViewById(R.id.likeBtn);
        TextView msgText = (TextView)itemView.findViewById(R.id.msgText);
        RelativeLayout relLayReply = (RelativeLayout)itemView.findViewById(R.id.relLayReply);


        public void setName(String _name , String _username){
            name.setText(_name);
            username.setText(_username);
        }
        public void setTime(Timestamp _time){
            time.setText(Html.fromHtml("&#8226;")+ Helper.shared().setTimeAgo(_time));
        }
        public void setMsgText(String text){
            msgText.setText(text);
        }
        public void setProfileImage(String _url){
            if (_url != null && !_url.isEmpty()){
                Picasso.get().load(_url).centerCrop().resize(128,128).placeholder(android.R.color.darker_gray)
                        .into(profileImage, new Callback() {
                            @Override
                            public void onSuccess() {
                                progressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError(Exception e) {
                                progressBar.setVisibility(View.GONE);
                            }
                        });
            }
        }


    }
}
