package com.vaye.app.Util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.kongzue.dialog.v3.TipDialog;
import com.kongzue.dialog.v3.WaitDialog;
import com.squareup.picasso.Picasso;
import com.vaye.app.Interfaces.CompletionWithValue;
import com.vaye.app.Interfaces.Notifications;
import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.LessonModel;
import com.vaye.app.Model.LessonPostModel;
import com.vaye.app.Model.MainPostModel;
import com.vaye.app.Model.OtherUser;
import com.vaye.app.R;
import com.vaye.app.Services.NotificaitonService;
import com.vaye.app.Services.UserService;
import com.vaye.app.Util.BottomSheetHelper.BottomSheetActionTarget;
import com.vaye.app.Util.BottomSheetHelper.BottomSheetAdapter;
import com.vaye.app.Util.BottomSheetHelper.BottomSheetModel;
import com.vaye.app.Util.BottomSheetHelper.BottomSheetTarget;
import com.vaye.app.Util.BottomSheetHelper.VayeAppBottomSheet;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Helper {
    private boolean isFallowing = false;
    private static final String TAG = "Helper";
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

   public void BottomSheet_LessonCurrenUser_Dialog(Activity activity, ArrayList<LessonPostModel> lessonPostModels, String  target , CurrentUser currentUser , BottomSheetModel model , LessonPostModel post , TrueFalse<Boolean> val){
       RecyclerView recyclerView;
       CardView headerView;
       Button cancel;
       BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity,R.style.BottomSheetDialogTheme);
       View view = LayoutInflater.from(activity.getApplicationContext())
               .inflate(R.layout.action_bottom_sheet_layout,(RelativeLayout)activity.findViewById(R.id.dialog));
       BottomSheetAdapter adapter = new BottomSheetAdapter(target ,currentUser ,activity ,model , bottomSheetDialog , post,lessonPostModels);
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
               val.callBack(true);
           }
       });
       bottomSheetDialog.setContentView(view);
       bottomSheetDialog.show();
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
       items.add(BottomSheetActionTarget.gonderiyi_sessize_al);

       ArrayList<Integer> res = new ArrayList<>();
       res.add(R.drawable.edit);
       res.add(R.drawable.trash);
       res.add(R.drawable.slient);


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

   public void VayeAppOtherUserBottomSheetLauncher(ArrayList<MainPostModel> allPost,Activity activity , OtherUser otherUser , CurrentUser currentUser , MainPostModel post , TrueFalse<Boolean> callback){
       RecyclerView recyclerView;
       CardView headerView;
       CircleImageView profileImage;
       TextView username;
       Button fallow;
       Button cancel;
         ArrayList<String > items = new ArrayList<>();
            items.add(BottomSheetActionTarget.gonderiyi_sessize_al);
           items.add(BottomSheetActionTarget.bu_gonderiyi_sikayet_et);
           items.add(BottomSheetActionTarget.bu_kullaniciyi_sessize_al);
           items.add(BottomSheetActionTarget.bu_kullaniciyi_sikayet_et);
           ArrayList<Integer> res = new ArrayList<>();
           res.add(R.drawable.slient);
           res.add(R.drawable.black_color_report);
           res.add(R.drawable.make_mute);
           res.add(R.drawable.red_report);

       BottomSheetModel model = new BottomSheetModel(items,BottomSheetTarget.vaye_app_other_user_launcher,res);
       BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity,R.style.BottomSheetDialogTheme);
       View view = LayoutInflater.from(activity.getApplicationContext())
               .inflate(R.layout.action_bottom_sheet_layout,(RelativeLayout)activity.findViewById(R.id.dialog));
       VayeAppBottomSheet adapter = new VayeAppBottomSheet(allPost,post , currentUser ,model,bottomSheetDialog,otherUser,activity);
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
               Log.d(TAG, "onClick: " + "show profile");
           }
       });

       headerView.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Log.d(TAG, "onClick: " + "show profile");
           }
       });
       if (otherUser.getThumb_image() != null && !otherUser.getThumb_image().isEmpty()){
           Picasso.get().load(otherUser.getThumb_image()).placeholder(android.R.color.darker_gray).resize(128,128)
                   .centerCrop().into(profileImage);
       }

   }

   public void BottomSheetOtherUser(Activity activity, OtherUser otherUser, String  target , CurrentUser currentUser , BottomSheetModel model , LessonPostModel post , TrueFalse<Boolean> val){
       RecyclerView recyclerView;
       CardView headerView;
       CircleImageView profileImage;
       TextView username;
       Button fallow;

       Button cancel;
       BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity,R.style.BottomSheetDialogTheme);
       View view = LayoutInflater.from(activity.getApplicationContext())
               .inflate(R.layout.action_bottom_sheet_layout,(RelativeLayout)activity.findViewById(R.id.dialog));
       BottomSheetAdapter adapter = new BottomSheetAdapter(target ,currentUser ,activity ,model , bottomSheetDialog , post);
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
                   isFallowing = _value;
               }else{
                   fallow.setBackgroundResource(R.drawable.button_fallow_back);
                   fallow.setText("Takip Et");
                   isFallowing = _value;
               }
           }
       });
        if (otherUser.getThumb_image() != null && !otherUser.getThumb_image().isEmpty()){
            Picasso.get().load(otherUser.getThumb_image()).placeholder(android.R.color.darker_gray).resize(128,128)
                    .centerCrop().into(profileImage);
        }


        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d(TAG, "onClick: " + "show profile");
            }
        });

        fallow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if (isFallowing){
                WaitDialog.show((AppCompatActivity) activity, null);
                UserService.shared().setUnFollow(currentUser.getUid(), otherUser.getUid(), new TrueFalse<Boolean>() {
                    @Override
                    public void callBack(Boolean _value) {
                        WaitDialog.dismiss();
                        TipDialog.show((AppCompatActivity) activity , "Takip Etmeyi Bıraktınız", TipDialog.TYPE.SUCCESS);
                        TipDialog.dismiss(1500);
                        bottomSheetDialog.dismiss();
                        isFallowing = false;
                    }
                });
            }else {
                WaitDialog.show((AppCompatActivity) activity, null);
                DocumentReference ref = FirebaseFirestore.getInstance().collection("user")
                        .document(otherUser.getUid())
                        .collection("fallowers")
                        .document(currentUser.getUid());
                Map<String , Object> map = new HashMap<>();
                map.put("user", FieldValue.arrayUnion(currentUser.getUid()));
                ref.set(map , SetOptions.merge()).addOnCompleteListener(activity, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            DocumentReference db = FirebaseFirestore.getInstance().collection("user")
                                    .document(currentUser.getUid())
                                    .collection("following")
                                    .document(otherUser.getUid());
                            Map<String  , Object> map1 = new HashMap<>();
                            map1.put("user",FieldValue.arrayUnion(otherUser.getUid()));
                            db.set(map1 , SetOptions.merge()).addOnCompleteListener(activity, new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                    UserService.shared().addAsMessegesFriend(currentUser, otherUser, new TrueFalse<Boolean>() {
                                        @Override
                                        public void callBack(Boolean _value) {
                                            NotificaitonService.shared().start_following_you(currentUser, otherUser, Notifications.NotificationDescription.following_you, Notifications.NotificationType.following_you, new TrueFalse<Boolean>() {
                                                @Override
                                                public void callBack(Boolean _value) {
                                                    WaitDialog.dismiss();
                                                    TipDialog.show((AppCompatActivity) activity , "Tekip Ediliyor", TipDialog.TYPE.SUCCESS);
                                                    TipDialog.dismiss(1500);
                                                    isFallowing = true;
                                                    bottomSheetDialog.dismiss();
                                                }
                                            });

                                        }
                                    });
                                    }
                                }
                            });
                        }
                    }
                });
            }
            }
        });

        username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: " + "show profile");
            }
        });

        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: " + "show profile");
            }
        });

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


}

