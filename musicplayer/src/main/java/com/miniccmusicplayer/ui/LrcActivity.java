package com.miniccmusicplayer.ui;


import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.support.v7.widget.Toolbar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hersch.musicplayer.R;
import com.miniccmusicplayer.bean.Song;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 歌词界面
 */
public class LrcActivity extends AppCompatActivity {
    private Toolbar toolbar;//菜单栏
    private ShareActionProvider mShareActionProvider;
    private SeekBar seekBar;//进度条
    private TextView toolBarSingerText;
    private TextView toolBarSongText;
    private TextView seekBarStartText;
    private TextView seekBarEndText;
    private Button nextBtn;//播放下一首
    private Button modeBtn;//播放模式
    private Button preBtn;//播放上一首
    private Button playBtn;//播放暂停按钮
    private LrcAlbumImageFragment mLrcAlbumImageFragment;
    private MusicService musicService;//后台服务实例
    private MyReceiveBroadcast myBroadcastReceiver;
    private final int SEEKBAR_MSG = 0;
    public static final String BROADCAST_ACTION = "com.hersch.helloui.LrcUi";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater layoutInflater = getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.activity_lrc, null);
        setContentView(view);
        bindToService();
        registerBroadcast();
        initView();
    }

    /**
     * 初始化组件
     */
    public void initView() {
        toolBarSingerText = (TextView) findViewById(R.id.lrc_toolbar_singer_text);
        toolBarSongText = (TextView) findViewById(R.id.lrc_toolbar_song_text);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setMax(100);
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.lrc_toolbar);
        seekBarStartText = (TextView) findViewById(R.id.seekbar_start_text);
        seekBarEndText = (TextView) findViewById(R.id.seekbar_end_text);
        preBtn = (Button) findViewById(R.id.lrc_pre_btn);
        nextBtn = (Button) findViewById(R.id.lrc_next_btn);
        modeBtn = (Button) findViewById(R.id.lrc_mode_btn);
        playBtn = (Button) findViewById(R.id.lrc_play_btn);
        preBtn.setOnClickListener(btnClickListener);
        nextBtn.setOnClickListener(btnClickListener);
        modeBtn.setOnClickListener(btnClickListener);
        playBtn.setOnClickListener(btnClickListener);
        setSupportActionBar(toolbar);
        setHomeBtnEnable();
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                SimpleDateFormat sDateFormat = new SimpleDateFormat("mm:ss");
                String s = sDateFormat.format(new Date(progress * musicService.getTotalTime() / 100));
                seekBarStartText.setText(s);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                musicService.seekToPosition(seekBar.getProgress() * musicService.getTotalTime() / 100);//将歌曲播放进度更新
            }
        });
    }

    /**
     * 再次返回该Activity时复原模式按钮的状态
     */
    public void backToModeBtn() {
        int mode = musicService.getPlayMode();
        switch (mode) {
            case MusicService.SINGLE_MODE:
                modeBtn.setBackgroundResource(R.drawable.single);
                break;
            case MusicService.RANDOM_MODE:
                modeBtn.setBackgroundResource(R.drawable.random);
                break;
            default:
                modeBtn.setBackgroundResource(R.drawable.list_circle);
                break;
        }
    }

    public MusicService getService() {
        return this.musicService;
    }

    public void bindToService() {
        Intent intent = new Intent(LrcActivity.this, MusicService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MyBinder myBinder = (MusicService.MyBinder) service;
            musicService = myBinder.getService();//获取歌曲服务
            setPlayBtnDrawable();//根据当前播放状态初始化play按钮的状态图
            setToolBarInfo();
            setDefaultFragment();//根据musicSevice获取当前的歌词与专辑图片
            backToModeBtn();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        if (musicService.isPlaying()) {
                            int position = musicService.getCurrentPosition();
                            int totalTime = musicService.getTotalTime();
                            Message message = new Message();
                            message.arg1 = position;
                            message.arg2 = totalTime;
                            message.what = SEEKBAR_MSG;
                            handler.sendMessage(message);
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }).start();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i("LrcUi","Service Disconnected");
        }
    };

    /**
     * 设置play按钮的状态图
     */
    public void setPlayBtnDrawable() {
        if (musicService.isPlaying()) {
            playBtn.setBackgroundResource(R.drawable.pause);
        } else {
            playBtn.setBackgroundResource(R.drawable.play);
        }
    }

    View.OnClickListener btnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.lrc_play_btn:
                    //0代表正在播放
                    if (musicService.isPlaying()) {
                        musicService.stopPlay();
                        playBtn.setBackgroundResource(R.drawable.play);
                    } else {
                        musicService.continuePlay();
                        playBtn.setBackgroundResource(R.drawable.pause);
                    }
                    break;
                case R.id.lrc_pre_btn:
                    musicService.preSongPlay();
                    setPlayBtnDrawable();
                    setDefaultFragment();
                    setToolBarInfo();
                    break;
                case R.id.lrc_next_btn:
                    musicService.nextSongPlay();
                    setPlayBtnDrawable();
                    setDefaultFragment();
                    setToolBarInfo();
                    break;
                case R.id.lrc_mode_btn:
                    musicService.setPlayMode();//改变播放状态
                    int playMode = musicService.getPlayMode();
                    if (playMode == musicService.SINGLE_MODE) {
                        modeBtn.setBackgroundResource(R.drawable.single);
                        Log.i("LrcUi", "Single");
                        Toast.makeText(getApplicationContext(), "单曲循环", Toast.LENGTH_SHORT).show();
                    } else if (playMode == musicService.RANDOM_MODE) {
                        modeBtn.setBackgroundResource(R.drawable.random);
                        Log.i("LrcUi", "Random");
                        Toast.makeText(getApplicationContext(), "随机播放", Toast.LENGTH_SHORT).show();
                    } else {
                        modeBtn.setBackgroundResource(R.drawable.list_circle);
                        Log.i("LrcUi", "Circle");
                        Toast.makeText(getApplicationContext(), "列表循环", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    /**
     * 设置菜单栏的歌曲歌手显示
     */
    public void setToolBarInfo() {
        int index = musicService.getPlayIndex();
        Song song = musicService.getSongList().get(index);
        toolBarSongText.setText(song.getTitle());
        toolBarSingerText.setText(song.getArtist());
    }
    /**
     * 设置Fragment
     */
    public void setDefaultFragment() {
        FragmentManager mFragmentManager = getSupportFragmentManager();
        FragmentTransaction mTransaction = mFragmentManager.beginTransaction();
        mLrcAlbumImageFragment = new LrcAlbumImageFragment();
        mTransaction.replace(R.id.sub_fragment, mLrcAlbumImageFragment);
        mTransaction.commitAllowingStateLoss();
        //mTransaction.commit();
        //commit与commitAllowingStateLoss区别在于commitAllowingStateLoss允许在Activity保存完状态后即执行完OnSaveInstance后
        // 继续提交事务，而commit如果在OnSaveInstance执行完后再提交事务则会报错
        //尽量将提交事务的操作置于保存状态之前
        //OnSaveInStance在OnPause()期间执行
    }
    public void registerBroadcast() {
        myBroadcastReceiver = new MyReceiveBroadcast();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BROADCAST_ACTION);
        registerReceiver(myBroadcastReceiver, intentFilter);
    }

    class MyReceiveBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String str = intent.getStringExtra("complete");
            if (str.equals("complete")) {
                musicService.nextSongPlay();
                setDefaultFragment();//代表在熄屏情况下再启动更新Fragment
                setToolBarInfo();
            }
        }
    }

    /**
     * 设置返回键
     */
    public void setHomeBtnEnable() {
        getSupportActionBar().setDisplayShowHomeEnabled(true);//设置左上角可见图标
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.lrc_toolbar_menu,menu);
        MenuItem item = menu.findItem(R.id.lrc_toolbar_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        Intent sendIntent =new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "MiniCC MusicPlayer");
        sendIntent.setType("text/plain");
        setShareIntent(sendIntent);

        return super.onCreateOptionsMenu(menu);
    }
    public void setShareIntent(Intent sendIntent){
        if(mShareActionProvider!=null){
            mShareActionProvider.setShareIntent(sendIntent);
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                //进度条消息
                case SEEKBAR_MSG:
                    int currentPosition = msg.arg1;
                    int totalTime = msg.arg2;
                    seekBar.setProgress(currentPosition * 100 / totalTime);
                    SimpleDateFormat sDateFormat = new SimpleDateFormat("mm:ss");
                    String endTime = sDateFormat.format(new Date(totalTime));
                    String currentTime = sDateFormat.format(new Date(currentPosition));
                    seekBarStartText.setText(currentTime);
                    seekBarEndText.setText(endTime);
                    break;
            }
        }
    };

    @Override
    protected void onStop() {
        Log.i("LrcUi", "Stop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        unbindService(serviceConnection);
        unregisterReceiver(myBroadcastReceiver);
        Log.i("LrcActivity", "Destroy");
        super.onDestroy();
    }
}
