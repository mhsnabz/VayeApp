package com.vaye.app.LoginRegister;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.vaye.app.R;

import java.util.ArrayList;


public class TeacherSignUp extends Fragment {

    View rootView;
    Spinner spinner;
    MaterialEditText name , email , password;
    Button createAccount;
    String unvan;
    public TeacherSignUp() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_teacher_sign_up, container, false);
        configureUI();
        return rootView;
    }



    private void configureUI(){
        spinner = (Spinner)rootView.findViewById(R.id.spinner);
        name = (MaterialEditText)rootView.findViewById(R.id.name);
        email = (MaterialEditText)rootView.findViewById(R.id.email);
        password = (MaterialEditText)rootView.findViewById(R.id.password);
        createAccount = (Button)rootView.findViewById(R.id.signUp);
        ArrayAdapter<CharSequence> qual_adapter = ArrayAdapter.createFromResource(getContext(), R.array.unvan,R.layout.spnr_qualification);
        qual_adapter.setDropDownViewResource(R.layout.drpdn_qual);

        spinner.setAdapter(qual_adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (adapterView.getAdapter().getItem(i).equals("Ünvan Seçiniz")){
                    spinner.setBackgroundColor(Color.GRAY);
                    unvan = null;
                }else{
                    spinner.setBackgroundColor(Color.RED);
                    unvan = adapterView.getAdapter().getItem(i).toString();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }


    private String getShort_unvan(String unvan){
        if (unvan == null){
            return  null;
        }else{
            if (unvan.equals("Asistan")){
                return "Ast.";
            }else if (unvan.equals("Araştırma Görevlisi")){
                return "Ars. Gör.";
            }
            else if (unvan.equals("Öğretim Görevlisi")){
                return "Öğr. Gör.";
            }
            else if (unvan.equals("Doktor")){
                return "Dr.";
            }
            else if (unvan.equals("Doçent Doktor")){
                return "Doç. Dr.";
            }
            else if (unvan.equals("Profesör Doktor")){
                return "Prof. Dr.";
            }
            else {
                return null;
            }
        }


    }

    private void setCreateAccount(){

    }

}