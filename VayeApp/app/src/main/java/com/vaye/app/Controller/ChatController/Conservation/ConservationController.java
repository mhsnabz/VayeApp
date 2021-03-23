package com.vaye.app.Controller.ChatController.Conservation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.MessagesModel;
import com.vaye.app.Model.OtherUser;
import com.vaye.app.R;
import com.vaye.app.Util.Helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConservationController extends AppCompatActivity {

    CircleImageView profileImage;
    ProgressBar progressBar;
    TextView title;
    Toolbar toolbar;
    ImageButton options;
    LinearLayout mediaLayout;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conservation_controller);
        mediaLayout = (LinearLayout)findViewById(R.id.mediaLayout);
        mediaLayout.setVisibility(View.VISIBLE);

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
        }

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

}