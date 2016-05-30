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
    private TextView lrcText;
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
        lrcText = (TextView)view.findViewById(R.id.lrcText);
        lrcText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.popBackStack();
            }
        });
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
        MusicService musicService = lrcUi.getService();
        int index = musicService.getPlayIndex();
        if(detectNet(lrcUi.getApplicationContext())) {
            musicService = ((LrcUi)getActivity()).getService();
            Song song = musicService.getSongList().get(index);
            new LrcContentGetter(song,handler,musicService);//获取歌词
        }
        else{
            Log.i("Lyric", "network error");
            lrcText.setText("网络未连接");
        }
    }
    Runnable updateLrcRunnable = new Runnable() {
        @Override
        public void run() {
//            lrcText.setIndex(lrcIndex());
//            PlayerActivity.lrcView.invalidate();
//            handler.postDelayed(mRunnable, 100);
        }
    };
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
