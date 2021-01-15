package com.vaye.app.Services;

import android.app.Activity;
import android.graphics.Bitmap;
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
import java.util.concurrent.Semaphore;

import id.zelory.compressor.Compressor;


public class MajorPostUploadService {

    private static final String TAG = "MajorPostUploadService";
    StorageTask<UploadTask.TaskSnapshot> uploadTask;

    private static final MajorPostUploadService instance = new MajorPostUploadService();
    public static MajorPostUploadService shared() {
        return instance;
    }
//lesson_key : String ,postDate : String ,currentUser : CurrentUser ,lessonName : String,
// type : [String] , data : [Data] , completion : @escaping([String]) -> Void
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
//lesson_key : String , date : String ,currentUser : CurrentUser
//                            , lessonName : String , type : [String] , datas : [Data]
    private void save_datas(Activity activity,String lesson_key , String date , CurrentUser currentUser , ArrayList<UploadFiles> files , StringArrayListInterface completion) throws InterruptedException {
        ArrayList<String> uploadedImageUrlsArray = new ArrayList<>();
        int uploadCount = 0;
        int imagesCount = files.size();
        Semaphore semaphore = new Semaphore(1);
        for (UploadFiles file : files){

            WaitDialog.show((AppCompatActivity) activity, "Dosyalar Yükleniyor");
            Thread thread = new Thread() {
                @Override
                public void run() {
                    super.run();
                    try {
                        sleep(2000);
                    }catch (InterruptedException ex){
                        Log.d(TAG, "run: " + ex.getLocalizedMessage());
                    }finally
                    {
                        saveDatasToDataBase(activity, lesson_key, date, currentUser, file.getType(), file.getFileUri(), uploadCount, imagesCount, new StringCompletion() {
                            @Override
                            public void getString(String string) throws InterruptedException {
                                uploadedImageUrlsArray.add(string);
                                completion.getArrayList(uploadedImageUrlsArray);
                                WaitDialog.dismiss();

                            }
                        });
                    }
                }
            };
            thread.start();
        }
    }
//lesson_key : String ,date : String ,currentUser : CurrentUser ,
// lessonName : String ,_ type : String ,_ data : Data
// ,_ uploadCount : Int,_ imagesCount : Int, completion : @escaping(String) ->Void)

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
            ref.putFile(data , metadata).addOnCompleteListener(activity, new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()){
                        try {
                            completion.getString(String.valueOf(task.getResult().getMetadata().getReference().getDownloadUrl()));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Log.d(TAG, "onComplete: "+ task.getResult().getMetadata().getReference().getDownloadUrl());
                    }
                }
            });
        }

    }

}
