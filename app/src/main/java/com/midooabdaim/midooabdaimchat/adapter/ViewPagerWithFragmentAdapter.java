package com.midooabdaim.midooabdaimchat.adapter;


import android.app.Activity;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.midooabdaim.midooabdaimchat.R;
import com.midooabdaim.midooabdaimchat.ui.fragment.homeCycle.viwePager.ChatsFragment;
import com.midooabdaim.midooabdaimchat.ui.fragment.homeCycle.viwePager.UsersFragment;

public class ViewPagerWithFragmentAdapter extends FragmentPagerAdapter {


    private Activity activity;

    public ViewPagerWithFragmentAdapter(FragmentManager fm, int behavior, Activity activity) {
        super(fm, behavior);
        this.activity = activity;
    }


    // Returns total number of pages
    @Override
    public int getCount() {
        return 2;
    }

    // Returns the fragment to display for that page
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new ChatsFragment();
            case 1:
                return new UsersFragment();

            default:
                return null;
        }
    }

    // Returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return activity.getString(R.string.chats);
            case 1:
                return activity.getString(R.string.users);
            default:
                return "null";
        }
    }


}
