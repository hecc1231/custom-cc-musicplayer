package com.miniccmusicplayer.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.hersch.musicplayer.R;
import com.miniccmusicplayer.bean.MyUser;

import java.util.regex.Pattern;

import cn.bmob.v3.listener.SaveListener;

public class RegisterUserFragment extends Fragment {
    private EditText mUserEdit;
    private EditText mPwdEdit;
    private Button mSignBtn;
    private final int PWD_LENGTH = 8;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.register_frg_user, container, false);
        initViews(view);
        return view;
    }
    public void initViews(View view){
        mUserEdit = (EditText)view.findViewById(R.id.user_frg_user_edit);
        mPwdEdit = (EditText)view.findViewById(R.id.user_frg_pwd_edit);
        mSignBtn = (Button)view.findViewById(R.id.user_frg_sign_btn);
        mSignBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkRegisterInfo()) {
                    registerUserToBmob();
                }
            }
        });
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        myUser.setUsername(mUserEdit.getText().toString());
        myUser.setPassword(mPwdEdit.getText().toString());
        myUser.signUp(getActivity().getApplicationContext(), new SaveListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(getContext(), "注册成功", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
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
