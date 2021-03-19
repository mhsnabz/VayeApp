package com.vaye.app.LoginRegister;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vaye.app.Model.SchoolModel;
import com.vaye.app.R;


public class StudentSignUp extends Fragment {

    SchoolModel model;

    public StudentSignUp(SchoolModel model) {
        this.model = model;
    }

    public StudentSignUp() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_student_sign_up, container, false);
    }
}