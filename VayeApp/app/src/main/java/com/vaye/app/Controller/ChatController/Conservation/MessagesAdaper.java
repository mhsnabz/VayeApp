package com.vaye.app.Controller.ChatController.Conservation;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.hendraanggrian.appcompat.widget.SocialTextView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.vaye.app.Controller.HomeController.LessonPostAdapter.MajorPostViewHolder;
import com.vaye.app.LoginRegister.MessageType;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.LessonPostModel;
import com.vaye.app.Model.MessagesModel;
import com.vaye.app.Model.OtherUser;
import com.vaye.app.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesAdaper extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private static String TAG = "MessagesAdaper";


    private static final int SEND_TEXT_MSG = 1;

    private static final int RECEIVED_TEXT_MSG = 2;
    CurrentUser currentUser;
    OtherUser otherUser;
    Context context;
    ArrayList<MessagesModel> messagesModels;

    public MessagesAdaper(CurrentUser currentUser, OtherUser otherUser, Context context, ArrayList<MessagesModel> messagesModels) {
        this.currentUser = currentUser;
        this.otherUser = otherUser;
        this.context = context;
        this.messagesModels = messagesModels;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == SEND_TEXT_MSG){
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.send_text_msg, parent, false);

            return new SendTextMsgViewHolder(itemView);
        }
        else if (viewType == RECEIVED_TEXT_MSG){
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.received_text_msg, parent, false);

            return new ReceivedTextMsgViewHolder(itemView);

        }
        return  null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i) {
        int viewType = getItemViewType(i);
        MessagesModel model = messagesModels.get(i);
        long previousTs = 0;
        if(i>1){
            MessagesModel pm = messagesModels.get(i-1);
            previousTs = pm.getDate().getSeconds();
        }
        Log.d(TAG, "onBindViewHolder: " + previousTs);
        Log.d(TAG, "onBindViewHolder: " + model.getDate().getSeconds());
        switch (viewType) {

            case SEND_TEXT_MSG:
                SendTextMsgViewHolder send_text = (SendTextMsgViewHolder)holder;
                send_text.setMsgLbl(model.getContent());
                send_text.setProfileImage(currentUser.getProfileImage());
                setTimeAgo(model.getDate().getSeconds(),send_text.time);
                if (i>0)
                setTimeTextVisibility(i,model.getDate(), messagesModels.get(i-1).getDate(), send_text.groupDate);
                break;
            case RECEIVED_TEXT_MSG:
                ReceivedTextMsgViewHolder received_text = (ReceivedTextMsgViewHolder)holder;
                received_text.setMsgLbl(model.getContent());
                received_text.setProfileImage(otherUser.getProfileImage());
                setTimeAgo(model.getDate().getSeconds(),received_text.time);
                if (i>0)
                setTimeTextVisibility(i,model.getDate(), messagesModels.get(i-1).getDate(), received_text.groupDate);
            break;
            default:
                throw new IllegalStateException("Unexpected value: " + viewType);
        }
    }
    private void setTimeTextVisibility( int i,Timestamp ts1, Timestamp ts2, TextView timeText){
        Log.d(TAG, "setTimeTextVisibility: " + i);
            if (i == 0){
                timeText.setVisibility(View.GONE);
                timeText.setText(getDate(ts1.getSeconds()));
                Log.d(TAG, "setTimeTextVisibility: gone");
            }else {
                boolean sameMonth = ts1.toDate().getYear() == ts2.toDate().getYear() &&
                        ts1.toDate().getDay() == ts2.toDate().getDay()  &&
                        ts1.toDate().getMonth()  == ts2.toDate().getDay() &&
                        ts1.toDate().getYear() ==   ts2.toDate().getYear();

                if(sameMonth){
                    timeText.setVisibility(View.GONE);
                    timeText.setText("");
                }else {
                    timeText.setVisibility(View.VISIBLE);
                    timeText.setText(getDate(ts1.getSeconds()));
                }
            }




    }
    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        cal.setTimeInMillis(time * 1000);
        String date = DateFormat.format("EEE, d MMM yyyy", cal).toString();
        return date;
    }
    private void setTimeAgo(long timeAgo, TextView textView){
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        cal.setTimeInMillis(timeAgo * 1000);
        String date = DateFormat.format("EEE, d MMM  HH:mm:ss", cal).toString();
        textView.setText(date);
    }
    @Override
    public int getItemCount() {
        return messagesModels.size();
    }

    @Override
    public int getItemViewType(int position) {
        MessagesModel model  =messagesModels.get(position);


        if (model.getType().equals(MessageType.text)){
            if (model.getSenderUid().equals(currentUser)){
                return SEND_TEXT_MSG;
            }else{
                return RECEIVED_TEXT_MSG;
            }
        }else{
            Log.d(TAG, "getItemViewType: " + model.getGeoPoint());
        }

        return super.getItemViewType(position);
    }

    public   class SendTextMsgViewHolder extends RecyclerView.ViewHolder{

        public SendTextMsgViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public TextView groupDate = (TextView)itemView.findViewById(R.id.groupDate);
        public SocialTextView msgLbl = (SocialTextView)itemView.findViewById(R.id.msgText);
        public ProgressBar progressBar =(ProgressBar)itemView.findViewById(R.id.progress);
        public CircleImageView profileImage = (CircleImageView)itemView.findViewById(R.id.profileImage);
        public TextView time = (TextView)itemView.findViewById(R.id.time);

        public void setMsgLbl(String msg_text){
            msgLbl.setText(msg_text);
        }
        public void setProfileImage(String url){
            if (url!=null && !url.isEmpty()){
                Picasso.get().load(url)
                        .resize(200,200)
                        .centerCrop()
                        .placeholder(android.R.color.darker_gray)
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
    public  class ReceivedTextMsgViewHolder extends RecyclerView.ViewHolder{

        public ReceivedTextMsgViewHolder(@NonNull View itemView) {
            super(itemView);
        }
        public TextView groupDate = (TextView)itemView.findViewById(R.id.groupDate);
        public SocialTextView msgLbl = (SocialTextView)itemView.findViewById(R.id.msgText);
        public ProgressBar progressBar =(ProgressBar)itemView.findViewById(R.id.progress);
        public CircleImageView profileImage = (CircleImageView)itemView.findViewById(R.id.profileImage);
        public TextView time = (TextView)itemView.findViewById(R.id.time);
        public void setMsgLbl(String msg_text){
            msgLbl.setText(msg_text);
        }
        public void setProfileImage(String url){
            if (url!=null && !url.isEmpty()){
                Picasso.get().load(url)
                        .resize(200,200)
                        .centerCrop()
                        .placeholder(android.R.color.darker_gray)
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
