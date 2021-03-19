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
import com.vaye.app.LoginRegister.Adapter.BolumAdapter;
import com.vaye.app.Model.BolumModel;
import com.vaye.app.Model.TaskUser;
import com.vaye.app.R;
import com.vaye.app.Util.Helper;

import java.util.ArrayList;
import java.util.Map;

public class SetBolumActivity extends AppCompatActivity {
    Toolbar toolbar;
    TextView toolbarTitle;
    TaskUser taskUser;
    RecyclerView list;
    BolumAdapter adapter ;
    String fakulte;
    ArrayList<BolumModel> bolumList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_bolum);
        setToolbar();
        Bundle extras = getIntent().getExtras();
        Intent intentIncoming = getIntent();
        if (extras != null){
            taskUser = intentIncoming.getParcelableExtra("taskUser");


            getBolum(intentIncoming.getStringExtra("fakulteName"));
            configureUI(taskUser,intentIncoming.getStringExtra("fakulteName"));
        }
    }
    private void getBolum(String fakulteName){

        WaitDialog.show(SetBolumActivity.this,"");
        DocumentReference db = FirebaseFirestore.getInstance().collection(taskUser.getShort_school())
                .document("fakulte").collection("fakulte").document(fakulteName);
        db.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    WaitDialog.dismiss();
                    Map<String, Object> map = task.getResult().getData();
                    for (Map.Entry<String, Object> entry : map.entrySet()) {
                        String k = entry.getKey();

                        System.out.println("Key: " + k + ", Value: " + entry.getValue().toString());
                        bolumList.add(new BolumModel(entry.getKey(),entry.getValue().toString()));
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });

    }

    private void configureUI(TaskUser taskUser,String fakulte){

        list = (RecyclerView)findViewById(R.id.list);
        list.setHasFixedSize(true);
        list.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BolumAdapter(fakulte,bolumList,this,taskUser);
       list.setAdapter(adapter);

    }
    private void setToolbar(){
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setSupportActionBar(toolbar);

        toolbar.setTitle("");
        toolbar.setSubtitle("");
        toolbarTitle = (TextView)toolbar.findViewById(R.id.toolbar_title);
        toolbarTitle.setText("Bölümünü Seç");

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Helper.shared().back(SetBolumActivity.this);
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
        searchView.setQueryHint("Bölüm Adı Giriniz");
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

                ArrayList<BolumModel> filtred = new ArrayList<>();

                for (BolumModel item : bolumList){
                    if (item.getValue().toLowerCase().contains(newText.toLowerCase())){
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
        Helper.shared().back(SetBolumActivity.this);
    }
}