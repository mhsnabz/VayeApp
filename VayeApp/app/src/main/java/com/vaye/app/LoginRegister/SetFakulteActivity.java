package com.vaye.app.LoginRegister;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.kongzue.dialog.v3.WaitDialog;
import com.vaye.app.Controller.Profile.EditProfileActivity;
import com.vaye.app.LoginRegister.Adapter.FakulteAdapter;
import com.vaye.app.Model.SchoolModel;
import com.vaye.app.Model.TaskUser;
import com.vaye.app.R;
import com.vaye.app.Util.Helper;

import java.util.ArrayList;

public class SetFakulteActivity extends AppCompatActivity {
    Toolbar toolbar;
    TextView toolbarTitle;
    TaskUser taskUser;
    RecyclerView list;
    FakulteAdapter adapter;
    ArrayList<String> fakulteList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_fakulte);
        setToolbar();
        Bundle extras = getIntent().getExtras();
        Intent intentIncoming = getIntent();
        if (extras != null){
            taskUser = intentIncoming.getParcelableExtra("taskUser");

            configureUI(taskUser);
            getFakulte();

        }
    }
    private void getFakulte(){

        WaitDialog.show(SetFakulteActivity.this,"");
        CollectionReference db = FirebaseFirestore.getInstance().collection(taskUser.getShort_school())
                .document("fakulte").collection("fakulte");
        db.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().getDocuments().isEmpty()){
                        return;
                    }else{
                        for (DocumentSnapshot item : task.getResult().getDocuments()){
                            fakulteList.add(item.getId());
                            adapter.notifyDataSetChanged();
                        }

                        WaitDialog.dismiss();
                    }
                }
            }
        });
    }

    private void configureUI(TaskUser taskUser){

        list = (RecyclerView)findViewById(R.id.list);
        list.setHasFixedSize(true);
        list.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FakulteAdapter(fakulteList,this,taskUser);
        list.setAdapter(adapter);

    }
    private void setToolbar(){
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setSupportActionBar(toolbar);

        toolbar.setTitle("");
        toolbar.setSubtitle("");
        toolbarTitle = (TextView)toolbar.findViewById(R.id.toolbar_title);
        toolbarTitle.setText("Fakulteni Seç");

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Helper.shared().back(SetFakulteActivity.this);
            }
        });
    }
    @SuppressLint("WrongConstant")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_bar_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        searchView.setQueryHint("Fakulte Adı Giriniz");
        searchView.setInputType(InputType.TYPE_CLASS_TEXT);
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("TAG", "onQueryTextChange: " + newText);

                ArrayList<String> filtred = new ArrayList<>();

                for (String item : fakulteList){
                    if (item.toLowerCase().contains(newText.toLowerCase())){
                        filtred.add(item);
                    }
                }
                adapter.setList(filtred);
                adapter.notifyDataSetChanged();

                return true;
            }
        });

        return true;

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Helper.shared().back(SetFakulteActivity.this);
    }
}