package com.vaye.app.Controller.HomeController.SchoolPostAdapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vaye.app.Controller.HomeController.School.NewSchoolPostActivity;
import com.vaye.app.Controller.HomeController.StudentSetNewPost.ChooseLessonAdapter;
import com.vaye.app.Controller.HomeController.StudentSetNewPost.StudentNewPostActivity;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.LessonModel;
import com.vaye.app.R;
import com.vaye.app.Util.Helper;

import java.util.ArrayList;

public class ChooseClupAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<String> clupName;
    Context context;
    CurrentUser currentUser;

    public void setList(ArrayList<String> clupName) {
        this.clupName = clupName;
    }
    public ChooseClupAdapter(ArrayList<String> clupName, Context context, CurrentUser currentUser) {
        this.clupName = clupName;
        this.context = context;
        this.currentUser = currentUser;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_lesson_item, parent, false);

        return new ChooseClupAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.setLessonName(clupName.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context , NewSchoolPostActivity.class);
                i.putExtra("clupName", clupName.get(position));
                i.putExtra("currentUser",currentUser);

                context.startActivity(i);
                Helper.shared().go((Activity) context);


            }
        });
    }

    @Override
    public int getItemCount() {
        return clupName.size();
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
