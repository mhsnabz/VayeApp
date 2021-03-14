package com.vaye.app.Controller.NotificationController;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.vaye.app.Controller.HomeController.LessonPostAdapter.MajorPostViewHolder;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.NoticesMainModel;
import com.vaye.app.Model.NotificationModel;
import com.vaye.app.R;
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
             Log.d(TAG, "onBindViewHolder: "+model.getNot_id());
            viewHolder.setCard(model.getRead());

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
        public CardView card = (CardView)itemView.findViewById(R.id.card);

        @SuppressLint("ResourceAsColor")
        public void setCard(Boolean _isSeen){
            Log.d(TAG, "setCard: " + _isSeen);
            if (_isSeen){
                card.setBackgroundColor(R.color.white);
            }else{
                card.setBackgroundColor(R.color.mainColorTransparent);
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
    }
}
