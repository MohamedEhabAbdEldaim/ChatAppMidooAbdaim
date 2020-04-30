package com.midooabdaim.midooabdaimchat.adapter;


import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.midooabdaim.midooabdaimchat.R;
import com.midooabdaim.midooabdaimchat.ui.fragment.homeCycle.viwePager.ChatsFragment;
import com.midooabdaim.midooabdaimchat.ui.fragment.homeCycle.viwePager.GroupsFragment;
import com.midooabdaim.midooabdaimchat.ui.fragment.homeCycle.viwePager.UsersFragment;

import java.util.ArrayList;

public class ViewPagerWithFragmentAdapter1 extends FragmentStateAdapter {
    private ArrayList<Fragment> fragments;
    public ArrayList<String> titles;


    public ViewPagerWithFragmentAdapter1(@NonNull Fragment fragment) {
        super(fragment);
        this.fragments = new ArrayList<>();
        this.titles = new ArrayList<>();

    }

    public void addPager(Fragment fragment, String title) {
        fragments.add(fragment);
        titles.add(title);
    }

    // Returns the fragment to display for that page
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragments.get(position);
    }

    // Returns total number of pages
    @Override
    public int getItemCount() {
        return fragments.size();
    }

}
