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
import com.google.firebase.firestore.GeoPoint;
import com.hendraanggrian.appcompat.widget.SocialTextView;
import com.makeramen.roundedimageview.RoundedImageView;
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

    private static final int RECEIVED_LOCATION_MSG = 3  ;
    private static final int SEND_LOCATION_MSG = 4;

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
        else if (viewType == SEND_LOCATION_MSG){
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.send_locaiton_msg, parent, false);

            return new SendLocationMsgViewHolder(itemView);
        }else if (viewType == RECEIVED_LOCATION_MSG){
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.received_locaiton_msg, parent, false);

            return new ReceivedLocaitonMsgViewHolder(itemView);
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
            previousTs = pm.getTime();
        }

        switch (viewType) {

            case SEND_TEXT_MSG:

                SendTextMsgViewHolder send_text = (SendTextMsgViewHolder)holder;
                send_text.setMsgLbl(model.getContent());
                send_text.setProfileImage(currentUser.getProfileImage());
                setTimeAgo(model.getTime(),send_text.time);

                if (i>0){
                    setTimeTextVisibility(model.getTime(), previousTs, send_text.groupDate);
                }

                break;
            case RECEIVED_TEXT_MSG:

                ReceivedTextMsgViewHolder received_text = (ReceivedTextMsgViewHolder)holder;
                received_text.setMsgLbl(model.getContent());
                received_text.setProfileImage(otherUser.getProfileImage());
                setTimeAgo(model.getTime(),received_text.time);

                if (i>0){
                    setTimeTextVisibility(model.getTime(), previousTs, received_text.groupDate);

                }

            break;

            case SEND_LOCATION_MSG:
                SendLocationMsgViewHolder send_location = (SendLocationMsgViewHolder)holder;
                send_location.setLocaitonView(model.getGeoPoint());
                send_location.setProfileImage(currentUser.getProfileImage());
                setTimeAgo(model.getTime(),send_location.time);
                if (i>0){
                    setTimeTextVisibility(model.getTime(), previousTs, send_location.groupDate);
                }

                break;
            case RECEIVED_LOCATION_MSG:
                ReceivedLocaitonMsgViewHolder received_location = (ReceivedLocaitonMsgViewHolder)holder;
                received_location.setLocaitonView(model.getGeoPoint());
                received_location.setProfileImage(currentUser.getProfileImage());
                setTimeAgo(model.getTime(),received_location.time);
                if (i>0){
                    setTimeTextVisibility(model.getTime(), previousTs, received_location.groupDate);
                }

                break;
            default:
                throw new IllegalStateException("Unexpected value: " + viewType);
        }
    }

    private void setTimeTextVisibility(long ts1, long ts2, TextView timeText){

        if(ts2==0){
            timeText.setVisibility(View.VISIBLE);
            timeText.setText(getDate(ts1));
          
        }else {
            Calendar cal1 = Calendar.getInstance();
            Calendar cal2 = Calendar.getInstance();
            cal1.setTimeInMillis(ts1);
            cal2.setTimeInMillis(ts2);

            boolean sameMonth = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                    cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&cal1.get(Calendar.WEEK_OF_MONTH) == cal2.get(Calendar.WEEK_OF_MONTH)&& cal1.get(Calendar.DAY_OF_WEEK) == cal2.get(Calendar.DAY_OF_WEEK);

            if(sameMonth){
                timeText.setVisibility(View.GONE);
                timeText.setText("");
            }else {
                timeText.setVisibility(View.VISIBLE);
                timeText.setText(getDate(ts1));
            }

        }
    }

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        cal.setTimeInMillis(time * 1000);
        String date = DateFormat.format("EEE, d MMM yyyy", time).toString();
        return date;
    }
    private void setTimeAgo(long timeAgo, TextView textView){
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        cal.setTimeInMillis(timeAgo * 1000);
        String date = DateFormat.format("EEE, d MMM  HH:mm", timeAgo).toString();
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
            if (model.getSenderUid().equals(currentUser.getUid())){

                return SEND_TEXT_MSG;
            }else{

                return RECEIVED_TEXT_MSG;
            }
        }else if (model.getType().equals(MessageType.location)){
            if (model.getSenderUid().equals(currentUser.getUid())){

                return SEND_LOCATION_MSG;
            }else{

                return RECEIVED_LOCATION_MSG;
            }

        }

        return super.getItemViewType(position);
    }

    public  class SendTextMsgViewHolder extends RecyclerView.ViewHolder{

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
    public class SendLocationMsgViewHolder extends RecyclerView.ViewHolder{

        public SendLocationMsgViewHolder(@NonNull View itemView) {
            super(itemView);
        }
        RoundedImageView locaitonView = (RoundedImageView)itemView.findViewById(R.id.locaitonView);
        public TextView groupDate = (TextView)itemView.findViewById(R.id.groupDate);
        public ProgressBar progressBar =(ProgressBar)itemView.findViewById(R.id.progress);
        public CircleImageView profileImage = (CircleImageView)itemView.findViewById(R.id.profileImage);
        public TextView time = (TextView)itemView.findViewById(R.id.time);


        public void setLocaitonView(GeoPoint geoPoint){
            if (geoPoint!=null){
                String latEiffelTower = String.valueOf(geoPoint.getLatitude());
                String lngEiffelTower = String.valueOf(geoPoint.getLongitude());
                String url = "http://maps.google.com/maps/api/staticmap?center=" + latEiffelTower + "," + lngEiffelTower + "&zoom=14&size=200x200&sensor=false&key=AIzaSyC2yPEvNrQQOnuBr7MGaByxk8PEwzrsG1I";
                Log.d(TAG, "setLocaitonView: "+url);
                Picasso.get().load(url).resize(600,450).centerCrop().placeholder(android.R.color.darker_gray).into(locaitonView);
            }
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
    public  class ReceivedLocaitonMsgViewHolder extends RecyclerView.ViewHolder{

        public ReceivedLocaitonMsgViewHolder(@NonNull View itemView) {
            super(itemView);

        }
        RoundedImageView locaitonView = (RoundedImageView)itemView.findViewById(R.id.locaitonView);
        public TextView groupDate = (TextView)itemView.findViewById(R.id.groupDate);
        public ProgressBar progressBar =(ProgressBar)itemView.findViewById(R.id.progress);
        public CircleImageView profileImage = (CircleImageView)itemView.findViewById(R.id.profileImage);
        public TextView time = (TextView)itemView.findViewById(R.id.time);


        public void setLocaitonView(GeoPoint geoPoint){
            if (geoPoint!=null){
                String latEiffelTower = String.valueOf(geoPoint.getLatitude());
                String lngEiffelTower = String.valueOf(geoPoint.getLongitude());
                String url = "http://maps.google.com/maps/api/staticmap?center=" + latEiffelTower + "," + lngEiffelTower + "&zoom=14&size=200x200&sensor=false&key=AIzaSyC2yPEvNrQQOnuBr7MGaByxk8PEwzrsG1I";
                Log.d(TAG, "setLocaitonView: "+url);
                Picasso.get().load(url).resize(200,200).centerCrop().placeholder(android.R.color.darker_gray).into(locaitonView);
            }
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
