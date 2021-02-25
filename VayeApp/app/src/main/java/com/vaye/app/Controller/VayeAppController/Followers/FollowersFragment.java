package com.vaye.app.Controller.VayeAppController.Followers;

import android.os.Bundle;

import androidx.annotation.NonNull;
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

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.vaye.app.Controller.HomeController.LessonPostAdapter.MajorPostAdapter;
import com.vaye.app.Controller.VayeAppController.VayeAppActivity;
import com.vaye.app.Controller.VayeAppController.VayeAppAdapter.BuySellAdapter;
import com.vaye.app.Controller.VayeAppController.VayeAppAdapter.FollowersAdapter;
import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.LessonPostModel;
import com.vaye.app.Model.MainPostModel;
import com.vaye.app.R;
import com.vaye.app.Util.Helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class FollowersFragment extends Fragment {

    String TAG = "FollowersFragment";
    View rootView;
    CurrentUser currentUser;
    FloatingActionButton newPost;
    RecyclerView postList;
    ArrayList<MainPostModel> post;
    ArrayList<String> postIds;
    DocumentSnapshot lastPage;
    FollowersAdapter adapter;
    SwipeRefreshLayout swipeRefreshLayout;
    int currentItems, totalItems, scrollOutItems;
    Boolean isLoadMore = true;
    ProgressBar progressBar ;
    NestedScrollView scrollView;
    int totalAdsCount = 0;
    AdLoader adLoader;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_followers, container, false);

        newPost = (FloatingActionButton)rootView.findViewById(R.id.newPostButton);

        postList = (RecyclerView)rootView.findViewById(R.id.majorPost);
        postList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        postList.setHasFixedSize(true);
        scrollView = (NestedScrollView)rootView.findViewById(R.id.nestedScroolView);
        progressBar = (ProgressBar)rootView.findViewById(R.id.progress);
        swipeRefreshLayout=(SwipeRefreshLayout)rootView.findViewById(R.id.swipeAndRefresh);
        VayeAppActivity activity = (VayeAppActivity) getActivity();
        MobileAds.initialize(getActivity(),getResources().getString(R.string.unit_id));
        currentUser = activity.getIntent().getParcelableExtra("currentUser");
        newPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Helper.shared().VayeAppChooseTargetBottomSheetLaunher(getActivity(), currentUser, new TrueFalse<Boolean>() {
                    @Override
                    public void callBack(Boolean _value) {
                        
                    }
                });
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
                    if (!post.isEmpty()){
                        for (int i = 0 ; i < post.size() ; i++){
                            if (post.get(i).getType().equals("ads"))
                                totalAdsCount ++;
                        }

                        if ((post.size() - totalAdsCount) % 5 == 0){
                            getAds();
                            progressBar.setVisibility(View.GONE);
                        }

                    }
                }
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getPost(currentUser);
            }
        });
        return rootView;
    }
    private void loadMoreItem(CurrentUser currentUser) {
        if (lastPage!=null){
            Query db = FirebaseFirestore.getInstance().collection("user").document(currentUser.getUid())
                    .collection("main-post")
                    .limit(5)
                    .orderBy("postId",Query.Direction.DESCENDING).startAfter(lastPage);
            db.get().addOnSuccessListener(getActivity(), new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if (!queryDocumentSnapshots.isEmpty()){
                        for (DocumentSnapshot item : queryDocumentSnapshots.getDocuments()){
                            if (item.exists()){
                                DocumentReference ref = FirebaseFirestore.getInstance().collection("main-post").document("post").collection("post")
                                        .document(item.getId());
                                ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if (documentSnapshot.exists()){
                                            post.add(documentSnapshot.toObject(MainPostModel.class));
                                            Collections.sort(post, new Comparator<MainPostModel>(){
                                                public int compare(MainPostModel obj1, MainPostModel obj2) {

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
                                            deletePostId(item.getId());
                                        }
                                    }
                                }).addOnFailureListener(getActivity(), new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });
                            }else{
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    }else{
                        progressBar.setVisibility(View.GONE);
                    }
                }
            });
        }
    }
    private void getPost(CurrentUser currentUser){
        swipeRefreshLayout.setRefreshing(true);
        post = new ArrayList<>();
        adapter = new FollowersAdapter(post , getActivity(), currentUser);
        postList.setAdapter(adapter);
        getAllPost(currentUser);

        if (!post.isEmpty()){

            if ((post.size() - totalAdsCount) % 5 == 0)
                getAds();
        }
    }
    private void deletePostId(String postId){
        DocumentReference db = FirebaseFirestore.getInstance().collection("user").document(currentUser.getUid())
                .collection("main-post").document(postId);
        db.delete();
    }
    private void getAllPost(CurrentUser currentUser) {
        ///user/VUSU6uA0odX7vuF5giXWbOUYzni1/main-post
        Query db = FirebaseFirestore.getInstance().collection("user").document(currentUser.getUid())
                .collection("main-post")
                .limit(5)
                .orderBy("postId",Query.Direction.DESCENDING);
        db.get().addOnSuccessListener(getActivity(), new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()){
                    for (DocumentSnapshot item : queryDocumentSnapshots.getDocuments()){
                        if (item.exists()){
                            Log.d(TAG, "onSuccess: "+ "doc ıd" + item.getId());
                            DocumentReference ref = FirebaseFirestore.getInstance().collection("main-post").document("post").collection("post")
                                    .document(item.getId());
                            ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if (documentSnapshot.exists()){
                                        Log.d(TAG, "onSuccess: " + documentSnapshot.getData());
                                        post.add(documentSnapshot.toObject(MainPostModel.class));
                                        Collections.sort(post, new Comparator<MainPostModel>(){
                                            public int compare(MainPostModel obj1, MainPostModel obj2) {

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
                            }).addOnFailureListener(getActivity(), new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });
                        }else{
                            deletePostId(item.getId());
                        }
                    }
                }else{

                }
            }
        });
        getAds();
    }
    private void getAds(){
        AdLoader.Builder builder = new AdLoader.Builder(getActivity(),getResources().getString(R.string.unit_id));

        adLoader = builder.forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
            @Override
            public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd)
            {
                if (!post.isEmpty()){
                    post.add(new MainPostModel("","","","",null,"","","","ads","",""
                            ,"","ads",0,post.get(post.size() -1).getPostTime(),null,null,null,null,null,null,unifiedNativeAd));

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
    @Override
    public void onDestroy() {
        super.onDestroy();
        for (int i = 0; i<post.size() ; i++){
            if  (post.get(i).getNativeAd() != null){
                post.get(i).getNativeAd().destroy();
            }
        }
    }
}