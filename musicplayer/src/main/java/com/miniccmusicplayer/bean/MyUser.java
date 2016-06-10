package com.miniccmusicplayer.bean;

import android.content.Intent;

import cn.bmob.v3.BmobUser;

/**
 * Created by Hersch on 2016/6/10.
 */
public class MyUser extends BmobUser {
    private Integer age;
    public void setAge(Integer age){
        this.age = age;
    }
}
