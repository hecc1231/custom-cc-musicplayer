package com.miniccmusicplayer.bean;

import cn.bmob.v3.BmobObject;

/**
 * Bmob表
 * 记录最近听过的音乐
 */
public class MyLatelySong extends BmobObject {
    private MyUser user;

    public MyUser getUser() {
        return user;
    }

    public void setUser(MyUser myUser) {
        this.user = myUser;
    }

    private String title;
    private String artist;
    private long id;
    private long duration;
    private String url;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

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
}
