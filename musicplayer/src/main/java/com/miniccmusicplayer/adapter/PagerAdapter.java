package com.miniccmusicplayer.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by Hersch on 2016/3/27.
 */
public class PagerAdapter extends FragmentPagerAdapter {
    private List<Fragment>mFragmentList;
    private List<String>mTabList;
    public PagerAdapter(FragmentManager fm, List<Fragment> mFragmentList,List<String>mTabList) {
        super(fm);
        this.mFragmentList = mFragmentList;
        this.mTabList = mTabList;
    }
    public PagerAdapter(FragmentManager fm){
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTabList.get(position);
    }
}
