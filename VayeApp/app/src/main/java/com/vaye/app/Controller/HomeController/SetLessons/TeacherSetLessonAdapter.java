package com.vaye.app.Controller.HomeController.SetLessons;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.kongzue.dialog.v3.TipDialog;
import com.kongzue.dialog.v3.WaitDialog;
import com.vaye.app.Interfaces.CallBackCount;
import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.LessonModel;
import com.vaye.app.Model.LessonPostModel;
import com.vaye.app.R;
import com.vaye.app.Services.LessonSettingService;

import java.util.ArrayList;

public class TeacherSetLessonAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<LessonModel> list;

    public void setList(ArrayList<LessonModel> list) {
        this.list = list;
    }

    CurrentUser currentUser;
    Context context;

    public TeacherSetLessonAdapter(ArrayList<LessonModel> list, CurrentUser currentUser, Context context) {
        this.list = list;
        this.currentUser = currentUser;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.lesson_search_item, parent, false);

        return new TeacherSetLessonViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        LessonModel model = list.get(position);
        TeacherSetLessonViewHolder teacherSetLessonViewHolder =(TeacherSetLessonViewHolder)holder;
        teacherSetLessonViewHolder.setLessonName(list.get(position).getLessonName());
        teacherSetLessonViewHolder.setTeacherName(list.get(position).getTeacherName());
        teacherSetLessonViewHolder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WaitDialog.show((AppCompatActivity)context,"Ders Ekleniyor");
                LessonSettingService.shared().addTeacherOnLesson(currentUser, model, new TrueFalse<Boolean>() {
                    @Override
                    public void callBack(Boolean _value) {
                        if (_value){
                            model.setTeacherEmail(currentUser.getEmail());
                            model.setTeacherId(currentUser.getUid());
                            model.setTeacherName(currentUser.getName());
                            teacherSetLessonViewHolder.setLessonName(list.get(position).getLessonName());
                            teacherSetLessonViewHolder.setTeacherName(list.get(position).getTeacherName());
                            notifyDataSetChanged();
                            WaitDialog.dismiss();
                        }else{
                            WaitDialog.dismiss();
                            TipDialog.show((AppCompatActivity)context,"Ders Eklenemedi , Tekrar Deneyin", TipDialog.TYPE.ERROR);
                            TipDialog.dismiss(1000);
                        }
                    }
                });


            }
        });

        teacherSetLessonViewHolder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WaitDialog.show((AppCompatActivity)context,"Ders Siliniyor");
                LessonSettingService.shared().removeTeacheronLesson(context,currentUser, model, new TrueFalse<Boolean>() {
                    @Override
                    public void callBack(Boolean _value) {
                        if (_value){
                            model.setTeacherEmail("empty");
                            model.setTeacherId("empty");
                            model.setTeacherName("empty");
                            teacherSetLessonViewHolder.setLessonName(list.get(position).getLessonName());
                            teacherSetLessonViewHolder.setTeacherName(list.get(position).getTeacherName());
                            notifyDataSetChanged();

                        }
                    }
                });

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class TeacherSetLessonViewHolder extends RecyclerView.ViewHolder{
        public TeacherSetLessonViewHolder(@NonNull View itemView) {
            super(itemView);
        }


       public TextView lessonName = (TextView)itemView.findViewById(R.id.lessonName);
       public ImageButton add = (ImageButton)itemView.findViewById(R.id.add);
       public  ImageButton remove = (ImageButton)itemView.findViewById(R.id.delete);
       public TextView teacherName = (TextView)itemView.findViewById(R.id.followersCount);


        public void setLessonName(String _lessonName){
            lessonName.setText(_lessonName);

        }
        public void setTeacherName(String _teacherName){

            if (_teacherName.equals("empty")){
                teacherName.setText("Ders Seçilebilir");
                add.setVisibility(View.VISIBLE);
                remove.setVisibility(View.GONE);
            }else{
                teacherName.setText(_teacherName);
                if (_teacherName.equals(currentUser.getName()) ){
                    remove.setVisibility(View.VISIBLE);
                    add.setVisibility(View.GONE);
                }else{
                    remove.setVisibility(View.GONE);
                    add.setVisibility(View.GONE);
                }
            }


        }

    }
}
