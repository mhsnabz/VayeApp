package com.vaye.app.Controller.ChatController.FriendList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.kongzue.dialog.v3.WaitDialog;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.vaye.app.Controller.ChatController.ChatList.ChatListAdapter;
import com.vaye.app.Controller.ChatController.Conservation.ConservationController;
import com.vaye.app.Interfaces.OtherUserService;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.FriendListModel;
import com.vaye.app.Model.OtherUser;
import com.vaye.app.R;
import com.vaye.app.Services.UserService;
import com.vaye.app.Util.Helper;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<FriendListModel> list;
    Context context;
    CurrentUser currentUser;

    public FriendListAdapter(ArrayList<FriendListModel> list, Context context, CurrentUser currentUser) {
        this.list = list;
        this.context = context;
        this.currentUser = currentUser;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.friend_list_cell, parent, false);

        return new FriendListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        FriendListModel model = list.get(position);
        FriendListViewHolder friendListViewHolder =(FriendListViewHolder)holder;
        friendListViewHolder.setname(model.getName(),model.getUserName());
        friendListViewHolder.setProfileImage(model.getThumb_image());
        friendListViewHolder.setSchool(model.getShort_school(),model.getBolum());
        friendListViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WaitDialog.show((AppCompatActivity)context,null);
                UserService.shared().getOtherUserById(model.getUid(), new OtherUserService() {
                    @Override
                    public void callback(OtherUser user) {
                        if (user!=null){
                            Intent i = new Intent(context, ConservationController.class);
                            i.putExtra("currentUser",currentUser);
                            i.putExtra("otherUser",user);
                            context.startActivity(i);
                            Helper.shared().go((Activity)context);
                            WaitDialog.dismiss();
                        }
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class FriendListViewHolder extends RecyclerView.ViewHolder{

        public FriendListViewHolder(@NonNull View itemView) {
            super(itemView);
        }
        public CircleImageView profileImage  =(CircleImageView)itemView.findViewById(R.id.profileImage);
        public ProgressBar progressBar =(ProgressBar)itemView.findViewById(R.id.progress);
        public TextView name =(TextView)itemView.findViewById(R.id.name);
        public TextView username =(TextView)itemView.findViewById(R.id.username);
        public TextView schoolName =(TextView)itemView.findViewById(R.id.schoolName);
        public TextView bolum =(TextView)itemView.findViewById(R.id.bolum);
        public void setProfileImage(String _url){
            if (_url!=null && !_url.isEmpty()){
                Picasso.get().load(_url)
                        .centerCrop()
                        .resize(200,200)
                        .placeholder(android.R.color.darker_gray)
                        .into(profileImage, new Callback() {
                            @Override
                            public void onSuccess() {
                                progressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError(Exception e) {
                                progressBar.setVisibility(View.GONE);
                                profileImage.setImageResource(android.R.color.darker_gray);
                            }
                        });
            }else {
                progressBar.setVisibility(View.GONE);
                profileImage.setImageResource(android.R.color.darker_gray);
            }
        }
        public void setname(String _name , String _username){
            name.setText(_name);
            username.setText(_username);
        }
        public void setSchool(String _schoolName , String _bolum){
            schoolName.setText(_schoolName);
            bolum.setText(_bolum);
        }
    }
}
