package com.vaye.app.Util.BottomSheetHelper;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vaye.app.R;

public class VayeAppBottomSheetLauncherViewHolder extends RecyclerView.ViewHolder {
    public VayeAppBottomSheetLauncherViewHolder(@NonNull View itemView) {
        super(itemView);

    }
    ImageButton imageView = (ImageButton)itemView.findViewById(R.id.image);
    TextView title = (TextView) itemView.findViewById(R.id.title);
    void setImageOne(int _res){
        imageView.setImageResource(_res);
    }
    void setTitle(String text){
        title.setText(text);
    }
}
