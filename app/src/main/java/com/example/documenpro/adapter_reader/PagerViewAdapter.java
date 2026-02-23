package com.example.documenpro.adapter_reader;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class PagerViewAdapter extends FragmentStatePagerAdapter {

    private final List<String> mFragmentTitleList_PagerView = new ArrayList<>();
    private final List<Fragment> mFragmentList_PagerView = new ArrayList<>();

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList_PagerView.get(position);
    }

    public void addFrag(Fragment fragment, String title) {
        mFragmentTitleList_PagerView.add(title);
        mFragmentList_PagerView.add(fragment);
    }

    @Override
    public int getCount() {
        return mFragmentList_PagerView.size();
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return mFragmentList_PagerView.get(position);
    }

    public PagerViewAdapter(FragmentManager manager) {
        super(manager);
    }
}