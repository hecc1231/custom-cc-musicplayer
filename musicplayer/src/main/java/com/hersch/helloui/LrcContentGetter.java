package com.hersch.helloui;

import android.app.Service;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.hersch.songobject.Song;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by Hersch on 2016/5/29.
 */
public class LrcContentGetter {
    private Song song;
    Handler handler;
    MusicService musicService;
    private List<String> lrcContentList;//歌词表
    private List<Integer>timeList;//歌词时间戳表
    private final int CONTENT_MIN_SIZE = 300;
    private final int MSG_ERROR = 1;
    private final int MSG_SUCCESS = 0;
    private final int TIME_INTERNAL = 5000;
    public LrcContentGetter(Song song,Handler handler,MusicService musicService){
        this.song = song;
        this.handler = handler;
        this.musicService = musicService;
        lrcContentList = new ArrayList<>();
        timeList = new ArrayList<>();
        lrcContentList.clear();
        timeList.clear();
        new Thread(getLrcRunnable).start();
    }
    public List<String> getLrcContentList(){
        return lrcContentList;
    }
    public List<Integer> getTimeList(){
        return timeList;
    }
    Runnable getLrcRunnable = new Runnable() {
        @Override
        public void run() {
            getLyric(song);
        }
    };
    /**
     *
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
            String lyricStr = getXMLFromURL(httpURLConnection);
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
                    //找到最合适的歌词则发送给歌词界面并跳出
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
                    timeList.clear();
                    lrcContentList.clear();
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
     * 获取歌词xml文件以及其中的歌词id号
     * @param httpURLConnection
     * @return
     */
    public String getXMLFromURL(HttpURLConnection httpURLConnection){
        String str = "";
        String xmlStr = "";
        try {
            BufferedReader bfReader=null;
            //采用GB2312是因为歌词xml是以gb2312编码
            bfReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(),"GB2312"));
            while((str=bfReader.readLine())!=null){
                xmlStr+=str;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return xmlStr;
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
                str = getSingleLineLrcTime(str);
                if(!str.equals("")) {
                    lyricStr += str + "\r\n";
                    lrcContentList.add(str);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lyricStr;
    }

    /**
     * 获取单行歌词的时间戳和歌词内容
     * @param str
     * @return
     */
    public String getSingleLineLrcTime(String str) {
        Pattern pattern = Pattern.compile("[0-9]{2}:[0-9]{2}.[0-9]{2}");
        str = str.substring(1);
        if (pattern.matcher(str).lookingAt()) {
            int index = 0;
            int middle = str.indexOf("]");
            String timeStr = str.substring(0, middle);//获取时间戳
            //会存在只有时间戳没有歌词的情况这个情况要把多余的时间戳舍去
            /**
             * [00:20.41]
             * [00:22.43]请你不要睡觉
             * 这个时候需要将[00:20.41]舍去，保证歌词与时间戳一一对应
             */
            //即除去只有时间戳的例子
            if(str.length()-1!=middle) {
                int totalTime = Integer.parseInt(timeStr.substring(0, 2)) * 60 * 1000 +
                        Integer.parseInt(timeStr.substring(3, 5)) * 1000 + Integer.parseInt(timeStr.substring(6, 8));//00:03.98
                timeList.add(totalTime);
            }
            //最后一行
            if((index=str.indexOf("]]></lyric>"))!=-1){
                str = str.substring(middle+1,index);
            }
            //其余行
            else {
                str = str.substring(middle+1);
            }
        }
        else{
            str = "";
        }
        return str;
    }
}
