package com.vaye.app.LoginRegister.Adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vaye.app.R;

public class FakulteAdapterVievHolder  extends RecyclerView.ViewHolder {
    public FakulteAdapterVievHolder(@NonNull View itemView) {
        super(itemView);
    }
    public TextView name = (TextView)itemView.findViewById(R.id.name);

    public void setName(String _name) {
        name.setText(_name);
    }
}
