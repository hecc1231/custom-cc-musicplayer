package com.miniccmusicplayer.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.hersch.musicplayer.R;
import com.miniccmusicplayer.bean.MyUser;

import java.util.regex.Pattern;

import cn.bmob.v3.listener.SaveListener;

public class RegisterActivity extends AppCompatActivity {
    private Button confirmBtn;
    private EditText userEdit;
    private EditText pwdEdit;
    private EditText pwdConfirmEdit;
    private EditText ageEdit;
    private Toolbar toolbar;
    private String userString;
    private String pwdString;
    private String pwdConfirmString;
    private String ageString;
    private final int PWD_LENGTH = 8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
       initViews();
    }
    public void initViews(){
        toolbar = (Toolbar)findViewById(R.id.register_toolbar);
        userEdit = (EditText) findViewById(R.id.register_user_edit);
        pwdEdit = (EditText) findViewById(R.id.register_pwd_edit);
        pwdConfirmEdit =(EditText)findViewById(R.id.register_pwd_confirm_edit);
        ageEdit = (EditText)findViewById(R.id.register_age_edit);
        confirmBtn = (Button) findViewById(R.id.register_confirm_btn);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userString = userEdit.getText().toString();
                pwdString = pwdEdit.getText().toString();
                pwdConfirmString = pwdConfirmEdit.getText().toString();
                ageString = ageEdit.getText().toString();
                if (checkRegisterInfo()) {
                    Integer age = Integer.parseInt(ageString);
                    registerUserToBmob(userString, pwdString, age);//注册
                    Intent intent = new Intent(RegisterActivity.this, LoginInActivity.class);
                    startActivity(intent);
                }
            }
        });
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);//设置左上角可见图标
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    public boolean checkRegisterInfo(){
        //用户名长度=0
        if (userString.length() == 0) {
            Toast.makeText(getApplicationContext(), "用户名为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        //密码长度小于最小长度
        else if (pwdString.length() <= PWD_LENGTH) {
            Toast.makeText(getApplicationContext(), "密码必须大于8位", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!isPwdAvaliable(pwdString)) {
            //判断密码格式为字母和数字的组合
            Toast.makeText(getApplicationContext(), "密码必须由字母或者数字组成", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!isAgeAvaliable(ageString)) {
            Toast.makeText(getApplicationContext(), "请输入正确的年龄格式", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(!pwdConfirmString.equals(pwdString)){
            Toast.makeText(getApplicationContext(), "两次密码输入不一致", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    public void registerUserToBmob(String userString,String pwdString,Integer age){
        MyUser myUser = new MyUser();
        myUser.setUsername(userString);
        myUser.setPassword(pwdString);
        myUser.setAge(age);
        myUser.signUp(getApplicationContext(), new SaveListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(getApplicationContext(), "注册成功", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onFailure(int i, String s) {
                Toast.makeText(getApplicationContext(), "该用户名已经被注册", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 判断年龄格式是否合法
     * @param ageString
     * @return
     */
    public boolean isAgeAvaliable(String ageString){
        Pattern pattern = Pattern.compile("[0-9]*");
        if(!pattern.matcher(ageString).matches()){
            return false;
        }
        return true;
    }
    public boolean isPwdAvaliable(String pwdString) {
        Pattern pattern = Pattern.compile("[[0-9]*|[a-z]*|[A-Z]*]*");
        if (!pattern.matcher(pwdString).matches()) {
            return false;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
