package com.vaye.app.Controller.ChatController.ChatPagerAdapter;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.vaye.app.Controller.ChatController.ChatList.ChatListFragment;
import com.vaye.app.Controller.ChatController.FriendList.FriendListFragment;
import com.vaye.app.Controller.ChatController.RequestList.RequestListFragment;
import com.vaye.app.Model.CurrentUser;

public class ChatViewPagerAdapter extends FragmentPagerAdapter {

    CurrentUser currentUser;
    Activity activity;

    public ChatViewPagerAdapter(@NonNull FragmentManager fm ) {
        super(fm);


    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case  0 :
                ChatListFragment chatListFragment = new ChatListFragment();
                return chatListFragment;

            case  1:
                FriendListFragment friendListFragment = new FriendListFragment();
                return  friendListFragment;
            case 2:
                RequestListFragment requestListFragment = new RequestListFragment();
                return requestListFragment;
            default: return null;
        }

    }

    @Override
    public int getCount() {
        return 3;
    }
}
