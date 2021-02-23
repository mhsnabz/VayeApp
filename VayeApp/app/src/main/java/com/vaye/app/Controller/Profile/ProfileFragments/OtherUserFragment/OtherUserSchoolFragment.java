package com.vaye.app.Controller.Profile.ProfileFragments.OtherUserFragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.OtherUser;
import com.vaye.app.R;


public class OtherUserSchoolFragment extends Fragment {
    CurrentUser currentUser;
    OtherUser otherUser;

    public OtherUserSchoolFragment(CurrentUser currentUser, OtherUser otherUser) {
        this.currentUser = currentUser;
        this.otherUser = otherUser;
    }

    public OtherUserSchoolFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_other_user_school, container, false);
    }
}