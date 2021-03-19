package com.vaye.app.LoginRegister;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.vaye.app.Controller.HomeController.Bolum.BolumFragment;
import com.vaye.app.Controller.Profile.CurrentUserProfile;
import com.vaye.app.Controller.Profile.ProfileFragments.CurrentUserFragment.FavFragment;
import com.vaye.app.Controller.Profile.ProfileFragments.CurrentUserFragment.MajorPostFragment;
import com.vaye.app.Controller.Profile.ProfileFragments.CurrentUserFragment.SchoolFragment;
import com.vaye.app.Controller.Profile.ProfileFragments.CurrentUserFragment.VayeAppFragment;
import com.vaye.app.Controller.Profile.ProfileViewPager;
import com.vaye.app.Model.SchoolModel;
import com.vaye.app.R;
import com.vaye.app.Util.Helper;

public class RegisterActivity extends AppCompatActivity {
    SchoolModel model;
    ProfileViewPager adapter;
    ViewPager viewPager;
    Toolbar toolbar;
    TextView title;
    TabLayout tabLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regisyer);
        Bundle extras = getIntent().getExtras();
        Intent intentIncoming = getIntent();
        if (extras != null){

          model =  intentIncoming.getParcelableExtra("schoolModel");

            Bundle bundle = new Bundle();
            bundle.putParcelable("schoolModel",model);

            TeacherSignUp fragobj = new TeacherSignUp();
            fragobj.setArguments(bundle);
            StudentSignUp studentSignUp = new StudentSignUp();
            studentSignUp.setArguments(bundle);
            setToolbar(model);
            configureUI();
        }

    }

    private void setToolbar(SchoolModel model){
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        title = toolbar.findViewById(R.id.toolbar_title);

        toolbar.setTitle("");
        toolbar.setSubtitle("");
        title.setText(model.getShortName());

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Helper.shared().back(RegisterActivity.this);
            }
        });
    }
    private void configureUI(){
        viewPager = findViewById(R.id.htab_viewpager);
        tabLayout = findViewById(R.id.htab_tabs);
        tabLayout.setupWithViewPager(viewPager);
        adapter = new ProfileViewPager(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        adapter.addFrag(new StudentSignUp(model),"Öğrenci");
        adapter.addFrag(new TeacherSignUp(model),"Öğretim Görevlisi");

        viewPager.setAdapter(adapter);
    }
}