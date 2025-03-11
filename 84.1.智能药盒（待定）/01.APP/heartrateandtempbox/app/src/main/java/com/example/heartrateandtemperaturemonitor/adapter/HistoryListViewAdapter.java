package com.example.heartrateandtemperaturemonitor.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.heartrateandtemperaturemonitor.dao.History;
import com.example.heartrateandtemperaturemonitor.databinding.HistoryListviewItemBinding;
import com.example.heartrateandtemperaturemonitor.databinding.UserListviewItemBinding;

import java.util.List;

public class HistoryListViewAdapter extends BaseAdapter {
    private Context context;
    private List<Object> list;

    public HistoryListViewAdapter(Context context, List<Object> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            HistoryListviewItemBinding binding = HistoryListviewItemBinding.inflate(LayoutInflater.from(context), viewGroup, false);
            view = binding.getRoot();
            holder = new ViewHolder(binding);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        initView(holder, i);
        return view;
    }

    private void initView(ViewHolder holder, int i) {
        History history = (History) list.get(i);
        Log.e("数据", history.toString());
        holder.binding.dataTimeText.setText(history.getCreateDateTime());
        if (history.getTemp() != null) {
            holder.binding.tempText.setText(history.getTemp());
        } else {
            holder.binding.tempText.setText("暂无");
        }
        if (history.getHreat() != null) {
            holder.binding.heartText.setText(history.getHreat());
        } else {
            holder.binding.heartText.setText("暂无");
        }
        if (history.getBlood() != null) {
            holder.binding.oText.setText(history.getBlood());
        } else {
            holder.binding.oText.setText("暂无");
        }

    }

    private class ViewHolder {
        private HistoryListviewItemBinding binding;

        private ViewHolder(HistoryListviewItemBinding binding) {
            this.binding = binding;
        }
    }
}
