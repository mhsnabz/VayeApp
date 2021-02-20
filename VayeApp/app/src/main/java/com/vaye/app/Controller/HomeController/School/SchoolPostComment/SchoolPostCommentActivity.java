package com.vaye.app.Controller.HomeController.School.SchoolPostComment;

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
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.vaye.app.Controller.VayeAppController.CommentController.MainPostCommentActivity;
import com.vaye.app.Controller.VayeAppController.CommentController.MainPostCommentAdapter;
import com.vaye.app.Controller.VayeAppController.CommentController.MainPostReplyCommentActivity;
import com.vaye.app.Interfaces.CallBackCount;
import com.vaye.app.Model.CommentModel;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.MainPostModel;
import com.vaye.app.Model.NoticesMainModel;
import com.vaye.app.R;
import com.vaye.app.Util.Helper;
import com.vaye.app.Util.SwipeController;
import com.vaye.app.Util.SwipeControllerActions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SchoolPostCommentActivity extends AppCompatActivity {
    SwipeController swipeController = null;
    ImageButton sendMsg;
    EditText msgText;
    String TAG = "SchoolPostCommentActivity";
    CurrentUser currentUser;
    NoticesMainModel postModel;
    Toolbar toolbar;
    TextView title;
    Boolean isLoadMore = true;
    DocumentSnapshot lastPage;
    String  firstPage;
    SwipeRefreshLayout swipeRefreshLayout;
    ArrayList<CommentModel> comments = new ArrayList<>();
    SchoolPostCommentAdapter adapter ;
    RecyclerView commentList;
    Boolean scrollingToBottom = false;

    LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
    Button loadMoreButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_post_comment);
        loadMoreButton = (Button)findViewById(R.id.loadMoreButton);
        loadMoreButton.setVisibility(View.GONE);
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
                    loadMoreComment(postModel);
                }
            });
            configureUI(currentUser , postModel);
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

    private void sendMsg() {

    }

    private void configureUI(CurrentUser currentUser, NoticesMainModel postModel) {
        commentList = (RecyclerView)findViewById(R.id.commentList);
        mLayoutManager.setReverseLayout(false);
        adapter = new SchoolPostCommentAdapter(comments,currentUser , SchoolPostCommentActivity.this , postModel);
        commentList.setLayoutManager(mLayoutManager);
        commentList.setAdapter(adapter);
        swipeController = new SwipeController(currentUser.getUid(), comments, new SwipeControllerActions() {
            @Override
            public void onLeftClicked(int position) {
                super.onLeftClicked(position);
                // currentUser = intentIncoming.getParcelableExtra("currentUser");
                //            postModel = intentIncoming.getParcelableExtra("post");
                //            comment = intentIncoming.getParcelableExtra("comment");
                Intent i = new Intent(SchoolPostCommentActivity.this , MainPostReplyCommentActivity.class);
                i.putExtra("currentUser",currentUser);
                i.putExtra("post",postModel);
                i.putExtra("comment",comments.get(position));
                startActivity(i);
                Helper.shared().go(SchoolPostCommentActivity.this);
            }

            @Override
            public void onRightClicked(int position) {

                super.onRightClicked(position);

                deleteComment(postModel, comments.get(position).getCommentId(), new CallBackCount() {
                    @Override
                    public void callBackCount(long count) {
                        postModel.setComment((int) count);
                        comments.remove(comments.get(position));
                        adapter.notifyDataSetChanged();

                        DocumentReference ref = FirebaseFirestore.getInstance().collection("main-post")
                                .document("post").collection("post").document(postModel.getPostId());
                        Map<String  , Object> map = new HashMap<>();
                        map.put("comment",count);
                        ref.set(map , SetOptions.merge());
                    }
                });
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
        getComment(currentUser , postModel);

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

    private void deleteComment(NoticesMainModel postModel, String commentId, CallBackCount count) {
    }

    private void getComment(CurrentUser currentUser, NoticesMainModel postModel) {

    }

    private void scrollRecyclerViewToBottom(RecyclerView commentList) {

    }

    private void loadMoreComment(NoticesMainModel postModel) {

    }

    private void setToolbar() {

    }
}