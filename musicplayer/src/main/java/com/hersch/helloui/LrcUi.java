package com.hersch.helloui;



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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.support.v7.widget.Toolbar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hersch.musicplayer.R;
import com.hersch.songobject.Song;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * 歌词界面
 */
public class LrcUi extends AppCompatActivity{
    private Toolbar toolbar;//菜单栏
    private SeekBar seekBar;//进度条
    private TextView toolBarSingerText;
    private TextView toolBarSongText;
    private TextView seekBarStartText;
    private TextView seekBarEndText;
    private Button nextBtn;//播放下一首
    private Button modeBtn;//播放模式
    private Button preBtn;//播放上一首
    private Button playBtn;//播放暂停按钮
    private ImageView albumImageView;//专辑画面
    private LrcContentFragment contentFragment;
    private MusicService musicService;//后台服务实例
    private MyReceiveBroadcast  myBroadcastReceiver;
    private final int SEEKBAR_CURRENT_POSITION = 0;
    public static final String BROADCAST_ACTION = "com.hersch.helloui.LrcUi";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //还原上一次Activity的播放模式
        Log.i("LrcUi","OnCreate");
        LayoutInflater layoutInflater = getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.activity_lrc_ui, null);
        setContentView(view);
        bindToService();
        registerBroadcast();
        initView();
    }
    /**
     * 初始化组件
     */
    public void initView(){
        toolBarSingerText = (TextView)findViewById(R.id.lrc_toolbar_singer_text);
        toolBarSongText = (TextView)findViewById(R.id.lrc_toolbar_song_text);
        seekBar = (SeekBar)findViewById(R.id.seekBar);
        seekBar.setMax(100);
        toolbar = (android.support.v7.widget.Toolbar)findViewById(R.id.lrc_toolbar);
        albumImageView = (ImageView)findViewById(R.id.album_image);
        seekBarStartText = (TextView)findViewById(R.id.seekbar_start_text);
        seekBarEndText = (TextView)findViewById(R.id.seekbar_end_text);
        preBtn = (Button)findViewById(R.id.lrc_pre_btn);
        nextBtn = (Button)findViewById(R.id.lrc_next_btn);
        modeBtn = (Button)findViewById(R.id.lrc_mode_btn);
        playBtn = (Button)findViewById(R.id.lrc_play_btn);
        preBtn.setOnClickListener(btnClickListener);
        nextBtn.setOnClickListener(btnClickListener);
        modeBtn.setOnClickListener(btnClickListener);
        playBtn.setOnClickListener(btnClickListener);
        albumImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment();
            }
        });
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
    public MusicService getService(){
        return this.musicService;
    }
    public void bindToService(){
        Intent intent = new Intent(LrcUi.this, MusicService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }
    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MyBinder myBinder= (MusicService.MyBinder)service;
            musicService = myBinder.getService();//获取歌曲服务
            setPlayBtnDrawable();//根据当前播放状态初始化play按钮的状态图
            setToolBarInfo();
            backToModeBtn();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while(true) {
                        if(musicService.isPlaying()) {
                            int position = musicService.getCurrentPosition();
                            int totalTime = musicService.getTotalTime();
                            Message message = new Message();
                            message.arg1 = position;
                            message.arg2 = totalTime;
                            message.what = SEEKBAR_CURRENT_POSITION;
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

        }
    };

    /**
     * 设置play按钮的状态图
     */
    public void setPlayBtnDrawable(){
        if(musicService.isPlaying()){
            playBtn.setBackgroundResource(R.drawable.pause);
        }
        else{
            playBtn.setBackgroundResource(R.drawable.play);
        }
    }
    View.OnClickListener btnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.lrc_play_btn:
                    //0代表正在播放
                    if(musicService.isPlaying()) {
                        musicService.stopPlay();
                        playBtn.setBackgroundResource(R.drawable.play);
                    }
                    else {
                        musicService.continuePlay();
                        playBtn.setBackgroundResource(R.drawable.pause);
                    }
                    break;
                case R.id.lrc_pre_btn:
                    musicService.preSongPlay();
                    setPlayBtnDrawable();
                    setFragment();
                    setToolBarInfo();
                    break;
                case R.id.lrc_next_btn:
                    musicService.nextSongPlay();
                    setPlayBtnDrawable();
                    setFragment();
                    setToolBarInfo();
                    break;
                case R.id.lrc_mode_btn:
                    musicService.setPlayMode();//改变播放状态
                    int playMode = musicService.getPlayMode();
                    if(playMode == musicService.SINGLE_MODE){
                        modeBtn.setBackgroundResource(R.drawable.single);
                        Log.i("LrcUi", "Single");
                        Toast.makeText(getApplicationContext(),"单曲循环",Toast.LENGTH_SHORT).show();
                    }
                    else if(playMode==musicService.RANDOM_MODE){
                        modeBtn.setBackgroundResource(R.drawable.random);
                        Log.i("LrcUi", "Random");
                        Toast.makeText(getApplicationContext(),"随机播放",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        modeBtn.setBackgroundResource(R.drawable.list_circle);
                        Log.i("LrcUi", "Circle");
                        Toast.makeText(getApplicationContext(),"列表循环",Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };
    /**
     * 设置菜单栏的歌曲歌手显示
     */
    public void setToolBarInfo(){
        int index = musicService.getPlayIndex();
        Song song = musicService.getSongList().get(index);
        toolBarSongText.setText(song.getTitle());
        toolBarSingerText.setText(song.getArtist());
    }
    /**
     * 设置Fragment
     */
    public void setFragment(){
        FragmentManager fragmentManager =getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        contentFragment = new LrcContentFragment();
        //修改当前歌曲的专辑照片
        //.........
        //

        transaction.replace(R.id.sub_fragment, contentFragment);
        //transaction.addToBackStack(null);//即将被代替的界面放入栈中保证回退
        transaction.commit();
    }
    public void registerBroadcast(){
        myBroadcastReceiver = new MyReceiveBroadcast();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BROADCAST_ACTION);
        registerReceiver(myBroadcastReceiver, intentFilter);
    }
    class MyReceiveBroadcast extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String str = intent.getStringExtra("complete");
            if(str.equals("complete")){
                setFragment();//代表自动播放完一首歌需要更新歌词界面
                setToolBarInfo();
            }
        }
    }
    /**
     * 设置返回键
     */
    public void setHomeBtnEnable(){
        getSupportActionBar().setDisplayShowHomeEnabled(true);//设置左上角可见图标
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case SEEKBAR_CURRENT_POSITION:
                    int currentPosition = msg.arg1;
                    int totalTime = msg.arg2;
                    seekBar.setProgress(currentPosition*100/totalTime);
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
        Log.i("LrcUi","Destroy");
        super.onDestroy();
    }
}
