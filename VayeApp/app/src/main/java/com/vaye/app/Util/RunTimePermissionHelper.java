package com.vaye.app.Util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.vaye.app.Controller.HomeController.HomeActivity;
import com.vaye.app.Controller.MapsController.VayeAppPlacePickerActivity;
import com.vaye.app.Interfaces.TrueFalse;

import java.util.List;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class RunTimePermissionHelper {
    String TAG =  "RunTimePermissionHelper";
    private static final RunTimePermissionHelper instance = new RunTimePermissionHelper();
    public static RunTimePermissionHelper shared() {  return instance;}
    public boolean checkAudioPermission(Activity context) {
        int result = ContextCompat.checkSelfPermission(context.getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(context.getApplicationContext(), RECORD_AUDIO);
        int result2 = ContextCompat.checkSelfPermission(context.getApplicationContext(), READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED && result2  == PackageManager.PERMISSION_GRANTED ;
    }
    public boolean checkGalleryPermission(Activity context){
        int result = ContextCompat.checkSelfPermission(context.getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result2 = ContextCompat.checkSelfPermission(context.getApplicationContext(), READ_EXTERNAL_STORAGE);
        int result3 = ContextCompat.checkSelfPermission(context.getApplicationContext(), CAMERA);
        return  result  == PackageManager.PERMISSION_GRANTED && result2  == PackageManager.PERMISSION_GRANTED && result3 == PackageManager.PERMISSION_GRANTED ;
    }
    public boolean checkLocationPermission(Activity context){
        int result = ContextCompat.checkSelfPermission(context.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        int result2 = ContextCompat.checkSelfPermission(context.getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION);
        return  result  == PackageManager.PERMISSION_GRANTED && result2  == PackageManager.PERMISSION_GRANTED;
    }

    public void locationPermission(Context context , TrueFalse<Boolean> callback){
        Dexter.withContext(context)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                            callback.callBack(true);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        if (permissionDeniedResponse.isPermanentlyDenied()){
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("İzin Vermediniz")
                                    .setMessage("Konumunuza Erişebilmemiz için konum servislerine izin vermeniz gerekiyor")
                                    .setPositiveButton("Vazgeç", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            callback.callBack(false);
                                        }
                                    })

                                    .setPositiveButton("Ayarlar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Intent intent = new Intent();
                                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            intent.setData(Uri.fromParts("package",context.getPackageName(),null));
                                            context.startActivity(intent);
                                        }
                                    }).show();
                        }else{

                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
        Dexter.withContext(context)
                .withPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        callback.callBack(true);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        if (permissionDeniedResponse.isPermanentlyDenied()){
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("İzin Vermediniz")
                                    .setMessage("Konumunuza Erişebilmemiz için konum servislerine izin vermeniz gerekiyor")
                                    .setPositiveButton("Vazgeç", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            callback.callBack(false);
                                        }
                                    })

                                    .setPositiveButton("Ayarlar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Intent intent = new Intent();
                                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            intent.setData(Uri.fromParts("package",context.getPackageName(),null));
                                            context.startActivity(intent);
                                        }
                                    }).show();
                        }else{

                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }

    public void requestGalleryCameraPermission(Context context , TrueFalse<Boolean> callback){
        Dexter.withContext(context).withPermissions(CAMERA , READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {

                        if (multiplePermissionsReport.getDeniedPermissionResponses().size() > 0 ){
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("Erişim Engellendi")
                                    .setMessage("Telefonunuza Resim , Dokuman indirilebilmeniz ve yükleyebilmeniz için Galeri ve Kameraya Erişim İzni vermeniz gerekiyor.")
                                    .setPositiveButton("Vazgeç", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            callback.callBack(false);
                                        }
                                    })

                                    .setPositiveButton("Ayarlar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Intent intent = new Intent();
                                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            intent.setData(Uri.fromParts("package",context.getPackageName(),null));
                                            context.startActivity(intent);

                                        }
                                    }).show();
                        }else{
                            callback.callBack(true);
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken)
                    {
                        for (PermissionRequest item : list){
                            Log.d(TAG, "onPermissionRationaleShouldBeShown: " + item.getName());
                        }
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }
    public void requestAudioPermission(Context context , TrueFalse<Boolean> callback){
        Dexter.withContext(context).withPermissions(RECORD_AUDIO , READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {

                        if (multiplePermissionsReport.getDeniedPermissionResponses().size() > 0 ){
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("Erişim Engellendi")
                                    .setMessage("Ses kaydedebilmeniz için telefonunuz mikrofonuna erişim izni vermeniz gerekiyor")
                                    .setPositiveButton("Vazgeç", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            callback.callBack(false);
                                        }
                                    })
                                    .setPositiveButton("Ayarlar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Intent intent = new Intent();
                                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            intent.setData(Uri.fromParts("package",context.getPackageName(),null));
                                            context.startActivity(intent);
                                        }
                                    }).show();
                        }else{
                            callback.callBack(true);
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken)
                    {
                        for (PermissionRequest item : list){
                            Log.d(TAG, "onPermissionRationaleShouldBeShown: " + item.getName());
                        }
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }

}
