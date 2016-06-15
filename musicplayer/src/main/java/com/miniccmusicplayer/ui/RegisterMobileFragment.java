package com.miniccmusicplayer.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.hersch.musicplayer.R;

import java.util.regex.Pattern;

public class RegisterMobileFragment extends Fragment {
    private Button mSignBtn;
    private EditText mPwdEdit;
    private EditText mMobileEdit;
    private EditText mSmsEdit;
    private Button mSmsBtn;
    private final int PWD_LENGTH = 8;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.register_frg_mobile, container, false);
        initViews(view);
        return view;
    }

    public void initViews(View view) {
        mMobileEdit = (EditText)view.findViewById(R.id.mobile_frg_mobile_edit);
        mPwdEdit = (EditText) view.findViewById(R.id.mobile_frg_pwd_edit);
        mSmsBtn = (Button) view.findViewById(R.id.mobile_frg_sms_btn);
        mSmsEdit = (EditText)view.findViewById(R.id.mobile_frg_sms_edit);
        mSignBtn = (Button)view.findViewById(R.id.mobile_frg_sign_btn);
        mSignBtn.setClickable(false);
        mSignBtn.setBackgroundColor(Color.LTGRAY);
        mSignBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkRegisterInfo()) {
                    registerUserToBmob();//注册
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

//        MyUser myUser = new MyUser();
//        myUser.setPassword(pwdEdit.getText().toString());
//        myUser.setMobilePhoneNumber(mobileEdit.getText().toString());
//        myUser.signUp(getApplicationContext(), new SaveListener() {
//            @Override
//            public void onSuccess() {
//                Toast.makeText(getApplicationContext(), "注册成功", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(RegisterActivity.this, LoginInActivity.class);
//                startActivity(intent);
//            }
//            @Override
//            public void onFailure(int i, String s) {
//                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    /**
     * 判断年龄格式是否合法
     *
     * @param ageString
     * @return
     */
    public boolean isAgeAvaliable(String ageString) {
        Pattern pattern = Pattern.compile("[0-9]*");
        if (!pattern.matcher(ageString).matches()) {
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
}
