package com.zzj.myapplication;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zzj.myapplication.model.Item;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 物品列表适配器
 * 负责绑定物品数据到 RecyclerView
 */
public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private List<Item> itemList;
    private OnItemClickListener listener;
    private boolean isPrivacyMode = false; // 隐私模式状态

    /**
     * 点击事件回调接口
     */
    public interface OnItemClickListener {
        void onItemClick(Item item);
    }

    public ItemAdapter(List<Item> itemList, OnItemClickListener listener) {
        this.itemList = itemList;
        this.listener = listener;
    }

    /**
     * 更新列表数据
     */
    public void setItemList(List<Item> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }
    
    /**
     * 设置隐私模式
     */
    public void setPrivacyMode(boolean isPrivacyMode) {
        this.isPrivacyMode = isPrivacyMode;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_item, parent, false);
        return new ViewHolder(view);
    }

    /**
     * 绑定数据到 ViewHolder
     * @param holder ViewHolder
     * @param position 数据位置
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = itemList.get(position);
        holder.tvName.setText(item.getName());
        
        // 处理金额和日均成本显示
        if (isPrivacyMode) {
            holder.tvPrice.setText("****");
            holder.tvDailyCost.setText("****");
        } else {
            holder.tvPrice.setText(String.format("¥%.2f", item.getPrice()));
            
            // 计算持有天数和日均成本 (需要去除时间部分，只比较日期)
            long purchaseTime = item.getPurchaseDate();
            long currentTime = System.currentTimeMillis();
            
            // 将两个时间戳转换为 Calendar，清除时分秒
            java.util.Calendar cal1 = java.util.Calendar.getInstance();
            cal1.setTimeInMillis(purchaseTime);
            cal1.set(java.util.Calendar.HOUR_OF_DAY, 0);
            cal1.set(java.util.Calendar.MINUTE, 0);
            cal1.set(java.util.Calendar.SECOND, 0);
            cal1.set(java.util.Calendar.MILLISECOND, 0);
            
            java.util.Calendar cal2 = java.util.Calendar.getInstance();
            cal2.setTimeInMillis(currentTime);
            cal2.set(java.util.Calendar.HOUR_OF_DAY, 0);
            cal2.set(java.util.Calendar.MINUTE, 0);
            cal2.set(java.util.Calendar.SECOND, 0);
            cal2.set(java.util.Calendar.MILLISECOND, 0);
            
            long diffInMillis = cal2.getTimeInMillis() - cal1.getTimeInMillis();
            long days = TimeUnit.MILLISECONDS.toDays(diffInMillis) + 1; // +1 包含起始日
            
            if (days < 1) days = 1; 
            
            double dailyCost = item.getPrice() / days;
            holder.tvDailyCost.setText(String.format("持有%d天 · ¥%.2f/天", days, dailyCost));
        }
        
        String statusText;
        int color;
        // 根据状态设置文本和颜色
        switch (item.getStatus()) {
            case Item.STATUS_IN_USE:
                statusText = "在用";
                color = 0xFF4CAF50; // Green
                break;
            case Item.STATUS_IDLE:
                statusText = "闲置";
                color = 0xFFFF9800; // Orange
                break;
            case Item.STATUS_LOST:
                statusText = "丢失";
                color = 0xFFF44336; // Red
                break;
            case Item.STATUS_SOLD:
                statusText = "已售";
                color = 0xFF9E9E9E; // Grey
                break;
            default:
                statusText = "未知";
                color = 0xFF9E9E9E;
        }
        holder.tvStatus.setText(statusText);
        holder.tvStatus.setBackgroundColor(color);
        
        // 如果有图片路径，加载显示图片
        if (item.getPhotoPath() != null && !item.getPhotoPath().isEmpty()) {
            try {
                holder.ivPhoto.setImageTintList(null);
                holder.ivPhoto.setImageURI(Uri.parse(item.getPhotoPath()));
            } catch (Exception e) {
                 holder.ivPhoto.setImageResource(android.R.drawable.ic_menu_camera); 
            }
        } else {
            holder.ivPhoto.setImageResource(android.R.drawable.ic_menu_camera); // 默认占位图
        }
        
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName;
        public TextView tvStatus;
        public TextView tvPrice;
        public TextView tvDailyCost; // 新增字段
        public ImageView ivPhoto;

        public ViewHolder(View view) {
            super(view);
            tvName = view.findViewById(R.id.tv_name);
            tvStatus = view.findViewById(R.id.tv_status);
            tvPrice = view.findViewById(R.id.tv_price);
            tvDailyCost = view.findViewById(R.id.tv_daily_cost);
            ivPhoto = view.findViewById(R.id.iv_photo);
        }
    }
}
