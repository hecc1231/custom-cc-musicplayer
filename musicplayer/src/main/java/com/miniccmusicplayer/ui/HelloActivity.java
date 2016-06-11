package com.miniccmusicplayer.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.hersch.musicplayer.R;

import java.util.Timer;
import java.util.TimerTask;

import cn.bmob.v3.Bmob;

public class HelloActivity extends AppCompatActivity {
    private Button jumpBtn;
    private Boolean isClick=false;
    public static final String APP_ID = "69475bb35369ed3025472c387bdad390";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello_ui);
        initBmob();
        jumpBtn = (Button)findViewById(R.id.jumpBtn);
        jumpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isClick = true;
                Intent intent = new Intent(HelloActivity.this, LoginInActivity.class);
                //Intent intent = new Intent(HelloActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        getPreferenceData();
    }
    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            if(!isClick) {
                Intent intent = new Intent(HelloActivity.this, LoginInActivity.class);
                //Intent intent = new Intent(HelloActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }
    };

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
            Intent intent = new Intent(HelloActivity.this,LoginInActivity.class);
            //Intent intent = new Intent(HelloActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
    public void initBmob(){
        Bmob.initialize(getApplicationContext(),APP_ID);
    }
}
