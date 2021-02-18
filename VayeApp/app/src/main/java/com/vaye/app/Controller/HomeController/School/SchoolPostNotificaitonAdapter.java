package com.vaye.app.Controller.HomeController.School;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.kongzue.dialog.v3.TipDialog;
import com.kongzue.dialog.v3.WaitDialog;
import com.vaye.app.Controller.HomeController.LessonPostAdapter.MajorPostViewHolder;
import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.SchoolPostNotificationModel;
import com.vaye.app.R;
import com.vaye.app.Services.SchoolPostService;

import java.util.ArrayList;

public class SchoolPostNotificaitonAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    CurrentUser currentUser;
    Context context;
    ArrayList<SchoolPostNotificationModel> list;

    public SchoolPostNotificaitonAdapter(CurrentUser currentUser, Context context, ArrayList<SchoolPostNotificationModel> list) {
        this.currentUser = currentUser;
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.school_post_not_setting, parent, false);

            return new NotificationSetting(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        NotificationSetting viewholder = (NotificationSetting)holder;
        viewholder.setClupName(list.get(position).getName());
        viewholder.setSwitchCompat(list.get(position).getFollowers(),currentUser);
        viewholder.setFollowerCount(list.get(position).getFollowers());



        viewholder.switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked){
                    WaitDialog.show((AppCompatActivity) context , null);
                    SchoolPostService.shared().followClup(currentUser, list.get(position).getName(), new TrueFalse<Boolean>() {
                        @Override
                        public void callBack(Boolean _value) {
                            if (_value){
                                WaitDialog.dismiss();
                                TipDialog.show((AppCompatActivity)context , "Takip Ediliyor", TipDialog.TYPE.SUCCESS);
                                TipDialog.dismiss(1000);
                                list.get(position).getFollowers().add(currentUser.getUid());
                                viewholder.setFollowerCount(list.get(position).getFollowers());
                                notifyDataSetChanged();
                            }else{
                                WaitDialog.dismiss();
                                TipDialog.show((AppCompatActivity)context , "Hata Oluştu", TipDialog.TYPE.ERROR);
                                TipDialog.dismiss(1000);
                            }
                        }
                    });
                }else{

                    WaitDialog.show((AppCompatActivity) context , null);
                    SchoolPostService.shared().unFollowClup(currentUser, list.get(position).getName(), new TrueFalse<Boolean>() {
                        @Override
                        public void callBack(Boolean _value) {
                            if (_value){
                                WaitDialog.dismiss();
                                TipDialog.show((AppCompatActivity)context , "Takip Etmeyi Bıraktınız", TipDialog.TYPE.SUCCESS);
                                TipDialog.dismiss(1000);
                                list.get(position).getFollowers().remove(currentUser.getUid());
                                viewholder.setFollowerCount(list.get(position).getFollowers());
                                notifyDataSetChanged();
                            }else{
                                WaitDialog.dismiss();
                                TipDialog.show((AppCompatActivity)context , "Hata Oluştu", TipDialog.TYPE.ERROR);
                                TipDialog.dismiss(1000);
                            }
                        }
                    });

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
    class NotificationSetting extends RecyclerView.ViewHolder{

        public NotificationSetting(@NonNull View itemView) {
            super(itemView);
        }

       public TextView clupName = (TextView)itemView.findViewById(R.id.clupName);
       public TextView followers = (TextView)itemView.findViewById(R.id.followerCount);
      public   Switch switchCompat = (Switch)itemView.findViewById(R.id.switch1);

        public void setClupName(String name){
            clupName.setText(name);
        }
        public void setSwitchCompat(ArrayList<String> list , CurrentUser currentUser){
            if (list.contains(currentUser.getUid())){
                switchCompat.setChecked(true);
            }else{
                switchCompat.setChecked(false);
            }
        }
        public void setFollowerCount(ArrayList<String> list){
            if (list.size()>0){
                followers.setText(list.size()+" Takipçi");
            }else{
                followers.setText("Hiç Kimse Takip Etmiyor");
            }
        }
    }
}
