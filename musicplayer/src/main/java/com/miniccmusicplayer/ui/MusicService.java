package com.miniccmusicplayer.ui;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;

import com.miniccmusicplayer.bean.Song;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Hersch on 2016/3/26.
 */
public class MusicService extends Service{
    public static final int LIST_MODE = 0;
    public static final int RANDOM_MODE = 1;
    public static final int SINGLE_MODE = 2;
    private int playMode = LIST_MODE;
    private int playIndex=0;
    private MediaPlayer musicPlayer;
    private List<Song>songList;//歌曲列表
    public List<Song> getSongList() {
        return songList;
    }
    public void initPlayer() {
        playIndex = 0;
        musicPlayer = new MediaPlayer();
        musicPlayer.reset();
        if(songList.size()>0){
            try {
                musicPlayer.setDataSource(songList.get(playIndex).getUrl());
                musicPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        musicPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                nextSongPlay();
                Intent intent = new Intent(LrcActivity.BROADCAST_ACTION);
                Bundle bundle = new Bundle();
                bundle.putString("complete", "complete");
                intent.putExtras(bundle);
                sendBroadcast(intent);
            }
        });
    }

    /**
     * 获取歌曲总数
     * @return
     */
    public int getSongNum() {
        return songList.size();
    }

    /**
     * 点击歌曲列表播放
     * @param index
     */
    public void onItemPlay(int index) {
        this.playIndex = index;
        try {
            musicPlayer.reset();//每次切换歌曲必须先将mediaPlayer重置
            musicPlayer.setDataSource(songList.get(playIndex).getUrl());
            musicPlayer.prepare();
            musicPlayer.start();
        } catch (IOException e) {
            Log.i("MusicService","setData Error!");
            e.printStackTrace();
        }
    }

    /**
     * 设置播放模式
     */
    public void setPlayMode(){
        this.playMode = playMode<2?playMode+1:0;
    }

    /**
     * 播放前一首
     */
    public void preSongPlay() {
        if(playMode == SINGLE_MODE){
            //
        }
        else if(playMode == LIST_MODE) {
            playIndex = playIndex > 0 ?playIndex-1:songList.size()-1;
        }
        else{
            Random random = new Random();
            playIndex = random.nextInt(getSongNum());
        }
        try {
            musicPlayer.reset();
            musicPlayer.setDataSource(songList.get(playIndex).getUrl());
            musicPlayer.prepare();
            musicPlayer.start();
        } catch (IOException e) {
            Log.i("MusicService","preSongPlay error");
            e.printStackTrace();
        }
    }

    /**
     * 播放模式，单曲,循环，随机
     * @return
     */
    public int getPlayMode(){
        return playMode;
    }
    public int getCurrentPosition(){
        return musicPlayer.getCurrentPosition();
    }

    /**
     * 播放下一首歌曲
     */
    public void nextSongPlay() {
        if(playMode == SINGLE_MODE){
            //
        }
        else if(playMode == LIST_MODE) {
            playIndex = playIndex < songList.size() - 1 ? playIndex + 1 : 0;
        }
        else{
            Random random = new Random();
            playIndex = random.nextInt(getSongNum());
        }
        try {
            musicPlayer.reset();
            musicPlayer.setDataSource(songList.get(playIndex).getUrl());
            musicPlayer.prepare();
            musicPlayer.start();
        } catch (IOException e) {
            Log.i("MusicService","nextSongPlay error");
            e.printStackTrace();
        }
    }

    /**
     * 获取当前播放歌曲的总时长
     * @return
     */
    public int getTotalTime(){
        return musicPlayer.getDuration();
    }

    /**
     * 当前歌曲播放点
     * @param postion
     */
    public void seekToPosition(int postion){
        musicPlayer.seekTo(postion);
    }
    public void stopPlay() {
        musicPlayer.pause();
        Log.i("MusicService",""+musicPlayer.getCurrentPosition());
    }
    public void continuePlay() {
        musicPlayer.start();
    }
    //初始化歌曲列表songList
    void initMusicList() {
        songList = new ArrayList<>();
        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        for (int i = 0; i < cursor.getCount(); i++)
        {
            cursor.moveToNext();
            long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID)); // 闊充箰id
            String title = cursor.getString((cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));// 获取歌曲名称
            String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));// 获取歌手名称
            long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));// 获取歌曲时长
            String url = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)); // 获取歌曲链接
            int isMusic = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));//
            if (isMusic != 0)
            { // 判断是否为音乐文件
                Song song = new Song();
                song.setId(id);
                song.setTitle(title);
                song.setArtist(artist);
                song.setDuration(duration);
                song.setUrl(url);
                songList.add(song);
            }
        }
        if(songList.size()==0)
        {
            //
        }

    }
    public boolean isPlaying(){
        return musicPlayer.isPlaying();
    }
    public int getPlayIndex(){
        return this.playIndex;
    }
    @Override
    public IBinder onBind(Intent intent) {
        Log.i("MusicService","OnBind");
        initMusicList();
        initPlayer();
        return new MyBinder();
    }
    class MyBinder extends Binder
    {
        public MusicService getService()
        {
            return MusicService.this;
        }
    }
    @Override
    public void onDestroy() {
        Log.i("MusicService","OnDestory");
        super.onDestroy();
    }
}
