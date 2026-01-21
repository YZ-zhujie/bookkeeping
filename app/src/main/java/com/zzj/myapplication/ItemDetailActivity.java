package com.zzj.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.zzj.myapplication.db.ItemDao;
import com.zzj.myapplication.model.Item;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * 物品详情页面
 * 展示物品详细信息，计算持有时间，并允许修改物品状态
 */
public class ItemDetailActivity extends AppCompatActivity {

    public static final String EXTRA_ITEM_ID = "extra_item_id";
    public static final String EXTRA_ITEM_NAME = "extra_item_name";
    public static final String EXTRA_ITEM_PRICE = "extra_item_price";
    public static final String EXTRA_ITEM_STATUS = "extra_item_status";
    public static final String EXTRA_ITEM_DATE = "extra_item_date";
    public static final String EXTRA_ITEM_PHOTO = "extra_item_photo";

    private TextView tvName, tvPrice, tvDate, tvDuration;
    private ImageView ivDetailPhoto;
    private RadioGroup rgStatus;
    private RadioButton rbInUse, rbIdle, rbLost, rbSold;
    private Button btnUpdate;
    private ItemDao itemDao;
    private int itemId;

    /**
     * Activity 创建时回调
     * 初始化 UI，加载数据并设置监听器
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        // 初始化 UI 控件
        ivDetailPhoto = findViewById(R.id.iv_detail_photo);
        tvName = findViewById(R.id.tv_detail_name);
        tvPrice = findViewById(R.id.tv_detail_price);
        tvDate = findViewById(R.id.tv_detail_date);
        tvDuration = findViewById(R.id.tv_detail_duration);
        rgStatus = findViewById(R.id.rg_status);
        rbInUse = findViewById(R.id.rb_in_use);
        rbIdle = findViewById(R.id.rb_idle);
        rbLost = findViewById(R.id.rb_lost);
        btnUpdate = findViewById(R.id.btn_update_status);
        Button btnEdit = findViewById(R.id.btn_edit_item);

        itemDao = new ItemDao(this);
        
        // 获取 Intent 传递的数据
        itemId = getIntent().getIntExtra(EXTRA_ITEM_ID, -1);
        
        // 初始化加载
        loadData();

        // 按钮点击事件：更新状态
        btnUpdate.setOnClickListener(v -> updateStatus());
        
        // 按钮点击事件：编辑物品
        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddItemActivity.class);
            intent.putExtra("item_id", itemId);
            startActivity(intent);
        });
        
        // 设置标题栏标题
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("物品详情");
        }
    }
    
    /**
     * 创建选项菜单
     * 加载编辑按钮等菜单项
     */
    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item_detail, menu);
        return true;
    }

    /**
     * 菜单项选中回调
     * 处理编辑按钮点击事件
     */
    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (item.getItemId() == R.id.action_edit) {
            Intent intent = new Intent(this, AddItemActivity.class);
            intent.putExtra("item_id", itemId);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }
    
    /**
     * 加载物品数据并显示
     * 包括基本信息、持有天数计算和状态回显
     */
    private void loadData() {
        if (itemId == -1) return;
        Item item = itemDao.getItem(itemId);
        if (item == null) return;
        
        tvName.setText(item.getName());
        tvPrice.setText(String.format("¥%.2f", item.getPrice()));
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        tvDate.setText("购买日期: " + sdf.format(new Date(item.getPurchaseDate())));
        
        // 计算持有天数 (标准化时间到 00:00:00)
        java.util.Calendar calPurchase = java.util.Calendar.getInstance();
        calPurchase.setTimeInMillis(item.getPurchaseDate());
        calPurchase.set(java.util.Calendar.HOUR_OF_DAY, 0);
        calPurchase.set(java.util.Calendar.MINUTE, 0);
        calPurchase.set(java.util.Calendar.SECOND, 0);
        calPurchase.set(java.util.Calendar.MILLISECOND, 0);

        java.util.Calendar calCurrent = java.util.Calendar.getInstance();
        calCurrent.setTimeInMillis(System.currentTimeMillis());
        calCurrent.set(java.util.Calendar.HOUR_OF_DAY, 0);
        calCurrent.set(java.util.Calendar.MINUTE, 0);
        calCurrent.set(java.util.Calendar.SECOND, 0);
        calCurrent.set(java.util.Calendar.MILLISECOND, 0);

        long diffInMillis = calCurrent.getTimeInMillis() - calPurchase.getTimeInMillis();
        long days = TimeUnit.MILLISECONDS.toDays(diffInMillis) + 1; // +1 包含起始日
        
        tvDuration.setText("已持有天数: " + Math.max(1, days) + " 天");
        
        if (item.getPhotoPath() != null && !item.getPhotoPath().isEmpty()) {
            try {
                ivDetailPhoto.setImageURI(Uri.parse(item.getPhotoPath()));
            } catch (Exception e) {
                 ivDetailPhoto.setImageResource(android.R.drawable.ic_menu_camera); 
            }
        }
        
        // 更新 RadioButton 选择
        switch (item.getStatus()) {
            case Item.STATUS_IN_USE: rbInUse.setChecked(true); break;
            case Item.STATUS_IDLE: rbIdle.setChecked(true); break;
            case Item.STATUS_LOST: rbLost.setChecked(true); break;
        }
    }

    /**
     * 更新物品状态到数据库
     */
    private void updateStatus() {
        int newStatus = Item.STATUS_IN_USE;
        if (rbInUse.isChecked()) newStatus = Item.STATUS_IN_USE;
        else if (rbIdle.isChecked()) newStatus = Item.STATUS_IDLE;
        else if (rbLost.isChecked()) newStatus = Item.STATUS_LOST;

        itemDao.updateItemStatus(itemId, newStatus);
        Toast.makeText(this, "状态已更新", Toast.LENGTH_SHORT).show();
        finish();
    }
}
