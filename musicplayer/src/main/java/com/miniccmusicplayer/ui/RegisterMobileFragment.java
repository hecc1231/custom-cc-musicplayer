package com.miniccmusicplayer.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.hersch.musicplayer.R;
import com.miniccmusicplayer.bean.MyUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.RequestSMSCodeListener;
import cn.bmob.v3.listener.SaveListener;

public class RegisterMobileFragment extends Fragment {
    private Button mSignBtn;
    private EditText mPwdEdit;
    private EditText mMobileEdit;
    private EditText mSmsEdit;
    private Button mSmsBtn;
    private final int PWD_LENGTH = 8;
    private final String SMS_MODEL = "SMS";
    private int time = 120;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.register_frg_mobile, container, false);
        initViews(view);
        cn.bmob.sms.BmobSMS.initialize(getContext(), HelloActivity.APP_ID);
        return view;
    }

    public void initViews(View view) {
        mMobileEdit = (EditText) view.findViewById(R.id.mobile_frg_mobile_edit);
        mPwdEdit = (EditText) view.findViewById(R.id.mobile_frg_pwd_edit);
        mSmsBtn = (Button) view.findViewById(R.id.mobile_frg_sms_btn);
        mSmsEdit = (EditText) view.findViewById(R.id.mobile_frg_sms_edit);
        mSignBtn = (Button) view.findViewById(R.id.mobile_frg_sign_btn);
        mSignBtn.setClickable(false);
        mSignBtn.setBackgroundColor(Color.LTGRAY);
        mSignBtn.setOnClickListener(onClickListener);
        mSmsBtn.setOnClickListener(onClickListener);
    }
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.mobile_frg_sms_btn:
                    BmobSMS.requestSMSCode(getContext(), mMobileEdit.getText().toString(),SMS_MODEL, new RequestSMSCodeListener() {
                        @Override
                        public void done(Integer integer, BmobException e) {
                            if (e == null) {
                                Toast.makeText(getContext(), "验证码短信发送成功", Toast.LENGTH_SHORT).show();
                                mSignBtn.setClickable(true);
                                mSignBtn.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_dark));
                                setSmsTimer();
                            } else {
                                Toast.makeText(getContext(), "验证码短信发送失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    break;
                case R.id.mobile_frg_sign_btn:
                    if (checkRegisterInfo()) {
                        registerUserToBmob();//注册
                    }
                    break;
            }
        }
    };
    public void setSmsTimer() {
        mSmsBtn.setClickable(false);
        mSmsBtn.setBackgroundColor(Color.LTGRAY);
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(time>=0) {
                    mSmsBtn.setText("剩余" +time+"s");
                    time--;
                    handler.postDelayed(this, 1000);
                }
                else{
                    mSmsBtn.setClickable(true);
                    mSmsBtn.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_dark));
                }
            }
        });
    }

    public boolean checkRegisterInfo() {
        //密码长度小于最小长度
        String pwdString = mPwdEdit.getText().toString();
        if (pwdString.length() <= PWD_LENGTH) {
            Toast.makeText(getActivity().getApplicationContext(), "密码必须大于8位", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!isPwdAvaliable(pwdString)) {
            //判断密码格式为字母和数字的组合
            Toast.makeText(getActivity().getApplicationContext(), "密码必须由字母或者数字组成", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void registerUserToBmob() {
        MyUser myUser = new MyUser();
        myUser.setPassword(mPwdEdit.getText().toString());
        myUser.setMobilePhoneNumber(mMobileEdit.getText().toString());
        myUser.signOrLogin(getContext(), mSmsEdit.getText().toString(), new SaveListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(getContext(), "注册成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int i, String s) {
                Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
            }
        });
    }
    public boolean isPwdAvaliable(String pwdString) {
        Pattern pattern = Pattern.compile("[[0-9]*|[a-z]*|[A-Z]*]*");
        if (!pattern.matcher(pwdString).matches()) {
            return false;
        }
        return true;
    }
}
