package com.example.hello_world.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.ListFragment;

import java.util.List;

public class RecordPagerAdapter extends FragmentPagerAdapter {

    List<Fragment>fragmentList;
    String[]title = {"公共课","专业课"};

    public RecordPagerAdapter(FragmentManager fm, List<Fragment>lf){
        super(fm);
        this.fragmentList = lf;
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return title[position];
    }
}
