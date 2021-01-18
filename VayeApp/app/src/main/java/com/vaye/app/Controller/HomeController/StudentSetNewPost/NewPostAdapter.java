package com.vaye.app.Controller.HomeController.StudentSetNewPost;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.vaye.app.Interfaces.DataTypes;
import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.LessonPostModel;
import com.vaye.app.Model.NewPostDataModel;
import com.vaye.app.R;
import com.vaye.app.Services.MajorPostService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NewPostAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ArrayList<NewPostDataModel> model;
    Context context;
    CurrentUser currentUser;

    public NewPostAdapter(ArrayList<NewPostDataModel> model, Context context, CurrentUser currentUser) {
        this.model = model;
        this.context = context;
        this.currentUser = currentUser;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_post_item, parent, false);

        return new NewPostViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            NewPostViewHolder viewHolder = (NewPostViewHolder) holder;
            viewHolder.setImageView(model.get(position).getFile() , model.get(position).getMimeType() , model.get(position).getFileUrl());
                viewHolder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MajorPostService.shared().deleteFileReference((Activity) context, model.get(position).getFileUrl(), model.get(position).getThumb_url(), currentUser, null, new TrueFalse<Boolean>() {
                            @Override
                            public void callBack(Boolean _value) {
                                for (NewPostDataModel data : model){
                                    if (data.getThumb_url().equals(model.get(position).getThumb_url())){
                                        model.remove(data);
                                        notifyDataSetChanged();
                                    }
                                }

                            }
                        });
                    }
                });
    }

    @Override
    public int getItemCount() {
        return model.size();
    }

    class NewPostViewHolder extends RecyclerView.ViewHolder{

        public NewPostViewHolder(@NonNull View itemView) {
            super(itemView);
        }
        RoundedImageView imageView = (RoundedImageView)itemView.findViewById(R.id.imageView);
        ProgressBar progressBar = (ProgressBar)itemView.findViewById(R.id.progress);
        ImageButton delete = (ImageButton)itemView.findViewById(R.id.delete);
        public void setImageView(Uri file, String mimeType , String fileURl){
            if (fileURl != null) {
                if (mimeType.equals(".doc")| mimeType.equals(".docx")){
                    imageView.setImageDrawable(context.getDrawable(R.drawable.doc_holder));
                    progressBar.setVisibility(View.GONE);
                }else if (mimeType.equals(".pdf")){
                    imageView.setImageDrawable(context.getDrawable(R.drawable.pdf_holder));
                    progressBar.setVisibility(View.GONE);
                }else if(mimeType.equals(DataTypes.mimeType.image))  {
                    Picasso.get().load(fileURl).centerCrop().resize(512,512).placeholder(android.R.color.darker_gray).into(imageView, new Callback() {
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

        public void updatePostType(CurrentUser currentUser , LessonPostModel postModel){
            DocumentReference ref = FirebaseFirestore.getInstance().collection(currentUser.getShort_school())
                    .document("lesson-post")
                    .collection("post")
                    .document(postModel.getPostId());
            Map<String , Object> map = new HashMap<>();

            map.put("type","post");
            ref.set(map , SetOptions.merge());
        }

    }



}
