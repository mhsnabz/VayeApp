package com.vaye.app.Controller.Profile.ProfileFragments.CurrentUserFragment;

import android.content.Intent;
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
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.vaye.app.Controller.HomeController.LessonPostAdapter.MajorPostAdapter;
import com.vaye.app.Controller.HomeController.StudentSetNewPost.StudentChooseLessonActivity;
import com.vaye.app.Controller.Profile.OtherUserProfileActivity;
import com.vaye.app.Interfaces.BlockOptionSelect;
import com.vaye.app.Interfaces.LessonPostModelCompletion;
import com.vaye.app.Interfaces.StringArrayListInterface;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.LessonPostModel;
import com.vaye.app.Model.OtherUser;
import com.vaye.app.R;
import com.vaye.app.Util.Helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class MajorPostFragment extends Fragment implements BlockOptionSelect {

    int color;

    View rootView;
    String TAG = "MajorPostFragment";

    CurrentUser currentUser;
    OtherUser otherUser;
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
    public MajorPostFragment() {
        // Required empty public constructor
    }

    public MajorPostFragment(int color , CurrentUser currentUser) {
        this.color=color;
        this.currentUser = currentUser;
    }
    public MajorPostFragment(int color , OtherUser otherUser) {
        this.color=color;
        this.otherUser = otherUser;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView =inflater.inflate(R.layout.fragment_major_post, container, false);
                postList = (RecyclerView)rootView.findViewById(R.id.majorPost);
                optionSelect = this::onSelectOption;
        postList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        postList.setHasFixedSize(true);
        scrollView = (NestedScrollView)rootView.findViewById(R.id.nestedScroolView);
        progressBar = (ProgressBar)rootView.findViewById(R.id.progress);

        MobileAds.initialize(getActivity(),getResources().getString(R.string.unit_id));

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
                            if (lessonPostModels.get(i).getType().equals("ads"))
                                totalAdsCount ++;
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
                    .collection("lesson-post")
                    .limit(5).orderBy("postId" , Query.Direction.DESCENDING).startAfter(lastPage);



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
                                            }else {
                                                isLoadMore = false;
                                                deletePostId(currentUser , item.getId());
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
                .collection("my-post")
                .limit(5)
                .orderBy("postId" , Query.Direction.DESCENDING);

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





    @Override
    public void onDestroy() {
        super.onDestroy();
        for (int i = 0; i<lessonPostModels.size() ; i++){
            if  (lessonPostModels.get(i).getNativeAd() != null){
                lessonPostModels.get(i).getNativeAd().destroy();
            }
        }
    }

    @Override
    public void onSelectOption(String target, OtherUser otherUser) {

    }
}