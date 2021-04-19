package com.vaye.app.Util.BottomSheetHelper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.vaye.app.Controller.HomeController.SingleImageActivity;
import com.vaye.app.Interfaces.CompletionWithValue;
import com.vaye.app.Interfaces.OnOptionSelect;
import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.MessagesModel;
import com.vaye.app.R;
import com.vaye.app.Services.UserService;

public class ProfileImageSettingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    CurrentUser currentUser;
    Context context;
    BottomSheetModel model;
    BottomSheetDialog dialog;

    OnOptionSelect optionSelect;

    public ProfileImageSettingAdapter(CurrentUser currentUser, Context context, BottomSheetModel model, BottomSheetDialog dialog , OnOptionSelect optionSelect  ) {
        this.currentUser = currentUser;
        this.context = context;
        this.model = model;
        this.dialog = dialog;
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
                if (VH_currentuser.title.getText().equals(BottomSheetActionTarget.delete_profile_image)){
                    UserService.shared().deleteProfileImage((Activity) context, currentUser, new CompletionWithValue() {
                        @Override
                        public void completion(Boolean bool, String val) {
                            dialog.dismiss();

                        }
                    });

                }else if (VH_currentuser.title.getText().equals(BottomSheetActionTarget.show_profile_image)){
                    Intent i = new Intent(context , SingleImageActivity.class);
                    i.putExtra("profileImage",currentUser.getProfileImage());
                    context.startActivity(i);
                    dialog.dismiss();

                    optionSelect.onChoose(CompletionWithValue.showImage);

                }else if (VH_currentuser.title.getText().equals(BottomSheetActionTarget.take_new_image)){
                    dialog.dismiss();

                    optionSelect.onChoose(CompletionWithValue.takePicture);

                }else if (VH_currentuser.title.getText().equals(BottomSheetActionTarget.choose_image_from_gallery)){
                    optionSelect.onChoose(CompletionWithValue.chooseImage);
                }
            }
        });
    }
    

    @Override
    public int getItemCount() {
        return model.getImagesHolder().size();
    }
}
