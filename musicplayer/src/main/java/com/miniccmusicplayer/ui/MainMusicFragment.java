package com.miniccmusicplayer.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hersch.musicplayer.R;
import com.miniccmusicplayer.adapter.RecyclerAdapter;
import com.miniccmusicplayer.bean.TestItemDecoraton;

public class MainMusicFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private GridLayout playBarGridLayout;
    private Button  playBtn;
    private Button nextBtn;
    private TextView textView;//显示歌曲总数
    private TextView songInfoText;//显示播放板上的歌曲信息
    private MusicService musicService;
    private final int MUSIC_FRAGMENT=0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        bindToService();
        View view = inflater.inflate(R.layout.main_frg_music, container, false);
        findViews(view);//初始化recyclerView
        return view;
    }

    @Override
    public void onStart() {
        setPlayBtnDrawable();
        super.onStart();
    }

    public void bindToService(){
        Intent intent = new Intent(getActivity().getApplicationContext(),MusicService.class);
        getActivity().getApplicationContext().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //绑定服务
    }
    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i("MusicFragment","Service Connect");
            MusicService.MyBinder myBinder = (MusicService.MyBinder) service;
            musicService = myBinder.getService();
            setMusicPagerView();//导入recyclerView视图
            setClickListener();
            setPlayBtnDrawable();//设置按钮播放状态
            new Thread(myRunnable).start();//开启子线程
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };
    public void setPlayBtnDrawable(){
        if(musicService!=null) {
            if (musicService.isPlaying()) {
                playBtn.setBackgroundResource(R.drawable.pause);
            } else {
                playBtn.setBackgroundResource(R.drawable.play);
            }
        }
    }
    /**
     * 接受子线程传来的下方歌曲和歌手信息（更新下方歌曲栏信息）
     */
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    String str = msg.getData().getString("data");
                    songInfoText.setText(str);
                    break;
                case 2:
                    break;
            }
            super.handleMessage(msg);
        }
    };
    Runnable myRunnable = new Runnable() {
        @Override
        public void run() {
            while(true) {
                int index = musicService.getPlayIndex();
                Message msg = new Message();
                msg.what = 1;
                String str = musicService.getSongList().get(index).getTitle()+"\r\n"+
                        musicService.getSongList().get(index).getArtist();
                Bundle data = new Bundle();
                data.putString("data",str);
                msg.setData(data);
                handler.sendMessage(msg);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };
    void setClickListener()
    {
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //0代表播放中，1代表暂停
                if(musicService.isPlaying()) {
                    musicService.stopPlay();
                    playBtn.setBackgroundResource(R.drawable.play);
                }
                else{
                    musicService.continuePlay();
                    playBtn.setBackgroundResource(R.drawable.pause);
                }
            }
        });
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicService.nextSongPlay();
                playBtn.setBackgroundResource(R.drawable.pause);
            }
        });
    }
    void findViews(View view)
    {
        mRecyclerView = (RecyclerView)view.findViewById(R.id.recyclerview);
        playBarGridLayout = (GridLayout)view.findViewById(R.id.music_play_bar);
        textView = (TextView)view.findViewById(R.id.music_tip_text);
        songInfoText = (TextView)view.findViewById(R.id.songInfoText);
        playBtn = (Button)view.findViewById(R.id.play_btn);
        nextBtn = (Button)view.findViewById(R.id.next_play_btn);
    }
    void setMusicPagerView()
    {
        //LayoutInflater inflater = LayoutInflater.from(this); LayoutInflater是每次new出一个新的View,跟以前new出来的View没有任何关系
        //mRecyclerView = (RecyclerView)inflater.inflate(R.layout.page_music,null).findViewById(R.id.recyclerview);
        textView.setText("本地歌曲(共" + musicService.getSongNum() + "首)");
        mRecyclerView.setHasFixedSize(true);
        //mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setBackgroundColor(Color.TRANSPARENT);
        mRecyclerView.addItemDecoration(new TestItemDecoraton(getActivity()));
        //线性布局管理器
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        //设置recyclerView适配器
        final RecyclerAdapter mRecyclerAdapter = new RecyclerAdapter(this.getActivity(),musicService.getSongList());
        //设置item点击监听
        mRecyclerAdapter.setOnItemClickListener(new RecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(getActivity(), "" + position, Toast.LENGTH_SHORT).show();
                musicService.onItemPlay(position);
                setPlayBtnDrawable();//改变播放按钮的状态
            }

            @Override
            public void onItemLongClick(View view, int position) {
//                mRecyclerAdapter.insertData();
            }
        });
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(mRecyclerAdapter);
        playBarGridLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),LrcActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("song",musicService.getSongList().get(musicService.getPlayIndex()));
                intent.putExtras(bundle);
                startActivityForResult(intent, MUSIC_FRAGMENT);
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
