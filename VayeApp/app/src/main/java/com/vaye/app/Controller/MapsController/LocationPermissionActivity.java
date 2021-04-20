package com.vaye.app.Controller.MapsController;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.OtherUser;
import com.vaye.app.R;

public class LocationPermissionActivity extends AppCompatActivity {
    private LottieAnimationView anim;
    OtherUser otherUser;
    CurrentUser currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_permission);

        anim = (LottieAnimationView) findViewById(R.id.anim);
        anim.setAnimation(R.raw.location_pin);
        anim.playAnimation();

        if (ContextCompat.checkSelfPermission(LocationPermissionActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
        ContextCompat.checkSelfPermission(LocationPermissionActivity.this , Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            Intent i = new Intent(LocationPermissionActivity.this,VayeAppPlacePickerActivity.class);

            startActivity(i);


            finish();
            return;
        }

    }

    public void permission(View view) {
        Dexter.withActivity(LocationPermissionActivity.this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        Intent i = new Intent(LocationPermissionActivity.this,VayeAppPlacePickerActivity.class);
                        startActivity(i);
                        finish();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                    if (permissionDeniedResponse.isPermanentlyDenied()){
                        AlertDialog.Builder builder = new AlertDialog.Builder(LocationPermissionActivity.this);
                        builder.setTitle("İzin Vermediniz")
                                .setMessage("Konumunuza Erişebilmemiz için konum servislerine izin vermeniz gerekiyor")
                                .setNegativeButton("Vazgeç",null)
                                .setPositiveButton("TAMAM", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent = new Intent();
                                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        intent.setData(Uri.fromParts("package",getPackageName(),null));

                                    }
                                }).show();
                    }else{
                        Toast.makeText(LocationPermissionActivity.this,"Izın Verildi",Toast.LENGTH_SHORT).show();;
                    }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
        Dexter.withActivity(LocationPermissionActivity.this)
                .withPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        Intent i = new Intent(LocationPermissionActivity.this,VayeAppPlacePickerActivity.class);
                        startActivity(i);
                        finish();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        if (permissionDeniedResponse.isPermanentlyDenied()){
                            AlertDialog.Builder builder = new AlertDialog.Builder(LocationPermissionActivity.this);
                            builder.setTitle("İzin Vermediniz")
                                    .setMessage("Konumunuza Erişebilmemiz için konum servislerine izin vermeniz gerekiyor")
                                    .setNegativeButton("Vazgeç",null)
                                    .setPositiveButton("TAMAM", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Intent intent = new Intent();
                                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            intent.setData(Uri.fromParts("package",getPackageName(),null));

                                        }
                                    }).show();
                        }else{
                            Toast.makeText(LocationPermissionActivity.this,"Izın Verildi",Toast.LENGTH_SHORT).show();;
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }

    public void dismiss(View view) {
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}