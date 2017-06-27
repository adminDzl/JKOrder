package com.example.ayou.jk_takeout.firstpage.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by kongweiwei on 2017/4/23.
 */
//主界面ViewPager适配器
/**
 * Created by kongweiwei on 2017/4/23.
 */
//主界面ViewPager适配器
public class MainFragmentStatePagerAdapter extends FragmentStatePagerAdapter {
    private List<Fragment> fragments;

    public MainFragmentStatePagerAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
    }
}
