package com.vaye.app.Controller.HomeController.PagerAdapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.net.http.SslError;
import android.os.AsyncTask;
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


import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnRenderListener;
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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
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
        }else if (viewType == DOC){
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.single_doc_holder, parent, false);

            return new DocViewHolder(itemView);
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
            case PDF:
                PDFViewHolder viewHolder = (PDFViewHolder) holder;
                ((PDFViewHolder) viewHolder).showPdf(url.get(i));

                break;
            case DOC:
                DocViewHolder docViewHolder = (DocViewHolder) holder;
                docViewHolder.setDoc(url.get(i));
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

    public class DocViewHolder extends RecyclerView.ViewHolder{

        public DocViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        WebView webView = (WebView)itemView.findViewById(R.id.webView);
        public void setDoc(String  url){
            String doc="<iframe src='http://docs.google.com/gview?embedded=true&url="+url+"' width='100%' height='100%' style='border: none;'></iframe>";

           // url=url.replaceAll(" ","%20");
            String newUA= "Chrome/43.0.2357.65 ";
            webView.getSettings().setUserAgentString(newUA);
            webView.loadUrl("https://view.officeapps.live.com/op/view.aspx?src="+url);
        }
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

        PDFView pdfView = (PDFView) itemView.findViewById(R.id.pdfView);

        public void showPdf(String _url) {




            try{
                new RetrievePdfStream().execute(_url);
            }
            catch (Exception e){
                Toast.makeText(context, "Failed to load Url :" + e.toString(), Toast.LENGTH_SHORT).show();
            }
        }

        class RetrievePdfStream extends AsyncTask<String, Void, InputStream> {
            @Override
            protected InputStream doInBackground(String... strings) {
                InputStream inputStream = null;

                try {
                    URL url = new URL(strings[0]);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    if (urlConnection.getResponseCode() == 200) {
                        inputStream = new BufferedInputStream(urlConnection.getInputStream());

                    }
                } catch (IOException e) {
                    return null;

                }
                return inputStream;
            }
            @Override
            protected void onPostExecute(InputStream inputStream) {
                pdfView.fromStream(inputStream)
                        .enableSwipe(true)
                        .onRender(new OnRenderListener() {
                            @Override
                            public void onInitiallyRendered(int nbPages, float pageWidth, float pageHeight) {
                                pdfView.fitToWidth();
                            }
                        })
                        .swipeHorizontal(false)
                        .onError(new OnErrorListener() {
                            @Override
                            public void onError(Throwable t) {

                            }
                        })
                        .load();
            }
        }

    }



}
