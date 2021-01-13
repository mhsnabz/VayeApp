package com.vaye.app.Controller.HomeController.LessonPostEdit;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PostDataAdaptar extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<String > url;
    
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class ImagesViewHolder extends RecyclerView.ViewHolder{

        public ImagesViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
