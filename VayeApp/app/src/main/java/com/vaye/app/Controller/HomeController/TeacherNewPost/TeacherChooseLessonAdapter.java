package com.vaye.app.Controller.HomeController.TeacherNewPost;

import android.content.Context;
import android.content.Intent;
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
import com.kongzue.dialog.v3.TipDialog;
import com.kongzue.dialog.v3.WaitDialog;
import com.vaye.app.Controller.HomeController.StudentSetNewPost.ChooseLessonAdapter;
import com.vaye.app.Interfaces.CompletionWithValue;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.LessonModel;
import com.vaye.app.Model.LessonUserList;
import com.vaye.app.R;

import java.util.ArrayList;
import java.util.List;

public class TeacherChooseLessonAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<LessonModel> list;
    CurrentUser currentUser;
    Context context;

    ArrayList<LessonUserList> users = new ArrayList<>();

    public TeacherChooseLessonAdapter(ArrayList<LessonModel> list, CurrentUser currentUser, Context context) {
        this.list = list;
        this.currentUser = currentUser;
        this.context = context;
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
                viewHolder.getFollowers(model.getLessonName());
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
        public void getFollowers(String lessonName){
            WaitDialog.show((AppCompatActivity)context,null);

            CollectionReference reference = FirebaseFirestore.getInstance().collection(currentUser.getShort_school()).document("lesson")
                    .collection(currentUser.getBolum()).document(lessonName).collection("fallowers");
            reference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()){
                        for (DocumentSnapshot item : task.getResult().getDocuments()){
                            for (LessonUserList list : users){
                                if (!list.getUid().equals(item.getString("uid"))){
                                    users.add(item.toObject(LessonUserList.class));
                                }
                            }
                        }

                        Intent intent = new Intent("users");

                        intent.putExtra("list", users);

                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                    }
                }
            });


        }
    }
}
