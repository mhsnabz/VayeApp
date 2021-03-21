package com.vaye.app.Controller.HomeController.TeacherNewPost;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.kongzue.dialog.v3.WaitDialog;
import com.vaye.app.Interfaces.StringArrayListInterface;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.LessonFallowerUser;
import com.vaye.app.Model.LessonModel;
import com.vaye.app.Model.LessonUserList;
import com.vaye.app.R;

import java.util.ArrayList;

public class TeacherChooseLessonAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<LessonModel> list;
    CurrentUser currentUser;
    Context context;
        String TAG = "TeacherChooseLessonAdapter";
    ArrayList<LessonFallowerUser> users = new ArrayList<>();
    int times = 0;
    ArrayList<String> studentLis = new ArrayList<>();
    StringArrayListInterface callback;
    String lesson_name;
    String lesson_key;
    public TeacherChooseLessonAdapter(ArrayList<LessonModel> list, CurrentUser currentUser, Context context ,StringArrayListInterface callback) {
        this.list = list;
        this.currentUser = currentUser;
        this.context = context;
        this.callback = callback;
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
        LessonModel model = list.get(position);
            ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.setLessonName(model.getLessonName());
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewHolder.getFollowers(model);
                list.remove(model);
                notifyItemRemoved(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    class ViewHolder extends RecyclerView.ViewHolder{

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        TextView lessonName = (TextView)itemView.findViewById(R.id.lessonName);

        public void setLessonName(String text){
            lessonName.setText(text);
        }
        public void getFollowers(LessonModel model){
            WaitDialog.show((AppCompatActivity)context,null);

            CollectionReference reference = FirebaseFirestore.getInstance().collection(currentUser.getShort_school()).document("lesson")
                    .collection(currentUser.getBolum()).document(model.getLessonName()).collection("fallowers");
            reference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()){
                        for (DocumentSnapshot item : task.getResult().getDocuments()){
                            studentLis.add(item.getString("uid"));

                        }
                        callback.getArrayList(studentLis);
                        lesson_name  = model.getLessonName();

                        times = times + 1;

                        Intent intent = new Intent("users");
                        if (times == 1){
                            intent.putExtra("lessonname",lesson_name);
                            intent.putExtra("list", studentLis);
                            intent.putExtra("times",times);
                            intent.putExtra("lesson_key",model.getLesson_key());
                            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                        }else if (times > 1){
                            intent.putExtra("lessonname",lesson_name);
                            intent.putExtra("list", users);
                            intent.putExtra("times",times);
                            intent.putExtra("lesson_key","genel_duyuru");
                            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                        }

                    }
                }
            });


        }
    }
}
