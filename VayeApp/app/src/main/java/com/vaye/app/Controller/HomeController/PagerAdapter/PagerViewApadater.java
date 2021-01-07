package com.vaye.app.Controller.HomeController.PagerAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.vaye.app.Controller.HomeController.Bolum.BolumFragment;
import com.vaye.app.Controller.HomeController.School.SchoolFragment;

public class PagerViewApadater extends FragmentPagerAdapter {
    public PagerViewApadater(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case  0 :
                BolumFragment bolumFragment = new BolumFragment();
                        return bolumFragment;

            case  1:
                SchoolFragment schoolFragment = new SchoolFragment();
                return  schoolFragment;
            default: return null;
        }

    }

    @Override
    public int getCount() {
        return 2;
    }
}
