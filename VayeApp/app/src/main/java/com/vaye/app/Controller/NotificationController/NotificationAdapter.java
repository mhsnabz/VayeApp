package com.vaye.app.Controller.NotificationController;

import android.annotation.SuppressLint;
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
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kongzue.dialog.v3.TipDialog;
import com.kongzue.dialog.v3.WaitDialog;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.vaye.app.Controller.HomeController.LessonPostAdapter.MajorPostViewHolder;
import com.vaye.app.Controller.HomeController.SinglePost.SinglePostActivity;
import com.vaye.app.Controller.NotificationService.NotificationPostType;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.LessonPostModel;
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
            viewHolder.setCard(model.getIsRead());
            viewHolder.setMainText(model);
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    WaitDialog.show((AppCompatActivity)context , "");
                    if (model.getPostType().equals(NotificationPostType.name.lessonPost)){
                        DocumentReference reference = FirebaseFirestore.getInstance().collection(currentUser.getShort_school())
                                .document("lesson-post")
                                .collection("post")
                                .document(model.getPostId());
                        reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()){
                                    if (task.getResult().exists()){
                                        Intent i = new Intent(context, SinglePostActivity.class);
                                        i.putExtra("currentUser",currentUser);
                                        i.putExtra("lessonPost",task.getResult().toObject(LessonPostModel.class));
                                        context.startActivity(i);
                                        Helper.shared().go((Activity)context);
                                        WaitDialog.dismiss();
                                    }else{
                                        WaitDialog.dismiss();
                                        TipDialog.show((AppCompatActivity)context,"Gönderi Silinmiş", TipDialog.TYPE.ERROR);
                                        TipDialog.dismiss(1000);
                                    }

                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                WaitDialog.dismiss();
                                TipDialog.show((AppCompatActivity)context,"Hata Oluştu", TipDialog.TYPE.ERROR);
                                TipDialog.dismiss(1000);

                            }
                        });

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
    }

}
