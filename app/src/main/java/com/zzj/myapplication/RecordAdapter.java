package com.zzj.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zzj.myapplication.model.Record;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 账单列表适配器
 * 负责绑定账单记录数据到 RecyclerView
 */
public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.ViewHolder> {

    private List<Record> recordList;
    private Map<Integer, String> categoryMap;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

    public RecordAdapter(List<Record> recordList) {
        this.recordList = recordList;
        this.categoryMap = new java.util.HashMap<>();
    }

    /**
     * 设置分类映射表
     * @param categoryMap 分类 ID 到名称的映射
     */
    public void setCategoryMap(Map<Integer, String> categoryMap) {
        this.categoryMap = categoryMap;
        notifyDataSetChanged();
    }

    /**
     * 更新列表数据
     * @param recordList 新的记录列表
     */
    public void setRecordList(List<Record> recordList) {
        this.recordList = recordList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_record, parent, false);
        return new ViewHolder(view);
    }

    /**
     * 绑定数据到 ViewHolder
     * @param holder ViewHolder
     * @param position 数据位置
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Record record = recordList.get(position);
        
        String categoryName = categoryMap.get(record.getCategoryId());
        holder.tvCategory.setText(categoryName != null ? categoryName : "未知分类");
        holder.tvNote.setText(record.getNote());
        
        String amountText = String.format("¥%.2f", record.getAmount());
        
        // 根据收支类型显示不同颜色和符号
        if (record.getType() == Record.TYPE_EXPENSE) {
            holder.tvAmount.setText("-" + amountText);
            holder.tvAmount.setTextColor(0xFFF44336); // Red
        } else {
            holder.tvAmount.setText("+" + amountText);
            holder.tvAmount.setTextColor(0xFF4CAF50); // Green
        }
        
        holder.tvDate.setText(dateFormat.format(new Date(record.getDate())));
    }

    @Override
    public int getItemCount() {
        return recordList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvCategory;
        public TextView tvNote;
        public TextView tvAmount;
        public TextView tvDate;

        public ViewHolder(View view) {
            super(view);
            tvCategory = view.findViewById(R.id.tv_category);
            tvNote = view.findViewById(R.id.tv_note);
            tvAmount = view.findViewById(R.id.tv_amount);
            tvDate = view.findViewById(R.id.tv_date);
        }
    }
}
