package com.vaye.app.Controller.NotificationController;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.rpc.Help;
import com.vaye.app.Controller.HomeController.LessonPostAdapter.MajorPostAdapter;
import com.vaye.app.Controller.NotificationService.PushNotificationService;
import com.vaye.app.Interfaces.CompletionWithValue;
import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.NotificationModel;
import com.vaye.app.R;
import com.vaye.app.Util.BottomNavHelper;
import com.vaye.app.Util.Helper;
import com.vaye.app.Util.NotifcationSwipeController;
import com.vaye.app.Util.SwipeController;
import com.vaye.app.Util.SwipeControllerActions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import q.rorbin.badgeview.QBadgeView;

public class NotificationActivity extends AppCompatActivity {
    CurrentUser currentUser;
    NotifcationSwipeController swipeController = null;
    Toolbar toolbar;
    TextView toolbarTitle;
    String TAG = "NotificationActivity";
    ImageButton options;
    SwipeRefreshLayout swipeRefreshLayout;
    DocumentSnapshot lastPage;
    ArrayList<NotificationModel> models;
    NotificationAdapter adapter;
    ProgressBar progressBar ;
    Boolean isLoadMore = true;
    NestedScrollView scrollView;
    RecyclerView postList;
    ImageButton setting;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        Bundle extras = getIntent().getExtras();
        Intent intentIncoming = getIntent();
        if (extras != null){
            currentUser = intentIncoming.getParcelableExtra("currentUser");
            setToolbar();
            Bundle bundle = new Bundle();
            bundle.putParcelable("currentUser",intentIncoming.getParcelableExtra("currentUser"));
            setupBottomNavBar(currentUser);
            swipeRefreshLayout  = (SwipeRefreshLayout)findViewById(R.id.swipeAndRefresh);

            progressBar = (ProgressBar)findViewById(R.id.progress);
            postList = (RecyclerView)findViewById(R.id.majorPost);
            postList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            postList.setHasFixedSize(true);
            scrollView = (NestedScrollView)findViewById(R.id.nestedScroolView);
            models = new ArrayList<>();
            adapter = new NotificationAdapter(models,currentUser,this);
            postList.setAdapter(adapter);

            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    getAllPost();
                }
            });

           getAllPost();
            configureRecylerViewDecoration(currentUser);

            scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    if ( isLoadMore &&  (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())){

                        progressBar.setVisibility(View.VISIBLE);
                        loadMoreItem(currentUser);
                        isLoadMore = false;
                        Log.d(TAG, "onScrollChange: "+"load more item");

                    }
                }
            });

        }
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("target_choose"));

    }

    private void getAllPost(){
        swipeRefreshLayout.setRefreshing(true);
        models = new ArrayList<>();
        adapter = new NotificationAdapter(models , currentUser , this);
        postList.setAdapter(adapter);
        getAllNotificaiton(currentUser);
    }

    private void loadMoreItem(CurrentUser currentUser){
        if (lastPage!=null){
            Query db = FirebaseFirestore.getInstance().collection("user")
                    .document(currentUser.getUid())
                    .collection("notification")
                    .limit(5).orderBy("not_id" , Query.Direction.DESCENDING).startAfter(lastPage);
            db.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()){
                        if (task.getResult().isEmpty()){
                            isLoadMore = false;
                            progressBar.setVisibility(View.GONE);
                        }else{
                            for (DocumentSnapshot item : task.getResult().getDocuments()){
                                models.add(item.toObject(NotificationModel.class));
                                Collections.sort(models, new Comparator<NotificationModel>(){
                                    public int compare(NotificationModel obj1, NotificationModel obj2) {
                                        return obj2.getTime().compareTo(obj1.getTime());
                                    }
                                });
                                adapter.notifyDataSetChanged();
                                swipeRefreshLayout.setRefreshing(false);
                                progressBar.setVisibility(View.GONE);
                                lastPage = task.getResult().getDocuments().get(task.getResult().getDocuments().size() - 1);
                                isLoadMore = true;
                            }
                        }
                    }else{
                        isLoadMore = false;
                        progressBar.setVisibility(View.GONE);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: "+e.getLocalizedMessage());
                }
            });
        }
    }
    private void getAllNotificaiton(CurrentUser currentUser){

        Query db = FirebaseFirestore.getInstance().collection("user")
                .document(currentUser.getUid())
                .collection("notification")
                .limit(15)
                .orderBy("not_id" , Query.Direction.DESCENDING);

        db.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().isEmpty()){
                        swipeRefreshLayout.setRefreshing(false);
                        progressBar.setVisibility(View.GONE);
                    }else{
                        for (DocumentSnapshot item : task.getResult().getDocuments()){

                            models.add(item.toObject(NotificationModel.class));

                           /* Collections.sort(models, new Comparator<NotificationModel>(){
                                public int compare(NotificationModel obj1, NotificationModel obj2) {
                                    return obj2.getTime().compareTo(obj1.getTime());
                                }
                            });*/
                            adapter.notifyDataSetChanged();
                            swipeRefreshLayout.setRefreshing(false);
                            progressBar.setVisibility(View.GONE);
                            lastPage = task.getResult().getDocuments().get(task.getResult().getDocuments().size() - 1);
                            isLoadMore = true;
                        }
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: "+e.getMessage());
            }
        });

    }
    private void configureRecylerViewDecoration(CurrentUser currentUser)
    {
        swipeController = new NotifcationSwipeController(currentUser.getUid(), models, new SwipeControllerActions() {
            @Override
            public void onLeftClicked(int position) {
                super.onLeftClicked(position);
                PushNotificationService.shared().makeReadLocalNotification(currentUser,models.get(position).getNot_id());
                models.get(position).setIsRead("true");
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onRightClicked(int position) {
                super.onRightClicked(position);
                PushNotificationService.shared().deleteLocalNotification(currentUser,models.get(position).getNot_id());
                models.remove(position);
                adapter.notifyDataSetChanged();

            }
        });
        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
        itemTouchhelper.attachToRecyclerView(postList);
        postList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                swipeController.onDraw(c);
            }
        });

    }
    private void setToolbar(){
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        toolbar.setSubtitle("");
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbarTitle = toolbar.findViewById(R.id.toolbar_title);
        options = (ImageButton)toolbar.findViewById(R.id.setting);
        toolbarTitle.setText("Bildirimler");
        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Helper.shared().LocalNotificationBottomSheetLauncher(NotificationActivity.this, currentUser, new TrueFalse<Boolean>() {
                    @Override
                    public void callBack(Boolean _value) {

                    }
                });
            }
        });
    }

    private void setupBottomNavBar(CurrentUser currentUser){
        BottomNavigationView navBar = (BottomNavigationView)findViewById(R.id.bottom_nav_bar);
        navBar.setElevation(5);
        BottomNavHelper.enableNavigation(this,navBar,currentUser);
        Menu menu = navBar.getMenu();
        MenuItem menuItem = menu.getItem(2);
        menuItem.setChecked(true);
        overridePendingTransition(0, 0);
       getBadgeCount();



    }

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String target = intent.getStringExtra("target");
            if (target.equals(CompletionWithValue.read_all_notificaiton)){
              for (NotificationModel model : models){
                    model.setIsRead("true");
                   adapter.notifyDataSetChanged();
              }
            }else if (target.equals(CompletionWithValue.delete_all_notification)){
                    models.clear();
                    adapter.notifyDataSetChanged();

            }
        }
    };

    private void getNotificaitonCount(){
        CollectionReference ref = (CollectionReference) FirebaseFirestore.getInstance().collection("user")
                .document(currentUser.getUid())
                .collection("notification").whereEqualTo("isRead","false");
        ref.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (value.isEmpty()){

                    }
            }
        });
    }

    private void getBadgeCount(){
        BottomNavigationView view;
        view=(BottomNavigationView)findViewById(R.id.bottom_nav_bar);
        BottomNavigationMenuView bottomNavigationMenuView =
                (BottomNavigationMenuView) view.getChildAt(0);
        final QBadgeView badge = new QBadgeView(this);
        final View v = bottomNavigationMenuView.getChildAt(2);

        Query ref =  FirebaseFirestore.getInstance().collection("user")
                .document(currentUser.getUid())
                .collection("notification").whereEqualTo("isRead","false");

        ref.addSnapshotListener(this,new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot.isEmpty()){
                    badge.hide(true);
                }else{
                    badge.bindTarget(v).setBadgeTextSize(14,true).setBadgePadding(7,true)
                            .setBadgeBackgroundColor(Color.RED).setBadgeNumber(documentSnapshot.getDocuments().size());;
                    if (documentSnapshot.getDocuments().size() < 1){
                        badge.hide(true);
                    }
                }
            }
        });

    }
}