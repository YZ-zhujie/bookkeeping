package com.zzj.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.zzj.myapplication.db.ItemDao;
import com.zzj.myapplication.model.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 物品列表 Fragment
 * 展示所有物品，并提供添加物品的入口
 * 包含汇总信息卡片和隐私模式切换
 */
public class ItemListFragment extends Fragment {

    private RecyclerView recyclerView;
    private FloatingActionButton fabAddItem;
    private ItemAdapter adapter;
    private ItemDao itemDao;
    private List<Item> itemList;
    
    // 汇总信息视图
    private TextView tvTotalItems, tvTotalCost;
    private ImageView ivPrivacyToggle;
    
    private boolean isPrivacyMode = false;

    /**
     * 创建 Fragment 视图
     * 初始化 RecyclerView, Adapter 和点击事件
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        // 初始化视图
        recyclerView = view.findViewById(R.id.recycler_view_items);
        fabAddItem = view.findViewById(R.id.fab_add_item);
        tvTotalItems = view.findViewById(R.id.tv_total_items);
        tvTotalCost = view.findViewById(R.id.tv_total_cost);
        ivPrivacyToggle = view.findViewById(R.id.iv_privacy_toggle);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        itemDao = new ItemDao(getContext());
        itemList = new ArrayList<>();
        
        // 初始化适配器并设置点击事件
        adapter = new ItemAdapter(itemList, item -> {
            Intent intent = new Intent(getContext(), ItemDetailActivity.class);
            intent.putExtra(ItemDetailActivity.EXTRA_ITEM_ID, item.getId());
            intent.putExtra(ItemDetailActivity.EXTRA_ITEM_NAME, item.getName());
            intent.putExtra(ItemDetailActivity.EXTRA_ITEM_PRICE, item.getPrice());
            intent.putExtra(ItemDetailActivity.EXTRA_ITEM_STATUS, item.getStatus());
            intent.putExtra(ItemDetailActivity.EXTRA_ITEM_DATE, item.getPurchaseDate());
            intent.putExtra(ItemDetailActivity.EXTRA_ITEM_PHOTO, item.getPhotoPath());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        // 点击悬浮按钮跳转到添加物品页面
        fabAddItem.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), AddItemActivity.class));
        });
        
        // 隐私模式切换监听
        ivPrivacyToggle.setOnClickListener(v -> togglePrivacyMode());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadItems();
    }
    
    /**
     * 切换隐私模式
     */
    private void togglePrivacyMode() {
        isPrivacyMode = !isPrivacyMode;
        
        // 更新图标状态 (可选: 改变透明度或图标)
        ivPrivacyToggle.setAlpha(isPrivacyMode ? 0.5f : 1.0f);
        
        // 更新 UI
        updateSummaryUI();
        adapter.setPrivacyMode(isPrivacyMode);
    }

    /**
     * 从数据库加载物品数据并更新列表
     */
    private void loadItems() {
        itemList = itemDao.getAllItems();
        adapter.setItemList(itemList);
        
        calculateStats();
    }
    
    /**
     * 计算并显示汇总信息
     */
    private void calculateStats() {
       updateSummaryUI();
    }
    
    private void updateSummaryUI() {
        if (itemList == null) return;

        int totalCount = itemList.size();
        double totalCost = 0;
        
        for (Item item : itemList) {
            totalCost += item.getPrice();
        }
        
        tvTotalItems.setText(String.format("共 %d 件物品", totalCount));
        
        if (isPrivacyMode) {
            tvTotalCost.setText("****");
        } else {
            tvTotalCost.setText(String.format("¥%.2f", totalCost));
        }
    }
}
