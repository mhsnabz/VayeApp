package com.vaye.app.Controller.HomeController.SinglePost;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.vaye.app.Controller.HomeController.StudentSetNewPost.StudentNewPostActivity;
import com.vaye.app.Model.CommentModel;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.LessonPostModel;
import com.vaye.app.R;
import com.vaye.app.Util.Helper;

import java.util.ArrayList;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

import static androidx.recyclerview.widget.ItemTouchHelper.DOWN;
import static androidx.recyclerview.widget.ItemTouchHelper.END;
import static androidx.recyclerview.widget.ItemTouchHelper.START;
import static androidx.recyclerview.widget.ItemTouchHelper.UP;

public class CommentActivity extends AppCompatActivity {
    ImageButton sendMsg;
    EditText msgText;
    CurrentUser currentUser;
    LessonPostModel postModel;
    Toolbar toolbar;
    TextView title;
    Boolean isLoadMore = true;

    SwipeRefreshLayout swipeRefreshLayout;
    ArrayList<CommentModel> comments = new ArrayList<>();
    CommentAdapter adapter ;
    RecyclerView commentList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);




        Bundle extras = getIntent().getExtras();
        Intent intentIncoming = getIntent();
        if (extras != null){
            swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeAndRefresh);

            currentUser = intentIncoming.getParcelableExtra("currentUser");
            postModel = intentIncoming.getParcelableExtra("post");
            setToolbar();

            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    getComment(currentUser , postModel);
                }
            });



            configureUI(currentUser , postModel);





        }else {
            finish();
        }
    }

    private void configureUI(CurrentUser currentUser , LessonPostModel postModel)
    {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(CommentActivity.this);
        mLayoutManager.setReverseLayout(false);
       // mLayoutManager.setStackFromEnd(true);
        commentList = (RecyclerView)findViewById(R.id.commentList);
        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0 , ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                switch (direction){
                    case ItemTouchHelper.RIGHT:
                        Toast.makeText(CommentActivity.this ,"Cevapla",Toast.LENGTH_SHORT).show();
                        break;
                    case ItemTouchHelper.LEFT:
                        Toast.makeText(CommentActivity.this ,"Sil",Toast.LENGTH_SHORT).show();
                        break;
                    default:break;
                }
            }


            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                new RecyclerViewSwipeDecorator.Builder(CommentActivity.this ,c ,recyclerView , viewHolder , dX,dX,actionState,isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(CommentActivity.this , R.color.red))
                        .addSwipeLeftActionIcon(R.drawable.delete_white)
                        .setSwipeLeftLabelColor(ContextCompat.getColor(CommentActivity.this,R.color.white))
                        .setSwipeRightLabelColor(ContextCompat.getColor(CommentActivity.this,R.color.white))
                        .addSwipeLeftLabel("Sil")
                        .addSwipeRightActionIcon(R.drawable.reply)
                        .addSwipeRightBackgroundColor(ContextCompat.getColor(CommentActivity.this,R.color.mainColor))
                        .addSwipeRightLabel("Cevapla")
                        .create().decorate();
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(commentList);
        adapter = new CommentAdapter(comments,currentUser ,CommentActivity.this);
        commentList.setLayoutManager(mLayoutManager);
        commentList.setAdapter(adapter);
        getComment(currentUser , postModel);

    }

    private void getComment(CurrentUser currentUser, LessonPostModel post) {

        Query db = FirebaseFirestore.getInstance().collection(currentUser.getShort_school())
                .document("lesson-post")
                .collection("post")
                .document(post.getPostId()).collection("comment").limitToLast(10).orderBy("postId", Query.Direction.DESCENDING);

        db.addSnapshotListener(CommentActivity.this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (value.isEmpty()) {

                    }else{
                        for (DocumentChange item : value.getDocumentChanges()){
                            if (item.getType().equals(DocumentChange.Type.ADDED))
                            {
                                comments.add(item.getDocument().toObject(CommentModel.class));
                                if (adapter!=null){
                                    adapter.notifyDataSetChanged();
                                    commentList.scrollToPosition(comments.size() - 1);
                                }


                            }else if (item.getType().equals(DocumentChange.Type.REMOVED)){
                               comments.remove(item.getDocument().toObject(CommentModel.class));
                                if (adapter!=null){
                                    adapter.notifyDataSetChanged();

                                }
                            }else if (item.getType().equals(DocumentChange.Type.MODIFIED)){
                              int index =  comments.indexOf(item.getDocument().get("commentId"));
                              comments.remove(index);
                                if (adapter!=null){
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        }
                    }
            }
        });
    }





    private void setToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        title = toolbar.findViewById(R.id.toolbar_title);
        toolbar.setTitle("");
        toolbar.setSubtitle("");
        title.setText("Yorumlar");
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Helper.shared().back(CommentActivity.this);
            }
        });
    }

    public void like(View view) {
    }

    public void disLike(View view) {
    }

    public void addFav(View view) {
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Helper.shared().back(CommentActivity.this);
    }
}