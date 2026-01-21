package com.zzj.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.zzj.myapplication.db.CategoryDao;
import com.zzj.myapplication.db.RecordDao;
import com.zzj.myapplication.model.Category;
import com.zzj.myapplication.model.Record;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 账单列表 Fragment
 * 展示所有的收支记录列表，并集成统计图表和筛选功能
 */
public class RecordListFragment extends Fragment {

    private static final String BING_IMAGE_URL = "https://www.tute.edu.cn/";

    private RecyclerView recyclerView;
    private RecordAdapter adapter;
    private RecordDao recordDao;
    private CategoryDao categoryDao;
    private List<Record> recordList;

    // UI 控件
    private Spinner spinnerTimeScope;
    private TextView tvSummaryIncome, tvSummaryExpense;
    private LineChart statsChart;

    // 0: 本周, 1: 本月
    private int currentTimeScope = 1;

    /**
     * 创建 Fragment 视图
     * 初始化图表、列表和筛选控件
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record_list, container, false);

        // 初始化视图
        spinnerTimeScope = view.findViewById(R.id.spinner_time_scope);
        tvSummaryIncome = view.findViewById(R.id.tv_summary_income);
        tvSummaryExpense = view.findViewById(R.id.tv_summary_expense);
        statsChart = view.findViewById(R.id.stats_chart);
        recyclerView = view.findViewById(R.id.recycler_view_records);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // 初始化 DAO 和 适配器
        recordDao = new RecordDao(getContext());
        categoryDao = new CategoryDao(getContext());
        recordList = new ArrayList<>();
        adapter = new RecordAdapter(recordList);

        adapter.setOnDeleteListener(record -> {
            new androidx.appcompat.app.AlertDialog.Builder(getContext())
                .setTitle("删除记录")
                .setMessage("确定要删除这条记录吗？")
                .setPositiveButton("删除", (dialog, which) -> {
                    recordDao.deleteRecord(record);
                    loadData();
                })
                .setNegativeButton("取消", null)
                .show();
        });

        // recyclerView 绑定 Adapter
        recyclerView.setAdapter(adapter);

        // 监听筛选变化
        spinnerTimeScope.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentTimeScope = position;
                loadData();
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // 绑定悬浮按钮点击事件
        FloatingActionButton fabShowImage = view.findViewById(R.id.fab_show_image);
        fabShowImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(BING_IMAGE_URL));
            startActivity(intent);
        });

        // 默认选中 "本周" (index 0)
        spinnerTimeScope.setSelection(0);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    /**
     * 根据当前筛选加载数据、更新列表和图表
     */
    private void loadData() {
        long[] range = getDateRange(currentTimeScope);
        recordList = recordDao.getRecordsByDateRange(range[0], range[1]);

        List<Category> categories = categoryDao.getAllCategories();
        Map<Integer, Category> categoryMap = new HashMap<>();
        // ui，图标
        for (Category c : categories) categoryMap.put(c.getId(), c);

        adapter.setCategoryMap(categoryMap);
        adapter.setRecordList(recordList);

        calculateAndDisplayStats(recordList);
    }

    /**
     * 计算并显示统计数据 (图表和总收支)
     * @param records 记录列表
     */
    private void calculateAndDisplayStats(List<Record> records) {
        double totalIncome = 0;
        double totalExpense = 0;

        // 按日期聚合数据用于图表显示
        // Map<时间戳, 金额> - 使用 TreeMap 确保按日期正确排序，有序
        java.util.TreeMap<Long, Double> dailyExpenseMap = new java.util.TreeMap<>();
        java.util.TreeMap<Long, Double> dailyIncomeMap = new java.util.TreeMap<>();

        // 收集所有唯一日期以确保 X 轴对齐
        java.util.TreeSet<Long> allDates = new java.util.TreeSet<>();

        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MM-dd", java.util.Locale.getDefault());
        Calendar cal = Calendar.getInstance();

        for (Record r : records) {
            // 将日期标准化为午夜 (00:00:00)
            cal.setTimeInMillis(r.getDate());
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            long dateKey = cal.getTimeInMillis();
            allDates.add(dateKey);

            if (r.getType() == Record.TYPE_INCOME) {
                totalIncome += r.getAmount();
                dailyIncomeMap.put(dateKey, dailyIncomeMap.getOrDefault(dateKey, 0.0) + r.getAmount());
            } else {
                totalExpense += r.getAmount();
                dailyExpenseMap.put(dateKey, dailyExpenseMap.getOrDefault(dateKey, 0.0) + r.getAmount());
            }
        }

        // 更新文本
        tvSummaryIncome.setText(String.format("收入: ¥%.2f", totalIncome));
        tvSummaryExpense.setText(String.format("支出: ¥%.2f", totalExpense));

        // 准备图表数据
        List<Entry> expenseEntries = new ArrayList<>();
        List<Entry> incomeEntries = new ArrayList<>();
        List<String> xLabels = new ArrayList<>();

        int index = 0;
        for (Long date : allDates) {
            xLabels.add(sdf.format(new java.util.Date(date)));

            // 如果该日期无数据，则补 0
            float expenseVal = dailyExpenseMap.getOrDefault(date, 0.0).floatValue();
            float incomeVal = dailyIncomeMap.getOrDefault(date, 0.0).floatValue();

            expenseEntries.add(new Entry(index, expenseVal));
            incomeEntries.add(new Entry(index, incomeVal));
            index++;
        }

        if (allDates.isEmpty()) {
            statsChart.clear();
            statsChart.setNoDataText("暂无数据");
            return;
        }

        LineDataSet setExpense = new LineDataSet(expenseEntries, "支出");
        setExpense.setColor(android.graphics.Color.RED);
        setExpense.setValueTextColor(android.graphics.Color.RED);
        setExpense.setLineWidth(2f);
        setExpense.setCircleColor(android.graphics.Color.RED);
        setExpense.setCircleRadius(4f);
        setExpense.setDrawValues(true);
        setExpense.setMode(LineDataSet.Mode.LINEAR);

        LineDataSet setIncome = new LineDataSet(incomeEntries, "收入");
        setIncome.setColor(android.graphics.Color.GREEN);
        setIncome.setValueTextColor(android.graphics.Color.GREEN);
        setIncome.setLineWidth(2f);
        setIncome.setCircleColor(android.graphics.Color.GREEN);
        setIncome.setCircleRadius(4f);
        setIncome.setDrawValues(true);
        setIncome.setMode(LineDataSet.Mode.LINEAR);

        LineData lineData = new LineData(setIncome, setExpense);
        statsChart.setData(lineData);

        // 配置坐标轴
        XAxis xAxis = statsChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xLabels));
        xAxis.setDrawGridLines(false);

        statsChart.getAxisRight().setEnabled(false);
        statsChart.getAxisLeft().setDrawGridLines(true);
        statsChart.getDescription().setEnabled(false);
        statsChart.animateX(1000);
        statsChart.invalidate(); // 刷新
    }

    /**
     * 获取时间范围 [start, end]
     * @param scopeIndex 时间范围索引 (0:周, 1:月，2全部)
     * @return 包含开始和结束毫秒数的数组
     */
    private long[] getDateRange(int scopeIndex) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);

        long start = 0;
        long end = System.currentTimeMillis(); // 当前时间作为结束时间

        // 调整结束时间为今天的结束时刻
        Calendar endCal = Calendar.getInstance();
        endCal.set(Calendar.HOUR_OF_DAY, 23);
        endCal.set(Calendar.MINUTE, 59);
        endCal.set(Calendar.SECOND, 59);
        end = endCal.getTimeInMillis();

        switch (scopeIndex) {
            case 0: // 本周 - 过去 7 天
                start = end - (7L * 24 * 60 * 60 * 1000);
                break;
            case 1: // 本月 - 过去 30 天
                start = end - (30L * 24 * 60 * 60 * 1000);
                break;
            case 2: // 全部 - 所有时间
                start = 0;
                break;
        }

        return new long[]{start, end};
    }
}
