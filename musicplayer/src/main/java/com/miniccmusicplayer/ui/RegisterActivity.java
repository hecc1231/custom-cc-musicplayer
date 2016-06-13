package com.miniccmusicplayer.ui;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import com.example.hersch.musicplayer.R;
import com.miniccmusicplayer.adapter.PagerAdapter;
import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {
    private Toolbar mToolBar;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private List<String> mTabList;
    private List<Fragment> mFragmentList;
    private final int USER_INDEX = 0;
    private final int MOBILE_INDEX = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initViews();
        initTabPagerList();
    }

    public void initViews() {
        mToolBar = (Toolbar)findViewById(R.id.register_toolbar);
        mViewPager = (ViewPager)findViewById(R.id.register_viewpager);
        mTabLayout = (TabLayout)findViewById(R.id.register_tablayout);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);//设置左上角可见图标
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    void initTabPagerList() {
        //inti tabList
        mTabList = new ArrayList<String>();
        mTabList.add("用户名注册");
        mTabList.add("手机号注册");

        //init viewLists
        mFragmentList = new ArrayList<>();
        mFragmentList.add(new RegisterUserFragment());
        mFragmentList.add(new RegisterMobileFragment());
        //set the Mode
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
        //add Tab into TabLayout
        mTabLayout.addTab(mTabLayout.newTab().setText(mTabList.get(0)));
        mTabLayout.addTab(mTabLayout.newTab().setText(mTabList.get(1)));
        //设置标签点击响应
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        PagerAdapter mPagerAdapter = new PagerAdapter(getSupportFragmentManager(), mFragmentList, mTabList);
        mViewPager.setAdapter(mPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
