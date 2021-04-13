package com.vaye.app.Controller.ChatController.Conservation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.rpc.Help;
import com.kongzue.dialog.v3.WaitDialog;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.MessagesModel;
import com.vaye.app.Model.OtherUser;
import com.vaye.app.R;
import com.vaye.app.Services.MessageService;
import com.vaye.app.Services.UserService;
import com.vaye.app.Util.Helper;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class RequestConservationActivity extends AppCompatActivity  implements MessagesAdaper.OnItemClickListener {
    CircleImageView profileImage;
    ProgressBar progressBar;
    TextView title;
    Toolbar toolbar;
    ImageButton options;
    CurrentUser currentUser;
    OtherUser otherUser;
    String firstPage;
    SwipeRefreshLayout swipeRefreshLayout;
    ArrayList<MessagesModel> messagesList = new ArrayList<>();
    RecyclerView list;
    MessagesAdaper adaper;
    DocumentSnapshot lastPage;
    LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
    Boolean scrollingToBottom = false;
    String TAG = "RequestConservationActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_conservation);
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

        }
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
                Helper.shared().MessageOptionsBottomSheetLauncaher(RequestConservationActivity.this, currentUser, otherUser);
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
                Helper.shared().back(RequestConservationActivity.this);
            }
        });
    }

    private void configureUI(OtherUser otherUser) {

        setSpannableText(findViewById(R.id.despcText));
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
    private void setSpannableText(TextView desp){
        SpannableStringBuilder builder = new SpannableStringBuilder();
        String name = otherUser.getName();
        SpannableString span_name = new SpannableString(name);
        span_name.setSpan(new ForegroundColorSpan(Color.BLACK),0,name.length(),0);
        builder.append(span_name);

        String username = otherUser.getUsername();
        SpannableString span_username = new SpannableString(username);
        span_username.setSpan(new ForegroundColorSpan(Color.GRAY),0,username.length(),0);
        builder.append(span_username);

         String text = " Size Mesaj Göndermek İstiyor.İsteği Kabul Ederseniz Arkadaş Listenize Eklecek";
        SpannableString span_text = new SpannableString(text);
        span_text.setSpan(new ForegroundColorSpan(Color.DKGRAY),0,text.length(),0);
        builder.append(span_text);
        desp.setText(builder,TextView.BufferType.SPANNABLE);

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
        final DownloadTask downloadTask = new DownloadTask(RequestConservationActivity.this);
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

    @Override
    protected void onStart() {
        super.onStart();
        getAllMessages();

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
        MessageService.shared().deleteRequestMessagesBadge(currentUser,otherUser);
        scrollRecyclerViewToBottom(list);
    }

    @Override
    protected void onStop() {
        super.onStop();
        DocumentReference setCurrentUserOnline = FirebaseFirestore.getInstance().collection("user")
                .document(currentUser.getUid())
                .collection("msg-request")
                .document(otherUser.getUid());
        Map<String, Object> map = new HashMap<>();
        map.put("badgeCount", 0);
        setCurrentUserOnline.update(map);
        Log.d(TAG, "onStop: onStop");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Helper.shared().back(RequestConservationActivity.this);
    }
    private Handler mHandler = new Handler();
    static MediaPlayer mediaPlayer = new MediaPlayer();
    private Runnable runnable;
    int lastPostion = -1;
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

    public void acceptRequest(View view) {
        WaitDialog.show(RequestConservationActivity.this,"Arkadaş Listesine Ekleniyor");
        UserService.shared().addAsMessegesFriend(currentUser, otherUser, new TrueFalse<Boolean>() {
            @Override
            public void callBack(Boolean _value) {
                if (_value){
                    UserService.shared().removeRequestBadgeCount(currentUser, otherUser, new TrueFalse<Boolean>() {
                        @Override
                        public void callBack(Boolean _value) {
                            if (_value){
                                finish();
                                Helper.shared().back(RequestConservationActivity.this);
                                WaitDialog.dismiss();
                            }
                        }
                    });
                }
            }
        });
    }

    public void deleteRequest(View view) {
        WaitDialog.show(RequestConservationActivity.this,"İstek Siliniyor");
        DocumentReference db = FirebaseFirestore.getInstance().collection("user")
                .document(currentUser.getUid())
                .collection("msg-request")
                .document(otherUser.getUid());
        db.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    finish();
                    Helper.shared().back(RequestConservationActivity.this);
                }
            }
        });
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