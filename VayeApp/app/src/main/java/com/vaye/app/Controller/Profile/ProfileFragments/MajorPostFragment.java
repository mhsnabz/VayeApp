package com.vaye.app.Controller.Profile.ProfileFragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vaye.app.R;


public class MajorPostFragment extends Fragment {

    int color;

    public MajorPostFragment() {
        // Required empty public constructor
    }

    public MajorPostFragment(int color) {
        this.color=color;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_major_post, container, false);
    }
}