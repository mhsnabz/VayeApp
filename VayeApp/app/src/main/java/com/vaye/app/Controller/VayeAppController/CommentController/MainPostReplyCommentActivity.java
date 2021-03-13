package com.vaye.app.Controller.VayeAppController.CommentController;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.hendraanggrian.appcompat.widget.SocialTextView;
import com.hendraanggrian.appcompat.widget.SocialView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.vaye.app.Model.CommentModel;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.MainPostModel;
import com.vaye.app.R;
import com.vaye.app.Util.Helper;
import com.vaye.app.Util.SwipeController;
import com.vaye.app.Util.SwipeControllerActions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainPostReplyCommentActivity extends AppCompatActivity {
    SwipeController swipeController = null;
    ImageButton sendMsg;
    EditText msgText;
    SocialTextView text;
    CommentModel comment;
    String TAG = "MainPostReplyCommentActivity";
    CurrentUser currentUser;
    MainPostModel postModel;
    Toolbar toolbar;
    TextView title;
    Boolean isLoadMore = true;
    DocumentSnapshot lastPage;
    String  firstPage;
    ImageButton likeBtn;
    CircleImageView profileImage;
    ProgressBar progressBar;
    SwipeRefreshLayout swipeRefreshLayout;
    TextView name , username ,time;
    ArrayList<CommentModel> comments = new ArrayList<>();
    MainPostCommentAdapter adapter ;
    RecyclerView commentList;
    Boolean scrollingToBottom = false;
    LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
    Button loadMoreButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_post_reply_comment);
        loadMoreButton = (Button)findViewById(R.id.loadMoreButton);
        loadMoreButton.setVisibility(View.GONE);
        text = (SocialTextView)findViewById(R.id.text);
        text.setOnMentionClickListener(new SocialView.OnClickListener() {
            @Override
            public void onClick(@NonNull SocialView view, @NonNull CharSequence textt) {
                Toast.makeText(MainPostReplyCommentActivity.this,textt,Toast.LENGTH_LONG).show();
            }
        });
        Bundle extras = getIntent().getExtras();
        Intent intentIncoming = getIntent();
        if (extras != null){
            swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeAndRefresh);

            currentUser = intentIncoming.getParcelableExtra("currentUser");
            postModel = intentIncoming.getParcelableExtra("post");
            comment = intentIncoming.getParcelableExtra("comment");
            setToolbar();

            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    loadMoreComment(postModel);
                }
            });
            configureUI(comment,currentUser , postModel);
            loadMoreButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    swipeRefreshLayout.setRefreshing(true);
                    loadMoreComment(postModel);
                }
            });


            sendMsg = (ImageButton)findViewById(R.id.send);
            msgText = (EditText)findViewById(R.id.msgText);
            sendMsg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendMsg();
                }
            });
        }else {
            finish();
        }
    }

    private void configureUI(CommentModel comment, CurrentUser currentUser, MainPostModel postModel) {
        text.setText(comment.getComment());
        for (String item : Helper.shared().getMentionedUser(comment.getComment())){
            Log.d(TAG, "configureUI: "+ item);
        }
        name = (TextView)findViewById(R.id.name);
        username = (TextView)findViewById(R.id.username);
        time = (TextView)findViewById(R.id.time);
        likeBtn = (ImageButton)findViewById(R.id.likeBtn);

        time.setText(Html.fromHtml("&#8226;")+ Helper.shared().setTimeAgo(comment.getTime()));
        name.setText(comment.getSenderName());
        username.setText(comment.getUsername());

        if (comment.getLikes().contains(currentUser.getUid())){
            likeBtn.setImageResource(R.drawable.like);
        }else{
            likeBtn.setImageResource(R.drawable.like_unselected);
        }
        profileImage = (CircleImageView)findViewById(R.id.profileImage);
        progressBar = (ProgressBar)findViewById(R.id.progress);
        if (comment.getSenderImage()!=null && !comment.getSenderImage().isEmpty()){
            Picasso.get().load(comment.getSenderUid())
                    .centerCrop()
                    .resize(128,128)
                    .placeholder(android.R.color.darker_gray)
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

        }
        commentList = (RecyclerView)findViewById(R.id.commentList);
        mLayoutManager.setReverseLayout(false);
        adapter = new MainPostCommentAdapter(comments,currentUser ,MainPostReplyCommentActivity.this , postModel);
        commentList.setLayoutManager(mLayoutManager);
        commentList.setAdapter(adapter);


        swipeController = new SwipeController(currentUser.getUid() , comments ,new SwipeControllerActions() {
            @Override
            public void onLeftClicked(int position) {
                super.onLeftClicked(position);
                msgText.append(" " + comments.get(position).getUsername() + " ");
            }

            @Override
            public void onRightClicked(int position) {
                super.onRightClicked(position);
                //Delete

                deleteComment(comment , comments.get(position).getCommentId(),currentUser);
                comments.remove(position);
                adapter.notifyItemRemoved(position);

            }
        });
        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
        itemTouchhelper.attachToRecyclerView(commentList);
        commentList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                swipeController.onDraw(c);
            }
        });

        getComment(currentUser);

        final View contentView = commentList;
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
                        scrollRecyclerViewToBottom(commentList);
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
    private void getComment(CurrentUser currentUser) {


        Query dbNext = FirebaseFirestore.getInstance().collection("main-post")
                .document("post")
                .collection("post")
                .document(comment.getPostId())
                .collection("comment-replied")
                .document("comment")
                .collection(comment.getCommentId()).limitToLast(10).orderBy("commentId", Query.Direction.ASCENDING);
        dbNext.addSnapshotListener(MainPostReplyCommentActivity.this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value.isEmpty()) {

                }else{
                    for (DocumentChange item : value.getDocumentChanges()){
                        if (item.getType().equals(DocumentChange.Type.ADDED))
                        {
                            comments.add(item.getDocument().toObject(CommentModel.class));

                            Collections.sort(comments, new Comparator<CommentModel>(){
                                public int compare(CommentModel obj1, CommentModel obj2) {

                                    return obj1.getCommentId().compareTo(obj2.getCommentId());

                                }

                            });
                            if (adapter!=null){

                                //   commentList.getLayoutManager().scrollToPosition(comments.size() - 1);
                                adapter.notifyDataSetChanged();
                            }
                            firstPage = comments.get(0).getCommentId();
                            if (comments.size() < 9){
                                loadMoreButton.setVisibility(View.GONE);
                            }else{
                                loadMoreButton.setVisibility(View.VISIBLE);
                            }

                        }
                    }
                    lastPage = value.getDocuments().get(value.getDocuments().size() - 1);

                }
            }
        });
    }

    private void deleteComment(CommentModel comment,String  commentID, CurrentUser currentUser) {

        DocumentReference ref = FirebaseFirestore.getInstance().collection("main-post")
                .document("post")
                .collection("post")
                .document(comment.getPostId())
                .collection("comment-replied")
                .document("comment")
                .collection(comment.getCommentId())
                .document(commentID);
        ref.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){

                    DocumentReference ref = FirebaseFirestore.getInstance().collection("main-post")
                            .document("post").collection("post").document(comment.getPostId())
                            .collection("comment").document(comment.getCommentId());
                    Map<String , Object> map = new HashMap<>();
                    map.put("replies", FieldValue.arrayRemove(commentID));
                    ref.set(map , SetOptions.merge());
                }
            }
        });
    }

    private void loadMoreComment(MainPostModel postModel) {
        if (lastPage == null){
            swipeRefreshLayout.setRefreshing(false);
            loadMoreButton.setVisibility(View.GONE);

            return;
        }else{

            Query dbNext = FirebaseFirestore.getInstance().collection("main-post")
                    .document("post")
                    .collection("post").document(comment.getPostId())
                    .collection("comment-replied")
                    .document("comment")
                    .collection(comment.getCommentId()).orderBy("commentId").endBefore(firstPage).limitToLast(5);
            dbNext.get().addOnCompleteListener(MainPostReplyCommentActivity.this, new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()){
                        if (!task.getResult().isEmpty()){
                            for (DocumentSnapshot item : task.getResult().getDocuments()){
                                comments.add(item.toObject(CommentModel.class));
                                Collections.sort(comments, new Comparator<CommentModel>(){
                                    public int compare(CommentModel obj1, CommentModel obj2) {

                                        return obj1.getTime().compareTo(obj2.getTime());

                                    }

                                });
                                adapter.notifyDataSetChanged();
                                swipeRefreshLayout.setRefreshing(false);
                                firstPage = comments.get(0).getCommentId();
                                loadMoreButton.setVisibility(View.VISIBLE);
                            }
                        }else{
                            swipeRefreshLayout.setRefreshing(false);
                            loadMoreButton.setVisibility(View.GONE);
                        }
                    }
                }
            });
        }
    }

    private void sendMsg() {

    }

    private void setToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        title = toolbar.findViewById(R.id.toolbar_title);
        toolbar.setTitle("");
        toolbar.setSubtitle("");
        title.setText("Yanıtlar");
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Helper.shared().back(MainPostReplyCommentActivity.this);
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Helper.shared().back(MainPostReplyCommentActivity.this);
    }
}