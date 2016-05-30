package com.hersch.helloui;

import android.app.Service;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Message;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.hersch.musicplayer.R;
import com.hersch.songobject.Song;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by Hersch on 2016/5/15.
 */
public class LrcContentFragment extends Fragment{
    private MyLrcTextView lrcText;
    private MusicService musicService;
    private LrcContentGetter mLrcContentGetter;
    private final int MSG_ERROR = 1;
    private final int MSG_SUCCESS = 0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.lrc_fragment_content, container, false);
        findViews(view);
        return view;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        searchLrcThread();
    }
    public void findViews(View view){
        lrcText = (MyLrcTextView)view.findViewById(R.id.lrc_text);
        lrcText.setClickable(false);
    }
    public Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            String str = "";
            switch (msg.what){
                case MSG_SUCCESS:
                    Bundle data = msg.getData();
                    str = data.getString("lyric");
                    lrcText.setText(str);
                    break;
                case MSG_ERROR:
                    data = msg.getData();
                    str = data.getString("error");
                    lrcText.setText(str);
                    break;
            }
        }
    };
    /**
     * 获取歌词
     */
    public void searchLrcThread(){
        LrcUi lrcUi = (LrcUi)getActivity();
        musicService = lrcUi.getService();
        int index = musicService.getPlayIndex();
        if(detectNet(lrcUi.getApplicationContext())) {
            musicService = ((LrcUi)getActivity()).getService();
            Song song = musicService.getSongList().get(index);
            mLrcContentGetter = new LrcContentGetter(song,handler,musicService);//获取歌词类
        }
        else{
            Log.i("Lyric", "network error");
            lrcText.setText("网络未连接");
        }
        //设置歌词更新,将歌词列表传入自定义textview中绘制
        lrcText.setLrcContentList(mLrcContentGetter.getLrcContentList());
        handler.post(updateLrcRunnable);
    }
    Runnable updateLrcRunnable = new Runnable() {
        @Override
        public void run() {
            lrcText.setIndex(currentLrcLine());//获取当前正在播放所在行
            lrcText.invalidate();//更新自定义的textView
            handler.postDelayed(updateLrcRunnable, 100);//每100ms更新一次
        }
    };

    /**
     * 根据时间戳计算当前时间应该播放哪一行歌词
     * @return
     */
    public int currentLrcLine(){
        int index = 0;
        int time = musicService.getCurrentPosition();
        List<Integer>timeList = mLrcContentGetter.getTimeList();
        List<String>lrcContentList = mLrcContentGetter.getLrcContentList();
        for(int i=0;i<lrcContentList.size();i++){
            if(time<timeList.get(i)&&i==0){
                index = 0;
                break;
            }
            else if(i<timeList.size()-1&&time>=timeList.get(i)&&time<timeList.get(i+1)){
                index = i;
                break;
            }
            else if(time>=timeList.get(i)&&i==timeList.size()-1){
                index = i;
                break;
            }
        }
        return index;
    }
    /**
     * 检测网络连接状态
     * @param context
     * @return
     */
    public static boolean detectNet(Context context)
    {
        try
        {
            // 获取连接管理对象
            ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null)
            {
                // 获取活动的网络连接
                NetworkInfo info = connectivity.getActiveNetworkInfo();
                if (info != null && info.isConnected())
                {
                    if (info.getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }
                }
            }
        } catch (Exception e)
        {
        }
        return false;
    }
}
