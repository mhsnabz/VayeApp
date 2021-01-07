package com.vaye.app.Controller.HomeController.LessonPostAdapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.LessonPostModel;

import java.util.ArrayList;

public class LessonPostAdapter extends RecyclerView.Adapter<LessonPostVieweHolder> {
    ArrayList<LessonPostModel> post;
    CurrentUser currentUser;
    Context context;

    public LessonPostAdapter(ArrayList<LessonPostModel> post, CurrentUser currentUser, Context context) {
        this.post = post;
        this.currentUser = currentUser;
        this.context = context;
    }

    @NonNull
    @Override
    public LessonPostVieweHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull LessonPostVieweHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
class LessonPostVieweHolder extends RecyclerView.ViewHolder{

    public LessonPostVieweHolder(@NonNull View itemView) {
        super(itemView);
    }
}
