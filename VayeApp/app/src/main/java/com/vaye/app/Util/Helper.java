package com.vaye.app.Util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.Timestamp;
import com.squareup.picasso.Picasso;
import com.vaye.app.Interfaces.CompletionWithValue;
import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.LessonModel;
import com.vaye.app.Model.LessonPostModel;
import com.vaye.app.Model.OtherUser;
import com.vaye.app.R;
import com.vaye.app.Services.UserService;
import com.vaye.app.Util.BottomSheetHelper.BottomSheetActionTarget;
import com.vaye.app.Util.BottomSheetHelper.BottomSheetAdapter;
import com.vaye.app.Util.BottomSheetHelper.BottomSheetModel;

import java.util.ArrayList;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class Helper {
    private static final Helper instance = new Helper();
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
                // if timestamp given in seconds, convert to millis
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
    public void NewPostBottomSheetAddLink(Activity activity, String  target,String link , CurrentUser currentUser , BottomSheetModel model  , CompletionWithValue val){
        RecyclerView recyclerView;

        Button cancel;

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity,R.style.BottomSheetDialogTheme);
        View view = LayoutInflater.from(activity.getApplicationContext())
                .inflate(R.layout.action_bottom_sheet_layout,(RelativeLayout)activity.findViewById(R.id.dialog));
        BottomSheetAdapter adapter = new BottomSheetAdapter(target ,link,currentUser ,activity ,model , bottomSheetDialog );
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
                val.completion(true , link);

            }
        });
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
    }

   public void BottomSheet_LessonCurrenUser_Dialog(Activity activity, ArrayList<LessonPostModel> lessonPostModels, String  target , CurrentUser currentUser , BottomSheetModel model , LessonPostModel post , TrueFalse<Boolean> val){
       RecyclerView recyclerView;

       Button cancel;
       BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity,R.style.BottomSheetDialogTheme);
       View view = LayoutInflater.from(activity.getApplicationContext())
               .inflate(R.layout.action_bottom_sheet_layout,(RelativeLayout)activity.findViewById(R.id.dialog));
       BottomSheetAdapter adapter = new BottomSheetAdapter(target ,currentUser ,activity ,model , bottomSheetDialog , post,lessonPostModels);
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


   public void BottomSheetOtherUser(Activity activity, OtherUser otherUser, ArrayList<LessonPostModel> lessonPostModels, String  target , CurrentUser currentUser , BottomSheetModel model , LessonPostModel post , TrueFalse<Boolean> val){
       RecyclerView recyclerView;
       RelativeLayout headerView;
       CircleImageView profileImage;
       TextView username;
       Button fallow;

       Button cancel;
       BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity,R.style.BottomSheetDialogTheme);
       View view = LayoutInflater.from(activity.getApplicationContext())
               .inflate(R.layout.action_bottom_sheet_layout,(RelativeLayout)activity.findViewById(R.id.dialog));
       BottomSheetAdapter adapter = new BottomSheetAdapter(target ,currentUser ,activity ,model , bottomSheetDialog , post,lessonPostModels);
       recyclerView = (RecyclerView)view.findViewById(R.id.optionList);
       headerView = (RelativeLayout)view.findViewById(R.id.header);
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


       UserService.shared().checkIsFallowing(currentUser, otherUser, new TrueFalse<Boolean>() {
           @Override
           public void callBack(Boolean _value) {
               if (_value){
                   fallow.setText("Takibi Bırak");
                   fallow.setBackgroundResource(R.drawable.button_unfollow_back);
               }else{
                   fallow.setBackgroundResource(R.drawable.button_fallow_back);
                   fallow.setText("Takip Et");
               }
           }
       });
        if (otherUser.getThumb_image() != null){
            Picasso.get().load(otherUser.getThumb_image()).placeholder(android.R.color.darker_gray).resize(128,128)
                    .centerCrop().into(profileImage);
        }

   }



}

