package com.vaye.app.Controller.ChatController;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vaye.app.Model.CurrentUser;
import com.vaye.app.R;


public class RequestListFragment extends Fragment {


    CurrentUser currentUser;



    public RequestListFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ChatActivity activity = (ChatActivity) getActivity();
        currentUser = activity.getIntent().getParcelableExtra("currentUser");
        return inflater.inflate(R.layout.fragment_request_list, container, false);
    }
}