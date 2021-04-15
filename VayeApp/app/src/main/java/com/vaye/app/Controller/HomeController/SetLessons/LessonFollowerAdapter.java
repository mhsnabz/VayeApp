package com.vaye.app.Controller.HomeController.SetLessons;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.kongzue.dialog.v3.WaitDialog;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.vaye.app.Controller.HomeController.TeacherNewPost.UserListAdapter;
import com.vaye.app.Controller.Profile.OtherUserProfileActivity;
import com.vaye.app.Interfaces.OtherUserService;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.LessonFallowerUser;
import com.vaye.app.Model.OtherUser;
import com.vaye.app.R;
import com.vaye.app.Services.UserService;
import com.vaye.app.Util.Helper;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class LessonFollowerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    ArrayList<LessonFallowerUser> lists;
    Context context;
    CurrentUser currentUser;

    public LessonFollowerAdapter(ArrayList<LessonFallowerUser> lists, Context context, CurrentUser currentUser) {
        this.lists = lists;
        this.context = context;
        this.currentUser = currentUser;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_list_item, parent, false);

        return new LessonFolloworViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        LessonFolloworViewHolder userListViewHolder = (LessonFolloworViewHolder) holder;
        LessonFallowerUser model = lists.get(position);
        userListViewHolder.setProfilImage(model.getThumb_image());
        userListViewHolder.setName(model.getName());
        userListViewHolder.setUsername(model.getUsername());
        userListViewHolder.setUserNumber(model.getNumber());
        userListViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserService.shared().getOtherUser((AppCompatActivity) context, model.getUid(), new OtherUserService() {
                    @Override
                    public void callback(OtherUser user) {
                        if (user!=null){
                            Intent i = new Intent(context, OtherUserProfileActivity.class);
                            i.putExtra("otherUser",user);
                            i.putExtra("currentUser",currentUser);
                            context.startActivity(i);
                            Helper.shared().go((AppCompatActivity)context);
                            WaitDialog.dismiss();
                        }
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    public class LessonFolloworViewHolder extends RecyclerView.ViewHolder{

        public LessonFolloworViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public CircleImageView profilImage = (CircleImageView)itemView.findViewById(R.id.profileImage);
        public ProgressBar progressBar =(ProgressBar)itemView.findViewById(R.id.progress);
        public TextView name = (TextView)itemView.findViewById(R.id.name);
        public TextView username = (TextView)itemView.findViewById(R.id.username);
        public TextView number = (TextView)itemView.findViewById(R.id.number);

        public void setName(String _name){
            name.setText(_name);
        }
        public void setUsername(String _username){
            username.setText(_username);
        }
        public void setUserNumber(String _number){
            if (_number!=null && !_number.isEmpty()){
                number.setText(_number);
            }
        }
        public void setProfilImage(String url){
            if (url!=null &&!url.isEmpty()){
                Picasso.get().load(url).resize(128,128)
                        .centerCrop().placeholder(android.R.color.darker_gray)
                        .into(profilImage, new Callback() {
                            @Override
                            public void onSuccess() {
                                progressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError(Exception e) {
                                progressBar.setVisibility(View.GONE);
                            }
                        });
            }else {
                progressBar.setVisibility(View.GONE);
                profilImage.setImageResource(android.R.color.darker_gray);
            }

        }
    }
}
