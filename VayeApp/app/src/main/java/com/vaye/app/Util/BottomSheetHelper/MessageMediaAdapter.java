package com.vaye.app.Util.BottomSheetHelper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.vaye.app.Controller.HomeController.SingleImageActivity;
import com.vaye.app.Interfaces.CompletionWithValue;
import com.vaye.app.Interfaces.OnOptionSelect;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.OtherUser;
import com.vaye.app.R;
import com.vaye.app.Services.UserService;

public class MessageMediaAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    CurrentUser currentUser;
    Context context;
    BottomSheetModel model;
    BottomSheetDialog dialog;
    OtherUser otherUser;
    String TAG = "MessageMediaAdapter";
    OnOptionSelect optionSelect;
    public MessageMediaAdapter(OtherUser otherUser, CurrentUser currentUser, Context context, BottomSheetModel model, BottomSheetDialog dialog , OnOptionSelect optionSelect) {
        this.currentUser = currentUser;
        this.context = context;
        this.model = model;
        this.dialog = dialog;
        this.otherUser = otherUser;
        this.optionSelect = optionSelect;
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
                if (VH_currentuser.title.getText().equals(BottomSheetActionTarget.send_image)){
                  dialog.dismiss();

                    optionSelect.onChoose(CompletionWithValue.send_image);
                }else if (VH_currentuser.title.getText().equals(BottomSheetActionTarget.send_location)){
                    dialog.dismiss();
                    optionSelect.onChoose(CompletionWithValue.send_location);

                }else if (VH_currentuser.title.getText().equals(BottomSheetActionTarget.send_document)){
                    optionSelect.onChoose(CompletionWithValue.send_document);

                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return model.getImagesHolder().size();
    }
}
