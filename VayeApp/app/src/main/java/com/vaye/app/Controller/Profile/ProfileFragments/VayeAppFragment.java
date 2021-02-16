package com.vaye.app.Controller.Profile.ProfileFragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.OtherUser;
import com.vaye.app.R;


public class VayeAppFragment extends Fragment {
    int color;
    OtherUser otherUser;
    CurrentUser currentUser;
    String TAG = "VayeAppFragment";
    public VayeAppFragment(int color , OtherUser otherUser) {
        this.color = color;
        this.otherUser = otherUser;
    }

    public VayeAppFragment(int color , CurrentUser currentUser) {
        this.color = color;
        this.currentUser = currentUser;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (currentUser==null){
            Log.d(TAG, "onCreateView: " + "current user is nil");
        }

        return inflater.inflate(R.layout.fragment_vaye_app, container, false);
    }
}