package com.hersch.helloui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.example.hersch.musicplayer.R;

import java.util.Timer;
import java.util.TimerTask;

public class HelloUi extends AppCompatActivity {
    private Button jumpBtn;
    private Boolean isClick=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello_ui);
        jumpBtn = (Button)findViewById(R.id.jumpBtn);
        getPreferenceData();
        jumpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isClick = true;
                Intent intent = new Intent(HelloUi.this, MainUi.class);
                startActivity(intent);
                finish();
            }
        });
    }
    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            if(!isClick) {
                Intent intent = new Intent(HelloUi.this, MainUi.class);
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
            Intent intent = new Intent(HelloUi.this,MainUi.class);
            startActivity(intent);
            finish();
        }
    }
}
