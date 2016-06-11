package com.miniccmusicplayer.bean;

import android.content.Intent;

import java.io.File;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by Hersch on 2016/6/10.
 */
public class MyUser extends BmobUser {
    private Integer age;
    private BmobFile icon;
    public void setAge(Integer age){
        this.age = age;
    }
    public void setIcon(BmobFile icon){
        this.icon = icon;
    }
    public BmobFile getIcon(){
        return this.icon;
    }
}
