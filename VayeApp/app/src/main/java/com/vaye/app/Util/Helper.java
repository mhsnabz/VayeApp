package com.vaye.app.Util;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.LottieOnCompositionLoadedListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.SetOptions;
import com.kongzue.dialog.v3.TipDialog;
import com.kongzue.dialog.v3.WaitDialog;
import com.squareup.picasso.Picasso;
import com.vaye.app.Controller.HomeController.HomeActivity;
import com.vaye.app.Controller.NotificationService.FollowNotification;
import com.vaye.app.Controller.NotificationService.MainPostNotification;
import com.vaye.app.Controller.NotificationService.MajorPostNotification;
import com.vaye.app.Controller.NotificationService.NoticesPostNotification;
import com.vaye.app.Controller.NotificationService.NotificationPostType;
import com.vaye.app.Controller.Profile.OtherUserProfileActivity;
import com.vaye.app.Controller.VayeAppController.Followers.FollowersFragment;
import com.vaye.app.Interfaces.BlockOptionSelect;
import com.vaye.app.Interfaces.CompletionWithValue;
import com.vaye.app.Interfaces.LocationCallback;
import com.vaye.app.Interfaces.LottieFrames;
import com.vaye.app.Interfaces.Notifications;
import com.vaye.app.Interfaces.OnOptionSelect;
import com.vaye.app.Interfaces.RecordedAudioCallback;
import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.LessonModel;
import com.vaye.app.Model.LessonPostModel;
import com.vaye.app.Model.MainPostModel;
import com.vaye.app.Model.NoticesMainModel;
import com.vaye.app.Model.NotificationModel;
import com.vaye.app.Model.OtherUser;
import com.vaye.app.R;
import com.vaye.app.Services.FollowService;
import com.vaye.app.Services.FoodMeService;
import com.vaye.app.Services.NotificaitonService;
import com.vaye.app.Services.UserService;
import com.vaye.app.Util.BottomSheetHelper.BottomSheetActionTarget;
import com.vaye.app.Util.BottomSheetHelper.BottomSheetAdapter;
import com.vaye.app.Util.BottomSheetHelper.BottomSheetLinkAdapter;
import com.vaye.app.Util.BottomSheetHelper.BottomSheetModel;
import com.vaye.app.Util.BottomSheetHelper.BottomSheetTarget;
import com.vaye.app.Util.BottomSheetHelper.ChatOptionAdapter;
import com.vaye.app.Util.BottomSheetHelper.MajorPostBottomAdapter;
import com.vaye.app.Util.BottomSheetHelper.MessageMediaAdapter;
import com.vaye.app.Util.BottomSheetHelper.ProfileImageSettingAdapter;
import com.vaye.app.Util.BottomSheetHelper.SchoolPostBottomSheetAdapter;
import com.vaye.app.Util.BottomSheetHelper.VayeAppBottomSheet;
import com.vaye.app.Util.BottomSheetHelper.VayeAppChooseTargetBottomSheetAdapter;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Helper {
    private boolean isFallowing = false;
    private static final String TAG = "Helper";
    private static final Helper instance = new Helper();
   private Boolean istanceOfCurrentUserProfile = false;
   private Boolean istanceOfOtherUserProfile = false;
    public static Helper shared() {  return instance;}
    public void back(Activity activity){
        activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
    public void go(Activity activity){
        activity.overridePendingTransition(R.anim.slide_in_rigth, R.anim.slide_out_left);
    }
    public void slideDown(Activity activity){
        activity.overridePendingTransition(R.anim.bottom_up, R.anim.bottom_down);
    }
    public  String  setTimeAgo(Timestamp _time) {
        if (_time != null) {
            long time = _time.getSeconds();

            int SECOND_MILLIS = 1000;
            int MINUTE_MILLIS = 60 * SECOND_MILLIS;
            int HOUR_MILLIS = 60 * MINUTE_MILLIS;
            final int DAY_MILLIS = 24 * HOUR_MILLIS;
            if (time < 1000000000000L) {
                time *= 1000;
            }

            long now = Calendar.getInstance().getTimeInMillis();
            if (time > now || time <= 0) {
                return "0";
            }

            // TODO: localize
            final long diff = now - time;
            if (diff < MINUTE_MILLIS) {
                return "şimdi";
            } else if (diff < 2 * MINUTE_MILLIS) {
                return "1dk";
            } else if (diff < 50 * MINUTE_MILLIS) {
                return String.valueOf(diff / MINUTE_MILLIS) + " dk";

            } else if (diff < 90 * MINUTE_MILLIS) {
                return "1 Sa";
            } else if (diff < 24 * HOUR_MILLIS) {
                return String.valueOf(diff / HOUR_MILLIS) + " sa";


            } else if (diff < 48 * HOUR_MILLIS) {
                return "dün";
            } else {
                return String.valueOf(diff / DAY_MILLIS) + " g";


            }
        }
        return "";
    }
    public  boolean isDownloadManagerAvailable(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            return true;
        }
        return false;
    }

    public void BottomSheetDialogHelper(Activity activity, String  target , CurrentUser currentUser , BottomSheetModel model , LessonModel lessonModel , TrueFalse<Boolean> val){
        RecyclerView recyclerView;

        Button cancel;

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity,R.style.BottomSheetDialogTheme);
        View view = LayoutInflater.from(activity.getApplicationContext())
                .inflate(R.layout.action_bottom_sheet_layout,(RelativeLayout)activity.findViewById(R.id.dialog));
        BottomSheetAdapter adapter = new BottomSheetAdapter(target ,currentUser ,activity ,model , bottomSheetDialog , lessonModel);
          recyclerView = (RecyclerView)view.findViewById(R.id.optionList);
          recyclerView.setAdapter(adapter);
          recyclerView.setLayoutManager(new LinearLayoutManager(activity));
          adapter.notifyDataSetChanged();
          cancel = (Button)view.findViewById(R.id.dismis);
          cancel.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                  Toast.makeText(activity,"Cancel Click",Toast.LENGTH_SHORT).show();
                    bottomSheetDialog.dismiss();
              }
          });

          bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
              @Override
              public void onDismiss(DialogInterface dialogInterface) {
                  val.callBack(true);
              }
          });
          bottomSheetDialog.setContentView(view);
          bottomSheetDialog.show();

    }
    int count = 0;
    public void LocationPickDialog(Activity activity , String _title , String _address , Double lat , Double longLat, LocationCallback locationCallback, TrueFalse<Boolean> callback){
        TextView title , address ;

        Button selecteButton,dismiss;
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity,R.style.BottomSheetDialogTheme);
        View view = LayoutInflater.from(activity.getApplicationContext())
                .inflate(R.layout.location_pick_layout,(RelativeLayout)activity.findViewById(R.id.dialog));
        selecteButton = (Button)view.findViewById(R.id.select);
        dismiss = (Button)view.findViewById(R.id.dismis);
        selecteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent("target_location");

                intent.putExtra("target",CompletionWithValue.location);
                intent.putExtra("lat",lat);
                intent.putExtra("longLat",longLat);
                intent.putExtra("locationName",_title);
                LocalBroadcastManager.getInstance(activity).sendBroadcast(intent);



                Intent chat = new Intent("locaiton_manager");
                count = count + 1;
                chat.putExtra("target",CompletionWithValue.get_locaiton);
                Log.d("ConservationController", "onClick: " + CompletionWithValue.get_locaiton);
                chat.putExtra("lat",lat);
                chat.putExtra("longLat",longLat);
                chat.putExtra("locationName",_title);
                chat.putExtra("count",count);
                LocalBroadcastManager.getInstance(activity).sendBroadcast(chat);
                bottomSheetDialog.dismiss();

            }
        });
        title = (TextView)view.findViewById(R.id.title);
        address = (TextView)view.findViewById(R.id.addresss);
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
            }
        });
        title.setText(_title);
        address.setText(_address);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
        bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                activity.finish();
            }
        });


    }
    public void BottomSheetAddLink(Activity activity, String  target , CurrentUser currentUser , BottomSheetModel model , LessonPostModel post , TrueFalse<Boolean> val){
        RecyclerView recyclerView;

        Button cancel;

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity,R.style.BottomSheetDialogTheme);
        View view = LayoutInflater.from(activity.getApplicationContext())
                .inflate(R.layout.action_bottom_sheet_layout,(RelativeLayout)activity.findViewById(R.id.dialog));
        BottomSheetAdapter adapter = new BottomSheetAdapter(target ,currentUser ,activity ,model , bottomSheetDialog , post);
        recyclerView = (RecyclerView)view.findViewById(R.id.optionList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        adapter.notifyDataSetChanged();
        cancel = (Button)view.findViewById(R.id.dismis);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                val.callBack(true);
            }
        });
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
    }
    Boolean isPlaying = false;
    Boolean isRecording = false;
    MediaRecorder mRecorder;
    MediaPlayer mPlayer;
    File soundName;

    private Spannable getSpannableText (String text1 , String text2 , String text3){

        SpannableStringBuilder builder = new SpannableStringBuilder();


        SpannableString redSpannable= new SpannableString(text1);
        redSpannable.setSpan(new ForegroundColorSpan(Color.BLACK), 0, text1.length(), 0);

        builder.append(redSpannable);


        SpannableString text2Spannable= new SpannableString(text2);
        text2Spannable.setSpan(new ForegroundColorSpan(Color.RED), 0, text2.length(), 0);

        builder.append(text2Spannable);


        SpannableString text3Spanable= new SpannableString(text3);
        text3Spanable.setSpan(new ForegroundColorSpan(Color.BLACK), 0, text3.length(), 0);
        builder.append(text3Spanable);


        return  builder;


    }

    public void RecorderBottomSheet(Activity activity, final String fileName  , RecordedAudioCallback callback) throws IOException {
        // mainText.setText(builder,TextView.BufferType.SPANNABLE);
        Log.d(TAG, "RecorderBottomSheet: " + fileName);
        LottieAnimationView record , play_pause,send;
        TextView textView;
        Button cancel;
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity,R.style.BottomSheetDialogTheme);
        View view = LayoutInflater.from(activity.getApplicationContext())
                .inflate(R.layout.sound_record_bottom_sheet,(RelativeLayout)activity.findViewById(R.id.dialog));
        File sampleDir = Environment.getExternalStorageDirectory();
        soundName = File.createTempFile("sound", ".m4a", sampleDir);

        record = (LottieAnimationView)view.findViewById(R.id.recoedAnim);
        play_pause = (LottieAnimationView)view.findViewById(R.id.playAnim);
        send = (LottieAnimationView)view.findViewById(R.id.sendAnim);
        textView = (TextView)view.findViewById(R.id.text);
        textView.setText(getSpannableText("Yeni Bir "," KAYIT"," Yapmak İçin Kayıt Butonuna Basın"),TextView.BufferType.SPANNABLE);
        record.setAnimation(R.raw.record);
        play_pause.setAnimation(R.raw.play_pause);
        send.setAnimation(R.raw.send);
        play_pause.setVisibility(View.INVISIBLE);
        send.setVisibility(View.INVISIBLE);
        play_pause.setMinFrame(10);
        play_pause.playAnimation();
        play_pause.pauseAnimation();

        play_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPlaying){
                    play_pause.playAnimation();
                    play_pause.removeAllAnimatorListeners();
                    play_pause.addAnimatorListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animator) {
                            play_pause.reverseAnimationSpeed();
                        }

                        @Override
                        public void onAnimationCancel(Animator animator) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animator) {

                        }
                    });

                    isPlaying = false;
                    Log.d(TAG, "onClick: not Recording ");
                }else{

                    play_pause.playAnimation();
                    Log.d(TAG, "onClick: isRecording ");
                    isPlaying = true;
                    play_pause.removeAllAnimatorListeners();
                    play_pause.addAnimatorListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animator) {
                            play_pause.reverseAnimationSpeed();
                        }

                        @Override
                        public void onAnimationCancel(Animator animator) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animator) {

                        }
                    });

                        mPlayer = new MediaPlayer();
                        try {
                            mPlayer.setDataSource(soundName.getAbsolutePath());
                            mPlayer.prepare();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        mPlayer.start();
                    //  mPlayer.setDataSource(Environment.getExternalStorageDirectory().getAbsolutePath() + "/recording.3gp");

                        Toast.makeText(activity, "Recording Started Playing", Toast.LENGTH_LONG).show();

                }


            }
        });
        record.pauseAnimation();
        record.setSpeed(1.5f);
        send.setSpeed(2f);

        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isRecording){


                    record.playAnimation();

                    record.removeAllAnimatorListeners();
                    record.addAnimatorListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animator) {
                            textView.setText(getSpannableText("Yeni Bir "," KAYIT"," Yapmak İçin Kayıt Butonuna Basın"),TextView.BufferType.SPANNABLE);
                            play_pause.setVisibility(View.VISIBLE);
                            send.setVisibility(View.VISIBLE);
                            record.reverseAnimationSpeed();
                        }

                        @Override
                        public void onAnimationCancel(Animator animator) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animator) {

                        }
                    });

                    isRecording = false;
                    Log.d(TAG, "onClick: not Recording ");

                    try {
                        mRecorder.stop();
                        mRecorder.release();
                    } catch(RuntimeException stopException) {
                        Log.d(TAG, "error recordin: "+ stopException.getLocalizedMessage());
                    }
                    mRecorder = null;
                    Log.d(TAG, "onClick: "+ Uri.parse(fileName));
                }else{
                    play_pause.setVisibility(View.INVISIBLE);
                    send.setVisibility(View.INVISIBLE);
                    textView.setText(getSpannableText("","KAYIT YAPILIYOR",""),TextView.BufferType.SPANNABLE);

                    record.playAnimation();
                    Log.d(TAG, "onClick: isRecording ");
                    isRecording = true;
                    record.removeAllAnimatorListeners();
                    record.addAnimatorListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animator) {
                            record.reverseAnimationSpeed();
                        }

                        @Override
                        public void onAnimationCancel(Animator animator) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animator) {

                        }
                    });

                    try {

                        mRecorder = new MediaRecorder();
                        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);

                        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                        mRecorder.setOutputFile(soundName.getAbsolutePath());
                        mRecorder.prepare();
                        mRecorder.start();
                    } catch (IOException e) {
                        Log.e(TAG, "sdcard access error");
                        return;
                    }



                }

            }
        });

    send.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            send.playAnimation();
            send.addAnimatorListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {

                    bottomSheetDialog.dismiss();
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
        }
    });


        cancel = (Button)view.findViewById(R.id.dismis);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                bottomSheetDialog.dismiss();
            }
        });
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
        bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                callback.callback(soundName);
            }
        });
    }
    public void NewPostBottomSheetAddLink(Activity activity, String  target,String link , CurrentUser currentUser , BottomSheetModel model  , CompletionWithValue val){
        RecyclerView recyclerView;
        CardView headerView;
        Button cancel;

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity,R.style.BottomSheetDialogTheme);
        View view = LayoutInflater.from(activity.getApplicationContext())
                .inflate(R.layout.action_bottom_sheet_layout,(RelativeLayout)activity.findViewById(R.id.dialog));
        BottomSheetAdapter adapter = new BottomSheetAdapter(target ,link,currentUser ,activity ,model , bottomSheetDialog );
        recyclerView = (RecyclerView)view.findViewById(R.id.optionList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        adapter.notifyDataSetChanged();
        headerView = (CardView)view.findViewById(R.id.header);
        headerView.setVisibility(View.GONE);
        cancel = (Button)view.findViewById(R.id.dismis);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(activity,"Cancel Click",Toast.LENGTH_SHORT).show();
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                val.completion(true , link);
                WaitDialog.dismiss();

            }
        });
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
    }

    public void MessageOptionsBottomSheetLauncaher(String target,OnOptionSelect optionSelect ,Activity activity , CurrentUser currentUser , OtherUser otherUser,BlockOptionSelect blockOptionSelect ){
        RecyclerView recyclerView;
        CardView headerView;
        Button cancel;

        View view = LayoutInflater.from(activity.getApplicationContext())
                .inflate(R.layout.action_bottom_sheet_layout,(RelativeLayout)activity.findViewById(R.id.dialog));
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity,R.style.BottomSheetDialogTheme);
        ArrayList<String > items = new ArrayList<>();
        ArrayList<Integer> res = new ArrayList<>();
        if (target.equals(BottomSheetTarget.request_conservation_options)){
            items.add(BottomSheetActionTarget.delete_conservation);
            items.add(BottomSheetActionTarget.report_chat_friend);
            items.add(BottomSheetActionTarget.getBu_kullaniciyi_engelle);
            res.add(R.drawable.trash);
            res.add(R.drawable.red_report);
            res.add(R.drawable.block_user);


        }else{
            items.add(BottomSheetActionTarget.delete_conservation);
            items.add(BottomSheetActionTarget.remove_from_friendList);
            items.add(BottomSheetActionTarget.report_chat_friend);
            items.add(BottomSheetActionTarget.getBu_kullaniciyi_engelle);
            res.add(R.drawable.trash);
            res.add(R.drawable.dismis);
            res.add(R.drawable.red_report);
            res.add(R.drawable.block_user);

        }

        headerView = (CardView)view.findViewById(R.id.header);
        headerView.setVisibility(View.GONE);

        BottomSheetModel model = new BottomSheetModel(items, target,res);


        ChatOptionAdapter adapter = new ChatOptionAdapter(currentUser,model,bottomSheetDialog,otherUser,activity,optionSelect,blockOptionSelect);
        recyclerView = (RecyclerView)view.findViewById(R.id.optionList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        adapter.notifyDataSetChanged();

        bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {

            }
        });

        cancel = (Button)view.findViewById(R.id.dismis);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();

    }
    public void SchoolPostCurrentUserBottomSheetLauncher(ArrayList<NoticesMainModel> allPost, Activity activity , CurrentUser currentUser , NoticesMainModel post , TrueFalse<Boolean> callback){
        RecyclerView recyclerView;
        CardView headerView;
        Button cancel;
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity,R.style.BottomSheetDialogTheme);
        View view = LayoutInflater.from(activity.getApplicationContext())
                .inflate(R.layout.action_bottom_sheet_layout,(RelativeLayout)activity.findViewById(R.id.dialog));
        ArrayList<String > items = new ArrayList<>();
        items.add(BottomSheetActionTarget.gonderiyi_düzenle);
        items.add(BottomSheetActionTarget.gonderiyi_sil);
        if (post.getSilent().contains(currentUser.getUid())){
            items.add(BottomSheetActionTarget.gonderi_bildirimlerini_ac);
        }else{
            items.add(BottomSheetActionTarget.gonderiyi_sessize_al);
        }


        ArrayList<Integer> res = new ArrayList<>();
        res.add(R.drawable.edit);
        res.add(R.drawable.trash);
        if (post.getSilent().contains(currentUser.getUid())){
            res.add(R.drawable.slient_selected);
        }else{

            res.add(R.drawable.slient);
        }
        BottomSheetModel model = new BottomSheetModel(items, BottomSheetTarget.school_app_current_user_launcher,res);
        SchoolPostBottomSheetAdapter adapter = new SchoolPostBottomSheetAdapter(allPost,post,currentUser,model,bottomSheetDialog,activity);
        recyclerView = (RecyclerView)view.findViewById(R.id.optionList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        adapter.notifyDataSetChanged();
        headerView = (CardView)view.findViewById(R.id.header);
        headerView.setVisibility(View.GONE);
        cancel = (Button)view.findViewById(R.id.dismis);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                callback.callBack(true);
            }
        });
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
    }

    public void SchoolPostOtherUserBottomSheetLauncher(ArrayList<NoticesMainModel> allPost,Activity activity , OtherUser otherUser , CurrentUser currentUser , NoticesMainModel post , TrueFalse<Boolean> callback){
        RecyclerView recyclerView;
        CardView headerView;
        CircleImageView profileImage;
        TextView username;
        Button fallow;
        Button cancel;
        ArrayList<String > items = new ArrayList<>();

        items.add(BottomSheetActionTarget.bu_gonderiyi_sikayet_et);
        if (otherUser.getSlient().contains(currentUser.getUid())){
            items.add(BottomSheetActionTarget.bu_kullaniciyi_sessiden_al);
        }else{
            items.add(BottomSheetActionTarget.bu_kullaniciyi_sessize_al);
        }

        items.add(BottomSheetActionTarget.bu_kullaniciyi_sikayet_et);
        ArrayList<Integer> res = new ArrayList<>();

        res.add(R.drawable.black_color_report);
        if (otherUser.getSlient().contains(currentUser.getUid())){
            Log.d(TAG, "VayeAppOtherUserBottomSheetLauncher: "+otherUser.getSlient());
            res.add(R.drawable.make_not_mute);
        }else{
            Log.d(TAG, "VayeAppOtherUserBottomSheetLauncher: "+otherUser.getSlient());
            res.add(R.drawable.make_mute);
        }

        res.add(R.drawable.red_report);
        BottomSheetModel model = new BottomSheetModel(items,BottomSheetTarget.school_app_other_user_launcher,res);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity,R.style.BottomSheetDialogTheme);
        View view = LayoutInflater.from(activity.getApplicationContext())
                .inflate(R.layout.action_bottom_sheet_layout,(RelativeLayout)activity.findViewById(R.id.dialog));
        SchoolPostBottomSheetAdapter adapter = new SchoolPostBottomSheetAdapter(allPost,post , currentUser ,model,bottomSheetDialog,otherUser,activity);
        recyclerView = (RecyclerView)view.findViewById(R.id.optionList);
        headerView = (CardView)view.findViewById(R.id.header);
        profileImage = (CircleImageView)view.findViewById(R.id.profileImage);
        headerView.setVisibility(View.VISIBLE);
        username = (TextView) view.findViewById(R.id.username);
        fallow = (Button)view.findViewById(R.id.fallow);
        username.setText( otherUser.getUsername());



        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        adapter.notifyDataSetChanged();
        cancel = (Button)view.findViewById(R.id.dismis);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                headerView.setVisibility(View.GONE);
                bottomSheetDialog.dismiss();

            }
        });
        bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                headerView.setVisibility(View.GONE);

                callback.callBack(true);
            }
        });
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
        username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (activity instanceof OtherUserProfileActivity){
                    return;
                }else{
                    Intent i = new Intent(activity , OtherUserProfileActivity.class);
                    i.putExtra("currentUser",currentUser);
                    i.putExtra("otherUser",otherUser);
                    activity.startActivity(i);
                    go(activity);
                    bottomSheetDialog.dismiss();
                }


            }
        });

        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (activity instanceof OtherUserProfileActivity){
                    return;
                }else{
                    Intent i = new Intent(activity , OtherUserProfileActivity.class);
                    i.putExtra("currentUser",currentUser);
                    i.putExtra("otherUser",otherUser);
                    activity.startActivity(i);
                    go(activity);
                    bottomSheetDialog.dismiss();
                }
            }
        });
        if (otherUser.getThumb_image() != null && !otherUser.getThumb_image().isEmpty()){
            Picasso.get().load(otherUser.getThumb_image()).placeholder(android.R.color.darker_gray).resize(128,128)
                    .centerCrop().into(profileImage);
        }

        UserService.shared().checkIsFallowing(currentUser, otherUser, new TrueFalse<Boolean>() {
            @Override
            public void callBack(Boolean _value) {
                if (_value){
                    fallow.setText("Takibi Bırak");
                    fallow.setBackgroundResource(R.drawable.button_unfollow_back);
                    isFallowing = _value;
                }else{
                    fallow.setBackgroundResource(R.drawable.button_fallow_back);
                    fallow.setText("Takip Et");
                    isFallowing = _value;
                }
            }
        });

        fallow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFallowing){
                    WaitDialog.show((AppCompatActivity) activity , "Lütfen Bekleyin");
                    FollowService.shared().unFollowUser(otherUser, currentUser, new TrueFalse<Boolean>() {
                        @Override
                        public void callBack(Boolean _value) {
                            if (_value){
                                WaitDialog.dismiss();
                                TipDialog.show((AppCompatActivity) activity , "Takip Etmeyi Bıraktınız", TipDialog.TYPE.SUCCESS);
                                TipDialog.dismiss(1000);
                                bottomSheetDialog.dismiss();
                                isFallowing = false;
                            }else{
                                WaitDialog.dismiss();
                                TipDialog.show((AppCompatActivity) activity , "Sorun Oluştu Lütfen Tekrar Deneyin", TipDialog.TYPE.WARNING);
                                TipDialog.dismiss(1000);
                                bottomSheetDialog.dismiss();
                            }
                        }
                    });
                }else{
                    WaitDialog.show((AppCompatActivity)activity , "Lütfen Bekleyin");
                    FollowService.shared().followUser(otherUser, currentUser, new TrueFalse<Boolean>() {
                        @Override
                        public void callBack(Boolean _value) {
                            if (_value){
                                WaitDialog.dismiss();
                                TipDialog.show((AppCompatActivity) activity , "Takip Ediliyor", TipDialog.TYPE.SUCCESS);
                                NotificaitonService.shared().start_following_you(currentUser, otherUser, Notifications.NotificationDescription.following_you, Notifications.NotificationType.following_you, new TrueFalse<Boolean>() {
                                    @Override
                                    public void callBack(Boolean _value) {

                                    }
                                });
                                TipDialog.dismiss(1000);
                                bottomSheetDialog.dismiss();
                                isFallowing = true;
                            }else{
                                WaitDialog.dismiss();
                                TipDialog.show((AppCompatActivity) activity , "Sorun Oluştu Lütfen Tekrar Deneyin", TipDialog.TYPE.WARNING);
                                TipDialog.dismiss(1000);
                                bottomSheetDialog.dismiss();
                            }
                        }
                    });
                }
            }
        });
    }

    public void MajorPostCurrentUserBottomSheetLaunher(Activity activity, ArrayList<LessonPostModel> allPost,  CurrentUser currentUser , LessonPostModel post , TrueFalse<Boolean> callback){
        RecyclerView recyclerView;
        CardView headerView;
        Button cancel;
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity,R.style.BottomSheetDialogTheme);
        View view = LayoutInflater.from(activity.getApplicationContext())
                .inflate(R.layout.action_bottom_sheet_layout,(RelativeLayout)activity.findViewById(R.id.dialog));
        ArrayList<String > items = new ArrayList<>();
        items.add(BottomSheetActionTarget.gonderiyi_düzenle);
        items.add(BottomSheetActionTarget.gonderiyi_sil);
        if (post.getSilent().contains(currentUser.getUid())){
            items.add(BottomSheetActionTarget.gonderi_bildirimlerini_ac);
        }else{
            items.add(BottomSheetActionTarget.gonderiyi_sessize_al);
        }


        ArrayList<Integer> res = new ArrayList<>();
        res.add(R.drawable.edit);
        res.add(R.drawable.trash);
        if (post.getSilent().contains(currentUser.getUid())){
            res.add(R.drawable.slient_selected);
        }else{

            res.add(R.drawable.slient);
        }

        BottomSheetModel model = new BottomSheetModel(items, BottomSheetTarget.major_currentUser_launcher,res);
        MajorPostBottomAdapter adapter = new MajorPostBottomAdapter(allPost,post,currentUser,model,bottomSheetDialog,activity);
        recyclerView = (RecyclerView)view.findViewById(R.id.optionList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        adapter.notifyDataSetChanged();
        headerView = (CardView)view.findViewById(R.id.header);
        headerView.setVisibility(View.GONE);
        cancel = (Button)view.findViewById(R.id.dismis);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                callback.callBack(true);
            }
        });
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();

    }
    public void MajorPostOtherUserBottomSheetLaunher(Activity activity, ArrayList<LessonPostModel> allPost, OtherUser otherUser, CurrentUser currentUser , LessonPostModel post , BlockOptionSelect optionSelect, TrueFalse<Boolean> callback){
        RecyclerView recyclerView;
        CardView headerView;
        CircleImageView profileImage;
        TextView username;
        Button fallow;
        Button cancel;
        ArrayList<String > items = new ArrayList<>();

        items.add(BottomSheetActionTarget.bu_gonderiyi_sikayet_et);
        if (otherUser.getSlient().contains(currentUser.getUid())){
            items.add(BottomSheetActionTarget.bu_kullaniciyi_sessiden_al);
        }else{
            items.add(BottomSheetActionTarget.bu_kullaniciyi_sessize_al);
        }
        items.add(BottomSheetActionTarget.bu_dersi_takip_etmeyi_birak);
        items.add(BottomSheetActionTarget.bu_kullaniciyi_sikayet_et);
        items.add(BottomSheetActionTarget.getBu_kullaniciyi_engelle);
        ArrayList<Integer> res = new ArrayList<>();

        res.add(R.drawable.black_color_report);
        if (otherUser.getSlient().contains(currentUser.getUid())){
            Log.d(TAG, "VayeAppOtherUserBottomSheetLauncher: "+otherUser.getSlient());
            res.add(R.drawable.make_not_mute);
        }else{
            Log.d(TAG, "VayeAppOtherUserBottomSheetLauncher: "+otherUser.getSlient());
            res.add(R.drawable.make_mute);
        }
        res.add(R.drawable.cancel);
        res.add(R.drawable.red_report);
        res.add(R.drawable.block_user);
        BottomSheetModel model = new BottomSheetModel(items,BottomSheetTarget.major_OtherUser_launcher,res);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity,R.style.BottomSheetDialogTheme);
        View view = LayoutInflater.from(activity.getApplicationContext())
                .inflate(R.layout.action_bottom_sheet_layout,(RelativeLayout)activity.findViewById(R.id.dialog));
        MajorPostBottomAdapter adapter = new MajorPostBottomAdapter(allPost,post , currentUser ,model,bottomSheetDialog,otherUser,activity,optionSelect);
        recyclerView = (RecyclerView)view.findViewById(R.id.optionList);
        headerView = (CardView)view.findViewById(R.id.header);
        profileImage = (CircleImageView)view.findViewById(R.id.profileImage);
        headerView.setVisibility(View.VISIBLE);
        username = (TextView) view.findViewById(R.id.username);
        fallow = (Button)view.findViewById(R.id.fallow);
        username.setText( otherUser.getUsername());



        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        adapter.notifyDataSetChanged();
        cancel = (Button)view.findViewById(R.id.dismis);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                headerView.setVisibility(View.GONE);
                bottomSheetDialog.dismiss();

            }
        });
        bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                headerView.setVisibility(View.GONE);

                callback.callBack(true);
            }
        });
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
        username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (activity instanceof OtherUserProfileActivity){
                    return;
                }else{
                    Intent i = new Intent(activity , OtherUserProfileActivity.class);
                    i.putExtra("currentUser",currentUser);
                    i.putExtra("otherUser",otherUser);
                    activity.startActivity(i);
                    go(activity);
                    bottomSheetDialog.dismiss();
                }
            }
        });

        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (activity instanceof OtherUserProfileActivity){
                    return;
                }else{
                    Intent i = new Intent(activity , OtherUserProfileActivity.class);
                    i.putExtra("currentUser",currentUser);
                    i.putExtra("otherUser",otherUser);
                    activity.startActivity(i);
                    go(activity);
                    bottomSheetDialog.dismiss();
                }
            }
        });
        if (otherUser.getThumb_image() != null && !otherUser.getThumb_image().isEmpty()){
            Picasso.get().load(otherUser.getThumb_image()).placeholder(android.R.color.darker_gray).resize(128,128)
                    .centerCrop().into(profileImage);
        }

        UserService.shared().checkIsFallowing(currentUser, otherUser, new TrueFalse<Boolean>() {
            @Override
            public void callBack(Boolean _value) {
                if (_value){
                    fallow.setText("Takibi Bırak");
                    fallow.setBackgroundResource(R.drawable.button_unfollow_back);
                    isFallowing = _value;
                }else{
                    fallow.setBackgroundResource(R.drawable.button_fallow_back);
                    fallow.setText("Takip Et");
                    isFallowing = _value;
                }
            }
        });

        fallow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFallowing){
                    WaitDialog.show((AppCompatActivity) activity , "Lütfen Bekleyin");
                    FollowService.shared().unFollowUser(otherUser, currentUser, new TrueFalse<Boolean>() {
                        @Override
                        public void callBack(Boolean _value) {
                            if (_value){
                                WaitDialog.dismiss();
                                TipDialog.show((AppCompatActivity) activity , "Takip Etmeyi Bıraktınız", TipDialog.TYPE.SUCCESS);
                                TipDialog.dismiss(1000);
                                bottomSheetDialog.dismiss();
                                isFallowing = false;
                            }else{
                                WaitDialog.dismiss();
                                TipDialog.show((AppCompatActivity) activity , "Sorun Oluştu Lütfen Tekrar Deneyin", TipDialog.TYPE.WARNING);
                                TipDialog.dismiss(1000);
                                bottomSheetDialog.dismiss();
                            }
                        }
                    });
                }else{
                    WaitDialog.show((AppCompatActivity)activity , "Lütfen Bekleyin");
                    FollowService.shared().followUser(otherUser, currentUser, new TrueFalse<Boolean>() {
                        @Override
                        public void callBack(Boolean _value) {
                            if (_value){
                                WaitDialog.dismiss();
                                TipDialog.show((AppCompatActivity) activity , "Takip Ediliyor", TipDialog.TYPE.SUCCESS);
                                NotificaitonService.shared().start_following_you(currentUser, otherUser, Notifications.NotificationDescription.following_you, Notifications.NotificationType.following_you, new TrueFalse<Boolean>() {
                                    @Override
                                    public void callBack(Boolean _value) {

                                    }
                                });
                                TipDialog.dismiss(1000);
                                bottomSheetDialog.dismiss();
                                isFallowing = true;
                            }else{
                                WaitDialog.dismiss();
                                TipDialog.show((AppCompatActivity) activity , "Sorun Oluştu Lütfen Tekrar Deneyin", TipDialog.TYPE.WARNING);
                                TipDialog.dismiss(1000);
                                bottomSheetDialog.dismiss();
                            }
                        }
                    });
                }
            }
        });

    }



    public void VayeAppCurrentUserBottomSheetLauncher(ArrayList<MainPostModel> allPost,Activity activity , CurrentUser currentUser , MainPostModel post , TrueFalse<Boolean> callback){
       RecyclerView recyclerView;
       CardView headerView;
       Button cancel;
       BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity,R.style.BottomSheetDialogTheme);
       View view = LayoutInflater.from(activity.getApplicationContext())
               .inflate(R.layout.action_bottom_sheet_layout,(RelativeLayout)activity.findViewById(R.id.dialog));
       ArrayList<String > items = new ArrayList<>();
       items.add(BottomSheetActionTarget.gonderiyi_düzenle);
       items.add(BottomSheetActionTarget.gonderiyi_sil);
       if (post.getSilent().contains(currentUser.getUid())){
           items.add(BottomSheetActionTarget.gonderi_bildirimlerini_ac);
       }else{
           items.add(BottomSheetActionTarget.gonderiyi_sessize_al);
       }


       ArrayList<Integer> res = new ArrayList<>();
       res.add(R.drawable.edit);
       res.add(R.drawable.trash);
       if (post.getSilent().contains(currentUser.getUid())){
           res.add(R.drawable.slient_selected);
       }else{

           res.add(R.drawable.slient);
       }



       BottomSheetModel model = new BottomSheetModel(items, BottomSheetTarget.vaye_app_current_user_launcher,res);
       VayeAppBottomSheet adapter = new VayeAppBottomSheet(allPost,post,currentUser,model,bottomSheetDialog,activity);
       recyclerView = (RecyclerView)view.findViewById(R.id.optionList);
       recyclerView.setAdapter(adapter);
       recyclerView.setLayoutManager(new LinearLayoutManager(activity));
       adapter.notifyDataSetChanged();
       headerView = (CardView)view.findViewById(R.id.header);
       headerView.setVisibility(View.GONE);
       cancel = (Button)view.findViewById(R.id.dismis);
       cancel.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {

               bottomSheetDialog.dismiss();
           }
       });

       bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
           @Override
           public void onDismiss(DialogInterface dialogInterface) {
               callback.callBack(true);
           }
       });
       bottomSheetDialog.setContentView(view);
       bottomSheetDialog.show();
   }
    public void VayeAppOtherUserBottomSheetLauncher(ArrayList<MainPostModel> allPost,Activity activity , OtherUser otherUser , CurrentUser currentUser , MainPostModel post ,BlockOptionSelect optionSelect, TrueFalse<Boolean> callback){
       RecyclerView recyclerView;
       CardView headerView;
       CircleImageView profileImage;
       TextView username;
       Button fallow;
       Button cancel;
         ArrayList<String > items = new ArrayList<>();

           items.add(BottomSheetActionTarget.bu_gonderiyi_sikayet_et);
           if (otherUser.getSlient().contains(currentUser.getUid())){
               items.add(BottomSheetActionTarget.bu_kullaniciyi_sessiden_al);
           }else{
               items.add(BottomSheetActionTarget.bu_kullaniciyi_sessize_al);
           }

           items.add(BottomSheetActionTarget.bu_kullaniciyi_sikayet_et);
           items.add(BottomSheetActionTarget.getBu_kullaniciyi_engelle);
           ArrayList<Integer> res = new ArrayList<>();

           res.add(R.drawable.black_color_report);
       if (otherUser.getSlient().contains(currentUser.getUid())){
           Log.d(TAG, "VayeAppOtherUserBottomSheetLauncher: "+otherUser.getSlient());
           res.add(R.drawable.make_not_mute);
       }else{
           Log.d(TAG, "VayeAppOtherUserBottomSheetLauncher: "+otherUser.getSlient());
           res.add(R.drawable.make_mute);
       }

           res.add(R.drawable.red_report);
         res.add(R.drawable.block_user);

       BottomSheetModel model = new BottomSheetModel(items,BottomSheetTarget.vaye_app_other_user_launcher,res);
       BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity,R.style.BottomSheetDialogTheme);
       View view = LayoutInflater.from(activity.getApplicationContext())
               .inflate(R.layout.action_bottom_sheet_layout,(RelativeLayout)activity.findViewById(R.id.dialog));
       VayeAppBottomSheet adapter = new VayeAppBottomSheet(allPost,post , currentUser ,model,bottomSheetDialog,otherUser,activity,optionSelect);
       recyclerView = (RecyclerView)view.findViewById(R.id.optionList);
       headerView = (CardView)view.findViewById(R.id.header);
       profileImage = (CircleImageView)view.findViewById(R.id.profileImage);
       headerView.setVisibility(View.VISIBLE);
       username = (TextView) view.findViewById(R.id.username);
       fallow = (Button)view.findViewById(R.id.fallow);
       username.setText( otherUser.getUsername());



       recyclerView.setAdapter(adapter);
       recyclerView.setLayoutManager(new LinearLayoutManager(activity));
       adapter.notifyDataSetChanged();
       cancel = (Button)view.findViewById(R.id.dismis);
       cancel.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               headerView.setVisibility(View.GONE);
               bottomSheetDialog.dismiss();

           }
       });
       bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
           @Override
           public void onDismiss(DialogInterface dialogInterface) {
               headerView.setVisibility(View.GONE);

               callback.callBack(true);
           }
       });
       bottomSheetDialog.setContentView(view);
       bottomSheetDialog.show();
       username.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               if (activity instanceof OtherUserProfileActivity){
                   return;
               }else{
                   Intent i = new Intent(activity , OtherUserProfileActivity.class);
                   i.putExtra("currentUser",currentUser);
                   i.putExtra("otherUser",otherUser);
                   activity.startActivity(i);
                   go(activity);
                   bottomSheetDialog.dismiss();
               }
           }
       });

       headerView.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               if (activity instanceof OtherUserProfileActivity){
                   return;
               }else{
                   Intent i = new Intent(activity , OtherUserProfileActivity.class);
                   i.putExtra("currentUser",currentUser);
                   i.putExtra("otherUser",otherUser);
                   activity.startActivity(i);
                   go(activity);
                   bottomSheetDialog.dismiss();
               }
           }
       });
       if (otherUser.getThumb_image() != null && !otherUser.getThumb_image().isEmpty()){
           Picasso.get().load(otherUser.getThumb_image()).placeholder(android.R.color.darker_gray).resize(128,128)
                   .centerCrop().into(profileImage);
       }

       UserService.shared().checkIsFallowing(currentUser, otherUser, new TrueFalse<Boolean>() {
           @Override
           public void callBack(Boolean _value) {
               if (_value){
                   fallow.setText("Takibi Bırak");
                   fallow.setBackgroundResource(R.drawable.button_unfollow_back);
                   isFallowing = _value;
               }else{
                   fallow.setBackgroundResource(R.drawable.button_fallow_back);
                   fallow.setText("Takip Et");
                   isFallowing = _value;
               }
           }
       });

       fallow.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               if (isFallowing){
                    WaitDialog.show((AppCompatActivity) activity , "Lütfen Bekleyin");
                    FollowService.shared().unFollowUser(otherUser, currentUser, new TrueFalse<Boolean>() {
                        @Override
                        public void callBack(Boolean _value) {
                            if (_value){
                                WaitDialog.dismiss();
                                TipDialog.show((AppCompatActivity) activity , "Takip Etmeyi Bıraktınız", TipDialog.TYPE.SUCCESS);
                                TipDialog.dismiss(1000);
                                bottomSheetDialog.dismiss();
                                isFallowing = false;
                            }else{
                                WaitDialog.dismiss();
                                TipDialog.show((AppCompatActivity) activity , "Sorun Oluştu Lütfen Tekrar Deneyin", TipDialog.TYPE.WARNING);
                                TipDialog.dismiss(1000);
                                bottomSheetDialog.dismiss();
                            }
                        }
                    });
               }else{
                   WaitDialog.show((AppCompatActivity)activity , "Lütfen Bekleyin");
                   FollowService.shared().followUser(otherUser, currentUser, new TrueFalse<Boolean>() {
                       @Override
                       public void callBack(Boolean _value) {
                           if (_value){
                               WaitDialog.dismiss();
                               TipDialog.show((AppCompatActivity) activity , "Takip Ediliyor", TipDialog.TYPE.SUCCESS);
                               NotificaitonService.shared().start_following_you(currentUser, otherUser, Notifications.NotificationDescription.following_you, Notifications.NotificationType.following_you, new TrueFalse<Boolean>() {
                                   @Override
                                   public void callBack(Boolean _value) {

                                   }
                               });
                               TipDialog.dismiss(1000);
                               bottomSheetDialog.dismiss();
                               isFallowing = true;
                           }else{
                               WaitDialog.dismiss();
                               TipDialog.show((AppCompatActivity) activity , "Sorun Oluştu Lütfen Tekrar Deneyin", TipDialog.TYPE.WARNING);
                               TipDialog.dismiss(1000);
                               bottomSheetDialog.dismiss();
                           }
                       }
                   });
               }
           }
       });


   }
    public void VayeAppChooseTargetBottomSheetLaunher(Activity activity ,CurrentUser currentUser , TrueFalse<Boolean> callback){
        RecyclerView recyclerView;
        CardView headerView;
        Button cancel;
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity,R.style.BottomSheetDialogTheme);
        View view = LayoutInflater.from(activity.getApplicationContext())
                .inflate(R.layout.action_bottom_sheet_layout,(RelativeLayout)activity.findViewById(R.id.dialog));
        ArrayList<String > items = new ArrayList<>();
        items.add(BottomSheetActionTarget.al_sat);
        items.add(BottomSheetActionTarget.yemek);
        items.add(BottomSheetActionTarget.kamp);
        ArrayList<Integer> res = new ArrayList<>();
        res.add(R.drawable.buy_sell_selected);
        res.add(R.drawable.food_me_selected);
        res.add(R.drawable.camping_selected);




        BottomSheetModel model = new BottomSheetModel(items, BottomSheetTarget.vaye_app_current_user_launcher,res);
        VayeAppChooseTargetBottomSheetAdapter adapter = new VayeAppChooseTargetBottomSheetAdapter(currentUser , activity , model , bottomSheetDialog);
        recyclerView = (RecyclerView)view.findViewById(R.id.optionList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        adapter.notifyDataSetChanged();
        headerView = (CardView)view.findViewById(R.id.header);
        headerView.setVisibility(View.GONE);
        cancel = (Button)view.findViewById(R.id.dismis);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                callback.callBack(true);
            }
        });
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
    }
    public void LocalNotificationBottomSheetLauncher(Activity activity ,CurrentUser currentUser , TrueFalse<Boolean> callback){
        RecyclerView recyclerView;
        CardView headerView;
        Button cancel;
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity,R.style.BottomSheetDialogTheme);
        View view = LayoutInflater.from(activity.getApplicationContext())
                .inflate(R.layout.action_bottom_sheet_layout,(RelativeLayout)activity.findViewById(R.id.dialog));
        ArrayList<String > items = new ArrayList<>();
        items.add(BottomSheetActionTarget.make_all_notification_read);
        items.add(BottomSheetActionTarget.delete_all_notification);

        ArrayList<Integer> res = new ArrayList<>();
        res.add(R.drawable.saw);
        res.add(R.drawable.trash);
        BottomSheetModel model = new BottomSheetModel(items, BottomSheetTarget.local_notification_setting,res);
        VayeAppChooseTargetBottomSheetAdapter adapter = new VayeAppChooseTargetBottomSheetAdapter(currentUser , activity , model , bottomSheetDialog);
        recyclerView = (RecyclerView)view.findViewById(R.id.optionList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        adapter.notifyDataSetChanged();
        headerView = (CardView)view.findViewById(R.id.header);
        headerView.setVisibility(View.GONE);
        cancel = (Button)view.findViewById(R.id.dismis);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                callback.callBack(true);
            }
        });
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
    }

    public void MessageMediaLauncher(Activity activity ,OtherUser otherUser, CurrentUser currentUser,OnOptionSelect optionSelect , TrueFalse<Boolean> callback){
        RecyclerView recyclerView;
        CardView headerView;
        Button cancel;
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity,R.style.BottomSheetDialogTheme);
        View view = LayoutInflater.from(activity.getApplicationContext())
                .inflate(R.layout.action_bottom_sheet_layout,(RelativeLayout)activity.findViewById(R.id.dialog));


        ArrayList<String > items = new ArrayList<>();
        items.add(BottomSheetActionTarget.send_image);
        items.add(BottomSheetActionTarget.send_location);
        items.add(BottomSheetActionTarget.send_document);

        ArrayList<Integer> res = new ArrayList<>();
        res.add(R.drawable.gallery_btn);
        res.add(R.drawable.orange_location);
        res.add(R.drawable.belge);
        BottomSheetModel model = new BottomSheetModel(items, BottomSheetTarget.media_item,res);

        MessageMediaAdapter adapter = new MessageMediaAdapter(otherUser,currentUser , activity , model , bottomSheetDialog ,optionSelect);
        recyclerView = (RecyclerView)view.findViewById(R.id.optionList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        adapter.notifyDataSetChanged();
        headerView = (CardView)view.findViewById(R.id.header);
        headerView.setVisibility(View.GONE);
        cancel = (Button)view.findViewById(R.id.dismis);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                callback.callBack(true);
            }
        });
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
    }

    public void ProfileImageSetting(Activity activity , OnOptionSelect onOptionSelect, CurrentUser currentUser ,  TrueFalse<Boolean> callback){
        RecyclerView recyclerView;
        CardView headerView;
        Button cancel;

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity,R.style.BottomSheetDialogTheme);
        View view = LayoutInflater.from(activity.getApplicationContext())
                .inflate(R.layout.action_bottom_sheet_layout,(RelativeLayout)activity.findViewById(R.id.dialog));
        ArrayList<String > items = new ArrayList<>();
        items.add(BottomSheetActionTarget.show_profile_image);
        items.add(BottomSheetActionTarget.delete_profile_image);
        items.add(BottomSheetActionTarget.take_new_image);
        items.add(BottomSheetActionTarget.choose_image_from_gallery);
        ArrayList<Integer> res = new ArrayList<>();
        res.add(R.drawable.full_screen);
        res.add(R.drawable.trash);
        res.add(R.drawable.camera);
        res.add(R.drawable.gallery_btn);

        BottomSheetModel model = new BottomSheetModel(items, BottomSheetTarget.profile_image_setting,res);
        ProfileImageSettingAdapter adapter = new ProfileImageSettingAdapter(currentUser , activity , model , bottomSheetDialog,onOptionSelect);
        recyclerView = (RecyclerView)view.findViewById(R.id.optionList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        adapter.notifyDataSetChanged();
        headerView = (CardView)view.findViewById(R.id.header);
        headerView.setVisibility(View.GONE);
        cancel = (Button)view.findViewById(R.id.dismis);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                callback.callBack(true);
            }
        });
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
    }

    public ArrayList<String> getMentionedUser(String text){
        ArrayList<String> user = new ArrayList<>();
        String[] words = text.split("[ \\.]");
        for (int i = 0; i < words.length; i++) {
            if (words[i].length() > 0
                    && words[i].charAt(0) == '@') {
                System.out.println(words[i]);
                user.add(words[i]);
            }
        }
        return  user;
    }

    public HashMap<String , Object> getDictionary( String postType , String  type , String text , CurrentUser currentUser ,String not_id , String targetCommentId,
    String postId , String lessonName , String clupName , String vayeAppPostName ){
        HashMap<String,Object> map = new HashMap<>();
        map.put( "type",type);
        map.put( "postType",postType);
        map.put( "senderUid",currentUser.getUid());
        map.put( "time",FieldValue.serverTimestamp());
        map.put( "senderImage",currentUser.getThumb_image());
        map.put( "text",text);
        map.put( "not_id",not_id);
        map.put( "isRead","false");
        map.put( "postId",postId);
        map.put( "username",currentUser.getUsername());
        map.put( "senderName",currentUser.getName());
        if (targetCommentId != null){
            map.put( "targetCommentId",targetCommentId);
        }else{
            map.put( "targetCommentId","");
        }

        if (lessonName != null){
            map.put("lessonName",lessonName);
        }else if (clupName !=null){
            map.put("lessonName",clupName);
        }
        else if (vayeAppPostName !=null){
            map.put("lessonName",vayeAppPostName);
        }else{
            map.put("lessonName","");
        }


        return  map;
    }


    public String getMainText(NotificationModel model){
        String text  = "";

        if (model.getPostType().equals( NotificationPostType.name.lessonPost)){
            if (model.getType().equals(MajorPostNotification.type.comment_like)){
                text = MajorPostNotification.descp.comment_like;
            }else if (model.getType().equals(MajorPostNotification.type.post_like)){
                text = MajorPostNotification.descp.post_like;
            }
            else if (model.getType().equals(MajorPostNotification.type.new_comment)){
                text = MajorPostNotification.descp.new_comment;
            }else if (model.getType().equals(MajorPostNotification.type.new_mentioned_comment)){
                text = MajorPostNotification.descp.new_mentioned_comment;
            }else if (model.getType().equals(MajorPostNotification.type.new_post)){
                text = MajorPostNotification.descp.new_post;
            }else if (model.getType().equals(MajorPostNotification.type.new_mentioned_post)){
                text = MajorPostNotification.descp.new_mentioned_post;
            }else if (model.getType().equals(MajorPostNotification.type.new_replied_comment)){
                text = MajorPostNotification.descp.new_replied_comment;
            }else if (model.getType().equals(MajorPostNotification.type.new_replied_mentioned_comment)){
                text = MajorPostNotification.descp.new_replied_mentioned_comment;
            }else if (model.getType().equals(MajorPostNotification.type.replied_comment_like)){
                text = MajorPostNotification.descp.replied_comment_like;
            }
        }
        else if (model.getPostType().equals( NotificationPostType.name.notices)){

            if (model.getType().equals(NoticesPostNotification.type.comment_like)){
                text = NoticesPostNotification.descp.comment_like;
            }
            else if (model.getType().equals(NoticesPostNotification.type.post_like)){
                text = NoticesPostNotification.descp.post_like;
            }
            else if (model.getType().equals(NoticesPostNotification.type.new_comment)){
                 text = NoticesPostNotification.descp.new_comment;
                Log.d(TAG, "getMainText: "+text);
            }else if (model.getType().equals(NoticesPostNotification.type.new_mentioned_comment)){
                text = NoticesPostNotification.descp.new_mentioned_comment;
            }else if (model.getType().equals(NoticesPostNotification.type.new_post)){
                text = NoticesPostNotification.descp.new_post;
            }else if (model.getType().equals(NoticesPostNotification.type.new_mentioned_post)){
                text = NoticesPostNotification.descp.new_mentioned_post;
            }else if (model.getType().equals(NoticesPostNotification.type.new_replied_comment)){
                text = NoticesPostNotification.descp.new_replied_comment;
            }else if (model.getType().equals(NoticesPostNotification.type.new_replied_mentioned_comment)){
                text = NoticesPostNotification.descp.new_replied_mentioned_comment;
            }else if (model.getType().equals(NoticesPostNotification.type.replied_comment_like)){
                text = NoticesPostNotification.descp.replied_comment_like;
            }
        }
        else if (model.getPostType().equals(NotificationPostType.name.mainPost)){
            if (model.getType().equals(MainPostNotification.type.comment_like)){
                text = MainPostNotification.descp.comment_like;
            }else if (model.getType().equals(MainPostNotification.type.new_comment)){
                text = MainPostNotification.descp.new_comment;
            }else if (model.getType().equals(MainPostNotification.type.new_mentioned_comment)){
                text = MainPostNotification.descp.new_mentioned_comment;
            }else if (model.getType().equals(MainPostNotification.type.new_post)){
                text = MainPostNotification.descp.new_post;
            }else if (model.getType().equals(MainPostNotification.type.new_mentioned_post)){
                text = MainPostNotification.descp.new_mentioned_post;
            }else if (model.getType().equals(MainPostNotification.type.new_replied_comment)){
                text = MainPostNotification.descp.new_replied_comment;
            }else if (model.getType().equals(MainPostNotification.type.new_replied_mentioned_comment)){
                text = MainPostNotification.descp.new_replied_mentioned_comment;
            }else if (model.getType().equals(MainPostNotification.type.replied_comment_like)){
                text = MainPostNotification.descp.replied_comment_like;
            }
            else if (model.getType().equals(MainPostNotification.type.post_like)){
                text = MainPostNotification.descp.post_like;
            }
        }
        else if (model.getPostType().equals(NotificationPostType.name.follow)){
            if (model.getType().equals(Notifications.NotificationType.following_you)){
                text = FollowNotification.desp.follow_you;
            }
        }

        return  text;
    }



}

