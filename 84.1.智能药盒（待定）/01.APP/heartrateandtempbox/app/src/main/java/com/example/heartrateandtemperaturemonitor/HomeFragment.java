package com.example.heartrateandtemperaturemonitor;


import static android.content.Context.MODE_PRIVATE;

import static com.example.heartrateandtemperaturemonitor.utils.Common.PushTopic;

import android.Manifest;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuHost;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.heartrateandtemperaturemonitor.bean.Receive;
import com.example.heartrateandtemperaturemonitor.bean.Send;
import com.example.heartrateandtemperaturemonitor.chart.CreationChart;
import com.example.heartrateandtemperaturemonitor.dao.History;
import com.example.heartrateandtemperaturemonitor.dao.HistoryDao;
import com.example.heartrateandtemperaturemonitor.dao.UserDao;
import com.example.heartrateandtemperaturemonitor.databinding.FragmentHomeBinding;
import com.example.heartrateandtemperaturemonitor.utils.BeatingAnimation;
import com.example.heartrateandtemperaturemonitor.utils.Common;
import com.example.heartrateandtemperaturemonitor.utils.HandlerAction;
import com.example.heartrateandtemperaturemonitor.utils.MToast;
import com.example.heartrateandtemperaturemonitor.utils.TimeCycle;
import com.example.heartrateandtemperaturemonitor.utils.ViewSetIsClick;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gyf.immersionbar.ImmersionBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;


public class HomeFragment extends Fragment implements HandlerAction {
    private FragmentHomeBinding binding;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private HistoryDao dao;
    private Map<String, String> map = new HashMap<>();
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_scrolling, menu);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        sharedPreferences = getContext().getSharedPreferences("Location", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        dao = new HistoryDao(getContext());
        EventBus.getDefault().register(this);
        setHasOptionsMenu(true);
        initView();
        binding.timeText1.setText(map.get("time1"));
        binding.timeText2.setText(map.get("time2"));
        binding.timeText3.setText(map.get("time3"));
        return binding.getRoot();
    }

    /***
     * 初始化控件
     */
    private void initView() {
        //转换为中国时区
        TimeZone.setDefault(TimeZone.getTimeZone("Etc/GMT-8"));
        String value = sharedPreferences.getString("theTime", null);
        if (value != null) {
            Type type = new TypeToken<Map<String, String>>() {
            }.getType();
            map = new Gson().fromJson(value, type);
        } else {
            map.put("time1", "0");
            map.put("time2", "0");
            map.put("time3", "0");

        }
        binding.toolbarLayout.setTitle(getActivity().getTitle());
        ImmersionBar.with(this).init();
        warringLayout(false, "");
        ViewSetIsClick.setViewIsClick(binding.phone, false);
        ViewSetIsClick.setViewIsClick(binding.updateEdit, false);
        binding.updateEdit.setText(Common.sendTitle);
        binding.phone.setText(String.valueOf(Common.user.getJphone()));
        binding.updateBtn.setOnClickListener(view -> {
            if (binding.updateBtn.getText().toString().equals("修改")) {
                ViewSetIsClick.setViewIsClick(binding.phone, true);
                binding.updateBtn.setText("提交");
            } else {
                String phone = binding.phone.getText().toString();
                if (phone.isEmpty()) {
                    MToast.mToast(getContext(), "不能为空");
                    return;
                }
                Common.user.setJphone(phone);
                new UserDao(getContext()).update(Common.user, Common.user.getUid() + "");
                MToast.mToast(getContext(), "修改成功");
                binding.phone.setText(phone);
                ViewSetIsClick.setViewIsClick(binding.phone, false);
                binding.updateBtn.setText("修改");
            }
        });
        binding.outlineBtn.setOnClickListener(view -> {
            Common.user = null;
            //退出登录
            startActivity(new Intent(getContext(), LoginActivity.class));
            getActivity().finish();
        });

        binding.callBtn.setOnClickListener(view -> {
            call("tel:" + binding.phone.getText().toString());
        });
        // 时间一
        binding.timeText1.setOnClickListener(view -> {
            // 获取当前时间
            Calendar currentTime = Calendar.getInstance();
            // 创建 TimePickerDialog 对话框
            TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), (view1, hourOfDay, minute) -> {
                // 当前选择的时间
                Calendar selectedTime = Calendar.getInstance();
                selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                selectedTime.set(Calendar.MINUTE, minute);
                String tempData = String.format("%02d:%02d", hourOfDay, minute);
                binding.timeText1.setText(String.format("%02d:%02d", hourOfDay, minute));
                String temp1 = binding.timeText1.getText().toString();
                String temp2 = binding.timeText2.getText().toString();
                String temp3 = binding.timeText3.getText().toString();
                String send1, send2,send3;
                if (!temp1.equals("0")) {
                    send1 = temp1.split(":")[0] + temp1.split(":")[1];
                } else {
                    send1 = "0";
                }
                if (!temp2.equals("0")) {
                    send2 = temp2.split(":")[0] + temp2.split(":")[1];
                } else {
                    send2 = "0";
                }
                if (!temp3.equals("0")) {
                    send3 = temp2.split(":")[0] + temp3.split(":")[1];
                } else {
                    send3 = "0";
                }
                map.replace("time1", tempData);
                sendMessage(1, send1, send2);

                editor.putString("theTime", new Gson().toJson(map));
                editor.commit();
            }, currentTime.get(Calendar.HOUR_OF_DAY), currentTime.get(Calendar.MINUTE), true);
            // 显示 TimePickerDialog 对话框
            timePickerDialog.show();
        });
        // 时间一
        binding.timeText2.setOnClickListener(view -> {
            // 获取当前时间
            Calendar currentTime = Calendar.getInstance();
            // 创建 TimePickerDialog 对话框
            TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), (view1, hourOfDay, minute) -> {
                // 当前选择的时间
                Calendar selectedTime = Calendar.getInstance();
                selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                selectedTime.set(Calendar.MINUTE, minute);
                String tempData = String.format("%02d:%02d", hourOfDay, minute);
                binding.timeText2.setText(String.format("%02d:%02d", hourOfDay, minute));
                String temp1 = binding.timeText1.getText().toString();
                String temp2 = binding.timeText2.getText().toString();
                String temp3 = binding.timeText3.getText().toString();
                String send1, send2,send3;
                if (!temp1.equals("0")) {
                    send1 = temp1.split(":")[0] + temp1.split(":")[1];
                } else {
                    send1 = "0";
                }
                if (!temp2.equals("0")) {
                    send2 = temp2.split(":")[0] + temp2.split(":")[1];
                } else {
                    send2 = "0";
                }
                if (!temp3.equals("0")) {
                    send3 = temp2.split(":")[0] + temp3.split(":")[1];
                } else {
                    send3 = "0";
                }
                map.replace("time2", tempData);
                sendMessage(1, send1, send2);

                editor.putString("theTime", new Gson().toJson(map));
                editor.commit();
            }, currentTime.get(Calendar.HOUR_OF_DAY), currentTime.get(Calendar.MINUTE), true);
            // 显示 TimePickerDialog 对话框
            timePickerDialog.show();
        });
        // 时间一
        binding.timeText3.setOnClickListener(view -> {
            // 获取当前时间
            Calendar currentTime = Calendar.getInstance();
            // 创建 TimePickerDialog 对话框
            TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), (view1, hourOfDay, minute) -> {
                // 当前选择的时间
                Calendar selectedTime = Calendar.getInstance();
                selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                selectedTime.set(Calendar.MINUTE, minute);
                String tempData = String.format("%02d:%02d", hourOfDay, minute);
                binding.timeText3.setText(String.format("%02d:%02d", hourOfDay, minute));
                String temp1 = binding.timeText1.getText().toString();
                String temp2 = binding.timeText2.getText().toString();
                String temp3 = binding.timeText3.getText().toString();
                String send1, send2,send3;
                if (!temp1.equals("0")) {
                    send1 = temp1.split(":")[0] + temp1.split(":")[1];
                } else {
                    send1 = "0";
                }
                if (!temp2.equals("0")) {
                    send2 = temp2.split(":")[0] + temp2.split(":")[1];
                } else {
                    send2 = "0";
                }
                if (!temp3.equals("0")) {
                    send3 = temp3.split(":")[0] + temp3.split(":")[1];
                } else {
                    send3 = "0";
                }
                map.replace("time2", tempData);
                sendMessage(2, send3, send2);

                editor.putString("theTime", new Gson().toJson(map));
                editor.commit();
            }, currentTime.get(Calendar.HOUR_OF_DAY), currentTime.get(Calendar.MINUTE), true);
            // 显示 TimePickerDialog 对话框
            timePickerDialog.show();
        });
        binding.timeButton.setOnClickListener(view -> {
            // 定义原始格式和目标格式
            DateTimeFormatter originalFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            DateTimeFormatter targetFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

            // 解析原始字符串为LocalDateTime对象
            LocalDateTime dateTime = LocalDateTime.parse(TimeCycle.getDateTime(), originalFormatter);

            // 格式化日期时间为目标格式
            String formattedDateTime = dateTime.format(targetFormatter);

            // 输出格式化后的日期时间
            System.out.println("格式化后的日期时间:" + formattedDateTime);
            sendMessage(3, formattedDateTime);


        });
        binding.updateBtn2.setOnClickListener(view -> {
            if (binding.updateBtn2.getText().toString().equals("修改")) {
                ViewSetIsClick.setViewIsClick(binding.updateEdit, true);
                binding.updateBtn2.setText("提交");
            } else {
                String data = binding.updateEdit.getText().toString();
                if (data.isEmpty()) {
                    MToast.mToast(getContext(), "不能为空");
                    return;
                }
                editor.putString("theData", data);
                editor.commit();
                Common.sendTitle = data;
                MToast.mToast(getContext(), "修改成功");
                binding.updateEdit.setText(data);
                ViewSetIsClick.setViewIsClick(binding.updateEdit, false);
                binding.updateBtn2.setText("修改");
            }
        });


    }
    /**
     * @param message 需要发送的消息
     * @brief 再次封装MQTT发送
     */
    private void sendMessage(int cmd, String... message) {
        if (Common.mqttHelper != null && Common.mqttHelper.getConnected()) {
            Send send = new Send();
            switch (cmd) {
                case 1:
                case 2:
                    send.setTime1(message[0]);
                    send.setTime2(message[1]);
                    break;
                case 3:
                    send.setTime(message[0]);
                    break;
            }
            send.setCmd(cmd);
            String str = new Gson().toJson(send);
            new Thread(() -> Common.mqttHelper.publish(PushTopic, str, 1)).start();
            //debugViewData(1, str);
        }
    }
    /**
     * 解析数据
     *
     * @param result
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveDataFormat(Receive result) {
        try {
            boolean saveFlag = false;
            History history = new History();
            if (result.getTemp() != null) {
                saveFlag = true;
                binding.tempText.setText(result.getTemp());
                history.setTemp(result.getTemp());
            }
            if (result.getHreat() != null) {
                saveFlag = true;
                binding.heartText.setText(result.getHreat());
                history.setHreat(result.getHreat());
                // 添加数据到曲线中
                CreationChart.addEntry(binding.chartView, result);
            }

            if (result.getBlood() != null) {
                saveFlag = true;
                binding.oText.setText(result.getBlood());
                history.setBlood(result.getHreat());
            }
            if (result.getWaning() != null) {
                if (result.getWaning().equals("1")) {
                    warringLayout(true, "警告");

                } else {
                    warringLayout(false, "");
                }
            }
            if (saveFlag)
                dao.insert(history);
        } catch (Exception e) {
            e.printStackTrace();
            MToast.mToast(getContext(), "数据解析失败");
        }
    }

    /**
     * @param visibility 是否显示
     * @param str        显示内容
     * @brief 显示警告弹窗和设置弹窗内容
     */
    private void warringLayout(boolean visibility, String str) {
        if (visibility) {
            binding.warringLayout.setVisibility(View.VISIBLE);
            binding.warringText.setText(str);
            new BeatingAnimation().onAnimation(binding.warringImage);
        } else {
            binding.warringLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 拨打电话（直接拨打）
     *
     * @param telPhone 电话
     */
    public void call(String telPhone) {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(telPhone));
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.setHistoryView) {
            startActivity(new Intent(getActivity(), HistoryActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(binding.toolbar);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}