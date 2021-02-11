package com.vaye.app.Util.BottomSheetHelper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.kongzue.dialog.v3.WaitDialog;
import com.vaye.app.Controller.ReportController.ReportActivity;
import com.vaye.app.Interfaces.Report;
import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.MainPostModel;
import com.vaye.app.Model.OtherUser;
import com.vaye.app.R;
import com.vaye.app.Services.MainPostService;
import com.vaye.app.Services.VayeAppPostService;
import com.vaye.app.Util.Helper;

import java.util.ArrayList;

public class VayeAppBottomSheet extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    static final int current_user_options= 0;
    static final int other_user_options= 1;
    MainPostModel post;
    CurrentUser currentUser;
    BottomSheetModel model;
    BottomSheetDialog dialog;
    OtherUser otherUser;
    Context context;
    ArrayList<MainPostModel> allPost;
    /***
    * other user consctrtuctor
    * */
    public VayeAppBottomSheet(ArrayList<MainPostModel> allPost,MainPostModel post, CurrentUser currentUser, BottomSheetModel model, BottomSheetDialog dialog, OtherUser otherUser, Context context) {
        this.post = post;
        this.currentUser = currentUser;
        this.model = model;
        this.dialog = dialog;
        this.otherUser = otherUser;
        this.context = context;
        this.allPost = allPost;
    }

    /**
     * current user consctructur
     * */
    public VayeAppBottomSheet(ArrayList<MainPostModel> allPost,MainPostModel post, CurrentUser currentUser, BottomSheetModel model, BottomSheetDialog dialog, Context context) {
        this.post = post;
        this.currentUser = currentUser;
        this.model = model;
        this.dialog = dialog;
        this.context = context;
        this.allPost = allPost;
    }
    @Override
    public int getItemViewType(int position) {
        if (model.getTarget().equals(BottomSheetTarget.vaye_app_current_user_launcher)){
            return current_user_options;
        }else if (model.getTarget().equals(BottomSheetTarget.vaye_app_other_user_launcher)){
            return other_user_options;
        }
        return super.getItemViewType(position);
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType){
            case current_user_options :
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.action_sheet_single_item, parent, false);

                return new VayeAppBottomSheetLauncherViewHolder(itemView);

            case  other_user_options :
                View itemView1 = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.action_sheet_single_item, parent, false);

                return new VayeAppBottomSheetLauncherViewHolder(itemView1);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i) {
        int viewType = getItemViewType(i);
        switch (viewType){
            case current_user_options:

                VayeAppBottomSheetLauncherViewHolder VH_currentuser = (VayeAppBottomSheetLauncherViewHolder) holder;
                VH_currentuser.setImageOne(model.getImagesHolder().get(i));
                VH_currentuser.setTitle(model.getItems().get(i));
                VH_currentuser.title.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (VH_currentuser.title.getText().equals(BottomSheetActionTarget.gonderiyi_sil)){
                            WaitDialog.show((AppCompatActivity) context,"Gönderi Siliniyor");
                            VayeAppPostService.shared().deletePost(post, new TrueFalse<Boolean>() {
                                @Override
                                public void callBack(Boolean _value) {
                                    dialog.dismiss();
                                    allPost.remove(post);
                                    WaitDialog.dismiss();
                                }
                            });
                        }else if (VH_currentuser.title.getText().equals(BottomSheetActionTarget.gonderiyi_düzenle)){

                            Toast.makeText(context,BottomSheetActionTarget.gonderiyi_düzenle,Toast.LENGTH_SHORT).show();

                        }else if (VH_currentuser.title.getText().equals(BottomSheetActionTarget.gonderiyi_sessize_al)){
                            MainPostService.shared().SetPostSlient((Activity) context, post, currentUser, new TrueFalse<Boolean>() {
                                @Override
                                public void callBack(Boolean _value) {
                                    dialog.dismiss();

                                }
                            });

                        }else if (VH_currentuser.title.getText().equals(BottomSheetActionTarget.gonderi_bildirimlerini_ac)){
                            MainPostService.shared().RemoveSlientFromPost((Activity) context, post, currentUser, new TrueFalse<Boolean>() {
                                @Override
                                public void callBack(Boolean _value) {
                                    dialog.dismiss();

                                }
                            });
                        }
                    }
                });


                break;
            case other_user_options:
                VayeAppBottomSheetLauncherViewHolder VH_otherUser = (VayeAppBottomSheetLauncherViewHolder) holder;
                VH_otherUser.setImageOne(model.getImagesHolder().get(i));
                VH_otherUser.setTitle(model.getItems().get(i));
                VH_otherUser.title.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (VH_otherUser.title.getText().equals(BottomSheetActionTarget.bu_gonderiyi_sikayet_et)){
                            Intent i = new Intent(context , ReportActivity.class);
                            i.putExtra("otherUser",post.getSenderUid());
                            i.putExtra("postId",post.getPostId());
                            i.putExtra("target", Report.ReportTarget.foodMePost);
                            i.putExtra("reportType", Report.ReportType.reportPost);
                            i.putExtra("currentUser",currentUser);
                            context.startActivity(i);
                            Helper.shared().go((Activity) context);
                            dialog.dismiss();
                        }else if (VH_otherUser.title.getText().equals(BottomSheetActionTarget.bu_kullaniciyi_sessize_al)){
                        MainPostService.shared().setUserSlient(currentUser, otherUser, new TrueFalse<Boolean>() {
                            @Override
                            public void callBack(Boolean _value) {

                                dialog.dismiss();
                            }
                        });
                        }else if (VH_otherUser.title.getText().equals(BottomSheetActionTarget.bu_kullaniciyi_sessiden_al)){
                            MainPostService.shared().setUserNotSlient(currentUser, otherUser, new TrueFalse<Boolean>() {
                                @Override
                                public void callBack(Boolean _value) {
                                    dialog.dismiss();
                                }
                            });

                        }else if (VH_otherUser.title.getText().equals(BottomSheetActionTarget.bu_kullaniciyi_sikayet_et)){
                            Intent i = new Intent(context , ReportActivity.class);
                            i.putExtra("otherUser",post.getSenderUid());
                            i.putExtra("postId",post.getPostId());
                            i.putExtra("target", Report.ReportTarget.foodMePost);
                            i.putExtra("reportType", Report.ReportType.reportUser);
                            i.putExtra("currentUser",currentUser);
                            context.startActivity(i);
                            Helper.shared().go((Activity) context);
                            dialog.dismiss();
                        }
                    }
                });
                break;
        }
    }

    @Override
    public int getItemCount() {
        return model.getImagesHolder().size();
    }
}
