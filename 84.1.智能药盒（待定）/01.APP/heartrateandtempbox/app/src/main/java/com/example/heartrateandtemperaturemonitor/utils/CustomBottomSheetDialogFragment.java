package com.example.heartrateandtemperaturemonitor.utils;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.heartrateandtemperaturemonitor.adapter.UserListViewAdapter;
import com.example.heartrateandtemperaturemonitor.dao.User;
import com.example.heartrateandtemperaturemonitor.dao.UserDao;
import com.example.heartrateandtemperaturemonitor.databinding.BottomSheetDialogFrgmentLayoutBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;


public class CustomBottomSheetDialogFragment extends BottomSheetDialogFragment implements HandlerAction {
    private BottomSheetDialogFrgmentLayoutBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 填充底部弹窗的布局文件
        binding = BottomSheetDialogFrgmentLayoutBinding.inflate(
                inflater,
                container,
                false
        );
        UserDao dao = new UserDao(getContext());
        List<Object> list = dao.query();
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                User u = (User) list.get(i);
                if (u.getUname().equals("admin")) {
                    list.remove(i);
                    break;
                }
            }
            if (list.size() > 0) {
                binding.settingList.setAdapter(new UserListViewAdapter(getContext(), list));
            } else {
                MToast.mToast(getContext(), "还没有数据");
            }
        }
        return binding.getRoot();
    }
}
