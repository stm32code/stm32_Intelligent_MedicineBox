package com.example.heartrateandtemperaturemonitor;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.RadioButton;

import com.example.heartrateandtemperaturemonitor.dao.User;
import com.example.heartrateandtemperaturemonitor.dao.UserDao;
import com.example.heartrateandtemperaturemonitor.databinding.ActivityRegisterBinding;
import com.example.heartrateandtemperaturemonitor.utils.MToast;
import com.gyf.immersionbar.ImmersionBar;

import java.util.List;
import java.util.Objects;
import java.util.Random;

public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding binding;
    private UserDao dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        dao = new UserDao(this);
        initView();
    }

    private void initView() {
        setSupportActionBar(binding.toolbar);
        binding.toolbarLayout.setTitle("注册用户");
        ImmersionBar.with(this).init();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);//添加默认的返回图标
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用

        binding.registerBtn.setOnClickListener(view -> {
            verifyData();
        });
    }

    /***
     * 数据验证
     */
    private void verifyData() {
        User user = new User();
        String name = binding.inputNameEdit.getText().toString();
        String password = binding.inputPasswordEdit.getText().toString();
        String phone = binding.inputPhoneEdit.getText().toString();
        if (name.isEmpty()) {
            MToast.mToast(this, "用户名不能为空");
            return;
        }
        if (password.isEmpty()) {
            MToast.mToast(this, "密码不能为空");
            return;
        }
        if (phone.isEmpty()) {
            MToast.mToast(this, "紧急联系人不能为空");
            return;
        }
        List<Object> objects = dao.query(name, "name");
        if (objects.size() != 0) {
            MToast.mToast(this, "已有该用户，请直接登录");
            return;
        }
        user.setUname(name);
        user.setUpassword(password);
        user.setJphone(phone);
        dao.insert(user);
        MToast.mToast(this, "添加成功");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}