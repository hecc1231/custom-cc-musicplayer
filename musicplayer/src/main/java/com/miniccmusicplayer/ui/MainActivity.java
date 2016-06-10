package com.miniccmusicplayer.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.*;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hersch.musicplayer.R;
import com.miniccmusicplayer.adapter.PagerAdapter;
import com.miniccmusicplayer.view.CircleImageView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TextView nameText;
    private List<String> tabList;
    private List<Fragment> mFragmentList;
    private CircleImageView circleImageView;
    private static final int MUSIC = 0;
    private static final int SEARCH = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_ui);
        initViews();
        setDrawerLayout();
        initTabPagerList();
        Intent intent = new Intent(MainActivity.this,MusicService.class);
        startService(intent);
        Log.i("MainUi","OnCreate");
    }

    void initViews()//初始化组件
    {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        navigationView = (NavigationView) findViewById(R.id.na_view);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tablayout);
        nameText = (TextView) findViewById(R.id.nameText);
        circleImageView = (CircleImageView)findViewById(R.id.navigationview_head_imageview);
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"选择图片暂未开发",Toast.LENGTH_SHORT).show();
            }
        });
        nameText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.drawer_alertdialg, null);
                final EditText nameEdit = (EditText) view.findViewById(R.id.name_edit);
                new AlertDialog.Builder(MainActivity.this).setTitle("请输入").setIcon(
                        R.drawable.search).setView(view).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        nameText.setText(nameEdit.getText().toString());
                    }
                }).setNegativeButton("取消", null).show();
            }
        });
    }

    void setDrawerLayout()//设置抽屉布局
    {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);//设置左上角可见图标

        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.drawer_open, R.string.drawer_close);//设置开关
        mDrawerToggle.syncState();//初始化状态
        drawerLayout.setDrawerListener(mDrawerToggle);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.drawer_own_music:
                        Toast.makeText(getApplicationContext(), "Music", Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawer(Gravity.LEFT);
                        viewPager.setCurrentItem(MUSIC);
                        break;
                    case R.id.drawe_favor_list:
                        Toast.makeText(getApplicationContext(), "List is clicked", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.drawer_home:
                        Toast.makeText(getApplicationContext(), "Home is clicked", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.drawer_share:
                        Toast.makeText(getApplicationContext(), "Share is clicked", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.drawer_surround:
                        Toast.makeText(getApplicationContext(), "Surround is clicked", Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        });
    }

    void initTabPagerList()//设置页卡布局
    {
        //inti tabList
        tabList = new ArrayList<String>();
        tabList.add("Music");
        tabList.add("Search");

        //init viewLists
        mFragmentList = new ArrayList<>();
        mFragmentList.add(new MainMusicFragment());
        mFragmentList.add(new MainSearchFragment());
        //set the Mode
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        //add Tab into TabLayout
        tabLayout.addTab(tabLayout.newTab().setText(tabList.get(0)));
        tabLayout.addTab(tabLayout.newTab().setText(tabList.get(1)));
        //设置标签点击响应
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        PagerAdapter mPagerAdapter = new PagerAdapter(getSupportFragmentManager(), mFragmentList, tabList);
        viewPager.setAdapter(mPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.hello_toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences sharedPreferences = getSharedPreferences("enterCount", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }
}
