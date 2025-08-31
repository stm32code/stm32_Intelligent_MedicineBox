package com.example.heartrateandtemperaturemonitor.utils;

import android.content.Context;
import android.view.LayoutInflater;

import androidx.appcompat.app.AlertDialog;

import com.example.heartrateandtemperaturemonitor.dao.User;
import com.example.heartrateandtemperaturemonitor.dao.UserDao;
import com.example.heartrateandtemperaturemonitor.databinding.UpdateAlertdialogViewBinding;


public class AlertDialogT implements HandlerAction {
    private Context context;
    private User user;
    private OnChangeData theOnChangeData;
    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;
    private UpdateAlertdialogViewBinding binding;

    public AlertDialogT(Context context, User user, OnChangeData theOnChangeData) {
        this.context = context;
        this.user = user;
        this.theOnChangeData = theOnChangeData;
        builder = new AlertDialog.Builder(context);
    }

    /***
     * 显示弹窗
     */
    public void showDialog() {
        binding = UpdateAlertdialogViewBinding.inflate(LayoutInflater.from(context));
        builder.setTitle("修改数据").setView(binding.getRoot());
        alertDialog = builder.create();
        binding.userID.setText(String.valueOf(user.getUid()));
        binding.name.setText(user.getUname());
        binding.password.setText(user.getUpassword());
        binding.phone.setText(user.getJphone());
        binding.submitBtn.setOnClickListener(view -> {
            String password = binding.password.getText().toString();
            String phone = binding.phone.getText().toString();
            if (password.isEmpty() || phone.isEmpty()) {
                MToast.mToast(context, "修改信息不能为空");
                return;
            }
            user.setUpassword(password);
            user.setJphone(phone);
            new UserDao(context).update(user, String.valueOf(user.getUid()));
            MToast.mToast(context, "修改完成");
            postDelayed(() -> {
                dismissDialog();
                theOnChangeData.onChange("Alert");
            }, 700);
        });
        alertDialog.show();
    }

    /**
     * 取消弹窗
     */
    public void dismissDialog() {
        alertDialog.dismiss();
    }
}
