package com.vaye.app.Controller.HomeController.PagerAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.TextView;


import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.RemoteMessage;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.vaye.app.Controller.HomeController.StudentSetNewPost.StudentNewPostActivity;
import com.vaye.app.Interfaces.DataTypes;
import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.R;
import com.vaye.app.Util.Helper;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class AllDatasActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 102;
    String TAG = "AllDatasActivity";
    Toolbar toolbar;
    ViewPager2 viewPager;
    AllDatasAdapter adapter ;
    ArrayList<String> url;
    CurrentUser currentUser;
    ImageButton downloadButton;
    StorageTask<FileDownloadTask.TaskSnapshot> downloadTask;

    KProgressHUD hud;
    FileDownloadTask.TaskSnapshot taskSnapshot;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_datas);
        Bundle extras = getIntent().getExtras();
        Intent intentIncoming = getIntent();
        if (extras != null){
            url = intentIncoming.getStringArrayListExtra("url");
            currentUser = intentIncoming.getParcelableExtra("currentUser");
            adapter = new AllDatasAdapter(url,this,currentUser);
            viewPager = (ViewPager2)findViewById(R.id.viewPager);
            viewPager.setAdapter(adapter);


        }
        setToolbar();
        hud = KProgressHUD.create(AllDatasActivity.this)
                .setStyle(KProgressHUD.Style.ANNULAR_DETERMINATE)
                .setLabel("Dosya İndiriliyor")
                .setMaxProgress(100);

    }


    private void setToolbar(){
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        downloadButton = (ImageButton)toolbar.findViewById(R.id.download);
        TextView title = toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        toolbar.setSubtitle("");
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        title.setText("Bütün Dosyalar");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Helper.shared().back(AllDatasActivity.this);
            }
        });
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: " + url.get(viewPager.getCurrentItem()));
                hud.show();
                if (haveStoragePermission()){
                    downloadFile(url.get(viewPager.getCurrentItem()), new TrueFalse<Boolean>() {
                        @Override
                        public void callBack(Boolean _value) {
                            if (_value){

                            }
                        }
                    });
                }else{

                }

            }
        });
    }
    private boolean checkGalleryPermissions() {
        if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Log.e("Permission error","You have permission");
            return true;
        }
        return  false;
    }


    String storagePermission[];


    @Override
    protected void onStart() {
        super.onStart();
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);

    }
    public  boolean haveStoragePermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.e("Permission error","You have permission");
                return true;
            } else {

                Log.e("Permission error","You have asked for permission");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //you dont need to worry about these stuff below api level 23
            Log.e("Permission error","You already have the permission");
            return true;
        }
    }
    String fileName = "";
    String mimeType = "";
    void downloadFile( String fileUrl , TrueFalse<Boolean> callback){

        if (fileUrl.contains("doc") ){
            fileName = String.valueOf(Calendar.getInstance().getTimeInMillis()) + ".doc";
            mimeType = DataTypes.contentType.doc;
        }else if (fileUrl.contains("docx")){
            fileName = String.valueOf(Calendar.getInstance().getTimeInMillis()) + ".docx";
            mimeType = DataTypes.contentType.docx;
        }else if (fileUrl.contains("pdf")){
            fileName = String.valueOf(Calendar.getInstance().getTimeInMillis()) + ".pdf";
            mimeType = DataTypes.contentType.pdf;
        }else {
            fileName = String.valueOf(Calendar.getInstance().getTimeInMillis()) + ".jpeg";
            mimeType = DataTypes.contentType.image;
        }
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                File direct = new File(Environment.getExternalStorageDirectory()
                        + "vayeapp");
                if (!direct.exists()) {
                    direct.mkdirs();
                }
                File ExistingFile =  new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS) + "/vayeapp/" + fileName);
                if (ExistingFile.exists()){
                    return;
                }else {
                    if (!ExistingFile.exists()){
                        DownloadManager mgr = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                        Uri downloadUri = Uri.parse(fileUrl);
                        DownloadManager.Request request = new DownloadManager.Request(
                                downloadUri);

                        request.setAllowedNetworkTypes(
                                DownloadManager.Request.NETWORK_WIFI
                                        | DownloadManager.Request.NETWORK_MOBILE)
                                .setAllowedOverRoaming(false)

                                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,"vayeapp/"+ fileName);


                        final long downloadId = mgr.enqueue(request);
                        final Handler handler=new Handler();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        boolean downloading = true;
                                        while (downloading) {
                                            DownloadManager.Query q = new DownloadManager.Query();
                                            q.setFilterById(downloadId);
                                            Cursor cursor = mgr.query(q);
                                            cursor.moveToFirst();
                                            int bytes_downloaded = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                                            int bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                                            double progress = (100.0 * bytes_downloaded) / bytes_total;
                                            hud.setProgress((int) progress);
                                            Log.d(TAG, "onProgress: " + progress);

                                            if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                                                downloading = false;
                                            }
                                            if (!downloading){
                                                Log.d(TAG, "run: download complete");
                                                callback.callBack(true);
                                               showNotification(ExistingFile,mimeType);

                                            }else {
                                                callback.callBack(false);
                                            }
                                            cursor.close();
                                        }
                                    }

                                });
                            }

                        }).start();

                    }
                }
            }
        });

    }
    BroadcastReceiver onComplete=new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {
            Log.d(TAG,"onComplete");
            // AND HERE SET YOUR ONW NOTIFICATION WHICH YOU WILL ABLE TO HANDLE
        }
    };
    private void showNotification(File fileName , String mimeType) {

        Intent intent = new Intent(Intent.ACTION_VIEW);

        Uri uri = FileProvider.getUriForFile(AllDatasActivity.this, AllDatasActivity.this.getApplicationContext().getPackageName() + ".provider", fileName);
        intent.setDataAndType(uri, mimeType);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        // startActivity(intent);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationManager manager=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        String channel = "com.vaye.app";
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            Notification.Builder notificationBuilder =
                    new Notification.Builder(getApplicationContext(), channel)
                            .setSmallIcon(R.drawable.notification_icon)
                            .setContentTitle("Dosya İndirildi")
                            .setContentIntent(pendingIntent)
                            .setAutoCancel(false);

            NotificationChannel notificationChannel = new NotificationChannel(channel, "VayeApp", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription("VayeApp");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            manager.createNotificationChannel(notificationChannel);
            manager.notify(new Random().nextInt(),notificationBuilder.build());
        }else{
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(getApplicationContext())
                            .setSmallIcon(R.drawable.notification_icon)
                            .setContentTitle("Dosya İndirildi")
                            .setAutoCancel(true)
                            .setPriority(Notification.PRIORITY_MAX);
            manager.notify(new Random().nextInt(),notificationBuilder.build());
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Helper.shared().back(AllDatasActivity.this);
    }

    @Override
    public void finish() {
        super.finish();
      //  adapter.
    }
}