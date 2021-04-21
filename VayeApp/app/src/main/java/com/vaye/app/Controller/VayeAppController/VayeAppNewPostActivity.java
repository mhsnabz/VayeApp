package com.vaye.app.Controller.VayeAppController;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.kongzue.dialog.v3.CustomDialog;
import com.kongzue.dialog.v3.TipDialog;
import com.kongzue.dialog.v3.WaitDialog;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.vaye.app.Controller.ChatController.Conservation.ConservationController;
import com.vaye.app.Controller.HomeController.StudentSetNewPost.NewPostAdapter;
import com.vaye.app.Controller.MapsController.LocationPermissionActivity;
import com.vaye.app.Interfaces.CompletionWithValue;
import com.vaye.app.Interfaces.DataTypes;
import com.vaye.app.Interfaces.Notifications;
import com.vaye.app.Interfaces.StringArrayListInterface;
import com.vaye.app.Interfaces.StringCompletion;
import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.LoginRegister.MessageType;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.MainPostModel;
import com.vaye.app.Model.MainPostTopicFollower;
import com.vaye.app.Model.NewPostDataModel;
import com.vaye.app.R;
import com.vaye.app.Services.MainPostNS;
import com.vaye.app.Services.MessageService;
import com.vaye.app.Util.BottomSheetHelper.BottomSheetActionTarget;
import com.vaye.app.Util.Helper;
import com.vaye.app.Util.RunTimePermissionHelper;
import com.vincent.filepicker.Constant;
import com.vincent.filepicker.activity.ImagePickActivity;
import com.vincent.filepicker.filter.entity.ImageFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

import static com.vincent.filepicker.activity.ImagePickActivity.IS_NEED_CAMERA;

public class VayeAppNewPostActivity extends AppCompatActivity {
    private static final String TAG = "StudentNewPostActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;
    Toolbar toolbar;
    Double lat = null , longLat = null ;
    String locationName = "";
    TextView title;
    ImageButton rigthBarButton;
    CurrentUser currentUser;
    StorageTask<UploadTask.TaskSnapshot> uploadTask;
    CircleImageView profileImage;
    TextView name , username , lessonName;
    ProgressBar progressBar;
    EditText text;
    RelativeLayout driveLayout;
    ImageButton driveIcon , deleteClick;
    TextView linkName;
    RecyclerView datas;
    KProgressHUD hud;
    MainPostModel mainPostModel;
    ArrayList<MainPostTopicFollower>  followers;
    String postType;
    String postName = "";
    GeoPoint location = null;
    private static final int image_pick_request =600;
    String value = "";
    ImageView sampleImage;
    //Stackview
    ImageButton addImage, map_pin , price , cancelPrice , cancelLacation;
    String contentType = "";
    String mimeType = "";
    long postDate = Calendar.getInstance().getTimeInMillis();
    ArrayList<NewPostDataModel> dataModel = new ArrayList<>();
    String notificationType = "";
    NewPostAdapter adapter ;
    RelativeLayout priceLayout , locationLayout;
    TextView totalPrice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vaye_app_new_post);
        Bundle extras = getIntent().getExtras();
        Intent intentIncoming = getIntent();
        sampleImage = (ImageView)findViewById(R.id.sampleImage);

        if (extras != null){
            currentUser = intentIncoming.getParcelableExtra("currentUser");
            postType = intentIncoming.getStringExtra("postType");

            followers = intentIncoming.getParcelableExtra("followers");
            setToolbar(postType);
            setView(currentUser,postType);
            setStackView(postType);
            setRecylerView(currentUser);
            priceLayout = (RelativeLayout)findViewById(R.id.priceLayout);
            cancelPrice = (ImageButton)findViewById(R.id.cancelPriceButton) ;
            totalPrice = (TextView)findViewById(R.id.totalPrice);

            LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                    new IntentFilter("target_location"));

            cancelPrice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    value = "";
                    setPriceLayout(value);
                }
            });

        }else {
            finish();
        }

        hud = KProgressHUD.create(VayeAppNewPostActivity.this)
                .setStyle(KProgressHUD.Style.ANNULAR_DETERMINATE)
                .setLabel("Dosya Yükleniyor")
                .setMaxProgress(100);
    }
    private void setToolbar(String  lessonName)
    {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        title = toolbar.findViewById(R.id.toolbar_title);
        rigthBarButton = toolbar.findViewById(R.id.rigthButton);
        rigthBarButton.setImageResource(R.drawable.post_it);
        toolbar.setTitle("");
        toolbar.setSubtitle("");
        title.setText(lessonName);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Helper.shared().back(VayeAppNewPostActivity.this);
            }
        });
        rigthBarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                 String  msgText = text.getText().toString();
                if (msgText.isEmpty()){
                    TipDialog.show(VayeAppNewPostActivity.this , "Gönderiniz Boş Olamaz", TipDialog.TYPE.ERROR);
                    return;
                }else {
                    WaitDialog.show(VayeAppNewPostActivity.this , "Gönderiniz Paylaşılıyor...");
                    if (postType.equals(BottomSheetActionTarget.al_sat)){
                        notificationType = Notifications.NotificationType.new_ad;
                        postName = BottomSheetActionTarget.sell_buy;
                    }else if (postType.equals(BottomSheetActionTarget.yemek)){
                        notificationType = Notifications.NotificationType.new_food_me;
                        postName = BottomSheetActionTarget.food_me;
                    }
                    else if (postType.equals(BottomSheetActionTarget.kamp)){
                        notificationType = Notifications.NotificationType.new_camping;
                        postName = BottomSheetActionTarget.camping;

                    }
                    MainPostNS.shared().setNewPostNotification(followers,currentUser,String.valueOf(Calendar.getInstance().getTimeInMillis()),postType,msgText, notificationType,String.valueOf(postDate));
                    for (String username : Helper.shared().getMentionedUser(msgText)){
                        MainPostNS.shared().setMentionedPost(username,currentUser , String.valueOf(postDate), Notifications.NotificationType.home_new_mentions_post, Notifications.NotificationDescription.home_new_mentions_post );
                    }
                    MainPostNS.shared().getMyFollowers(currentUser.getUid(), new StringArrayListInterface() {
                        @Override
                        public void getArrayList(ArrayList<String> list) {


                               MainPostNS.shared().setNewPost(postName, "post", locationName, value, currentUser, location, postDate, list, msgText, dataModel, new TrueFalse<Boolean>() {
                                    @Override
                                    public void callBack(Boolean _value) {
                                        if (_value){
                                            DocumentReference ref = FirebaseFirestore.getInstance().collection("main-post")
                                                    .document("post")
                                                    .collection("post")
                                                    .document(String.valueOf(postDate));

                                            Map<String , Object> mapObj = new HashMap<>();
                                            if (!dataModel.isEmpty()){
                                                mapObj.put("type","data");
                                                mapObj.put("geoPoint",location);
                                                for (int i = 0 ; i < dataModel.size() ; i ++){
                                                    mapObj.put("data",FieldValue.arrayUnion(dataModel.get(i).getFileUrl()));
                                                    mapObj.put("thumbData",FieldValue.arrayUnion(dataModel.get(i).getThumb_url()));
                                                    ref.set(mapObj, SetOptions.merge());

                                                }

                                            }else{
                                                mapObj.put("geoPoint",location);
                                                ref.set(mapObj, SetOptions.merge());
                                            }

                                            WaitDialog.dismiss();
                                            TipDialog.show(VayeAppNewPostActivity.this , "Gönderiniz Paylaşıldı", TipDialog.TYPE.SUCCESS);
                                            TipDialog.dismiss(1000);
                                            finish();
                                            Helper.shared().back(VayeAppNewPostActivity.this);
                                        }

                                    }
                                });

                        }
                    });
                }
            }
        });
    }
    private void setView(CurrentUser currentUser , String selectedLessonName){
        profileImage = (CircleImageView)findViewById(R.id.profileImage);
        name = (TextView)findViewById(R.id.name);
        username = (TextView)findViewById(R.id.username);
        lessonName = (TextView)findViewById(R.id.lessonName);
        progressBar = (ProgressBar)findViewById(R.id.progress);
        text = (EditText)findViewById(R.id.text);


        if (currentUser.getThumb_image()!=null && !currentUser.getThumb_image().isEmpty()) {
            Picasso.get().load(currentUser.getThumb_image()).placeholder(android.R.color.darker_gray)
                    .centerCrop().resize(256,256)
                    .into(profileImage, new Callback() {
                        @Override
                        public void onSuccess() {
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(Exception e) {
                            progressBar.setVisibility(View.GONE);

                        }
                    });
        }else{
            progressBar.setVisibility(View.GONE);
            profileImage.setImageResource(android.R.color.darker_gray);
        }

        name.setText(currentUser.getName());
        username.setText(currentUser.getUsername());
        lessonName.setText(selectedLessonName);
    }
    private void setRecylerView(CurrentUser currentUser ){
        adapter = new NewPostAdapter(dataModel,VayeAppNewPostActivity.this,currentUser);
        datas = (RecyclerView)findViewById(R.id.datasRec);
        datas.setLayoutManager(new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL));
        datas.setAdapter(adapter);


    }

    public boolean isServicesOk(){
        Log.d(TAG, "isServicesOk: " +"check google service version");
        int avabile = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(VayeAppNewPostActivity.this);
        if (avabile == ConnectionResult.SUCCESS){
            return true;
        }else if (GoogleApiAvailability.getInstance().isUserResolvableError(avabile))
        {
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(VayeAppNewPostActivity.this ,avabile,ERROR_DIALOG_REQUEST);
            dialog.show();
            return  false;
        }else{

        }
        return  false;
    }
    private void setStackView(String postType){

        addImage = (ImageButton)findViewById(R.id.gallery);
        price = (ImageButton)findViewById(R.id.price);
        map_pin = (ImageButton)findViewById(R.id.map_pin);
        map_pin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isServicesOk()){
                    Intent i = new Intent(VayeAppNewPostActivity.this , LocationPermissionActivity.class);
                    startActivity(i);
                }
            }
        });

        if (postType.equals(BottomSheetActionTarget.al_sat)){
            addImage.setVisibility(View.VISIBLE);
            price.setVisibility(View.VISIBLE);
            map_pin.setVisibility(View.VISIBLE);

        }else if (postType.equals(BottomSheetActionTarget.yemek)){
            addImage.setVisibility(View.VISIBLE);
            price.setVisibility(View.GONE);
            map_pin.setVisibility(View.VISIBLE);
        }else if (postType.equals(BottomSheetActionTarget.kamp)){
            addImage.setVisibility(View.VISIBLE);
            price.setVisibility(View.GONE);
            map_pin.setVisibility(View.VISIBLE);
        }
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();
            }
        });

        price.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CustomDialog.show(VayeAppNewPostActivity.this, R.layout.add_price_layout, new CustomDialog.OnBindView() {
                    @Override
                    public void onBind(CustomDialog dialog, View v) {
                        Button addPrice = (Button)v.findViewById(R.id.addPrice);
                        EditText price = (EditText)v.findViewById(R.id.price);
                        Button cancel = (Button)v.findViewById(R.id.cancel);
                        ImageButton dismiss = ( ImageButton)v.findViewById(R.id.dismis);
                        dismiss.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.doDismiss();
                            }
                        });

                        addPrice.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String priceValue = price.getText().toString();
                                if (!priceValue.isEmpty()){
                                    value = priceValue;
                                    setPriceLayout(value);
                                }
                                dialog.doDismiss();
                            }
                        });
                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.doDismiss();
                            }
                        });


                    }
                });
            }
        });
    }

    private void setPriceLayout(String value){

        if (value.isEmpty() && value.equals("")){

            priceLayout.setVisibility(View.GONE);
            totalPrice.setText("");
        }else{
            priceLayout.setVisibility(View.VISIBLE);
            totalPrice.setText(value + " ₺");
        }

    }
    private void uploadImage() {
        if (!RunTimePermissionHelper.shared().checkGalleryPermission(this)){
            RunTimePermissionHelper.shared().requestGalleryCameraPermission(this, new TrueFalse<Boolean>() {
                @Override
                public void callBack(Boolean _value) {
                    if (_value){
                        pickGallery();
                    }
                }
            });
        }else{
            pickGallery();

        }
    }

    private void pickGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        intent.setAction(Intent.ACTION_GET_CONTENT);

        intent.addCategory(intent.CATEGORY_OPENABLE);
        startActivityForResult(intent,image_pick_request);

    }

    public  float getImageSizeFromUriInMegaByte(Context context, Uri uri) {
        String scheme = uri.getScheme();
        float dataSize = 0;
        if (scheme.equals(ContentResolver.SCHEME_CONTENT)) {
            try {
                InputStream fileInputStream = context.getContentResolver().openInputStream(uri);
                if (fileInputStream != null) {
                    dataSize = fileInputStream.available();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (scheme.equals(ContentResolver.SCHEME_FILE)) {
            String path = uri.getPath();
            File file = null;
            try {
                file = new File(path);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (file != null) {
                dataSize = file.length();
            }
        }
        return dataSize / (1024 * 1024) ;
    }

    private double getTotalSize(Context context , ArrayList<NewPostDataModel> dataModel){
        double totolVal = 0.0;
        for (NewPostDataModel model : dataModel){
            totolVal += getImageSizeFromUriInMegaByte(context,model.getFile());
        }
        return  totolVal;
    }
    public Bitmap decodeUri(Uri uri) {
        ParcelFileDescriptor parcelFD = null;
        try {
            parcelFD = getContentResolver().openFileDescriptor(uri, "r");
            FileDescriptor imageSource = parcelFD.getFileDescriptor();

            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeFileDescriptor(imageSource, null, o);

            // the new size we want to scale to
            final int REQUIRED_SIZE = 1024;

            // Find the correct scale value. It should be the power of 2.
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE) {
                    break;
                }
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            // decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            Bitmap bitmap = BitmapFactory.decodeFileDescriptor(imageSource, null, o2);

            sampleImage.setImageBitmap(bitmap);
            return  BitmapFactory.decodeFileDescriptor(imageSource, null, o2);
        } catch (FileNotFoundException e) {
            // handle errors
        } catch (IOException e) {
            // handle errors
        } finally {
            if (parcelFD != null)
                try {
                    parcelFD.close();
                } catch (IOException e) {
                    // ignored
                }
        }
     return  null;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == image_pick_request) {
                Uri file = data.getData();
                decodeUri(data.getData());
                Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(decodeUri(data.getData()), 300, 300);
                Log.d(TAG, "onActivityResult: " + ThumbImage.getHeight());
                Log.d(TAG, "onActivityResult: " + file.getPath());
                String mimeType = DataTypes.mimeType.image;
                String contentType = DataTypes.contentType.image;
                String fileName = file.getPath().toString();
                dataModel.add(new NewPostDataModel(fileName, file, null, null, mimeType, contentType));
                saveDatasToDataBase(contentType, mimeType, VayeAppNewPostActivity.this, String.valueOf(postDate), currentUser, file, new StringCompletion() {
                    @Override
                    public void getString(String url) {
                        setThumbImage(DataTypes.contentType.image, VayeAppNewPostActivity.this, String.valueOf(postDate), DataTypes.mimeType.image, currentUser, "image", decodeUri(data.getData()), new StringCompletion() {
                            @Override
                            public void getString(String thumb_url) {
                                updateImages(VayeAppNewPostActivity.this,  currentUser, url, thumb_url, new TrueFalse<Boolean>() {
                                    @Override
                                    public void callBack(Boolean _value) {
                                        WaitDialog.dismiss();
                                        TipDialog.show(VayeAppNewPostActivity.this , "Dosya Yüklendi", TipDialog.TYPE.SUCCESS);
                                        TipDialog.dismiss(1500);
                                        for (int i = 0 ; i< dataModel.size() ; i++){
                                            if (dataModel.get(i).getFile() == file){
                                                dataModel.get(i).setFileUrl(url);
                                                dataModel.get(i).setThumb_url(thumb_url);
                                                adapter.notifyDataSetChanged();
                                            }
                                        }
                                        title.setText(String.valueOf(getTotalSize(VayeAppNewPostActivity.this , dataModel) +"mb"));
                                    }
                                });
                            }
                        });
                    }
                });
            }
        /*if (resultCode == RESULT_OK){
            if (requestCode == image_pick_request){
                Uri file = Uri.parse(data.getData().getPath());
                Log.d(TAG, "onActivityResult: " + file.getPath());
                String mimeType = DataTypes.mimeType.image;
                String contentType = DataTypes.contentType.image;
               // String fileName = file.getPath().toString();
                dataModel.add(new NewPostDataModel("fileName", file, null, null, mimeType, contentType));

                saveDatasToDataBase(DataTypes.contentType.image, DataTypes.mimeType.image, this, String.valueOf(postDate), currentUser, file, new StringCompletion() {
                    @Override
                    public void getString(String url) {
                        try {
                            setThumbData(DataTypes.contentType.image, VayeAppNewPostActivity.this, String.valueOf(postDate), DataTypes.mimeType.image, currentUser, "image", file, new StringCompletion() {
                                @Override
                                public void getString(String thumb_url) {
                                    updateImages(VayeAppNewPostActivity.this,  currentUser, url, thumb_url, new TrueFalse<Boolean>() {
                                        @Override
                                        public void callBack(Boolean _value) {
                                            WaitDialog.dismiss();
                                            TipDialog.show(VayeAppNewPostActivity.this , "Dosya Yüklendi", TipDialog.TYPE.SUCCESS);
                                            TipDialog.dismiss(1500);
                                            for (int i = 0 ; i< dataModel.size() ; i++){
                                                if (dataModel.get(i).getFile() == file){
                                                    dataModel.get(i).setFileUrl(url);
                                                    dataModel.get(i).setThumb_url(thumb_url);
                                                    adapter.notifyDataSetChanged();
                                                }
                                            }
                                            title.setText(String.valueOf(getTotalSize(VayeAppNewPostActivity.this , dataModel) +"mb"));
                                        }
                                    });
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.d(TAG, "getString: " + e.getLocalizedMessage());
                        }

                    }
                });
            }
        }*/

        }
    }
    //TODO:: upload images
    private void saveDatasToDataBase(String contentType, String mimeType, Activity activity , String date , CurrentUser currentUser , Uri data,
                                     StringCompletion completion ){
        hud.show();
        Log.d(TAG, "saveDatasToDataBase: " + mimeType);
        Log.d(TAG, "saveDatasToDataBase: " + contentType);

        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType(contentType)
                .build();
            StorageReference ref = FirebaseStorage.getInstance().getReference().child("main-post")
                .child(currentUser.getShort_school())
                .child(postType)
                .child(currentUser.getUsername())
                .child(date)
                .child(String.valueOf(Calendar.getInstance().getTimeInMillis()) +mimeType);
        uploadTask =  ref.putFile(data , metadata).addOnCompleteListener(activity, new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()){

                    task.getResult().getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String  url = uri.toString();
                            Log.d(TAG, "onComplete: "+ url);
                            hud.dismiss();
                            WaitDialog.show((AppCompatActivity) activity , "Dosya Yükleniyor");
                            completion.getString(url);


                        }
                    });

                }

            }

        });

        uploadTask.addOnProgressListener(activity, new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {

                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                hud.setProgress((int) progress);
                Log.d(TAG, "onProgress: " + progress);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: "+e.getLocalizedMessage());
                Log.d(TAG, "onFailure: "+e.getMessage());
                Log.d(TAG, "onFailure: "+e.getCause());
            }
        });

    }
    public static String getRealPathFromUri(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void setThumbImage(String contentType, Activity activity ,String date ,String mimeType, CurrentUser currentUser , String type , Bitmap data,
                               StringCompletion completion ){
        if (type == "image"){
            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setContentType(contentType)
                    .build();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            data.compress(Bitmap.CompressFormat.JPEG, 25, baos);
            final byte[] thumb_byte = baos.toByteArray();

            StorageReference ref = FirebaseStorage.getInstance().getReference().child("main-post-thumb")
                    .child(currentUser.getShort_school())
                    .child(postType)
                    .child(currentUser.getUsername())
                    .child(date)
                    .child(String.valueOf(Calendar.getInstance().getTimeInMillis()) +mimeType);

            ref.putBytes(thumb_byte, metadata).addOnCompleteListener(activity, new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()){
                        task.getResult().getMetadata().getReference().getDownloadUrl().addOnSuccessListener(activity, new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String thumb_url = uri.toString();
                                completion.getString(thumb_url);

                            }
                        });
                    }
                }
            });

        }
    }

    private void setThumbData(String contentType, Activity activity ,String date ,String mimeType, CurrentUser currentUser , String type , Uri data,
                              StringCompletion completion) throws IOException {
        WaitDialog.show((AppCompatActivity) activity ,null);
        if (type == "image"){

            if (RunTimePermissionHelper.shared().checkGalleryPermission(VayeAppNewPostActivity.this)){
                StorageMetadata metadata = new StorageMetadata.Builder()
                        .setContentType(contentType)
                        .build();

                File thumb_file = new File(getRealPathFromUri(VayeAppNewPostActivity.this , data));
                if (thumb_file.exists()){

                }else{
                    Log.d(TAG, "setThumbData: file not exist");
                }
                Bitmap thumb_bitmap = new Compressor(this)
                        .setMaxHeight(300)
                        .setMaxWidth(300).setQuality(100)
                        .compressToBitmap(thumb_file);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                final byte[] thumb_byte = baos.toByteArray();

                StorageReference ref = FirebaseStorage.getInstance().getReference().child("main-post-thumb")
                        .child(currentUser.getShort_school())
                        .child(postType)
                        .child(currentUser.getUsername())
                        .child(date)
                        .child(String.valueOf(Calendar.getInstance().getTimeInMillis()) +mimeType);

                ref.putBytes(thumb_byte, metadata).addOnCompleteListener(activity, new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()){
                            task.getResult().getMetadata().getReference().getDownloadUrl().addOnSuccessListener(activity, new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String thumb_url = uri.toString();
                                    completion.getString(thumb_url);

                                }
                            });
                        }
                    }
                });
            }

        }

    }
    private void  updateImages(Activity activity , CurrentUser currentUser ,String url , String  thumb_url, TrueFalse<Boolean> callback ){

        ///user/VUSU6uA0odX7vuF5giXWbOUYzni1/saved-task/task
        DocumentReference ref = FirebaseFirestore.getInstance().collection("user")
                .document(currentUser.getUid())
                .collection("saved-task")
                .document("task");
        Map<String , Object> map = new HashMap<>();
        map.put("data", FieldValue.arrayUnion(url));
        map.put("thumbData",FieldValue.arrayUnion(thumb_url));
        map.put("type","data");
        ref.set(map , SetOptions.merge()).addOnCompleteListener(activity, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){

                    callback.callBack(true);
                }
            }
        });

    }
    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String target = intent.getStringExtra("target");
            if (target.equals(CompletionWithValue.location)){
                 lat = intent.getDoubleExtra("lat",-45);
                 longLat = intent.getDoubleExtra("longLat",45);
                 locationName = intent.getStringExtra("locationName");
                 location = new GeoPoint(lat,longLat);
                Log.d(TAG, "onReceive: locaiton " + lat + " " + longLat + " " + locationName );
                setLocationLayout();
            }
        }
    };
    private void setLocationLayout(){
        locationLayout = (RelativeLayout)findViewById(R.id.locationLayout);
        locationLayout.setVisibility(View.VISIBLE);

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Helper.shared().back(VayeAppNewPostActivity.this);
    }

    public void hideLocationLayout(View view) {
        locationLayout.setVisibility(View.GONE);
        lat = null;
        locationName = "";
        longLat = null;
    }
}