package com.example.heartrateandtemperaturemonitor.chart;

import static com.github.mikephil.charting.components.YAxis.AxisDependency.LEFT;

import android.graphics.Color;


import com.example.heartrateandtemperaturemonitor.bean.Receive;
import com.example.heartrateandtemperaturemonitor.utils.TimeCycle;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CreationChart {

    private static List<String> xValues = new ArrayList<>();

    /**
     * 初始化Chart
     *
     * @param chart
     */
    public static void initChart(LineChart chart) {
        // 开启文本描述
        chart.getDescription().setEnabled(true);
        // 开启触摸手势
        chart.setTouchEnabled(true);
        // 允许缩放和拖动
        chart.setDragEnabled(true); //拖动
        chart.setScaleEnabled(true); //缩放
        chart.setDrawGridBackground(false);
        // 如果禁用，可以分别在x轴和y轴上进行缩放
        chart.setPinchZoom(true);
        // 设置一个替代背景
        //chart.setBackgroundColor(Color.rgb(255, 255, 255));
        LineData data = new LineData();
        data.setValueTextColor(Color.WHITE);
        // 添加空数据
        chart.setData(data);
        XAxis xl = chart.getXAxis();
        xl.setPosition(XAxis.XAxisPosition.BOTTOM); // 设置 x 轴显示在底部
        xl.setTextColor(Color.WHITE);
        xl.setDrawGridLines(false);
        xl.setAxisLineColor(Color.rgb(126, 198, 153));
        xl.setTextColor(Color.rgb(126, 198, 153));
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);
        xl.setGranularity(2);//每隔5个数据才显示
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setAxisMaximum(150f); //最大条目
        leftAxis.setAxisMinimum(0f);//最小条目
        leftAxis.setLabelCount(10);//设置最大分为几格
        leftAxis.setDrawGridLines(true);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setAxisLineColor(Color.rgb(126, 198, 153));
        leftAxis.setTextColor(Color.rgb(126, 198, 153));
        leftAxis.setEnabled(true);
        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);
    }

    /***
     * 添加条目
     * @param chart
     * @param d
     */
    public static void addEntry(LineChart chart, Receive d) {
        LineData data = chart.getData();
        xValues.add(TimeCycle.getDateTime().split(" ")[1]);
        List<LineDataSet> dataSet = new ArrayList<LineDataSet>();
        float[] values = {Float.parseFloat(d.getBlood()), Float.parseFloat(d.getHreat())};
        if (data != null && data.getDataSetCount() > 0) {
            for (int i = 0; i < 2; i++) {
                dataSet.add((LineDataSet) data.getDataSetByIndex(i));
                data.addEntry(new Entry(dataSet.get(i).getEntryCount(), values[i]), i);
            }
            data.notifyDataChanged();
        } else {
            for (int i = 0; i < 2; i++) {
                dataSet.add(createSet(i));
            }
            LineData lineData = new LineData(dataSet.get(0), dataSet.get(1));
            chart.setData(lineData);
        }

        // 更新 x 轴上的时间
        setXAxisValues(chart);
        // 限制x可见数目
        chart.setVisibleXRange(5, 5);
        // 提交数据数据改变，更新图表
        chart.notifyDataSetChanged();
        // 移动到最新条目
        if (chart.getData().getDataSetByIndex(0).getEntryCount() > 0)
            chart.moveViewToX(chart.getData().getDataSetByIndex(0).getEntryCount());
    }

    /**
     * 时间格式
     */
    private static String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    /**
     * 设置x轴的值
     */
    private static void setXAxisValues(LineChart chart) {
        if (xValues.size() >= 5000) {
            xValues.remove(0);
        }
//        xValues.add(getCurrentTime());
        chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xValues));
    }

    /**
     * 新建数据格式
     *
     * @return
     */
    private static LineDataSet createSet(int i) {
        LineDataSet set;
        switch (i) {
            case 1:
                set = new LineDataSet(null, "心率");
                set.setColor(Color.rgb(255, 102, 102)); //折线颜色
                break;
            default:
                set = new LineDataSet(null, "血氧");
                set.setColor(Color.rgb(255, 178, 102)); //折线颜色
        }
        set.setAxisDependency(LEFT);
        set.setLineWidth(2f);
        set.setCircleRadius(4f);
        set.setFillAlpha(65);//填充透明度
        set.setFillColor(ColorTemplate.getHoloBlue());
        set.setHighLightColor(Color.rgb(124, 117, 117));//高亮颜色
        set.setDrawValues(false);//是否显示数值
        set.setDrawCircles(true);//是否在每个节点上画上一个圆圈
        set.setDrawFilled(false); //是否在折线下方填满颜色
        set.setMode(LineDataSet.Mode.LINEAR);
        return set;
    }
}
