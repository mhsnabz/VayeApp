package com.vaye.app.Controller.ChatController.Conservation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.internal.ads.zzako;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;



public class MessagesAdaper extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    public interface OnItemClickListener {
        void onItemClick(ImageButton b ,SeekBar seekBar , TextView timer, View view, MessagesModel model, int position);
    }

    private MediaPlayer mediaPlayer;
    private Handler handler = new Handler();

    private boolean isPlaying = false;
    View senderMSg;
    View getterMsg;
    private static String TAG = "MessagesAdaper";
    private Runnable updater;

    private static final int SEND_TEXT_MSG = 1;
    Timer timerClass ;

    private static final int RECEIVED_TEXT_MSG = 2;

    private static final int RECEIVED_LOCATION_MSG = 3  ;
    private static final int SEND_LOCATION_MSG = 4;
    private static final int RECEIVED_IMAGE_MSG = 5;
    private static final int SEND_IMAGE_MSG= 6;
    private static final int SEND_AUDIO_MSG= 7;
    private static final int RECEIVED_AUDIO_MSG= 8;

    CurrentUser currentUser;
    OtherUser otherUser;
    Context context;
    ArrayList<MessagesModel> messagesModels;
    OnItemClickListener mOnItemClickListener;
    public MessagesAdaper(CurrentUser currentUser, OtherUser otherUser, Context context, ArrayList<MessagesModel> messagesModels ,OnItemClickListener onItemClickListener ) {
        this.currentUser = currentUser;
        this.otherUser = otherUser;
        this.context = context;
        this.messagesModels = messagesModels;
        this.mOnItemClickListener = onItemClickListener;

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
        else if (viewType == RECEIVED_IMAGE_MSG){
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.image_receive_msg, parent, false);

            return new ReceivedImageMsgViewHolder(itemView);
        }
        else if (viewType == SEND_IMAGE_MSG){
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.image_send_message, parent, false);

            return new SendImageMsgViewHolder(itemView);
        }else if (viewType == SEND_AUDIO_MSG){
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.msg_audio_send, parent, false);
            senderMSg = itemView;
            return new SendAudioMsgHolder(itemView);
        }else if (viewType == RECEIVED_AUDIO_MSG){
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.msg_audio_received, parent, false);
            getterMsg = itemView;
            return new ReceivedAudioMsgHolder(itemView);
        }
        return  null;
    }




    @Override
    public long getItemId(int position) {
        return position;
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
            case SEND_AUDIO_MSG:
                SendAudioMsgHolder send_audio = (SendAudioMsgHolder)holder;
                send_audio.setProfileImage(currentUser.getProfileImage());
                setTimeAgo(model.getTime(),send_audio.time);

                send_audio.seekBar.setMax(100);
                if (i>0){
                    setTimeTextVisibility(model.getTime(), previousTs, send_audio.groupDate);
                }

                send_audio.play_pause.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                    }
                });
                break;
            case RECEIVED_AUDIO_MSG:
                ReceivedAudioMsgHolder received_audio = (ReceivedAudioMsgHolder)holder;
                received_audio.setProfileImage(otherUser.getProfileImage());
                setTimeAgo(model.getTime(),received_audio.time);
                received_audio.seekBar.setMax(100);

                Log.d(TAG, "onBindViewHolder: postion " + i);
                if (i>0){
                    setTimeTextVisibility(model.getTime(), previousTs, received_audio.groupDate);
                }

                received_audio.play_pause.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mOnItemClickListener.onItemClick(received_audio.play_pause,received_audio.seekBar,received_audio.timer,view,model,i);
                    }
                });

                break;
            case SEND_IMAGE_MSG:
                SendImageMsgViewHolder send_image = (SendImageMsgViewHolder)holder;
                send_image.setMsg_image(model.getContent());
                send_image.setProfileImage(currentUser.getProfileImage());
                setTimeAgo(model.getTime(),send_image.time);

                if (i>0){
                    setTimeTextVisibility(model.getTime(), previousTs, send_image.groupDate);
                }

                break;
            case RECEIVED_IMAGE_MSG:
                ReceivedImageMsgViewHolder received_image = (ReceivedImageMsgViewHolder)holder;
                received_image.setMsg_image(model.getContent());
                received_image.setProfileImage(otherUser.getProfileImage());
                setTimeAgo(model.getTime(),received_image.time);

                if (i>0){
                    setTimeTextVisibility(model.getTime(), previousTs, received_image.groupDate);
                }

                break;
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

                send_location.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getLocationUrl(model.getGeoPoint())));
                        context.startActivity(intent);
                    }
                });
                setTimeAgo(model.getTime(),send_location.time);
                if (i>0){
                    setTimeTextVisibility(model.getTime(), previousTs, send_location.groupDate);
                }

                break;
            case RECEIVED_LOCATION_MSG:
                ReceivedLocaitonMsgViewHolder received_location = (ReceivedLocaitonMsgViewHolder)holder;
                received_location.setLocaitonView(model.getGeoPoint());
                received_location.setProfileImage(otherUser.getProfileImage());
                setTimeAgo(model.getTime(),received_location.time);
                if (i>0){
                    setTimeTextVisibility(model.getTime(), previousTs, received_location.groupDate);
                }
                received_location.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getLocationUrl(model.getGeoPoint())));
                        context.startActivity(intent);
                    }
                });
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

    private String getLocationUrl(GeoPoint geoPoint){
        String latEiffelTower = String.valueOf(geoPoint.getLatitude());
        String lngEiffelTower = String.valueOf(geoPoint.getLongitude());
        return  "http://maps.google.com/maps/api/staticmap?center=" + latEiffelTower + "," + lngEiffelTower + "&zoom=14&size=300x300&sensor=false&key=AIzaSyC2yPEvNrQQOnuBr7MGaByxk8PEwzrsG1I";

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

        }else if (model.getType().equals(MessageType.photo)){
            if (model.getSenderUid().equals(currentUser.getUid())){

                return SEND_IMAGE_MSG;
            }else{

                return RECEIVED_IMAGE_MSG;
            }

        }else if (model.getType().equals(MessageType.audio)){
            if (model.getSenderUid().equals(currentUser.getUid())){
                return SEND_AUDIO_MSG;
            }else{
                return RECEIVED_AUDIO_MSG;
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

                Picasso.get().load(getLocationUrl(geoPoint)).resize(600,450).centerCrop().placeholder(android.R.color.darker_gray).into(locaitonView);
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
    public class SendImageMsgViewHolder extends RecyclerView.ViewHolder{

        public SendImageMsgViewHolder(@NonNull View itemView) {
            super(itemView);
        }
        public TextView groupDate = (TextView)itemView.findViewById(R.id.groupDate);
        public ProgressBar progressBar =(ProgressBar)itemView.findViewById(R.id.progress);
        public CircleImageView profileImage = (CircleImageView)itemView.findViewById(R.id.profileImage);
        public TextView time = (TextView)itemView.findViewById(R.id.time);
        public RoundedImageView msg_image = (RoundedImageView)itemView.findViewById(R.id.imageView);
        public ProgressBar imageProgress = (ProgressBar)itemView.findViewById(R.id.progressImage);
        public void setMsg_image(String url)
        {
            if (url!=null && !url.isEmpty()){
                Picasso.get().load(url)
                        .resize(400,500)
                        .centerCrop()
                        .placeholder(android.R.color.darker_gray)
                        .into(msg_image, new Callback() {
                            @Override
                            public void onSuccess() {
                                imageProgress.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError(Exception e) {
                                msg_image.setImageResource(android.R.color.darker_gray);
                                imageProgress.setVisibility(View.GONE);
                            }
                        });
            }else{
                msg_image.setImageResource(android.R.color.darker_gray);
                imageProgress.setVisibility(View.GONE);
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

                Picasso.get().load(getLocationUrl(geoPoint)).resize(200,200).centerCrop().placeholder(android.R.color.darker_gray).into(locaitonView);
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
    public class ReceivedImageMsgViewHolder extends RecyclerView.ViewHolder{

        public ReceivedImageMsgViewHolder(@NonNull View itemView) {
            super(itemView);
        }
        public TextView groupDate = (TextView)itemView.findViewById(R.id.groupDate);
        public ProgressBar progressBar =(ProgressBar)itemView.findViewById(R.id.progress);
        public CircleImageView profileImage = (CircleImageView)itemView.findViewById(R.id.profileImage);
        public TextView time = (TextView)itemView.findViewById(R.id.time);
        public RoundedImageView msg_image = (RoundedImageView)itemView.findViewById(R.id.imageView);
        public ProgressBar imageProgress = (ProgressBar)itemView.findViewById(R.id.progressImage);
        public void setMsg_image(String url)
        {
            if (url!=null && !url.isEmpty()){
                Picasso.get().load(url)
                        .resize(400,500)
                        .centerCrop()
                        .placeholder(android.R.color.darker_gray)
                        .into(msg_image, new Callback() {
                            @Override
                            public void onSuccess() {
                                imageProgress.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError(Exception e) {
                                msg_image.setImageResource(android.R.color.darker_gray);
                                imageProgress.setVisibility(View.GONE);
                            }
                        });
            }else{
                msg_image.setImageResource(android.R.color.darker_gray);
                imageProgress.setVisibility(View.GONE);
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

    public class ReceivedAudioMsgHolder extends  RecyclerView.ViewHolder{

        public ReceivedAudioMsgHolder(@NonNull View itemView) {
            super(itemView);
        }
        public TextView groupDate = (TextView)itemView.findViewById(R.id.groupDate);
        public ProgressBar progressBar =(ProgressBar)itemView.findViewById(R.id.progress);
        public CircleImageView profileImage = (CircleImageView)itemView.findViewById(R.id.profileImage);
        public TextView time = (TextView)itemView.findViewById(R.id.time);
        public ImageButton play_pause = (ImageButton)itemView.findViewById(R.id.playButton);
        public SeekBar seekBar = (SeekBar) itemView.findViewById(R.id.seekBar);
        public TextView timer = (TextView)itemView.findViewById(R.id.timer);
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
    public interface CustomOnClickListener{
        void onClick(int position);
    }
    public class SendAudioMsgHolder extends  RecyclerView.ViewHolder{

        public SendAudioMsgHolder(@NonNull View itemView) {
            super(itemView);

        }
        public TextView groupDate = (TextView)itemView.findViewById(R.id.groupDate);
        public ProgressBar progressBar =(ProgressBar)itemView.findViewById(R.id.progress);
        public CircleImageView profileImage = (CircleImageView)itemView.findViewById(R.id.profileImage);
        public TextView time = (TextView)itemView.findViewById(R.id.time);
        public ImageButton play_pause = (ImageButton)itemView.findViewById(R.id.playButton);
        public SeekBar seekBar = (SeekBar) itemView.findViewById(R.id.seekBar);
        public TextView timer = (TextView)itemView.findViewById(R.id.timer);



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
