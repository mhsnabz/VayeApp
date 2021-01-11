package com.vaye.app.Controller.HomeController.PagerAdapter;

import android.content.Context;
import android.content.Intent;

import android.net.Uri;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;


import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import com.vaye.app.Model.CurrentUser;
import com.vaye.app.R;

import java.util.ArrayList;

public class AllDatasAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public AllDatasAdapter(ArrayList<String> url, Context context, CurrentUser currentUser) {
        this.url = url;
        this.context = context;
        this.currentUser = currentUser;

    }

    ArrayList<String> url;
    Context context;
    CurrentUser currentUser;



    private static final int IMAGE  = 1;

    private static final int DOC_PDF = 2;




    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == IMAGE){
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.single_image, parent, false);

            return new ImageViewHolder(itemView);
        }
        else if (viewType == DOC_PDF){
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.pdf_doc_item, parent, false);

            return new PDF_DOC_ViewHolder(itemView);
        }
        return null;

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i)
    {
        int viewType = getItemViewType(i);
        switch (viewType) {
            case IMAGE:
                ImageViewHolder postHolder = (ImageViewHolder) holder;
                postHolder.setImageView(url.get(i));
                break;
            case DOC_PDF:
                PDF_DOC_ViewHolder dataHolder = (PDF_DOC_ViewHolder) holder;
                Button btn = (Button)dataHolder.itemView.findViewById(R.id.showButton);
                if (url.get(i).contains("doc") || url.get(i).contains("docx")){
                    btn.setText("WORD Dosyasını Görüntüle");
                }else if (url.get(i).contains("pdf")){
                    btn.setText("PDF'yi Görüntüle");
                }
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dataHolder.showData(url.get(i));
                    }
                });

                break;
        }
    }

    @Override
    public int getItemCount() {
        return url.size();
    }

    @Override
    public int getItemViewType(int position) {
        String URL = url.get(position);
        if (URL.contains("jpg")){
            return  IMAGE;
        }
        else if (URL.contains("pdf") ||URL.contains("doc") || URL.contains("docx") ){
            return DOC_PDF;
        }
        return super.getItemViewType(position);
    }



    public class ImageViewHolder extends RecyclerView.ViewHolder{

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        ProgressBar progressBar = (ProgressBar)itemView.findViewById(R.id.progress);
        ImageView imageView = (ImageView)itemView.findViewById(R.id.imageView);

        public void setImageView(String imageUrl){
            Picasso.get().load(imageUrl).placeholder(android.R.color.darker_gray).networkPolicy(NetworkPolicy.OFFLINE)
                    .into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(imageUrl).placeholder(android.R.color.darker_gray)
                                    .into(imageView, new Callback() {
                                        @Override
                                        public void onSuccess() {
                                                progressBar.setVisibility(View.GONE);
                                        }

                                        @Override
                                        public void onError(Exception e) {

                                        }
                                    });
                        }
                    });
        }


    }


    public class PDF_DOC_ViewHolder extends RecyclerView.ViewHolder{


        public PDF_DOC_ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
        Button showButton = (Button)itemView.findViewById(R.id.showButton);

        public void showData(String url){
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            context.startActivity(browserIntent);
        }
    }

}
