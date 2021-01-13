package com.vaye.app.Controller.HomeController.LessonPostEdit;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Data;

import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.vaye.app.Controller.HomeController.LessonPostAdapter.MajorPostViewHolder;
import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.LessonPostModel;
import com.vaye.app.R;
import com.vaye.app.Services.MajorPostService;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

public class PostDataAdaptar extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    ArrayList<String > url;
    ArrayList<String> thumb_url;
    Context context;
    CurrentUser currentUser ;
    LessonPostModel model;

    public PostDataAdaptar(ArrayList<String> url, ArrayList<String> thumb_url, Context context, CurrentUser currentUser, LessonPostModel model) {
        this.url = url;
        this.thumb_url = thumb_url;
        this.context = context;
        this.currentUser = currentUser;
        this.model = model;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_post_item, parent, false);

        return new ImagesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ImagesViewHolder viewHolder = (ImagesViewHolder) holder;
            viewHolder.setImageView(url.get(position));
            viewHolder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   MajorPostService.shared().deleteImage((Activity) context, url.get(position), thumb_url.get(position), currentUser, model, new TrueFalse<Boolean>() {
                       @Override
                       public void callBack(Boolean _value) {
                           if (_value){
                               model.getData().remove(url.get(position));
                               model.getThumbData().remove(thumb_url.get(position));
                               notifyDataSetChanged();
                           }
                       }
                   });
                }
            });
    }

    @Override
    public int getItemCount() {
        return url.size();
    }

    class ImagesViewHolder extends RecyclerView.ViewHolder{

        public ImagesViewHolder(@NonNull View itemView) {
            super(itemView);
        }
        RoundedImageView imageView = (RoundedImageView)itemView.findViewById(R.id.imageView);
        ProgressBar progressBar = (ProgressBar)itemView.findViewById(R.id.progress);
        ImageButton delete = (ImageButton)itemView.findViewById(R.id.delete);
        public void setImageView(String  file){
            Picasso.get().load(file).centerCrop().resize(512,512).placeholder(android.R.color.darker_gray).into(imageView, new Callback() {
                @Override
                public void onSuccess() {
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onError(Exception e) {
                    progressBar.setVisibility(View.GONE);

                }
            });
        }


    }
}
