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

/**
 * Created by Hersch on 2016/5/15.
 */
public class LrcContentFragment extends Fragment{
    private TextView lrcText;
    private final int CONTENT_MIN_SIZE = 300;
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
        new Thread(MyRunnable).start();
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
    Runnable MyRunnable = new Runnable() {
        @Override
        public void run() {
            LrcUi lrcUi = (LrcUi)getActivity();
            MusicService musicService = lrcUi.getService();
            int index = musicService.getPlayIndex();
           // lrcUi.setSongTitle(musicService.getSongList().get(index).getTitle()+musicService.getSongList().get(index).getArtist());
            if(detectNet(lrcUi.getApplicationContext())) {
                musicService = ((LrcUi)getActivity()).getService();
                getLyric(musicService.getSongList().get(index));
            }
            else{
                Log.i("Lyric", "network error");
                lrcText.setText("网络未连接");
            }
        }
    };

    /**
     * 通过歌曲名和歌手名称在线获取歌词
     * @param song
     */
    public void getLyric(Song song){
        URL url = null;
        try {
            //中文必须经过gbk也就是GB2312的转码，因为api采用的是gbk编码格式
            //http://qqmusic.qq.com/fcgi-bin/qm_getLyricId.fcg?name=连哭都是我的错&singer=东来东往&from=qqplayer
            url = new URL("http://qqmusic.qq.com/fcgi-bin/qm_getLyricId.fcg?name="+ URLEncoder.encode(song.getTitle(), "gbk")
                    +"&singer="+URLEncoder.encode(song.getArtist(), "gbk")+"&from=qqplayer");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            String lyricStr = getDataFromURL(httpURLConnection);
            int count = countLyric(lyricStr);//获得歌词版本的数量
            if(count==0) {
                Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("error", "no lyric found");
                msg.setData(bundle);
                msg.what = MSG_ERROR;
                handler.sendMessage(msg);
                return ;
            }
            else {
                while (count > 0) {
                    int startIndex = lyricStr.indexOf("songinfo");
                    int dotStartIndex = lyricStr.indexOf("\"", startIndex);
                    int dotEndIndex = lyricStr.indexOf("\"", dotStartIndex + 1);
                    String strId = lyricStr.substring(dotStartIndex + 1, dotEndIndex);
                    int id = Integer.parseInt(strId);
                    lyricStr = lyricStr.substring(dotEndIndex+1);//将扫描过的歌词剪去

                    //http://music.qq.com/miniportal/static/lyric/歌曲ID求余100/歌曲ID.xml
                    //获取歌词的xml文件
                    url = new URL("http://music.qq.com/miniportal/static/lyric/"+id%100+"/"+id+".xml");
                    httpURLConnection = (HttpURLConnection) url.openConnection();
                    String contentLrc = getDataFromURL(httpURLConnection);
                    //找到歌词则发送给歌词界面并跳出
                    if(contentLrc.length()>=CONTENT_MIN_SIZE){
                        Message msg = new Message();
                        Bundle data = new Bundle();
                        data.putString("lyric", contentLrc);
                        msg.setData(data);
                        msg.what = MSG_SUCCESS;
                        handler.sendMessage(msg);
                        return;
                    }
                    count--;
                }
                //未找到歌词返回error
                Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("error", "no lyric found");
                msg.setData(bundle);
                msg.what = MSG_ERROR;
                handler.sendMessage(msg);
                return ;
            }
        }catch(MalformedURLException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    /**
     * 计算同一首歌的歌词版本数
     * @param lyricStr
     * @return
     */
    public int countLyric(String lyricStr){
        int startCountIndex = lyricStr.indexOf("<songcount>");
        int endCountIndex =lyricStr.indexOf("</songcount>");
        String strCount = lyricStr.substring(startCountIndex + 11, endCountIndex);
        return Integer.parseInt(strCount);
    }

    /**
     * 从URL通过BufferedReader获取歌词信息
     * @param httpURLConnection
     * @return
     */
    public String getDataFromURL(HttpURLConnection httpURLConnection){
        String str = "";
        String lyricStr = "";
        try {
            BufferedReader bfReader=null;
            //采用GB2312是因为歌词xml是以gb2312编码
            bfReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(),"GB2312"));
            while((str=bfReader.readLine())!=null){
                int index = 0;
                if((index=str.indexOf("]]></lyric>"))!=-1){
                    str = str.substring(0,index);
                    str = str.substring(str.indexOf("]")+1);
                }
                else {
                    str = str.substring(str.indexOf("]")+1);
                }
                lyricStr+=str+"\r\n";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lyricStr;
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
