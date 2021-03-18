package com.vaye.app.LoginRegister.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vaye.app.Controller.HomeController.SetLessons.StudentLessonAdapter;
import com.vaye.app.LoginRegister.RegisterActivity;
import com.vaye.app.Model.LessonModel;
import com.vaye.app.Model.SchoolModel;
import com.vaye.app.R;
import com.vaye.app.Util.Helper;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChooseSchoolAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<SchoolModel> list;
    Context context;
    public void setList(ArrayList<SchoolModel> list) {
        this.list = list;
    }
    public ChooseSchoolAdapter(ArrayList<SchoolModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.school_item, parent, false);
        return new SchoolViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        SchoolModel model = list.get(position);
            SchoolViewHolder schoolViewHolder = (SchoolViewHolder)holder;
            schoolViewHolder.configure(model.getLogo(),model.getName());
            schoolViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(context, RegisterActivity.class);
                    i.putExtra("schoolModel",model);
                    context.startActivity(i);
                    Helper.shared().go((Activity)context);
                }
            });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class SchoolViewHolder extends RecyclerView.ViewHolder{

        public SchoolViewHolder(@NonNull View itemView) {
            super(itemView);
        }
        public CircleImageView logo = (CircleImageView)itemView.findViewById(R.id.logo);
        public TextView schoolName = (TextView) itemView.findViewById(R.id.schoolName);

        public void configure(Drawable drawable_logo , String school_name){
            logo.setImageDrawable(drawable_logo);
            schoolName.setText(school_name);
        }
    }
}
