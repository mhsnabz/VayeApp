package com.vaye.app.Util.BottomSheetHelper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.kongzue.dialog.v3.WaitDialog;
import com.vaye.app.Controller.VayeAppController.VayeAppNewPostActivity;
import com.vaye.app.Interfaces.MainPostFollowers;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.MainPostTopicFollower;
import com.vaye.app.R;
import com.vaye.app.Services.MainPostService;
import com.vaye.app.Util.Helper;

import java.util.ArrayList;

public class VayeAppChooseTargetBottomSheetAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    CurrentUser currentUser;

    Context context;
    BottomSheetModel model;
    BottomSheetDialog dialog;

    public VayeAppChooseTargetBottomSheetAdapter(CurrentUser currentUser, Context context, BottomSheetModel model, BottomSheetDialog dialog) {
        this.currentUser = currentUser;
        this.context = context;
        this.model = model;
        this.dialog = dialog;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.action_sheet_single_item, parent, false);

        return new VayeAppBottomSheetLauncherViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i) {
        VayeAppBottomSheetLauncherViewHolder VH_currentuser = (VayeAppBottomSheetLauncherViewHolder) holder;
        VH_currentuser.setImageOne(model.getImagesHolder().get(i));
        VH_currentuser.setTitle(model.getItems().get(i));
        VH_currentuser.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (VH_currentuser.title.getText().equals(BottomSheetActionTarget.al_sat)){

                    WaitDialog.show((AppCompatActivity)context , null);
                    MainPostService.shared().getTopicFollowers(BottomSheetActionTarget.sell_buy, new MainPostFollowers() {
                        @Override
                        public void getTopicFollower(ArrayList<MainPostTopicFollower> list) {

                            Intent i = new Intent(context , VayeAppNewPostActivity.class);
                            i.putExtra("followers",list);
                            i.putExtra("currentUser",currentUser);
                            i.putExtra("postType",BottomSheetActionTarget.al_sat);
                            context.startActivity(i);
                            dialog.dismiss();
                            Helper.shared().go((Activity) context);
                            WaitDialog.dismiss();
                        }
                    });

                }else if (VH_currentuser.title.getText().equals(BottomSheetActionTarget.yemek)){
                    WaitDialog.show((AppCompatActivity)context , null);
                    MainPostService.shared().getTopicFollowers(BottomSheetActionTarget.food_me, new MainPostFollowers() {
                        @Override
                        public void getTopicFollower(ArrayList<MainPostTopicFollower> list) {

                            Intent i = new Intent(context , VayeAppNewPostActivity.class);
                            i.putExtra("followers",list);
                            i.putExtra("currentUser",currentUser);
                            i.putExtra("postType",BottomSheetActionTarget.yemek);
                            context.startActivity(i);
                            dialog.dismiss();
                            Helper.shared().go((Activity) context);
                            WaitDialog.dismiss();
                        }
                    });

                }else if (VH_currentuser.title.getText().equals(BottomSheetActionTarget.kamp)){
                    WaitDialog.show((AppCompatActivity)context , null);
                    MainPostService.shared().getTopicFollowers(BottomSheetActionTarget.camping, new MainPostFollowers() {
                        @Override
                        public void getTopicFollower(ArrayList<MainPostTopicFollower> list) {

                            Intent i = new Intent(context , VayeAppNewPostActivity.class);
                            i.putExtra("followers",list);
                            i.putExtra("currentUser",currentUser);
                            i.putExtra("postType",BottomSheetActionTarget.kamp);
                            context.startActivity(i);
                            dialog.dismiss();
                            Helper.shared().go((Activity) context);
                            WaitDialog.dismiss();
                        }
                    });
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return model.getImagesHolder().size();
    }
}
