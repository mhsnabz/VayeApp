package com.vaye.app.Controller.VayeAppController.Followers;

import android.os.Bundle;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.android.gms.ads.AdLoader;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.vaye.app.Controller.HomeController.LessonPostAdapter.MajorPostAdapter;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.LessonPostModel;
import com.vaye.app.R;

import java.util.ArrayList;


public class FollowersFragment extends Fragment {

    String TAG = "FollowersFragment";
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
    LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
    AdLoader adLoader;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_followers, container, false);
    }
}