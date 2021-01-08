package com.vaye.app.Controller.HomeController.LessonPostAdapter;

import android.content.Context;
import android.media.Image;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.LessonPostModel;
import com.vaye.app.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class LessonPostAdapter extends RecyclerView.Adapter<LessonPostViewHolder> {
    ArrayList<LessonPostModel> post;
    CurrentUser currentUser;
    Context context;

    public LessonPostAdapter(ArrayList<LessonPostModel> post, CurrentUser currentUser, Context context) {
        this.post = post;
        this.currentUser = currentUser;
        this.context = context;
    }

    @NonNull
    @Override
    public LessonPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_lesson_post_item, parent, false);

        return new LessonPostViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull LessonPostViewHolder holder, int i) {

        holder.setCommentLbl(post.get(i).getComment());
        holder.setName(post.get(i).getSenderName());
        holder.setUserName(post.get(i).getUsername());
        holder.setProfileImage(post.get(i).getThumb_image());
        holder.setLessonName(post.get(i).getLessonName());
        holder.setText(post.get(i).getText());
        holder.setDislikeLbl(post.get(i).getDislike());
        holder.setLikeLbl(post.get(i).getLikes());
        holder.setLike(post.get(i).getLikes(),currentUser , context);
        holder.setDislike(post.get(i).getDislike(),currentUser , context);
        holder.setFav(post.get(i).getFavori(),currentUser,context);

        holder.itemView.findViewById(R.id.fav).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              if (holder.isFav(post.get(i).getFavori() ,currentUser)) {
                  post.get(i).getFavori().remove(currentUser.getUid());
                 holder.setFav(post.get(i).getFavori() , currentUser ,context);
                  notifyDataSetChanged();
              }else {
                  post.get(i).getFavori().add(currentUser.getUid());
                  holder.setFav(post.get(i).getFavori() , currentUser ,context);
                  notifyDataSetChanged();
              }
            }
        });


    }

    @Override
    public int getItemCount() {
        return post.size();
    }
}
 class LessonPostViewHolder extends RecyclerView.ViewHolder{


    public LessonPostViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    CircleImageView profileImage = (CircleImageView)itemView.findViewById(R.id.profileImage);
    ProgressBar progressBar = (ProgressBar)itemView.findViewById(R.id.progress);
    TextView name = (TextView)itemView.findViewById(R.id.name);
    TextView userName = (TextView)itemView.findViewById(R.id.username);
    ImageButton more = (ImageButton)itemView.findViewById(R.id.setting);
    TextView lessonName = (TextView)itemView.findViewById(R.id.lessonName);
    TextView text  = (TextView)itemView.findViewById(R.id.text);
    ImageButton like = (ImageButton)itemView.findViewById(R.id.like);
    TextView likeLbl = (TextView)itemView.findViewById(R.id.likeCount);

    ImageButton dislike = (ImageButton)itemView.findViewById(R.id.dislike);
    TextView dislikeLbl = (TextView)itemView.findViewById(R.id.dislikeCount);

    ImageButton comment = (ImageButton)itemView.findViewById(R.id.comment);
    TextView commentLbl = (TextView)itemView.findViewById(R.id.commentCount);
    ImageButton fav = (ImageButton)itemView.findViewById(R.id.fav);


    public void setProfileImage(String url){
        if (!url.isEmpty()){
            Picasso.get().load(url)
                    .centerCrop()
                    .resize(256,256).placeholder(android.R.color.darker_gray)
                    .into(profileImage, new Callback() {
                        @Override
                        public void onSuccess() {
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });
        }else{
            profileImage.setImageResource(android.R.color.darker_gray);
            progressBar.setVisibility(View.GONE);
        }
    }

    public void setLessonName(String _lessonName){
        lessonName.setText(_lessonName);
    }
    public void setUserName(String _username){
        userName.setText(_username);
    }
    public void setName(String _name){
        name.setText(_name);
    }
    public void setText(String  _text){
        text.setText(_text);
    }

    public void setLikeLbl(ArrayList<String> likes){
        if (likes.isEmpty()){
            likeLbl.setText("0");
        }else{
            likeLbl.setText(String.valueOf(likes.size()));
        }
    }

    public void setDislikeLbl(ArrayList<String> disLikes){
        if (disLikes.isEmpty()){
            dislikeLbl.setText("0");
        }else{
            dislikeLbl.setText(String.valueOf(disLikes.size()));
        }
    }

    public void setCommentLbl(int size){
        commentLbl.setText(String.valueOf(size));
    }

    public void setDislike(ArrayList<String> dislikes , CurrentUser user , Context context){
        if (dislikes.contains(user.getUid())){
            like.setImageDrawable( ContextCompat.getDrawable(context, R.drawable.like_unselected));
            dislike.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.dislike_selected));
        }
    }
    public void setLike(ArrayList<String> likes , CurrentUser user , Context context){
        if (likes.contains(user.getUid())){
            like.setImageDrawable( ContextCompat.getDrawable(context, R.drawable.like));
            dislike.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.dislike));
        }
    }
    public void setFav(ArrayList<String> favs , CurrentUser user , Context context){
        if (favs.contains(user.getUid())){
            fav.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.star_selected));
        }else{
            fav.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.star));
        }
    }
    public Boolean isFav(ArrayList<String> favs , CurrentUser user){
        if (favs.contains(user.getUid())){
            return  true;
        }else{
            return  false;
        }
    }


}