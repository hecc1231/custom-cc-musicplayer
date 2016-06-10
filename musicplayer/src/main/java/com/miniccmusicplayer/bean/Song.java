package com.miniccmusicplayer.bean;

import java.io.Serializable;

/**
 * Created by Hersch on 2016/3/26.
 */
public class Song implements Serializable {
    private long id;//歌曲id
    private String artist;//歌手名字
    private String title;//歌曲名字
    private String url;//歌词链接url
    private long duration;
    private String lyricContent;

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String mTitle) {
        this.title = mTitle;
    }
    public long getId() {
        return id;
    }
    public void setId(long songId) {
        this.id = songId;
    }
    public String getLyricContent() {
        return lyricContent;
    }

    public void setLyricContent(String lyricContent) {
        this.lyricContent = lyricContent;
    }

}
