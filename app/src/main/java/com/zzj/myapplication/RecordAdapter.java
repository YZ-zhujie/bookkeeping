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
    private Map<Integer, com.zzj.myapplication.model.Category> categoryMap;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

    private OnRecordDeleteListener deleteListener;

    public interface OnRecordDeleteListener {
        void onDeleteClick(Record record);
    }

    public void setOnDeleteListener(OnRecordDeleteListener listener) {
        this.deleteListener = listener;
    }

    public RecordAdapter(List<Record> recordList) {
        this.recordList = recordList;
        this.categoryMap = new java.util.HashMap<>();
    }

    /**
     * 设置分类映射表
     * @param categoryMap 分类 ID 到 Category 对象的映射
     */
    public void setCategoryMap(Map<Integer, com.zzj.myapplication.model.Category> categoryMap) {
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
        
        com.zzj.myapplication.model.Category category = categoryMap.get(record.getCategoryId());
        String categoryName = "未知分类";
        if (category != null) {
            categoryName = category.getName();

            // Load Icon
            String iconName = category.getIconResName();
            if (iconName != null && !iconName.isEmpty()) {
                int resId = holder.itemView.getContext().getResources().getIdentifier(
                        iconName, "drawable", holder.itemView.getContext().getPackageName());
                if (resId != 0) {
                    holder.ivIcon.setImageResource(resId);
                } else {
                    holder.ivIcon.setImageResource(android.R.drawable.ic_menu_help);
                }
            } else {
                holder.ivIcon.setImageResource(android.R.drawable.ic_menu_help);
            }
        }

        holder.tvCategory.setText(categoryName);
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

        holder.btnDelete.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDeleteClick(record);
            }
        });
    }

    @Override
    public int getItemCount() {
        return recordList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public android.widget.ImageView ivIcon;
        public TextView tvCategory;
        public TextView tvNote;
        public TextView tvAmount;
        public TextView tvDate;
        public android.widget.ImageButton btnDelete;

        public ViewHolder(View view) {
            super(view);
            ivIcon = view.findViewById(R.id.iv_icon);
            tvCategory = view.findViewById(R.id.tv_category);
            tvNote = view.findViewById(R.id.tv_note);
            tvAmount = view.findViewById(R.id.tv_amount);
            tvDate = view.findViewById(R.id.tv_date);
            btnDelete = view.findViewById(R.id.btn_delete);
        }
    }
}
