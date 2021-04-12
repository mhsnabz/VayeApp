package com.vaye.app.Controller.ChatController.Conservation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.media.Image;
import android.media.MediaMetadataRetriever;
import android.media.MediaParser;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.core.utilities.Utilities;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.installations.Utils;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.google.rpc.Help;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.kongzue.dialog.v3.TipDialog;
import com.kongzue.dialog.v3.WaitDialog;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.vaye.app.Controller.MapsController.LocationPermissionActivity;
import com.vaye.app.Controller.VayeAppController.VayeAppNewPostActivity;
import com.vaye.app.FCM.MessagingService;
import com.vaye.app.Interfaces.CompletionWithValue;
import com.vaye.app.Interfaces.DataTypes;
import com.vaye.app.Interfaces.LocationCallback;
import com.vaye.app.Interfaces.RecordedAudioCallback;
import com.vaye.app.Interfaces.SavedAudioFileUrl;
import com.vaye.app.Interfaces.StringCompletion;
import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.LoginRegister.MessageType;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.MessagesModel;
import com.vaye.app.Model.NewPostDataModel;
import com.vaye.app.Model.OtherUser;
import com.vaye.app.R;
import com.vaye.app.Services.MessageService;
import com.vaye.app.Util.Helper;
import com.vincent.filepicker.Constant;
import com.vincent.filepicker.activity.ImagePickActivity;
import com.vincent.filepicker.filter.entity.ImageFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PrimitiveIterator;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.vincent.filepicker.activity.ImagePickActivity.IS_NEED_CAMERA;

public class ConservationController extends AppCompatActivity implements MessagesAdaper.OnItemClickListener {
    String TAG = "ConservationController";
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final int REQUEST_AUDIO_PERMISSION_CODE = 1;
    String SDCardRoot = Environment.getExternalStorageDirectory()
            .toString();
    MessagesAdaper.OnItemClickListener onItemClickListener;
    Boolean isOnline = false;
    CircleImageView profileImage;
    ProgressBar progressBar;
    TextView title;
    Toolbar toolbar;
    ImageButton options;
    LinearLayout mediaLayout;
    ImageButton mediaItem, soundRecorder;
    CurrentUser currentUser;
    OtherUser otherUser;
    ArrayList<MessagesModel> messagesList = new ArrayList<>();
    RecyclerView list;
    MessagesAdaper adaper;
    DocumentSnapshot lastPage;
    LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
    Boolean scrollingToBottom = false;
    String firstPage;
    SwipeRefreshLayout swipeRefreshLayout;
    ImageButton sendButton;
    TextInputEditText msg_edittex;
    String filename = "";
    GeoPoint geoPoint = null;
    ArrayList<NewPostDataModel> dataModel = new ArrayList<>();
    String storagePermission[];
    KProgressHUD hud;
    private static final int gallery_request = 400;
    String fileName = "";
    StorageTask<UploadTask.TaskSnapshot> uploadTask;
    int lastPostion = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conservation_controller);
        filename = Environment.getExternalStorageDirectory().getAbsolutePath();
        fileName += "/" + String.valueOf(Calendar.getInstance().getTimeInMillis()) + ".3gp";
        ;
        mediaLayout = (LinearLayout) findViewById(R.id.mediaLayout);
        mediaLayout.setVisibility(View.VISIBLE);
        sendButton = (ImageButton) findViewById(R.id.send);

        mediaItem = (ImageButton) findViewById(R.id.media);
        soundRecorder = (ImageButton) findViewById(R.id.audio);
        hud = KProgressHUD.create(ConservationController.this)
                .setStyle(KProgressHUD.Style.ANNULAR_DETERMINATE)
                .setLabel("Dosya Gönderiliyor")
                .setMaxProgress(100);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMsg();
            }
        });
        soundRecorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!CheckPermissions()) {
                    RequestPermissions();
                } else {
                    try {
                        Helper.shared().RecorderBottomSheet(ConservationController.this, Environment.getExternalStorageDirectory().getAbsolutePath() + "/recording.3gp", new RecordedAudioCallback() {
                            @Override
                            public void callback(File filePath) {
                                Uri uri = Uri.fromFile(filePath);
                                int duration = (int) ((getDuration(filePath) % (1000 * 60 * 60)) % (1000 * 60) / 1000);
                                saveDatasToDataBaseAudio(DataTypes.mimeType.audio,
                                        DataTypes.contentType.audio, ConservationController.this, otherUser, currentUser, uri, new SavedAudioFileUrl() {
                                            @Override
                                            public void callback(String url, String filename) {
                                                MessageService.shared().sendTextMsg(currentUser,otherUser,filename,isOnline,Calendar.getInstance().getTimeInMillis(),null,duration,0f,0f,url,String.valueOf(Calendar.getInstance().getTimeInMillis()),MessageType.audio);
                                            }


                                        });
                            }

                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
        msg_edittex = (TextInputEditText) findViewById(R.id.msgText);
        Bundle extras = getIntent().getExtras();
        Intent intentIncoming = getIntent();
        if (extras != null) {
            currentUser = intentIncoming.getParcelableExtra("currentUser");
            otherUser = intentIncoming.getParcelableExtra("otherUser");
            setToolbar(otherUser);
            configureUI(otherUser);

            swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeAndRefresh);
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    loadMoreMessages();
                }
            });
            mediaItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Helper.shared().MessageMediaLauncher(ConservationController.this, otherUser, currentUser, new TrueFalse<Boolean>() {
                        @Override
                        public void callBack(Boolean _value) {

                        }
                    });
                }
            });
        }

    }

    private void targetChooser() {

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("media_item_target"));
        LocalBroadcastManager.getInstance(this).registerReceiver(mLocaitonReciver, new IntentFilter("locaiton_manager"));
        LocalBroadcastManager.getInstance(this).registerReceiver(mLocaitonReciver, new IntentFilter("chat_option"));

    }

    private void setToolbar(OtherUser otherUser) {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        title = toolbar.findViewById(R.id.toolbar_title);
        toolbar.setTitle("");
        toolbar.setSubtitle("");
        title.setText(otherUser.getName());
        profileImage = (CircleImageView) toolbar.findViewById(R.id.profileImage);
        progressBar = (ProgressBar) toolbar.findViewById(R.id.progress);
        options = (ImageButton) toolbar.findViewById(R.id.options);
        if (otherUser.getThumb_image() != null && !otherUser.getThumb_image().isEmpty()) {
            Picasso.get().load(otherUser.getThumb_image())
                    .resize(256, 256)
                    .centerCrop()
                    .placeholder(android.R.color.darker_gray)
                    .into(profileImage, new Callback() {
                        @Override
                        public void onSuccess() {
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(Exception e) {
                            progressBar.setVisibility(View.GONE);
                            profileImage.setImageResource(android.R.color.darker_gray);
                        }
                    });
        } else {
            progressBar.setVisibility(View.GONE);
            profileImage.setImageResource(android.R.color.darker_gray);
        }
        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              Helper.shared().MessageOptionsBottomSheetLauncaher(ConservationController.this, currentUser, otherUser);
            }
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Helper.shared().back(ConservationController.this);
            }
        });
    }

    private void configureUI(OtherUser otherUser) {
        list = (RecyclerView) findViewById(R.id.msgList);
        mLayoutManager.setReverseLayout(false);
        list.setLayoutManager(mLayoutManager);
        list.setHasFixedSize(true);
        adaper = new MessagesAdaper(currentUser, otherUser, this, messagesList, this::onItemClick);

        list.setAdapter(adaper);
        final View contentView = list;
        contentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                Rect r = new Rect();
                contentView.getWindowVisibleDisplayFrame(r);
                int screenHeight = contentView.getRootView().getHeight();

                // r.bottom is the position above soft keypad or device button.
                // if keypad is shown, the r.bottom is smaller than that before.
                int keypadHeight = screenHeight - r.bottom;

                if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                    // keyboard is opened
                    if (!scrollingToBottom) {
                        scrollingToBottom = true;
                        scrollRecyclerViewToBottom(list);
                    }
                } else {
                    // keyboard is closed
                    scrollingToBottom = false;
                }
            }
        });
    }

    private void scrollRecyclerViewToBottom(RecyclerView recyclerView) {
        RecyclerView.Adapter adapter = recyclerView.getAdapter();
        if (adapter != null && adapter.getItemCount() > 0) {
            recyclerView.scrollToPosition(adapter.getItemCount() - 1);
        }
    }

    private void loadMoreMessages() {
        if (lastPage == null) {
            swipeRefreshLayout.setRefreshing(false);
            return;
        } else {
            Query dbNext = FirebaseFirestore.getInstance().collection("messages")
                    .document(currentUser.getUid())
                    .collection(otherUser.getUid()).orderBy("id").endBefore(firstPage)
                    .limitToLast(5);
            dbNext.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        if (!task.getResult().getDocuments().isEmpty()) {
                            for (DocumentSnapshot item : task.getResult().getDocuments()) {
                                messagesList.add(item.toObject(MessagesModel.class));
                                Collections.sort(messagesList, new Comparator<MessagesModel>() {
                                    public int compare(MessagesModel obj1, MessagesModel obj2) {
                                        return obj1.getId().compareTo(obj2.getId());
                                    }

                                });
                            }
                            adaper.notifyDataSetChanged();
                            swipeRefreshLayout.setRefreshing(false);
                            firstPage = messagesList.get(0).getId();

                        } else {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                }
            });
        }
    }

    private void getAllMessages() {
        final DownloadTask downloadTask = new DownloadTask(ConservationController.this);
        messagesList.clear();
        adaper.notifyDataSetChanged();
        Query db = FirebaseFirestore.getInstance().collection("messages")
                .document(currentUser.getUid())
                .collection(otherUser.getUid()).limitToLast(10).orderBy("id", Query.Direction.ASCENDING);
        db.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (!value.isEmpty()) {
                    for (DocumentChange item : value.getDocumentChanges()) {
                        if (item.getType().equals(DocumentChange.Type.ADDED)) {
                            messagesList.add(item.getDocument().toObject(MessagesModel.class));
                            adaper.notifyDataSetChanged();

                            if (item.getDocument().getString("senderUid").equals(currentUser.getUid())) {
                                scrollRecyclerViewToBottom(list);
                            }
                        }
                        firstPage = messagesList.get(0).getId();
                    }
                    lastPage = value.getDocuments().get(value.getDocuments().size() - 1);
                }
            }
        });

    }

    private void sendMsg() {
        String msg = msg_edittex.getText().toString();
        long time = Calendar.getInstance().getTimeInMillis();
        msg_edittex.setText("");
        if (msg.isEmpty()) {
            return;
        } else {
            String messageId = String.valueOf(Calendar.getInstance().getTimeInMillis());
            MessageService.shared().sendTextMsg(currentUser, otherUser, filename, isOnline, time, null, 0, 0f, 0f, msg, messageId, MessageType.text);

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        getAllMessages();
        targetChooser();

        DocumentReference ref = FirebaseFirestore.getInstance().collection("user")
                .document(otherUser.getUid());
        ref.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value.exists()) {
                    otherUser = value.toObject(OtherUser.class);
                }
            }
        });
        DocumentReference refCurrentUser = FirebaseFirestore.getInstance().collection("user")
                .document(currentUser.getUid());
        refCurrentUser.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value.exists()) {
                    currentUser = value.toObject(CurrentUser.class);
                }
            }
        });
        MessageService.shared().setCurrentUserOnline(currentUser, otherUser, true);
        MessageService.shared().deleteBadge(currentUser, otherUser);
        Log.d("MessageService", "onStart: badge delete");
        DocumentReference isOnlineDb = FirebaseFirestore.getInstance().collection("user")
                .document(otherUser.getUid())
                .collection("msg-list")
                .document(currentUser.getUid());

        isOnlineDb.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value.exists()) {
                    isOnline = value.getBoolean("isOnline");
                }
            }
        });

        scrollRecyclerViewToBottom(list);

    }

    @Override
    protected void onStop() {
        super.onStop();
        DocumentReference setCurrentUserOnline = FirebaseFirestore.getInstance().collection("user")
                .document(currentUser.getUid())
                .collection("msg-list")
                .document(otherUser.getUid());
        Map<String, Object> map = new HashMap<>();
        map.put("isOnline", false);
        map.put("badgeCount", 0);
        setCurrentUserOnline.set(map, SetOptions.merge());
        Log.d(TAG, "onStop: onStop");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onStop: onPause");

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onStop: onResume");


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Helper.shared().back(ConservationController.this);
    }

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String target = intent.getStringExtra("target");
            Log.d(TAG, "onReceive: " + target);
            if (target != null && target.equals(CompletionWithValue.send_image)) {
                sendImage();
            } else if (target != null && target.equals(CompletionWithValue.send_location)) {
                sendLocation();
            } else if (target != null && target.equals(CompletionWithValue.send_document)) {
                sendDocument();
            }else if (target !=null && target.equals(CompletionWithValue.remove_chat)){
                WaitDialog.show(ConservationController.this,"Sohbet Siliniyor");
                MessageService.shared().removeChat(currentUser, otherUser, new TrueFalse<Boolean>() {
                    @Override
                    public void callBack(Boolean _value) {
                        if (_value){
                            finish();
                            Helper.shared().back(ConservationController.this);
                            WaitDialog.dismiss();
                        }
                    }
                });

            }
            else if (target !=null && target.equals(CompletionWithValue.remove_from_friend_list)){
                Toast.makeText(ConservationController.this,"remove_from_friend_list",Toast.LENGTH_SHORT).show();
            }
            else if (target !=null && target.equals(CompletionWithValue.make_slient_chat_friend)){
                Toast.makeText(ConservationController.this,"make_slient_chat_friend",Toast.LENGTH_SHORT).show();
            }
            else if (target !=null && target.equals(CompletionWithValue.report_chat_user)){
                Toast.makeText(ConservationController.this,"report_chat_user",Toast.LENGTH_SHORT).show();
            }
        }
    };



    private void sendImage() {
        if (!checkGalleryPermissions()) {
            requestStoragePermission();
        } else {
            pickGallery();
        }
    }

    private void sendDocument() {

    }

    private void sendLocation() {
        if (isServicesOk()) {
            Intent i = new Intent(ConservationController.this, LocationPermissionActivity.class);
            startActivity(i);
        }
    }
    private long getDuration(File file) {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(file.getAbsolutePath());

        return Long.parseLong(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
    }
    public boolean isServicesOk() {
        Log.d(TAG, "isServicesOk: " + "check google service version");
        int avabile = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(ConservationController.this);
        if (avabile == ConnectionResult.SUCCESS) {
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(avabile)) {
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(ConservationController.this, avabile, ERROR_DIALOG_REQUEST);
            dialog.show();
            return false;
        } else {

        }
        return false;
    }

    public BroadcastReceiver mLocaitonReciver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String target = intent.getStringExtra("target");
            Log.d(TAG, "mLocaitonReciver: " + target);
            if (target != null && target.equals(CompletionWithValue.get_locaiton)) {
                geoPoint = new GeoPoint(intent.getDoubleExtra("lat", -45), intent.getDoubleExtra("longLat", 45));
                Log.d(TAG, "getLocation: " + geoPoint.getLongitude());
                Log.d(TAG, "getLocation: " + geoPoint.getLatitude());
                if (intent.getIntExtra("count", 1) == 1) {
                    sendLocationMessage(new GeoPoint(geoPoint.getLatitude(), geoPoint.getLongitude()));

                }
            }
        }
    };


    private void sendLocationMessage(GeoPoint geoPoint) {
        String msgID = String.valueOf(Calendar.getInstance().getTimeInMillis());
        long time = Calendar.getInstance().getTimeInMillis();
        MessageService.shared().sendTextMsg(currentUser, otherUser, filename, isOnline, time, geoPoint, 0, 200f, 200f, "Konum", msgID, MessageType.location);
    }


    private boolean checkGalleryPermissions() {
        boolean result = ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, storagePermission, gallery_request);

    }

    private void pickGallery() {
        Intent intent1 = new Intent(this, ImagePickActivity.class);
        intent1.putExtra(IS_NEED_CAMERA, false);
        intent1.putExtra(Constant.MAX_NUMBER, 1);
        startActivityForResult(intent1, Constant.REQUEST_CODE_PICK_IMAGE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constant.REQUEST_CODE_PICK_IMAGE:
                if (resultCode == RESULT_OK) {
                    ArrayList<ImageFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_IMAGE);
                    Uri file = Uri.fromFile(new File(list.get(0).getPath()));
                    String fileName = list.get(0).getName();
                    Uri fileUri = Uri.fromFile(new File(list.get(0).getPath()));
                    String mimeType = DataTypes.mimeType.image;
                    String contentType = DataTypes.contentType.image;
                    dataModel.add(new NewPostDataModel(fileName, fileUri, null, null, mimeType, contentType));
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(new File(file.getPath()).getAbsolutePath(), options);
                    float imageHeight = options.outHeight;
                    float imageWidth = options.outWidth;
                    Log.d(TAG, "imageWidth: " + imageWidth);
                    Log.d(TAG, "imageHeight: " + imageHeight);
                    saveDatasToDataBase(contentType, mimeType, ConservationController.this, otherUser, currentUser, file, new StringCompletion() {
                        @Override
                        public void getString(String url) {
                            Log.d(TAG, "getString: url : " + url);
                            MessageService.shared().sendTextMsg(currentUser, otherUser, url, isOnline, Calendar.getInstance().getTimeInMillis(), geoPoint, 0, imageWidth, imageHeight, url, String.valueOf(Calendar.getInstance().getTimeInMillis()), MessageType.photo);
                            TipDialog.show(ConservationController.this, "Dosya Gönderildi", TipDialog.TYPE.SUCCESS);
                            TipDialog.dismiss(500);
                        }
                    });
                }
                break;

        }
    }

    //TODO:: upload images

    private void saveDatasToDataBaseAudio(String contentType, String mimeType, Activity activity, OtherUser otherUser, CurrentUser currentUser, Uri data,
                                     SavedAudioFileUrl completion) {
        hud.show();

        String dataName = String.valueOf(Calendar.getInstance().getTimeInMillis());
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType(contentType)
                .build();
        StorageReference ref = FirebaseStorage.getInstance().getReference().child("messages")
                .child(currentUser.getUid())
                .child(otherUser.getUid())
                .child(dataName + mimeType);

        uploadTask = ref.putFile(data, metadata).addOnCompleteListener(activity, new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {

                    task.getResult().getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();
                            Log.d(TAG, "onComplete: " + url);
                            hud.dismiss();

                            completion.callback(url,dataName+".m4a");


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
        });

    }


    private void saveDatasToDataBase(String contentType, String mimeType, Activity activity, OtherUser otherUser, CurrentUser currentUser, Uri data,
                                     StringCompletion completion) {
        hud.show();

        String dataName = String.valueOf(Calendar.getInstance().getTimeInMillis());
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType(contentType)
                .build();
        StorageReference ref = FirebaseStorage.getInstance().getReference().child("messages")
                .child(currentUser.getUid())
                .child(otherUser.getUid())
                .child(dataName + mimeType);

        uploadTask = ref.putFile(data, metadata).addOnCompleteListener(activity, new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {

                    task.getResult().getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();
                            Log.d(TAG, "onComplete: " + url);
                            hud.dismiss();

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
        });

    }

    public boolean CheckPermissions() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void RequestPermissions() {
        ActivityCompat.requestPermissions(ConservationController.this, new String[]{RECORD_AUDIO, WRITE_EXTERNAL_STORAGE}, REQUEST_AUDIO_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_AUDIO_PERMISSION_CODE:
                if (grantResults.length > 0) {
                    boolean permissionToRecord = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean permissionToStore = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (permissionToRecord && permissionToStore) {
                        try {
                            Helper.shared().RecorderBottomSheet(ConservationController.this, Environment.getExternalStorageDirectory().getAbsolutePath() + "/recording.3gp", new RecordedAudioCallback() {
                                @Override
                                public void callback(File filePath) {
                                    Uri uri = Uri.fromFile(filePath);
                                    int duration = (int) ((getDuration(filePath) % (1000 * 60 * 60)) % (1000 * 60) / 1000);
                                    saveDatasToDataBaseAudio(DataTypes.mimeType.audio,
                                            DataTypes.contentType.audio, ConservationController.this, otherUser, currentUser, uri, new SavedAudioFileUrl() {
                                                @Override
                                                public void callback(String url, String filename) {
                                                    MessageService.shared().sendTextMsg(currentUser,otherUser,filename,isOnline,Calendar.getInstance().getTimeInMillis(),null,duration,0f,0f,url,String.valueOf(Calendar.getInstance().getTimeInMillis()),MessageType.audio);
                                                }


                                            });
                                }

                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    private Handler mHandler = new Handler();
    static MediaPlayer mediaPlayer = new MediaPlayer();
    private Runnable runnable;
    @Override
    public void onItemClick(ImageButton b, SeekBar seekBar, TextView timer,ProgressBar waitProgress, View view, MessagesModel model, int position) {
        if (runnable !=null){
            mHandler.removeCallbacks(runnable);
        }


        if (lastPostion == -1){
            if (mediaPlayer!=null){

                mediaPlayer.stop();

                mediaPlayer.reset();
                mediaPlayer = null;
            }
            lastPostion = position;
            try {
                mediaPlayer= new MediaPlayer();

                File ExistingFile =  new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_MUSIC) + "/vayeapp/" + model.getFileName());
                if (ExistingFile.exists()){
                    mediaPlayer.setDataSource(ExistingFile.getAbsolutePath());
                    Log.d(TAG, "file name: "+ExistingFile.getAbsolutePath());
                }else{
                    waitProgress.setVisibility(View.VISIBLE);
                    b.setVisibility(View.INVISIBLE);
                    downloadAudio( model.getContent(), model.getFileName(), new TrueFalse<Boolean>() {
                        @Override
                        public void callBack(Boolean _value) {
                            if (_value){
                                waitProgress.setVisibility(View.GONE);
                                b.setVisibility(View.VISIBLE);
                            }
                        }
                    });

                }

                //mediaPlayer.prepare();
                mediaPlayer.prepareAsync();
                mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        Log.e(TAG, "onError: " + what);
                        return false;
                    }
                });

                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        Log.i(TAG, "onCompletion: Completed");
                        b.setImageResource(R.drawable.play_button);

                        seekBar.setProgress(0);
                        lastPostion = -1;
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        mediaPlayer = null;
                    }
                });

                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        Log.i(TAG, "onPrepared: Prepared MediaPlayer");
                        if (mediaPlayer.isPlaying()){
                            mediaPlayer.stop();
                            mediaPlayer.start();
                            runnable = new Runnable() {
                                @Override
                                public void run() {
                                    if (mediaPlayer!=null){
                                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                                        seekBar.setProgress(mCurrentPosition);
                                    }
                                    mHandler.postDelayed(runnable,1000);
                                }
                            };
                            runnable.run();
                        }else{
                            mediaPlayer.start();
                            runnable = new Runnable() {
                                @Override
                                public void run() {
                                    if (mediaPlayer!=null){
                                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                                        seekBar.setProgress(mCurrentPosition);


                                    }

                                  mHandler.postDelayed(runnable,1000);
                                }
                            };
                            runnable.run();
                        }

                        b.setImageResource(R.drawable.pause);



                    }
                });


            }catch (Exception ex){
                Log.d(TAG, "exception : " + ex.getLocalizedMessage());
                Log.d(TAG, "exception : " + ex.getCause());
            }
            //play sound
        }else if (lastPostion == position){

            if (mediaPlayer != null){
                if (mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                    b.setImageResource(R.drawable.play_button);
                    b.setImageResource(R.drawable.play_button);



                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            Log.i(TAG, "onCompletion: Completed");
                            b.setImageResource(R.drawable.play_button);
                            seekBar.setProgress(0);
                            lastPostion = -1;
                        }
                    });

                }else{
                    b.setImageResource(R.drawable.pause);
                    mediaPlayer.start();
                    runnable = new Runnable() {
                        @Override
                        public void run() {
                            if (mediaPlayer!=null){
                                int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                                seekBar.setProgress(mCurrentPosition);

                            }

                            mHandler.postDelayed(runnable,1000);
                        }
                    };
                    runnable.run();
                }

            }

        }else if (position != lastPostion){
            if (mediaPlayer.isPlaying()){
                b.setImageResource(R.drawable.play_button);
                mediaPlayer.stop();
                seekBar.setProgress(0);
            }
            ImageButton imageButton = (ImageButton)list.findViewHolderForAdapterPosition(lastPostion).itemView.findViewById(R.id.playButton);
            imageButton.setImageResource(R.drawable.play_button);
            SeekBar mainSeekBar = (SeekBar)list.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.seekBar);
            lastPostion = -1;

            mHandler.removeCallbacks(runnable);
            return;
           /*if (mediaPlayer!=null){
                try{
                    mediaPlayer.pause();
                    mediaPlayer.reset();
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                    Log.d(TAG, "release: relase");
                }catch (Exception exception){
                    Log.d(TAG, "release: "+exception.getCause());
                    Log.d(TAG, "release: "+exception.getLocalizedMessage());
                }
                try {
                    mediaPlayer = new MediaPlayer();
                    File ExistingFile =  new File(Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_MUSIC) + "/vayeapp/" + model.getFileName());
                    if (ExistingFile.exists()){
                        mediaPlayer.setDataSource(ExistingFile.getAbsolutePath());
                    }else{
                        waitProgress.setVisibility(View.VISIBLE);
                        b.setVisibility(View.INVISIBLE);
                        downloadAudio( model.getContent(), model.getFileName(), new TrueFalse<Boolean>() {
                            @Override
                            public void callBack(Boolean _value) {
                                Log.d(TAG, "callBack: " + _value);
                                if (_value){
                                    waitProgress.setVisibility(View.GONE);
                                    b.setVisibility(View.VISIBLE);
                                }
                            }
                        });

                        // mediaPlayer.setDataSource(model.getContent());
                    }

                    mediaPlayer.prepareAsync();
                    mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                        @Override
                        public boolean onError(MediaPlayer mp, int what, int extra) {
                            Log.e(TAG, "onError: " + what);
                            return false;
                        }
                    });

                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            Log.i(TAG, "onCompletion: Completed");
                            b.setImageResource(R.drawable.play_button);
                            seekBar.setProgress(0);
                            lastPostion = -1;
                        }
                    });

                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            Log.i(TAG, "other song: Prepared MediaPlayer");
                            mp.start();
                            b.setImageResource(R.drawable.pause);

                            runnable = new Runnable() {
                                @Override
                                public void run() {
                                    if (mediaPlayer!=null){
                                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                                        seekBar.setProgress(mCurrentPosition);
                                    }

                                    mHandler.postDelayed(this::run,1000);
                                }
                            };
                            runnable.run();
                            mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                                @Override
                                public void onSeekComplete(MediaPlayer arg0) {
                                    Log.d(TAG, "onSeekComplete() current pos : " + arg0.getCurrentPosition());
                                    mediaPlayer.seekTo(arg0.getCurrentPosition());
                                    SystemClock.sleep(200);
                                    mediaPlayer.start();
                                }
                            });

                        }
                    });

                }catch (Exception ex){
                    Log.d(TAG, "onItemClick: " + ex.getStackTrace());
                }
            }*/


        }
    }
    String secondToMinutes(int totalSecs){

       int minutes = 0;
       int seconds = 0;
        minutes = (totalSecs % 3600) / 60;
        seconds = totalSecs % 60;

        return String.format("%02d:%02d", minutes, seconds);
    }

     void downloadAudio( String fileUrl , String fileName , TrueFalse<Boolean> callback){


        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                File direct = new File(Environment.getExternalStorageDirectory()
                        + "vayeapp");
                if (!direct.exists()) {
                    direct.mkdirs();
                }
                File ExistingFile =  new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_MUSIC) + "/vayeapp/" + fileName);
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

                               .setDestinationInExternalPublicDir(Environment.DIRECTORY_MUSIC,"vayeapp/"+ fileName);


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
                                           if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                                               downloading = false;
                                           }
                                           if (!downloading){
                                               Log.d(TAG, "run: download complete");
                                               callback.callBack(true);
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

}
