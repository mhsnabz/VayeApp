package com.vaye.app.Controller.VayeAppController.BuySell;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.kongzue.dialog.v3.WaitDialog;
import com.vaye.app.Controller.HomeController.HomeActivity;
import com.vaye.app.Controller.VayeAppController.VayeAppActivity;
import com.vaye.app.Controller.VayeAppController.VayeAppAdapter.BuySellAdapter;
import com.vaye.app.Controller.VayeAppController.VayeAppAdapter.FoodMeAdapter;
import com.vaye.app.Controller.VayeAppController.VayeAppNewPostActivity;
import com.vaye.app.Interfaces.BlockOptionSelect;
import com.vaye.app.Interfaces.MainPostFollowers;
import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.MainPostModel;
import com.vaye.app.Model.MainPostTopicFollower;
import com.vaye.app.Model.OtherUser;
import com.vaye.app.R;
import com.vaye.app.Services.BlockService;
import com.vaye.app.Services.MainPostService;
import com.vaye.app.Services.UserService;
import com.vaye.app.Util.BottomSheetHelper.BottomSheetActionTarget;
import com.vaye.app.Util.Helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class BuySellFragment extends Fragment implements BlockOptionSelect {

    String TAG = "BuySellFragment";
    View rootView;
    CurrentUser currentUser;
    FloatingActionButton newPost;
    RecyclerView postList;
    ArrayList<MainPostModel> post;
    ArrayList<String> postIds;
    DocumentSnapshot lastPage;
    BuySellAdapter adapter;
    SwipeRefreshLayout swipeRefreshLayout;
    int currentItems, totalItems, scrollOutItems;
    Boolean isLoadMore = true;
    ProgressBar progressBar ;
    NestedScrollView scrollView;
    int totalAdsCount = 0;
    AdLoader adLoader;
    BlockOptionSelect optionSelect;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_buy_sell, container, false);
        optionSelect = this::onSelectOption;
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
                WaitDialog.show((AppCompatActivity) getActivity(), null);
                MainPostService.shared().getTopicFollowers(BottomSheetActionTarget.sell_buy, new MainPostFollowers() {
                    @Override
                    public void getTopicFollower(ArrayList<MainPostTopicFollower> list) {

                        Intent i = new Intent(getContext() , VayeAppNewPostActivity.class);
                        i.putExtra("followers",list);
                        i.putExtra("currentUser",currentUser);
                        i.putExtra("postType",BottomSheetActionTarget.al_sat);
                        getActivity().startActivity(i);

                        Helper.shared().go(getActivity());
                        WaitDialog.dismiss();
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
                            if (post.get(i).getType() !=null ){
                                if (post.get(i).getType().equals("ads"))
                                    totalAdsCount ++;
                            }
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

            Query db = FirebaseFirestore.getInstance().collection(currentUser.getShort_school())
                    .document("main-post")
                    .collection("sell-buy").orderBy("postId", Query.Direction.DESCENDING).orderBy("postID", Query.Direction.DESCENDING)
                    .limit(5).startAfter(lastPage);
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
                                            if (documentSnapshot.getString("senderUid") != null || !documentSnapshot.getString("senderUid").isEmpty()) {
                                                UserService.shared().checkBlock(documentSnapshot.getString("senderUid"), currentUser, new TrueFalse<Boolean>() {
                                                    @Override
                                                    public void callBack(Boolean _value) {
                                                        if (_value){
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
                                                            post.add(new MainPostModel("empty", Timestamp.now()));
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
                                                });
                                            }
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
        adapter = new BuySellAdapter(post , getActivity(), currentUser,optionSelect);
        postList.setAdapter(adapter);
        getAllPost(currentUser);

        if (!post.isEmpty()){

            if ((post.size() - totalAdsCount) % 5 == 0)
                getAds();
        }
    }
    private void deletePostId(String postId){
        DocumentReference db = FirebaseFirestore.getInstance().collection(currentUser.getShort_school())
                .document("main-post")
                .collection("sell-buy").document(postId);
        db.delete();
    }
    private void getAllPost(CurrentUser currentUser) {

        Query db = FirebaseFirestore.getInstance().collection(currentUser.getShort_school())
                .document("main-post")
                .collection("sell-buy").orderBy("postId", Query.Direction.DESCENDING).orderBy("postID", Query.Direction.DESCENDING)
                .limit(5);
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
                                        if (documentSnapshot.getString("senderUid") != null || !documentSnapshot.getString("senderUid").isEmpty()) {
                                            UserService.shared().checkBlock(documentSnapshot.getString("senderUid"), currentUser, new TrueFalse<Boolean>() {
                                                @Override
                                                public void callBack(Boolean _value) {
                                                    if (_value){
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
                                                        post.add(new MainPostModel("empty", Timestamp.now()));
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
                                            });
                                        }

                                    }
                                }
                            }).addOnFailureListener(getActivity(), new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });
                        }else{
                            swipeRefreshLayout.setRefreshing(false);
                            isLoadMore = false;
                        }
                    }
                }else{
                    swipeRefreshLayout.setRefreshing(false);
                    isLoadMore = false;
                    progressBar.setVisibility(View.GONE);
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
    private void getCurrent(){
        if (FirebaseAuth.getInstance().getCurrentUser().getUid() != null){
            DocumentReference ref = FirebaseFirestore.getInstance().collection("user").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
            ref.addSnapshotListener((VayeAppActivity)getActivity(),new EventListener<DocumentSnapshot>() {
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
    public void onStart() {
        super.onStart();
        getCurrent();
    }

    @Override
    public void onSelectOption(String target, OtherUser otherUser) {
        Log.d(TAG, "onSelectOption: " + target);
        Log.d(TAG, "onSelectOption: " + otherUser.getName());
        BlockService.shared().report((VayeAppActivity) getActivity(), target, currentUser, otherUser, new TrueFalse<Boolean>() {
            @Override
            public void callBack(Boolean _value) {

            }
        });
    }
}