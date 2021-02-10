package com.vaye.app.Util.BottomSheetHelper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.MainPostModel;
import com.vaye.app.Model.OtherUser;
import com.vaye.app.R;

public class VayeAppBottomSheet extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    static final int current_user_options= 0;
    static final int other_user_options= 1;
    MainPostModel post;
    CurrentUser currentUser;
    BottomSheetModel model;
    BottomSheetDialog dialog;
    OtherUser otherUser;
    Context context;
    /***
    * other user consctrtuctor
    * */
    public VayeAppBottomSheet(MainPostModel post, CurrentUser currentUser, BottomSheetModel model, BottomSheetDialog dialog, OtherUser otherUser, Context context) {
        this.post = post;
        this.currentUser = currentUser;
        this.model = model;
        this.dialog = dialog;
        this.otherUser = otherUser;
        this.context = context;
    }

    /**
     * current user consctructur
     * */
    public VayeAppBottomSheet(MainPostModel post, CurrentUser currentUser, BottomSheetModel model, BottomSheetDialog dialog, Context context) {
        this.post = post;
        this.currentUser = currentUser;
        this.model = model;
        this.dialog = dialog;
        this.context = context;
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
                break;
            case other_user_options:
                VayeAppBottomSheetLauncherViewHolder VH_otherUser = (VayeAppBottomSheetLauncherViewHolder) holder;
                VH_otherUser.setImageOne(model.getImagesHolder().get(i));
                VH_otherUser.setTitle(model.getItems().get(i));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return model.getImagesHolder().size();
    }
}
