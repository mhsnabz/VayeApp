package com.vaye.app.Controller.HomeController.StudentSetNewPost;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vaye.app.Controller.HomeController.LessonPostAdapter.MajorPostViewHolder;
import com.vaye.app.Interfaces.MajorPostFallower;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.LessonFallowerUser;
import com.vaye.app.Model.LessonModel;
import com.vaye.app.R;
import com.vaye.app.Services.MajorPostService;
import com.vaye.app.Util.Helper;

import java.util.ArrayList;

public class ChooseLessonAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<LessonModel> lessonList;
    Context context ;
    CurrentUser currentUser;

    public ChooseLessonAdapter(ArrayList<LessonModel> lessonList, Context context, CurrentUser currentUser) {
        this.lessonList = lessonList;
        this.context = context;
        this.currentUser = currentUser;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_lesson_item, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.setLessonName(lessonList.get(position).getLessonName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MajorPostService.shared().getLessonFallower(currentUser, lessonList.get(position).getLessonName(), new MajorPostFallower() {
                    @Override
                    public void onCallback(ArrayList<LessonFallowerUser> lessonFallowerUser) {
                        Intent i = new Intent(context , StudentNewPostActivity.class);
                        i.putExtra("lessonModel", lessonList.get(position));
                        i.putExtra("currentUser",currentUser);
                        i.putExtra("lessonFallower", lessonFallowerUser);
                        context.startActivity(i);
                        Helper.shared().go((Activity) context);
                    }
                });

            }
        });
    }

    @Override
    public int getItemCount() {
        return lessonList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        TextView lessonName = (TextView)itemView.findViewById(R.id.lessonName);

        public void setLessonName(String text){
            lessonName.setText(text);
        }
    }
}
