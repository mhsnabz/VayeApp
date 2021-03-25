package com.vaye.app.Controller.ChatController.Conservation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.vaye.app.Controller.MapsController.LocationPermissionActivity;
import com.vaye.app.Controller.VayeAppController.VayeAppNewPostActivity;
import com.vaye.app.FCM.MessagingService;
import com.vaye.app.Interfaces.CompletionWithValue;
import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.LoginRegister.MessageType;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.MessagesModel;
import com.vaye.app.Model.OtherUser;
import com.vaye.app.R;
import com.vaye.app.Services.MessageService;
import com.vaye.app.Util.Helper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConservationController extends AppCompatActivity {
    String TAG = "ConservationController";
    private static final int ERROR_DIALOG_REQUEST = 9001;
    Boolean isOnline = false;
    CircleImageView profileImage;
    ProgressBar progressBar;
    TextView title;
    Toolbar toolbar;
    ImageButton options;
    LinearLayout mediaLayout;
    ImageButton mediaItem , soundRecorder;
    CurrentUser currentUser;
    OtherUser otherUser;
    ArrayList<MessagesModel> messagesList = new ArrayList<>();
    RecyclerView list;
    MessagesAdaper adaper;
    DocumentSnapshot lastPage;
    LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
    Boolean scrollingToBottom = false;
    String  firstPage;
    SwipeRefreshLayout swipeRefreshLayout;
    ImageButton sendButton;
    TextInputEditText msg_edittex;
    String filename = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conservation_controller);
        mediaLayout = (LinearLayout)findViewById(R.id.mediaLayout);
        mediaLayout.setVisibility(View.VISIBLE);
        sendButton = (ImageButton)findViewById(R.id.send);

        mediaItem = (ImageButton)findViewById(R.id.media);
        soundRecorder = (ImageButton)findViewById(R.id.audio);


        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMsg();
            }
        });
        msg_edittex = (TextInputEditText)findViewById(R.id.msgText);
        Bundle extras = getIntent().getExtras();
        Intent intentIncoming = getIntent();
        if (extras != null){
            currentUser = intentIncoming.getParcelableExtra("currentUser");
            otherUser = intentIncoming.getParcelableExtra("otherUser");
            setToolbar(otherUser);
            configureUI(otherUser);
            getAllMessages();
            swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeAndRefresh);
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    loadMoreMessages();
                }
            });
            mediaItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Helper.shared().MessageMediaLauncher(ConservationController.this,otherUser, currentUser, new TrueFalse<Boolean>() {
                        @Override
                        public void callBack(Boolean _value) {

                        }
                    });
                }
            });
        }
        targetChooser();
    }

    private void targetChooser(){

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("media_item_target"));
    }

    private void setToolbar(OtherUser otherUser) {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        title = toolbar.findViewById(R.id.toolbar_title);
        toolbar.setTitle("");
        toolbar.setSubtitle("");
        title.setText(otherUser.getName());
        profileImage = (CircleImageView)toolbar.findViewById(R.id.profileImage);
        progressBar = (ProgressBar)toolbar.findViewById(R.id.progress);
        options = (ImageButton)toolbar.findViewById(R.id.options);
        if (otherUser.getThumb_image()!=null && !otherUser.getThumb_image().isEmpty()){
            Picasso.get().load(otherUser.getThumb_image())
                    .resize(256,256)
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
        }else{
            progressBar.setVisibility(View.GONE);
            profileImage.setImageResource(android.R.color.darker_gray);
        }
        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ConservationController.this,"Options Click",Toast.LENGTH_SHORT).show();
            }
        });

        if (getSupportActionBar() != null){
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
    private void configureUI(OtherUser otherUser){
        list = (RecyclerView)findViewById(R.id.msgList);
        mLayoutManager.setReverseLayout(false);
        list.setLayoutManager(mLayoutManager);
        list.setHasFixedSize(true);
        adaper = new MessagesAdaper(currentUser,otherUser,this,messagesList);
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
                }
                else {
                    // keyboard is closed
                    scrollingToBottom = false;
                }
            }
        });
    }
    private  void scrollRecyclerViewToBottom(RecyclerView recyclerView) {
        RecyclerView.Adapter adapter = recyclerView.getAdapter();
        if (adapter != null && adapter.getItemCount() > 0) {
            recyclerView.scrollToPosition(adapter.getItemCount() - 1);
        }
    }
    private void loadMoreMessages(){
        if (lastPage == null){
            swipeRefreshLayout.setRefreshing(false);
            return;
        }else{
            Query dbNext =  FirebaseFirestore.getInstance().collection("messages")
                    .document(currentUser.getUid())
                    .collection(otherUser.getUid()).orderBy("id").endBefore(firstPage)
                    .limitToLast(5);
            dbNext.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()){
                        if (!task.getResult().getDocuments().isEmpty()){
                            for (DocumentSnapshot item : task.getResult().getDocuments()){
                                messagesList.add(item.toObject(MessagesModel.class));
                                Collections.sort(messagesList, new Comparator<MessagesModel>(){
                                    public int compare(MessagesModel obj1, MessagesModel obj2) {
                                        return obj1.getId().compareTo(obj2.getId());
                                    }

                                });
                            }
                            adaper.notifyDataSetChanged();
                            swipeRefreshLayout.setRefreshing(false);
                            firstPage = messagesList.get(0).getId();

                        }else{
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                }
            });
        }
    }
    private void getAllMessages(){

       Query db = FirebaseFirestore.getInstance().collection("messages")
                .document(currentUser.getUid())
                .collection(otherUser.getUid()).limitToLast(10).orderBy("id", Query.Direction.ASCENDING);
       db.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
           @Override
           public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (!value.isEmpty()){
                    for (DocumentChange item : value.getDocumentChanges()){
                        if (item.getType().equals(DocumentChange.Type.ADDED)){
                            messagesList.add(item.getDocument().toObject(MessagesModel.class));
                            adaper.notifyDataSetChanged();
                            if (item.getDocument().getString("senderUid").equals(currentUser.getUid())){
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

    private void sendMsg(){
        String msg = msg_edittex.getText().toString();
        long time = Calendar.getInstance().getTimeInMillis();
        msg_edittex.setText("");
        if (msg.isEmpty()){
            return;
        }else{
            String messageId = String.valueOf(Calendar.getInstance().getTimeInMillis());
            MessageService.shared().sendTextMsg(currentUser,otherUser,filename,isOnline,time,null,0,0f,0f,msg,messageId, MessageType.text);

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        DocumentReference ref = FirebaseFirestore.getInstance().collection("user")
                .document(otherUser.getUid());
        ref.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value.exists()){
                    otherUser = value.toObject(OtherUser.class);
                }
            }
        });

        MessageService.shared().setCurrentUserOnline(currentUser,otherUser,true);
        MessageService.shared().deleteBadge(currentUser,otherUser);

        DocumentReference isOnlineDb = FirebaseFirestore.getInstance().collection("user")
                .document(otherUser.getUid())
                .collection("msg-list")
                .document(currentUser.getUid());

        isOnlineDb.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value.exists()){
                    isOnline = value.getBoolean("isOnline");
                }
            }
        });

        scrollRecyclerViewToBottom(list);

    }

    @Override
    protected void onStop() {
        super.onStop();
        DocumentReference setCurrentUserOnline =  FirebaseFirestore.getInstance().collection("user")
                .document(currentUser.getUid())
                .collection("msg-list")
                .document(otherUser.getUid());
        Map<String , Object> map = new HashMap<>();
        map.put("isOnline",false);
        map.put("badgeCount",0);
        setCurrentUserOnline.set(map, SetOptions.merge());
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String target = intent.getStringExtra("target");
            if (target.equals(CompletionWithValue.send_image)){
             sendImage();
            }else if (target.equals(CompletionWithValue.send_location)){
                    sendLocation();
            }else if(target.equals(CompletionWithValue.send_document)){

            }
        }
    };


    private void sendImage(){

    }
    private void sendDocument(){

    }
    private void sendLocation(){
        if (isServicesOk()){
            Intent i = new Intent(ConservationController.this , LocationPermissionActivity.class);
            startActivity(i);
        }
    }

    public boolean isServicesOk(){
        Log.d(TAG, "isServicesOk: " +"check google service version");
        int avabile = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(ConservationController.this);
        if (avabile == ConnectionResult.SUCCESS){
            return true;
        }else if (GoogleApiAvailability.getInstance().isUserResolvableError(avabile))
        {
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(ConservationController.this ,avabile,ERROR_DIALOG_REQUEST);
            dialog.show();
            return  false;
        }else{

        }
        return  false;
    }
}