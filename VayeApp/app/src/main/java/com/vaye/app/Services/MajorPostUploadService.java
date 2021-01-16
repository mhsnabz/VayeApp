package com.vaye.app.Services;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.google.protobuf.StringValue;
import com.google.type.Date;
import com.kongzue.dialog.v3.TipDialog;
import com.kongzue.dialog.v3.WaitDialog;
import com.vaye.app.Interfaces.DataTypes;
import com.vaye.app.Interfaces.StringArrayListInterface;
import com.vaye.app.Interfaces.StringCompletion;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.UploadFiles;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Semaphore;

import id.zelory.compressor.Compressor;


public class MajorPostUploadService {

    private static final String TAG = "MajorPostUploadService";
    StorageTask<UploadTask.TaskSnapshot> uploadTask;

    private static final MajorPostUploadService instance = new MajorPostUploadService();
    public static MajorPostUploadService shared() {
        return instance;
    }

    public void uploadToDatebase(Activity activity,String lesson_key , String postDate , CurrentUser currentUser , ArrayList<UploadFiles> files , StringArrayListInterface listOfUrl){
        try {
            save_datas(activity, lesson_key, postDate, currentUser, files, new StringArrayListInterface() {
                @Override
                public void getArrayList(ArrayList<String> list) {
                    Log.d(TAG, "getArrayList: " + list.size());
                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }




    public void uploadSingleFile(Activity activity,String lesson_key , String postDate , CurrentUser currentUser , UploadFiles files , StringCompletion url){
        save_single_data(activity, lesson_key, postDate, currentUser, files, new StringCompletion() {
            @Override
            public void getString(String string)  {
                save_single_data(activity, lesson_key, postDate, currentUser, files, new StringCompletion() {
                    @Override
                    public void getString(String string) {
                        url.getString(string);
                    }
                });
            }
        });
    }

    private void save_single_data(Activity activity,String lesson_key , String date , CurrentUser currentUser , UploadFiles files , StringCompletion val){
        WaitDialog.show((AppCompatActivity) activity ,"Dosya Yükleniyor");

        saveDatasToDataBase(activity, lesson_key, date, currentUser, files.getType(), files.getFileUri(), 0, 1, new StringCompletion() {
            @Override
            public void getString(String string) {
                val.getString(string);
            }
        });
    }



    int uploadCount;

    private void save_datas(Activity activity,String lesson_key , String date , CurrentUser currentUser , ArrayList<UploadFiles> files , StringArrayListInterface completion) throws InterruptedException {
        ArrayList<String> uploadedImageUrlsArray = new ArrayList<>();
        uploadCount = 0;
        int imagesCount = files.size();
        Semaphore semaphore = new Semaphore(1);
        for (UploadFiles file : files){


            WaitDialog.show((AppCompatActivity) activity , imagesCount+". Dosya Yükleniyor");
          //  semaphore.acquire();


            saveDatasToDataBase(activity, lesson_key, date, currentUser, file.getType(), file.getFileUri(), uploadCount, imagesCount, new StringCompletion() {
                @Override
                public void getString(String string) {
                    uploadCount++;
                    uploadedImageUrlsArray.add(string);
                    WaitDialog.show((AppCompatActivity) activity , uploadCount +".Dosya Yüklenidi");
                    if (uploadCount == imagesCount){
                        completion.getArrayList(uploadedImageUrlsArray);
                        WaitDialog.dismiss();

                    }else{
                    //   semaphore.release();
                       WaitDialog.dismiss();
                    }
                }
            });
        }
    }


    private void saveDatasToDataBase(Activity activity ,String lesson_key , String date , CurrentUser currentUser , String type , Uri data,
    int uploadCount , int imagesCount , StringCompletion completion ){

        if (type.equals("image")){
            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setContentType("image/jpg")
                    .build();
            StorageReference ref = FirebaseStorage.getInstance().getReference().child(currentUser.getShort_school())
                    .child(currentUser.getBolum_key())
                    .child(lesson_key)
                    .child(currentUser.getUsername())
                    .child(date)
                    .child(String.valueOf(Calendar.getInstance().getTimeInMillis()) +DataTypes.mimeType.image);
       uploadTask =  ref.putFile(data , metadata).addOnCompleteListener(activity, new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()){
                        completion.getString(String.valueOf(task.getResult().getMetadata().getReference().getDownloadUrl()));
                        Log.d(TAG, "onComplete: "+ task.getResult().getMetadata().getReference().getDownloadUrl());
                        WaitDialog.dismiss();
                        }

                    }

            });
        }


            uploadTask.addOnProgressListener(activity, new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                Log.d(TAG, "onProgress: " + snapshot.getBytesTransferred());
                Log.d(TAG, "upload count: " + uploadCount);
                Log.d(TAG, "upload count: " + imagesCount);
            }
        });

    }



}
