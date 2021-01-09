package com.vaye.app.Controller.HomeController.PagerAdapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.kongzue.dialog.v3.TipDialog;
import com.kongzue.dialog.v3.WaitDialog;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.ProgressCallback;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.vaye.app.Controller.HomeController.LessonPostAdapter.MajorPostViewHolder;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.R;
import com.koushikdutta.ion.Ion;

import java.io.File;
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
    Future<File> downloading;

    private static final int PDF = 1;
    private static final int IMAGE  = 2;
    private static final int DOC  = 3;




    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == IMAGE){
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.single_image, parent, false);

            return new ImageViewHolder(itemView);
        }else if (viewType==PDF){
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.single_pdf_view, parent, false);

            return new PDFViewHolder(itemView);
        }else {
        return null;

    }


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
            case PDF:
                PDFViewHolder viewHolder = (PDFViewHolder) holder;
                ((PDFViewHolder) holder).showPdf(url.get(i));
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
        }else if (URL.contains("doc") || URL.contains("docx")){
            return  DOC;
        }else if (URL.contains("pdf")){
            return PDF;
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
    public class PDFViewHolder extends RecyclerView.ViewHolder{

        public PDFViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        WebView webView = (WebView)itemView.findViewById(R.id.webView);

        public void showPdf(String url){
            Log.d("allDAtasAdapter", "showPdf: " + url);
            WaitDialog.show((AppCompatActivity) context,"");
            webView.getSettings().setJavaScriptEnabled(true);
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                    WaitDialog.dismiss();
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    WaitDialog.dismiss();
                }

                @Override
                public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                    super.onReceivedHttpError(view, request, errorResponse);
                    TipDialog.show((AppCompatActivity) context, "Pdf Yüklenirken Hata Oluştu", TipDialog.TYPE.ERROR);


                }

                @Override
                public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                    super.onReceivedSslError(view, handler, error);
                    TipDialog.show((AppCompatActivity) context, "Pdf Yüklenirken Hata Oluştu", TipDialog.TYPE.ERROR);

                }

                @Override
                public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                    super.onReceivedError(view, request, error);
                    TipDialog.show((AppCompatActivity) context, "Pdf Yüklenirken Hata Oluştu", TipDialog.TYPE.ERROR);
                }
            });

            String pdf = url;
            String doc="<iframe src='http://docs.google.com/gview?embedded=true&url='+url 'width='100%' height='100%' style='border: none;'></iframe>";

            webView.getSettings().setJavaScriptEnabled(true);

            webView.getSettings().setAllowFileAccess(true);

            webView.loadUrl( "http://docs.google.com/gview?embedded=true&url=https://firebasestorage.googleapis.com/v0/b/vaye-app.appspot.com/o/İSTE%2FBilgisayar%20Mühendisliği%2FBilişim%20Hukuku%2F@mhsnabz%2F1610132319770%2F1610132325127.pdf?alt=media&token=bda8711d-e604-4e74-9e55-b1e22ea4f15c");
        }
    }



}
