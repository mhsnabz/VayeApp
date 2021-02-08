package com.vaye.app.Controller.VayeAppController;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.vaye.app.Controller.HomeController.Bolum.BolumFragment;
import com.vaye.app.Controller.HomeController.School.SchoolFragment;
import com.vaye.app.Controller.VayeAppController.BuySell.BuySellFragment;
import com.vaye.app.Controller.VayeAppController.Camping.CampingFragment;
import com.vaye.app.Controller.VayeAppController.Followers.FollowersFragment;
import com.vaye.app.Controller.VayeAppController.FoodMe.FoodMeFragment;

public class VayeAppPager extends FragmentPagerAdapter {
    public VayeAppPager(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case  0 :
                FollowersFragment followersFragment = new FollowersFragment();
                return followersFragment;

            case  1:
                BuySellFragment buySellFragment = new BuySellFragment();
                return  buySellFragment;
            case 2:
                FoodMeFragment foodMeFragment = new FoodMeFragment();
                return  foodMeFragment;
            case 3:
                CampingFragment campingFragment = new CampingFragment();
                return  campingFragment;
            default: return null;
        }

    }

    @Override
    public int getCount() {
        return 4;
    }
}
