package com.vaye.app.Controller.HomeController.School;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vaye.app.Controller.HomeController.HomeActivity;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.R;

public class SchoolFragment extends Fragment {
    String TAG = "SchoolFragment";
    View rootView;
    CurrentUser currentUser;
    public SchoolFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_school, container, false);
        HomeActivity activity = (HomeActivity) getActivity();
        currentUser = activity.getIntent().getParcelableExtra("currentUser");
        Log.d(TAG, "onCreate: " + currentUser.getUsername());
        return rootView;
    }
}