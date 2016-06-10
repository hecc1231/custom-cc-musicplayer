package com.miniccmusicplayer.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.hersch.musicplayer.R;
import com.miniccmusicplayer.bean.Song;
import com.miniccmusicplayer.view.CircleImageView;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Hersch on 2016/6/6.
 */
public class LrcAlbumImageFragment extends Fragment {
    private CircleImageView mAlbumImageView;
    private MusicService mMusicService;
    private Bitmap mBitmap;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.lrc_fragment_album_image, container, false);
        findViews(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager mFragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();
                mFragmentTransaction.replace(R.id.sub_fragment, new LrcContentFragment());
                mFragmentTransaction.commit();
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //获取专辑界面
        //mMusicService = ((LrcUi) getActivity()).getService();
        //new Thread(myRunnable).start();

    }

    /**
     * 子线程:获取专辑图片
     */
    Runnable myRunnable = new Runnable() {
        @Override
        public void run() {
            URL url = null;
            Song song = mMusicService.getSongList().get(mMusicService.getPlayIndex());
            try {
                url = new URL("http://geci.me/api/lyric/" + URLEncoder.encode(song.getTitle(), "utf8"));
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                String xml = getXMLFromURL(conn);
                int index = xml.indexOf("aid");
                int lastIndex = xml.indexOf("lrc");
                String str = null;
                if (index < lastIndex) {
                    str = xml.substring(index + 6, lastIndex - 3);
                    url = new URL("http://geci.me/api/cover/" + str);
                    conn = (HttpURLConnection) url.openConnection();
                    xml = getXMLFromURL(conn);
                    index = xml.indexOf("thumb");
                    lastIndex = xml.lastIndexOf("jpg");
                    if (index < lastIndex) {
                        str = xml.substring(index + 9, lastIndex + 3);
                    }
                    if (str != null) {
                        setAlbumImage(str);
                    }
                    Message message = Message.obtain();
                    mHandler.sendMessage(message);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * 获取专辑图片的url
     *
     * @param httpURLConnection
     * @return
     */
    public String getXMLFromURL(HttpURLConnection httpURLConnection) {
        String str = "";
        String xmlStr = "";
        try {
            BufferedReader bfReader = null;
            //采用GB2312是因为歌词xml是以gb2312编码
            bfReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "GB2312"));
            while ((str = bfReader.readLine()) != null) {
                xmlStr += str;
            }
        } catch (FileNotFoundException e) {
            Toast.makeText(getActivity().getApplicationContext(), "未找到歌词", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return xmlStr;
    }
    public void setAlbumImage(String path) {
        URL url = null;
        try {
            url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = conn.getInputStream();
                mBitmap = BitmapFactory.decodeStream(inputStream);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void findViews(View view) {
        mAlbumImageView = (CircleImageView) view.findViewById(R.id.lrc_frg_album_image);
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mAlbumImageView.setImageBitmap(mBitmap);
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
