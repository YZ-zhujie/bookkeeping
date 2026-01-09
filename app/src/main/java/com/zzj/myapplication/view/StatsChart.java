package com.zzj.myapplication.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 自定义统计折线图 View
 * 用于展示每日支出/收入趋势
 * X轴: 日期 (MM-dd)
 * Y轴: 金额
 */
public class StatsChart extends View {

    private Paint linePaint;
    private Paint pointPaint;
    private Paint axisPaint;
    private Paint textPaint;
    private List<ChartData> dataList;
    
    // 配置参数
    private int axisColor = Color.parseColor("#888888");
    private int lineColor = Color.parseColor("#2196F3");
    private int pointColor = Color.parseColor("#FF5722");
    private int textColor = Color.parseColor("#333333");

    public StatsChart(Context context) {
        super(context);
        init();
    }

    public StatsChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public StatsChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * 初始化画笔配置
     */
    private void init() {
        linePaint = new Paint();
        linePaint.setColor(lineColor);
        linePaint.setStrokeWidth(5f);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setAntiAlias(true);

        pointPaint = new Paint();
        pointPaint.setColor(pointColor);
        pointPaint.setStyle(Paint.Style.FILL);
        pointPaint.setAntiAlias(true);
        pointPaint.setStrokeWidth(10f); // 点的大小

        axisPaint = new Paint();
        axisPaint.setColor(axisColor);
        axisPaint.setStrokeWidth(2f);
        axisPaint.setStyle(Paint.Style.STROKE);

        textPaint = new Paint();
        textPaint.setColor(textColor);
        textPaint.setTextSize(24f); 
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);

        dataList = new ArrayList<>();
    }

    /**
     * 设置图表数据并刷新视图
     * @param data 图表数据列表
     */
    public void setData(List<ChartData> data) {
        this.dataList = data;
        // 确保数据按日期排序 (由调用者保证或在此处排序)
        // 理想情况下数据传入时已排序
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        if (dataList == null || dataList.isEmpty()) return;

        int width = getWidth();
        int height = getHeight();
        int paddingBottom = 60;
        int paddingLeft = 80;
        int paddingRight = 40;
        int paddingTop = 40;

        // 绘制坐标轴
        // Y轴
        canvas.drawLine(paddingLeft, paddingTop, paddingLeft, height - paddingBottom, axisPaint);
        // X轴
        canvas.drawLine(paddingLeft, height - paddingBottom, width - paddingRight, height - paddingBottom, axisPaint);

        // 绘制坐标轴标题
        textPaint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("日期", width - paddingRight - 10, height - paddingBottom + 30, textPaint); // X轴标题
        canvas.drawText("金额", paddingLeft + 10, paddingTop + 20, textPaint); // Y轴标题

        // 查找最大值以确定比例
        double maxVal = 0;
        for (ChartData d : dataList) {
            if (d.value > maxVal) maxVal = d.value;
        }
        if (maxVal == 0) maxVal = 100; // 如果全为0，默认范围
        
        // 增加顶部内边距，避免线条紧贴顶部
        maxVal = maxVal * 1.2;

        float xStep = (float)(width - paddingLeft - paddingRight) / (dataList.size() > 1 ? dataList.size() - 1 : 1);
        float yHeight = height - paddingBottom - paddingTop;

        // 绘制Y轴网格和标签
        int gridCount = 5;
        textPaint.setTextAlign(Paint.Align.RIGHT);
        for (int i = 0; i <= gridCount; i++) {
            float y = height - paddingBottom - (i * yHeight / gridCount);
            float value = (float)(maxVal * i / gridCount);
            
            // 绘制淡色网格线
            Paint gridPaint = new Paint(axisPaint);
            gridPaint.setColor(Color.LTGRAY);
            gridPaint.setPathEffect(new android.graphics.DashPathEffect(new float[]{10, 10}, 0));
            if (i > 0) { // 不要覆盖X轴
                canvas.drawLine(paddingLeft, y, width - paddingRight, y, gridPaint);
            }
            
            // 绘制标签
            canvas.drawText(String.valueOf((int)value), paddingLeft - 10, y + 10, textPaint);
        }

        // 绘制数据路径和点
        Path path = new Path();
        for (int i = 0; i < dataList.size(); i++) {
            ChartData d = dataList.get(i);
            float x = paddingLeft + i * xStep;
            float y = (float)(height - paddingBottom - (d.value / maxVal * yHeight));

            if (i == 0) {
                path.moveTo(x, y);
            } else {
                path.lineTo(x, y);
            }

            // 绘制点
            canvas.drawCircle(x, y, 8f, pointPaint);
            
            // 绘制X轴标签 (日期)
            textPaint.setTextAlign(Paint.Align.CENTER);
            if (dataList.size() <= 7 || i % (dataList.size() / 5) == 0) {
                 canvas.drawText(d.label, x, height - paddingBottom + 60, textPaint);
            }
        }
        canvas.drawPath(path, linePaint);
    }

    public static class ChartData {
        public String label; // 日期 (MM-dd)
        public double value; // 金额

        public ChartData(String label, double value) {
            this.label = label;
            this.value = value;
        }
    }
}
