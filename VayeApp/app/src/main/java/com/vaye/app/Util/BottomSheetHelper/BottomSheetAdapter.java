package com.vaye.app.Util.BottomSheetHelper;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.vaye.app.Controller.HomeController.LessonPostAdapter.MajorPostViewHolder;
import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.LessonModel;
import com.vaye.app.Model.LessonPostModel;
import com.vaye.app.R;
import com.vaye.app.Services.LessonSettingService;
import com.vaye.app.Util.Helper;

public class BottomSheetAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{

    static final int view_lesson = 0;
    static final int view_currentUser = 1;


    String target;
    CurrentUser currentUser;
    Context context;
    BottomSheetModel model;
    BottomSheetDialog dialog;
    LessonModel lessonModel;

    LessonPostModel post;

    public BottomSheetAdapter(String target, CurrentUser currentUser, Context context, BottomSheetModel model,BottomSheetDialog dialog, LessonPostModel post) {
        this.target = target;
        this.currentUser = currentUser;
        this.context = context;
        this.dialog = dialog;
        this.post = post;
        this.model = model;
    }



    public BottomSheetAdapter(String target, CurrentUser currentUser, Context context, BottomSheetModel model, BottomSheetDialog dialog, LessonModel lessonModel) {
        this.target = target;
        this.currentUser = currentUser;
        this.context = context;
        this.model = model;
        this.dialog = dialog;
        this.lessonModel = lessonModel;
    }

    @Override
    public int getItemViewType(int position) {
        if (model.getTarget().equals(BottomSheetTarget.lessonTarget)){
            return view_lesson;
        }else if (model.getTarget().equals(BottomSheetTarget.lesson_currentUser_target)){
            return view_currentUser;
        }
        return super.getItemViewType(position);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        switch (viewType){
            case view_lesson:
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.action_sheet_single_item, parent, false);

                return new BottomSheetLessonViewHolder(itemView);

            case view_currentUser:
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.action_sheet_single_item, parent, false);

                return new BottomSheetLessonCurrentUserViewHolder(view);

        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i)
    {
        int viewType = getItemViewType(i);
        if (viewType == view_lesson){

            BottomSheetLessonViewHolder lessonViewHolder = (BottomSheetLessonViewHolder) holder;
            Log.d("TAG", "onBindViewHolder: " + i);
             lessonViewHolder.setImageOne(model.getImagesHolder().get(i));
            lessonViewHolder.setTitle(model.getItems().get(i));
            lessonViewHolder.title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (model.getItems().get(i).equals(BottomSheetActionTarget.dersi_takip_et)){

                        LessonSettingService.shared().addLesson(lessonModel, currentUser, (Activity) context, new TrueFalse<Boolean>() {
                            @Override
                            public void callBack(Boolean _value) {
                                if (_value){
                                    dialog.dismiss();
                                }
                            }
                        });

                    }else if (model.getItems().get(i).equals(BottomSheetActionTarget.ders_hakkında)){
                        Toast.makeText(context,"About Lesson",Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                    else if (model.getItems().get(i).equals(BottomSheetActionTarget.problem_bildir)){
                        Toast.makeText(context,"Report Lesson",Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }
            });


        }
        else if (viewType == view_currentUser){
            BottomSheetLessonCurrentUserViewHolder currentUser_holder = (BottomSheetLessonCurrentUserViewHolder) holder;
            currentUser_holder.setTitle(model.getItems().get(i));
            currentUser_holder.setImageOne(model.getImagesHolder().get(i));

            currentUser_holder.title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (model.getItems().get(i).equals(BottomSheetActionTarget.gonderiyi_düzenle)){
                        Toast.makeText(context , "Gönderiyi Düzenle",Toast.LENGTH_SHORT).show();
                    }
                    else if (model.getItems().get(i).equals(BottomSheetActionTarget.gonderiyi_sil))
                    {
                        Toast.makeText(context , "Gönderiyi Sil",Toast.LENGTH_SHORT).show();

                    }else if (model.getItems().get(i).equals(BottomSheetActionTarget.gonderiyi_sessize_al)){
                        Toast.makeText(context , "Gönderiyi Sessize Al",Toast.LENGTH_SHORT).show();
                    }else if (model.getItems().get(i).equals(BottomSheetActionTarget.gonderi_bildirimlerini_ac)){
                        Toast.makeText(context , "Gönderiyi Bildirimlerini  Aç",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }



    @Override
    public int getItemCount() {
        return model.getImagesHolder().size();
    }



    public class BottomSheetLessonViewHolder extends RecyclerView.ViewHolder {
        public BottomSheetLessonViewHolder(@NonNull View itemView) {
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

    class BottomSheetLessonCurrentUserViewHolder extends  RecyclerView.ViewHolder{

        public BottomSheetLessonCurrentUserViewHolder(@NonNull View itemView) {
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



}
