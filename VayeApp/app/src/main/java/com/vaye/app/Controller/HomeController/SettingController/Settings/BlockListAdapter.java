package com.vaye.app.Controller.HomeController.SettingController.Settings;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.kongzue.dialog.v3.TipDialog;
import com.kongzue.dialog.v3.WaitDialog;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.vaye.app.Controller.HomeController.TeacherNewPost.UserListAdapter;
import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.OtherUser;
import com.vaye.app.R;
import com.vaye.app.Services.BlockService;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class BlockListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    CurrentUser currentUser;
    ArrayList<OtherUser> list;
    Context context;

    public BlockListAdapter(CurrentUser currentUser, ArrayList<OtherUser> list, Context context) {
        this.currentUser = currentUser;
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_list_item, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder =(ViewHolder)holder;
        OtherUser user = list.get(position);
        viewHolder.setName(user.getName());
        viewHolder.setUsername(user.getUsername());
        viewHolder.setProfilImage(user.getThumb_image());
        viewHolder.setShortSchool(user.getShort_school());
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WaitDialog.show((AppCompatActivity)context,"Lütfen Bekleyin");
                BlockService.shared().removeOnBlockList(currentUser, user, new TrueFalse<Boolean>() {
                    @Override
                    public void callBack(Boolean _value) {
                        if (_value){
                            list.remove(position);
                            notifyItemRemoved(position);
                            WaitDialog.dismiss();
                            TipDialog.show((AppCompatActivity)context,"Engel Kaldırıldı", TipDialog.TYPE.SUCCESS);
                            TipDialog.dismiss(1500);
                        }
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    class ViewHolder extends RecyclerView.ViewHolder{

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
        public CircleImageView profilImage = (CircleImageView)itemView.findViewById(R.id.profileImage);
        public ProgressBar progressBar =(ProgressBar)itemView.findViewById(R.id.progress);
        public TextView name = (TextView)itemView.findViewById(R.id.name);
        public TextView username = (TextView)itemView.findViewById(R.id.username);
        public TextView number = (TextView)itemView.findViewById(R.id.number);

        public void setName(String _name){
            name.setText(_name);
        }
        public void setUsername(String _username){
            username.setText(_username);
        }
        public void setShortSchool(String _number){
            if (_number!=null && !_number.isEmpty()){
                number.setText(_number);
            }
        }
        public void setProfilImage(String url){
            if (url!=null &&!url.isEmpty()){
                Picasso.get().load(url).resize(128,128)
                        .centerCrop().placeholder(android.R.color.darker_gray)
                        .into(profilImage, new Callback() {
                            @Override
                            public void onSuccess() {
                                progressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError(Exception e) {
                                progressBar.setVisibility(View.GONE);
                            }
                        });
            }else {
                progressBar.setVisibility(View.GONE);
                profilImage.setImageResource(android.R.color.darker_gray);
            }

        }
    }

}
