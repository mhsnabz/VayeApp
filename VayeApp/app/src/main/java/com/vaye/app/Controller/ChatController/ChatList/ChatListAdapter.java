package com.vaye.app.Controller.ChatController.ChatList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
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

import com.google.firebase.Timestamp;
import com.kongzue.dialog.v3.WaitDialog;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.vaye.app.Controller.ChatController.Conservation.ConservationController;
import com.vaye.app.Controller.HomeController.LessonPostAdapter.MajorPostViewHolder;
import com.vaye.app.Interfaces.OtherUserService;
import com.vaye.app.LoginRegister.MessageType;
import com.vaye.app.Model.ChatListModel;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.OtherUser;
import com.vaye.app.R;
import com.vaye.app.Services.UserService;
import com.vaye.app.Util.Helper;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<ChatListModel> chatList;
    Context context;
    CurrentUser currentUser;
    String TAG  = "ChatListAdapter";
    public ChatListAdapter(ArrayList<ChatListModel> chatList, Context context, CurrentUser currentUser) {
        this.chatList = chatList;
        this.context = context;
        this.currentUser = currentUser;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_list_cell, parent, false);

        return new ChatListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatListModel model = chatList.get(position);
        ChatListViewHolder chatListViewHolder = (ChatListViewHolder)holder;
        chatListViewHolder.setBadgeCount(model.getBadgeCount());
        chatListViewHolder.setname(model.getName(),model.getUsername());
        chatListViewHolder.setLastMsg(model.getType(),model.getLastMsg());
        chatListViewHolder.setProfileImage(model.getThumbImage());
        chatListViewHolder.setTime(model.getTime());

        chatListViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
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
        return chatList.size();
    }

    public class ChatListViewHolder extends RecyclerView.ViewHolder{

        public ChatListViewHolder(@NonNull View itemView) {
            super(itemView);
        }
        public CircleImageView profileImage  =(CircleImageView)itemView.findViewById(R.id.profileImage);
        public ProgressBar progressBar =(ProgressBar)itemView.findViewById(R.id.progress);
        public TextView name =(TextView)itemView.findViewById(R.id.name);
        public TextView username =(TextView)itemView.findViewById(R.id.username);
        public TextView lastMsg =(TextView)itemView.findViewById(R.id.lastMsg);
        public TextView badgeCount =(TextView)itemView.findViewById(R.id.badgeCount);
        public RelativeLayout badgeRel =(RelativeLayout)itemView.findViewById(R.id.badgeRel);
        public ImageView lastMsgImage = (ImageView)itemView.findViewById(R.id.lastMsgImage);
        public TextView time =(TextView)itemView.findViewById(R.id.time);
        public void setTime(Timestamp _time){
            time.setText(Helper.shared().setTimeAgo(_time));
        }

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
        public void setBadgeCount(int _badgeCount){
            if (_badgeCount > 0){
                badgeRel.setVisibility(View.VISIBLE);
                if (_badgeCount > 9){
                    badgeCount.setText("+9");
                }else{
                    badgeCount.setText(String.valueOf(_badgeCount));
                }

            }else{
                badgeRel.setVisibility(View.GONE);
            }
        }

        public void setLastMsg(String type , String _lastMsg){

         if (type.equals(MessageType.text)){
                lastMsgImage.setImageDrawable(context.getResources().getDrawable(R.drawable.font));
                if (_lastMsg!=null && !_lastMsg.isEmpty())
             lastMsg.setText(_lastMsg);
            }else if (type.equals(MessageType.audio)){
                lastMsgImage.setImageDrawable(context.getResources().getDrawable(R.drawable.audio_waves));

             lastMsg.setText("Ses Kaydı");
            }else if (type.equals(MessageType.location)){
                lastMsgImage.setImageDrawable(context.getResources().getDrawable(R.drawable.orange_location));
             lastMsg.setText("Konum");
            }else if (type.equals(MessageType.photo)){
                lastMsgImage.setImageDrawable(context.getResources().getDrawable(R.drawable.gallery_btn));
             lastMsg.setText("Resim");
            }
        }



    }
}
