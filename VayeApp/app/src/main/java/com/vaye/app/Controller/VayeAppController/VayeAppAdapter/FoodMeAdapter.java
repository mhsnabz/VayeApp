package com.vaye.app.Controller.VayeAppController.VayeAppAdapter;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.LessonPostModel;
import com.vaye.app.Model.MainPostModel;

import java.util.ArrayList;

public class FoodMeAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<MainPostModel> post;
    Context context;
    CurrentUser currentUser;
    private static final int VIEW_TYPE_LESSON_POST = 1;
    private static final int VIEW_TYPE_LESSON_POST_DATA  = 2;
    private static final int VIEW_TYPE_ADS  = 3;
    private static final int VIEW_TYPE_EMPTY  = 4;

    public FoodMeAdapter(ArrayList<MainPostModel> post, Context context, CurrentUser currentUser) {
        this.post = post;
        this.context = context;
        this.currentUser = currentUser;
    }

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
        return post.size();
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public int getItemViewType(int position) {
        MainPostModel model = (MainPostModel)post.get(position);
        if (model.getType()!=null&& model.getType().equals("ads")) {
            return  VIEW_TYPE_ADS;
        }else if ( model.getType().equals("data")){
            return  VIEW_TYPE_LESSON_POST_DATA;
        }else if  ( model.getType().equals("post")){
            return   VIEW_TYPE_LESSON_POST;
        }else if (model.getEmpty().equals("empty")){
            return  VIEW_TYPE_EMPTY;
        }
        return super.getItemViewType(position);
    }
}
