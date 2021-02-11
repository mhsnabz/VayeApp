package com.vaye.app.Controller.VayeAppController.CommentController;

import android.text.Html;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.hendraanggrian.appcompat.widget.SocialTextView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.Model.CommentModel;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.R;
import com.vaye.app.Util.Helper;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainPostCommentViewHolder extends RecyclerView.ViewHolder{

    public MainPostCommentViewHolder(@NonNull View itemView) {
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
    TextView replyCountText = (TextView)itemView.findViewById(R.id.replyCount);
    ImageButton likeBtn = (ImageButton)itemView.findViewById(R.id.likeBtn);
    SocialTextView msgText = (SocialTextView)itemView.findViewById(R.id.msgText);
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
        }else{
            profileImage.setImageResource(android.R.color.darker_gray);
            progressBar.setVisibility(View.GONE);
        }
    }
    public void setLikeCount(String count){
        likeCount.setText(count);
    }
    public void setReplyCount(int replyCount){
        if (replyCount > 0){
            relLayReply.setVisibility(View.VISIBLE);

            replyCountText.setText(String.valueOf(replyCount) +" yanıtı gör");
        }
    }
    public void setLikeBtn(CurrentUser currentUser,ArrayList<String> likes){
        if (likes.contains(currentUser.getUid())) {
            likeBtn.setImageResource(R.drawable.like);
        }else{
            likeBtn.setImageResource(R.drawable.like_unselected);
        }
    }

    public void like_dislike_click(CurrentUser currentUser, CommentModel model , TrueFalse<Boolean> val){
        if (model.getLikes().contains(currentUser.getUid())){
            model.getLikes().remove(currentUser.getUid());
            val.callBack(false);
            //like
        }else {
            //remove like
            model.getLikes().add(currentUser.getUid());
            val.callBack(true);
        }
    }


}

