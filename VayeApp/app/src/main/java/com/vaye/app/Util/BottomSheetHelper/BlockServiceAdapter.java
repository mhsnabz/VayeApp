package com.vaye.app.Util.BottomSheetHelper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.vaye.app.Interfaces.BlockOptionSelect;
import com.vaye.app.Interfaces.CompletionWithValue;
import com.vaye.app.Interfaces.OnOptionSelect;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.OtherUser;
import com.vaye.app.R;
import com.vaye.app.Services.BlockService;

import java.util.ArrayList;

public class BlockServiceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<String> reason;
    CurrentUser currentUser ;
    OtherUser otherUser;
    BottomSheetDialog dialog;
    BlockOptionSelect optionSelect;

    public BlockServiceAdapter(ArrayList<String> reason, CurrentUser currentUser, OtherUser otherUser, BottomSheetDialog dialog, BlockOptionSelect optionSelect) {
        this.reason = reason;
        this.currentUser = currentUser;
        this.otherUser = otherUser;
        this.dialog = dialog;
        this.optionSelect = optionSelect;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.block_reason_item, parent, false);

        return new BlockServiceViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        BlockServiceViewHolder viewHolder = (BlockServiceViewHolder) holder;
        viewHolder.title.setText(reason.get(position));

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewHolder.title.getText().equals(BlockService.BlockReason.fake_account)) {
                    optionSelect.onSelectOption(CompletionWithValue.fake_account,otherUser);
                    dialog.dismiss();

                } else if (viewHolder.title.getText().equals(BlockService.BlockReason.nudity)) {
                    optionSelect.onSelectOption(CompletionWithValue.nudity,otherUser);
                    dialog.dismiss();
                }
                else if (viewHolder.title.getText().equals(BlockService.BlockReason.swear)) {
                    optionSelect.onSelectOption(CompletionWithValue.swear,otherUser);
                    dialog.dismiss();
                }
                else if (viewHolder.title.getText().equals(BlockService.BlockReason.other)) {
                    optionSelect.onSelectOption(CompletionWithValue.other,otherUser);
                    dialog.dismiss();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return reason.size();
    }

    public class BlockServiceViewHolder extends RecyclerView.ViewHolder {
        public BlockServiceViewHolder(@NonNull View itemView) {
            super(itemView);

        }

        TextView title = (TextView) itemView.findViewById(R.id.item);
    }
}
