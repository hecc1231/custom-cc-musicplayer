package com.miniccmusicplayer.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.example.hersch.musicplayer.R;
import com.miniccmusicplayer.bean.MyUser;

import java.util.Timer;
import java.util.TimerTask;

import cn.bmob.v3.Bmob;
import cn.bmob.sms.BmobSMS;
import cn.bmob.v3.BmobUser;

public class HelloActivity extends AppCompatActivity {
    private Button jumpBtn;
    private Boolean isClick=false;//防止出现点击和自动跳转重复
    public static final String APP_ID = "69475bb35369ed3025472c387bdad390";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello);
        initBmob();
        jumpBtn = (Button)findViewById(R.id.jumpBtn);
        jumpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isClick = true;
                jumpToNextActivity();
            }
        });
        getPreferenceData();
    }
    public void initBmob(){
        Bmob.initialize(getApplicationContext(), APP_ID);
    }
    public void jumpToNextActivity(){
        //当前存在用户
        if(BmobUser.getCurrentUser(getApplicationContext(),MyUser.class)!=null) {
            Intent intent = new Intent(HelloActivity.this, MainActivity.class);
            //Intent intent = new Intent(HelloActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        //没有用户那么跳转登录界面
        else{
            Intent intent = new Intent(HelloActivity.this, LoginActivity.class);
            //Intent intent = new Intent(HelloActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            if(!isClick) {
                Intent intent = new Intent(HelloActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("HelloActivity", "OnDestroy");
    }

    /**
     * 记录应用是第几次进入
     */
    public void getPreferenceData(){
        SharedPreferences sharedPreferences = getSharedPreferences("enterCount", Context.MODE_PRIVATE);
        //取不到count就取false
        if(!sharedPreferences.getBoolean("count",false)){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("count",true);
            editor.commit();
            Timer timer = new Timer();
            timer.schedule(timerTask, 1000 * 5);
        }
        else{
            jumpToNextActivity();
        }
    }
}
