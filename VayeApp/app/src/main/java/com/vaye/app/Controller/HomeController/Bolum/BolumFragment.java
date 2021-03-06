package com.vaye.app.Controller.HomeController.Bolum;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.vaye.app.Controller.ChatController.ChatActivity;
import com.vaye.app.Controller.HomeController.HomeActivity;
import com.vaye.app.Controller.HomeController.LessonPostAdapter.MajorPostAdapter;
import com.vaye.app.Controller.HomeController.StudentSetNewPost.StudentChooseLessonActivity;
import com.vaye.app.Controller.HomeController.TeacherNewPost.TeacherChooseLesson;
import com.vaye.app.Interfaces.BlockOptionSelect;
import com.vaye.app.Interfaces.LessonPostModelCompletion;
import com.vaye.app.Interfaces.StringArrayListInterface;
import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.LessonModel;
import com.vaye.app.Model.LessonPostModel;
import com.vaye.app.Model.OtherUser;
import com.vaye.app.R;
import com.vaye.app.Services.BlockService;
import com.vaye.app.Services.UserService;
import com.vaye.app.Util.AdsHelper.AdUnifiedListeningg;
import com.vaye.app.Util.Helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class BolumFragment extends Fragment  implements  BlockOptionSelect {

    String TAG = "BolumFragment";
    View rootView;
    CurrentUser currentUser;
    FloatingActionButton newPost;
    RecyclerView postList;
    ArrayList<LessonPostModel> lessonPostModels;
    ArrayList<String> postIds;
    DocumentSnapshot lastPage;
    MajorPostAdapter adapter;
    SwipeRefreshLayout swipeRefreshLayout;
    int currentItems, totalItems, scrollOutItems;
    Boolean isLoadMore = true;
    ProgressBar progressBar ;
    NestedScrollView scrollView;
        int totalAdsCount = 0;
    AdLoader adLoader;
    BlockOptionSelect optionSelect;
    ListenerRegistration listenerRegistration;
    public BolumFragment() {

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
            rootView = inflater.inflate(R.layout.fragment_bolum, container, false);
            optionSelect = this::onSelectOption;
            newPost = (FloatingActionButton)rootView.findViewById(R.id.newPostButton);
            postList = (RecyclerView)rootView.findViewById(R.id.majorPost);
            postList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            postList.setHasFixedSize(true);
            scrollView = (NestedScrollView)rootView.findViewById(R.id.nestedScroolView);
            progressBar = (ProgressBar)rootView.findViewById(R.id.progress);
        HomeActivity activity = (HomeActivity) getActivity();
        MobileAds.initialize(getActivity(),getResources().getString(R.string.unit_id));
        currentUser = activity.getIntent().getParcelableExtra("currentUser");


            newPost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setNewPost();
                }
            });


        swipeRefreshLayout=(SwipeRefreshLayout)rootView.findViewById(R.id.swipeAndRefresh);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
               getPost(currentUser);
            }
        });

        getPost(currentUser);


        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if ( isLoadMore &&  (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())){

                   progressBar.setVisibility(View.VISIBLE);
                    loadMoreItem(currentUser);
                    isLoadMore = false;
                    Log.d(TAG, "onScrollChange: "+"load more item");
                    if (!lessonPostModels.isEmpty()){
                        for (int i = 0 ; i < lessonPostModels.size() ; i++){
                            if ( lessonPostModels.get(i).getType()!=null){
                                if (lessonPostModels.get(i).getType().equals("ads"))
                                    totalAdsCount ++;
                            }

                        }

                        if ((lessonPostModels.size() - totalAdsCount) % 5 == 0){
                           // if (!lessonPostModels.get(lessonPostModels.size() -1).getType().equals("ads"))
                            getAds();
                            progressBar.setVisibility(View.GONE);
                        }

                    }


                }
            }
        });

            return rootView;


    }


    public void createUnifiedAds(int unitid , AdUnifiedListeningg listening){
        AdLoader.Builder builder = new AdLoader.Builder(getContext(),getContext().getString(unitid));
        builder.forUnifiedNativeAd(listening);
        builder.withAdListener(listening);
        AdLoader adLoader = builder.build();
        adLoader.loadAd(new AdRequest.Builder().build());
        listening.setAdLoader(adLoader);

    }

    private void getPost(CurrentUser currentUser ){
        swipeRefreshLayout.setRefreshing(true);
        lessonPostModels = new ArrayList<>();
        adapter = new MajorPostAdapter(lessonPostModels , currentUser , getActivity(),optionSelect);
        postList.setAdapter(adapter);
        getAllPost(currentUser);

        if (!lessonPostModels.isEmpty()){

            if ((lessonPostModels.size() - totalAdsCount) % 5 == 0)
                getAds();
        }

    }

    private void loadMoreItem(CurrentUser currentUser){
        if (lastPage!=null){
           // isLoadMore = true;
            Query db = FirebaseFirestore.getInstance().collection("user")
                    .document(currentUser.getUid())
                    .collection("lesson-post").orderBy("postId", Query.Direction.DESCENDING).orderBy("postID", Query.Direction.DESCENDING)
                    .limit(5).startAfter(lastPage);



            db.get().addOnCompleteListener(getActivity(), new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()){
                        if (!task.getResult().isEmpty()){
                            for (DocumentSnapshot item : task.getResult().getDocuments()){
                                if (item.exists()){
                                    DocumentReference ref = FirebaseFirestore.getInstance().collection(currentUser.getShort_school())
                                            .document("lesson-post")
                                            .collection("post").document(item.getId());


                                    ref.get().addOnCompleteListener(getActivity(), new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task1)
                                        {
                                            if (task1.getResult().exists()){
                                                if (task1.getResult().getString("senderUid") !=null){
                                                    UserService.shared().checkBlock(task1.getResult().getString("senderUid"), currentUser, new TrueFalse<Boolean>() {
                                                        @Override
                                                        public void callBack(Boolean _value) {
                                                            if (_value){
                                                                lessonPostModels.add(task1.getResult().toObject(LessonPostModel.class));
                                                                Collections.sort(lessonPostModels, new Comparator<LessonPostModel>(){
                                                                    public int compare(LessonPostModel obj1, LessonPostModel obj2) {
                                                                        return obj2.getPostTime().compareTo(obj1.getPostTime());
                                                                    }

                                                                });
                                                                adapter.notifyDataSetChanged();
                                                                isLoadMore = true;
                                                                lastPage = task.getResult().getDocuments().get(task.getResult().getDocuments().size() - 1);

                                                                Log.d(TAG, "onSuccess: "+lastPage.getId());
                                                            }else{
                                                                lessonPostModels.add(new LessonPostModel("empty",Timestamp.now()));
                                                                Collections.sort(lessonPostModels, new Comparator<LessonPostModel>(){
                                                                    public int compare(LessonPostModel obj1, LessonPostModel obj2) {
                                                                        return obj2.getPostTime().compareTo(obj1.getPostTime());
                                                                    }

                                                                });
                                                                adapter.notifyDataSetChanged();
                                                                isLoadMore = true;
                                                                lastPage = task.getResult().getDocuments().get(task.getResult().getDocuments().size() - 1);

                                                                Log.d(TAG, "onSuccess: "+lastPage.getId());
                                                            }
                                                        }
                                                    });
                                                }


                                            }else {
                                                isLoadMore = false;
                                                deletePostId(currentUser , item.getId());

                                            }
                                        }
                                    }).addOnFailureListener(getActivity(), new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                        }
                                    }).addOnCanceledListener(getActivity(), new OnCanceledListener() {
                                        @Override
                                        public void onCanceled() {

                                        }
                                    });

                                }else{

                                    isLoadMore = false;
                                    deletePostId(currentUser , item.getId());
                                }
                            }
                        }else {
                            isLoadMore = false;
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                }
            }).addOnFailureListener(getActivity(), new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: "+e.getLocalizedMessage());
                    progressBar.setVisibility(View.GONE);
                }
            }).addOnCanceledListener(getActivity(), new OnCanceledListener() {
                @Override
                public void onCanceled() {
                    progressBar.setVisibility(View.GONE);
                }
            });



        }else{
            isLoadMore = false;
            progressBar.setVisibility(View.GONE);
        }


    }


    private void getAllPost(CurrentUser currentUser){
        Query db = FirebaseFirestore.getInstance().collection("user")
                .document(currentUser.getUid())
                .collection("lesson-post").orderBy("postId", Query.Direction.DESCENDING).orderBy("postID", Query.Direction.DESCENDING)
                .limit(5);

            db.get().addOnSuccessListener(getActivity(), new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if (!queryDocumentSnapshots.isEmpty()){
                        for (DocumentSnapshot item : queryDocumentSnapshots.getDocuments()){
                            if (item.exists()){
                                DocumentReference ref = FirebaseFirestore.getInstance().collection(currentUser.getShort_school())
                                        .document("lesson-post")
                                        .collection("post").document(item.getId());

                                ref.get().addOnSuccessListener(getActivity(), new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if (documentSnapshot.exists()){
                                            if (documentSnapshot.getString("senderUid") != null || !documentSnapshot.getString("senderUid").isEmpty()){
                                                UserService.shared().checkBlock(documentSnapshot.getString("senderUid"), currentUser, new TrueFalse<Boolean>() {
                                                    @Override
                                                    public void callBack(Boolean _value) {
                                                        if (_value){
                                                            lessonPostModels.add(documentSnapshot.toObject(LessonPostModel.class));
                                                            Collections.sort(lessonPostModels, new Comparator<LessonPostModel>(){
                                                                public int compare(LessonPostModel obj1, LessonPostModel obj2) {

                                                                    return obj2.getPostTime().compareTo(obj1.getPostTime());

                                                                }

                                                            });
                                                            adapter.notifyDataSetChanged();
                                                            swipeRefreshLayout.setRefreshing(false);
                                                            progressBar.setVisibility(View.GONE);
                                                            lastPage = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                                                            isLoadMore = true;
                                                            Log.d(TAG, "onSuccess: "+lastPage.getId());
                                                        }else{

                                                            lessonPostModels.add(new LessonPostModel("empty",Timestamp.now()));
                                                            Log.d(TAG, "callBack: " +"empty post added");
                                                            Log.d(TAG, "callBack: " +"empty post id: " +documentSnapshot.getId());
                                                            Collections.sort(lessonPostModels, new Comparator<LessonPostModel>(){
                                                                public int compare(LessonPostModel obj1, LessonPostModel obj2) {

                                                                    return obj2.getPostTime().compareTo(obj1.getPostTime());

                                                                }

                                                            });
                                                            adapter.notifyDataSetChanged();
                                                            swipeRefreshLayout.setRefreshing(false);
                                                            progressBar.setVisibility(View.GONE);
                                                            lastPage = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                                                            isLoadMore = true;
                                                            Log.d(TAG, "onSuccess: "+lastPage.getId());
                                                        }
                                                    }
                                                });
                                            }


                                        }else {
                                            deletePostId(currentUser , item.getId());
                                        }

                                    }
                                }).addOnFailureListener(getActivity(), new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "onFailure:  get all post" + e.getLocalizedMessage());
                                    }
                                });
                            }else{
                                Log.d(TAG, "onSuccess: empty");
                            }
                        }
                    }else{
                        isLoadMore = false;
                        swipeRefreshLayout.setRefreshing(false);
                        progressBar.setVisibility(View.GONE);
                    }

                }
            }).addOnFailureListener(getActivity(),new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: " + e.getLocalizedMessage());
                }
            });


            getAds();

    }

    private void deletePostId(CurrentUser currentUser , String postID){
        DocumentReference ref = FirebaseFirestore.getInstance().collection("user")
                .document(currentUser.getUid())
                .collection("lesson-post")
                .document(postID);
                ref.delete();
    }
    private void getAds(){
        AdLoader.Builder builder = new AdLoader.Builder(getActivity(),getResources().getString(R.string.unit_id));

        adLoader = builder.forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
            @Override
            public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd)
            {
                if (!lessonPostModels.isEmpty()){
                    lessonPostModels.add(new LessonPostModel("","","","","","","","","",null,null,null,null,null,null,null,0,
                            lessonPostModels.get(lessonPostModels.size() -1).getPostTime(),unifiedNativeAd,"","ads",""));
                    adapter.notifyDataSetChanged();
                }

            }
        }).withAdListener(new AdListener(){
            @Override
            public void onAdImpression() {
                super.onAdImpression();
            }

            @Override
            public void onAdClicked() {
                super.onAdClicked();
            }

            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                Log.d(TAG, "onAdFailedToLoad: "+loadAdError);
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
            }

        }).build();

        adLoader.loadAd(new  AdRequest.Builder().build());
    }

    private void setNewPost(){
        if (currentUser.getPriority().equals("student")){
            Intent i = new Intent(getActivity(),StudentChooseLessonActivity.class);
            i.putExtra("currentUser",currentUser);
            getActivity().startActivity(i);
            Helper.shared().go(getActivity());
        }else if (currentUser.getPriority().equals("teacher")){
            Intent i = new Intent(getActivity(), TeacherChooseLesson.class);
            i.putExtra("currentUser",currentUser);
            getActivity().startActivity(i);
            Helper.shared().go(getActivity());
        }

    }
    private void getPostId(CurrentUser currentUser , StringArrayListInterface result){
        postIds = new ArrayList<>();
        Query db = FirebaseFirestore.getInstance().collection("user")
                .document(currentUser.getUid())
                .collection("lesson-post")
                .limit(1)
                .orderBy("postId" , Query.Direction.DESCENDING);
        db.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().isEmpty()){
                        result.getArrayList(postIds);
                    }else{

                        for (DocumentSnapshot item : task.getResult().getDocuments()){
                            DocumentReference ref = FirebaseFirestore.getInstance().collection(currentUser.getShort_school())
                                    .document("lesson-post")
                                    .collection("post").document(item.getId());

                            ref.get().addOnSuccessListener(getActivity(), new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if (documentSnapshot.exists()){
                                        lessonPostModels.add(documentSnapshot.toObject(LessonPostModel.class));
                                        Log.d(TAG, "onSuccess: "+ documentSnapshot.getString("type"));
                                        adapter.notifyDataSetChanged();
                                        swipeRefreshLayout.setRefreshing(false);
                                        lastPage =  task.getResult().getDocuments().get(task.getResult().size() - 1);
                                    }else {
                                        deletePostId(currentUser , item.getId());
                                    }

                                }
                            }).addOnFailureListener(getActivity(), new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });

                        }
                        result.getArrayList(postIds);
                    }
                }
            }
        });
    }
    private void getLessonPost(CurrentUser currentUser , String postIds , LessonPostModelCompletion result){


    }


    @Override
    public void onStart() {
        super.onStart();
        getCurrent();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        for (int i = 0; i<lessonPostModels.size() ; i++){
         if  (lessonPostModels.get(i).getNativeAd() != null){
             lessonPostModels.get(i).getNativeAd().destroy();
         }
        }
    }

    private void getCurrent(){
        if (FirebaseAuth.getInstance().getCurrentUser().getUid() != null){
            DocumentReference ref = FirebaseFirestore.getInstance().collection("user").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
            ref.addSnapshotListener((HomeActivity)getActivity(),new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    if(error == null){
                        List<String> blockList = (List<String>)value.get("blockList");
                        List<String> blockByOtherUser = (List<String>)value.get("blockByOtherUser");
                        if (blockList != null && blockByOtherUser!=null){
                            if (!currentUser.getBlockList().equals(blockList) || !currentUser.getBlockByOtherUser().equals(blockByOtherUser) ){
                                currentUser = value.toObject(CurrentUser.class);
                                getPost(value.toObject(CurrentUser.class));
                            }
                        }

                    }
                }
            });
        }
    }


    @Override
    public void onSelectOption(String target, OtherUser otherUser) {
        Log.d(TAG, "onSelectOption: " + target);
        Log.d(TAG, "onSelectOption: " + otherUser.getName());
        BlockService.shared().report((HomeActivity) getActivity(), target, currentUser, otherUser, new TrueFalse<Boolean>() {
            @Override
            public void callBack(Boolean _value) {

            }
        });
    }
}