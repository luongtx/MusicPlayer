package com.example.mymusicapp.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

public class AdapterViewPager extends FragmentStatePagerAdapter {

    private ArrayList<String> list_page_title;
    private ArrayList<Fragment> list_page_fragment;
    public AdapterViewPager(@NonNull FragmentManager fm) {
        super(fm);
        list_page_title = new ArrayList<>();
        list_page_fragment = new ArrayList<>();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
//        return list_page_title.get(position);
        return null;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return list_page_fragment.get(position);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return list_page_fragment.size();
    }

    public void addFragment(Fragment fragment, String title) {
        list_page_fragment.add(fragment);
        list_page_title.add(title);
    }

    public ArrayList<Fragment> get_list_fragment() {
        return list_page_fragment;
    }
}
