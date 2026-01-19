package com.zzj.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.zzj.myapplication.db.AccountDao;
import com.zzj.myapplication.db.RecordDao;
import com.zzj.myapplication.model.Account;
import com.zzj.myapplication.model.Record;

import java.util.Calendar;
import java.util.List;

/**
 * 首页仪表盘 Fragment
 * 展示账户总资产概览和本月收支统计，提供快速记账入口
 */
public class DashboardFragment extends Fragment {

    /**
     * 后续增加，显示支付类型的汇总
     */
    private TextView tvTotalBalance;
    private TextView tvMonthIncome;
    private TextView tvMonthExpense;
    private Button btnAddRecord;

    private AccountDao accountDao;
    private RecordDao recordDao;

    /**
     * 创建 Fragment 视图
     * 初始化 UI 控件和数据访问对象
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        // 初始化 UI 控件
        tvTotalBalance = view.findViewById(R.id.tv_total_balance);
        tvMonthIncome = view.findViewById(R.id.tv_month_income);
        tvMonthExpense = view.findViewById(R.id.tv_month_expense);
        btnAddRecord = view.findViewById(R.id.btn_add_record);

        // 初始化数据访问对象
        accountDao = new AccountDao(getContext());
        recordDao = new RecordDao(getContext());

        // 点击 "记一笔" 按钮跳转到记账页面
            btnAddRecord.setOnClickListener(v -> {
             startActivity(new Intent(getActivity(), AddRecordActivity.class));
        });

        return view;
    }

    /**
     * Fragment 可见时回调
     * 重新加载数据以刷新显示
     */
    @Override
    public void onResume() {
        super.onResume();
        // 每次页面可见时重新加载数据
        loadData();
    }

    /**
     * 加载并计算财务数据 (总资产、本月收支)
     */
    private void loadData() {
        // 1. 计算所有账户的总资产
        List<Account> accounts = accountDao.getAllAccounts();
        double totalBalance = 0;
        for (Account account : accounts) {
            totalBalance += account.getBalance();
        }
        tvTotalBalance.setText(String.format("¥%.2f", totalBalance));

        // 2. 计算本月收入和支出
        List<Record> records = recordDao.getAllRecords();
        double monthIncome = 0;
        double monthExpense = 0;

        // 获取当前时间
        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentYear = calendar.get(Calendar.YEAR);

        for (Record record : records) {
            calendar.setTimeInMillis(record.getDate());
            // 筛选当前年份和月份的记录
            if (calendar.get(Calendar.MONTH) == currentMonth && calendar.get(Calendar.YEAR) == currentYear) {
                if (record.getType() == Record.TYPE_INCOME) {
                    monthIncome += record.getAmount();
                } else {
                    monthExpense += record.getAmount();
                }
            }
        }

        // 更新 UI 显示
        tvMonthIncome.setText(String.format("¥%.2f", monthIncome));
        tvMonthExpense.setText(String.format("¥%.2f", monthExpense));
    }
}
