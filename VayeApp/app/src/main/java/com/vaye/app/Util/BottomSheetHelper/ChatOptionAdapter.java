package com.vaye.app.Util.BottomSheetHelper;

import android.app.Dialog;
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
import com.vaye.app.Interfaces.CompletionWithValue;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.OtherUser;
import com.vaye.app.R;

public class ChatOptionAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    CurrentUser currentUser;
    BottomSheetModel model;
    BottomSheetDialog dialog;
    OtherUser otherUser;
    Context context;
    String TAG = "ChatOptionAdapter";
    public ChatOptionAdapter(CurrentUser currentUser, BottomSheetModel model, BottomSheetDialog dialog, OtherUser otherUser, Context context) {
        this.currentUser = currentUser;
        this.model = model;
        this.dialog = dialog;
        this.otherUser = otherUser;
        this.context = context;
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
                if (VH_currentuser.title.getText().equals(BottomSheetActionTarget.remove_from_friendList)){
                    dialog.dismiss();
                    Intent intent = new Intent("media_item_target");

                    intent.putExtra("target", CompletionWithValue.remove_from_friend_list);

                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }else if (VH_currentuser.title.getText().equals(BottomSheetActionTarget.report_chat_friend)){
                    dialog.dismiss();
                    Intent intent = new Intent("media_item_target");

                    intent.putExtra("target", CompletionWithValue.report_chat_user);

                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }

                else if (VH_currentuser.title.getText().equals(BottomSheetActionTarget.delete_conservation)){
                    dialog.dismiss();
                    Intent intent = new Intent("media_item_target");

                    intent.putExtra("target", CompletionWithValue.remove_chat);

                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return model.getImagesHolder().size();
    }
}
