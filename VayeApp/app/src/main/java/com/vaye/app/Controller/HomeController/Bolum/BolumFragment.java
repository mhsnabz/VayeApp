package com.vaye.app.Controller.HomeController.Bolum;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.vaye.app.Controller.HomeController.HomeActivity;
import com.vaye.app.Controller.HomeController.LessonPostAdapter.LessonPostAdapter;
import com.vaye.app.Interfaces.CurrentUserService;
import com.vaye.app.Interfaces.LessonPostModelCompletion;
import com.vaye.app.Interfaces.StringArrayListInterface;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.LessonPostModel;
import com.vaye.app.R;
import com.vaye.app.Services.MajorPostService;
import com.vaye.app.Services.UserService;

import java.util.ArrayList;


public class BolumFragment extends Fragment {

    String TAG = "BolumFragment";
    View rootView;
    CurrentUser currentUser;
    FloatingActionButton newPost;
    RecyclerView postList;
    ArrayList<LessonPostModel> lessonPostModels;
    ArrayList<String> postIds;
    DocumentSnapshot lastPage;
    LessonPostAdapter adapter;
    LinearLayoutManager layoutManager
            = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
    public BolumFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        HomeActivity activity = (HomeActivity) getActivity();

        currentUser = activity.getIntent().getParcelableExtra("currentUser");

        lessonPostModels = new ArrayList<>();
        getPostId(currentUser, new StringArrayListInterface() {
            @Override
            public void getArrayList(ArrayList<String> list) {
               if (!list.isEmpty()){
                   for(int i = 0; i<list.size() ; i++){
                       getLessonPost(currentUser, list.get(i), new LessonPostModelCompletion() {
                           @Override
                           public void onCallback(LessonPostModel postModels) {
                               lessonPostModels.add(postModels);
                               Log.d(TAG, "onCallback: " + postModels.getText());
                               adapter.notifyDataSetChanged();
                           }
                       });
                   }

               }
            }
        });



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
            rootView = inflater.inflate(R.layout.fragment_bolum, container, false);
            newPost = (FloatingActionButton)rootView.findViewById(R.id.newPostButton);
            postList = (RecyclerView)rootView.findViewById(R.id.majorPost);
            postList.setLayoutManager(layoutManager);
            newPost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setNewPost();
                }
            });

        adapter = new LessonPostAdapter(lessonPostModels , currentUser , getActivity());
        postList.setAdapter(adapter);

            return rootView;
    }

    private void setNewPost(){
        Toast.makeText(getActivity(),"New Post" , Toast.LENGTH_SHORT).show();
    }
    private void getPostId(CurrentUser currentUser , StringArrayListInterface result){
        postIds = new ArrayList<>();
        Query db = FirebaseFirestore.getInstance().collection("user")
                .document(currentUser.getUid())
                .collection("lesson-post")
                .limitToLast(5)
                .orderBy("postId" , Query.Direction.DESCENDING);
        db.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().isEmpty()){
                        result.getArrayList(postIds);
                    }else{

                        for (DocumentSnapshot item : task.getResult().getDocuments()){
                            postIds.add(item.getId());
                            lastPage =  task.getResult().getDocuments().get(task.getResult().size() - 1);
                            System.out.println("last page ->" + lastPage.getId());
                        }
                        result.getArrayList(postIds);
                    }
                }
            }
        });
    }
    private void getLessonPost(CurrentUser currentUser , String postIds , LessonPostModelCompletion result){
        CollectionReference ref = FirebaseFirestore.getInstance().collection(currentUser.getShort_school())
                .document("lesson-post")
                .collection("post");
        ref.document(postIds).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isComplete()){

                    if (task.getResult().exists()){
                        result.onCallback(task.getResult().toObject(LessonPostModel.class));
                    }
                }
            }
        });

    }
}