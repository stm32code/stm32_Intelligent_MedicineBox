package com.example.heartrateandtemperaturemonitor;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.blankj.utilcode.util.LogUtils;
import com.example.heartrateandtemperaturemonitor.bean.Receive;
import com.example.heartrateandtemperaturemonitor.dao.User;
import com.example.heartrateandtemperaturemonitor.dao.UserDao;
import com.example.heartrateandtemperaturemonitor.databinding.ActivityLoginBinding;
import com.example.heartrateandtemperaturemonitor.utils.Common;
import com.example.heartrateandtemperaturemonitor.utils.MToast;
import com.google.gson.Gson;
import com.gyf.immersionbar.ImmersionBar;
import com.itfitness.mqttlibrary.MQTTHelper;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private UserDao dao;

    // 适配8.0及以上 创建渠道
    private final String mNormalChannelId = "my_notification_normal";
    private final String mNormalChannelName = "重要通知";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        dao = new UserDao(this);
        getPermission();
        SharedPreferences sharedPreferences = getSharedPreferences("Location", MODE_PRIVATE);
        Common.sendTitle = sharedPreferences.getString("theData", "今日药物，布洛芬两颗，请及时用药");
        initView();
    }

    private void getPermission() {

        List<String> perms = new ArrayList<>();
        perms.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        perms.add(Manifest.permission.ACCESS_FINE_LOCATION);
        perms.add(Manifest.permission.VIBRATE);
        perms.add(Manifest.permission.RECEIVE_BOOT_COMPLETED);
        perms.add(Manifest.permission.ACCESS_NOTIFICATION_POLICY);
        perms.add(Manifest.permission.ACCESS_NETWORK_STATE);
        perms.add(Manifest.permission.INTERNET);
        perms.add(Manifest.permission.CALL_PHONE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { //13
            perms.add(Manifest.permission.POST_NOTIFICATIONS);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {//12
            perms.add(Manifest.permission.BLUETOOTH_CONNECT);
            perms.add(Manifest.permission.BLUETOOTH_SCAN);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {//10
            perms.add(Manifest.permission.USE_FULL_SCREEN_INTENT);
        }
        if (!EasyPermissions.hasPermissions(this, perms.toArray(new String[0]))) {
            //请求权限
            EasyPermissions.requestPermissions(this, "这是必要的权限", 100, perms.toArray(new String[0]));
        }

    }


    private void initView() {
        setSupportActionBar(binding.toolbar);
        binding.toolbarLayout.setTitle("登录");
        ImmersionBar.with(this).init();

        //Android版本必须大于8.0才能使用该通知组
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(mNormalChannelId, // 渠道ID
                    mNormalChannelName,// 渠道名称
                    NotificationManager.IMPORTANCE_HIGH//重要程度
            );
            channel.setDescription("这是重要的通知，必须开启");
            channel.setShowBadge(true);
            channel.setImportance(NotificationManager.IMPORTANCE_HIGH);
            channel.setBypassDnd(true);//绕过免打扰
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);//设置显示，即使在息屏
            channel.enableVibration(true);//设置振动
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        binding.loginBtn.setOnClickListener(view -> {
            verifyData();
        });
        /***
         * 跳转注册
         */
        binding.skipRegisterBtn.setOnClickListener(view -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }


    private void verifyData() {
        String name = binding.inputNameEdit.getText().toString();
        String password = binding.inputPasswordEdit.getText().toString();
        if (name.isEmpty()) {
            MToast.mToast(this, "用户名不能为空");
            return;
        }
        if (password.isEmpty()) {
            MToast.mToast(this, "密码不能为空");
            return;
        }
        List<Object> objects = dao.query(name, password);
        if (objects.size() == 0) {
            MToast.mToast(this, "账号或密码错误");
            return;
        }
        User user = (User) objects.get(0);
        if (user.getUname().equals(name) && user.getUpassword().equals(password)) {
            Common.createNotificationForNormal(this, "用药提醒", Common.sendTitle, 0);
            MToast.mToast(this, Common.sendTitle);
            if (user.getPer() == 1) startActivity(new Intent(this, AdminActivity.class));
            else {
                Common.user = user;
                startActivity(new Intent(this, MainActivity.class));
            }
            finish();
        } else {
            MToast.mToast(this, "账号或密码错误");
        }
    }
}