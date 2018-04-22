package com.remia.lab3_pedometer.util;

import android.graphics.Color;
import android.view.View;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.remia.lab3_pedometer.R;

/**
 * Created by 23533 on 2018/4/19.
 */

public class AccelChart {
    private LineDataSet lineDataSet;
    private LineData lineData;
    private Entry entry;
    private LineChart mLineChart;

    int numOfPoint = 50;

    private ChartListener listener;

    public void registerListener(ChartListener listener) {
        this.listener = listener;
    }

    public void initLineChart(){
        mLineChart=listener.showChart();

        lineData = new LineData();
        lineDataSet = new LineDataSet(null, "test");
        mLineChart.setData(lineData);

        mLineChart.setTouchEnabled(true);
        mLineChart.setDragEnabled(true);// 可拖曳
        mLineChart.setScaleEnabled(true);// 可缩放
        mLineChart.setDrawGridBackground(false);
        mLineChart.setPinchZoom(true);
        mLineChart.getAxisRight().setEnabled(false);// 隐藏右边的坐标轴
        mLineChart.getXAxis().setGridColor(Color.TRANSPARENT);//去掉网格中竖线的显示

        lineDataSet.setDrawCircles(false);
        lineDataSet.setDrawFilled(true);
        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        mLineChart.invalidate();
    }

    public void plotupdate(float plotA){
        if (lineDataSet.getEntryCount() == 0)
            lineData.addDataSet(lineDataSet);
        lineData.setDrawValues(false);
        mLineChart.setData(lineData);
        entry = new Entry(lineDataSet.getEntryCount(), plotA);
        lineData.addEntry(entry, 0);
        lineData.notifyDataChanged();
        mLineChart.notifyDataSetChanged();
        mLineChart.setVisibleXRangeMinimum(numOfPoint);
        mLineChart.setVisibleXRangeMaximum(numOfPoint);
        mLineChart.moveViewToX(lineData.getEntryCount() - 5);
    }
}
