package com.vaye.app.LoginRegister.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vaye.app.LoginRegister.SetBolumActivity;
import com.vaye.app.Model.SchoolModel;
import com.vaye.app.Model.TaskUser;
import com.vaye.app.R;
import com.vaye.app.Util.Helper;

import java.util.ArrayList;

public class FakulteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<String> list;
    Context context;
    TaskUser taskUser;
    public void setList(ArrayList<String> list) {
        this.list = list;
    }
    public FakulteAdapter(ArrayList<String> list, Context context, TaskUser taskUser) {
        this.list = list;
        this.context = context;
        this.taskUser = taskUser;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fakulte_bolum_item, parent, false);
        return new FakulteAdapterVievHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        FakulteAdapterVievHolder fakulteAdapterVievHolder = (FakulteAdapterVievHolder)holder;
        String item = list.get(position);
        fakulteAdapterVievHolder.setName(item);
        fakulteAdapterVievHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, SetBolumActivity.class);
                i.putExtra("fakulteName",item);
                i.putExtra("taskUser",taskUser);
                context.startActivity(i);
                Helper.shared().go((Activity)context);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


}
