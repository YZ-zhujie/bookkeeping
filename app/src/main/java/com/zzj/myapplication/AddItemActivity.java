package com.zzj.myapplication;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;
import android.widget.EditText;
import com.zzj.myapplication.db.ItemDao;
import com.zzj.myapplication.model.Item;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 添加/编辑物品页面
 */
public class AddItemActivity extends AppCompatActivity {

    private EditText etName, etPrice, etDateDisplay;
    private ImageView ivPhoto;
    private android.view.View cardPhoto;
    private Button btnSave;
    private ItemDao itemDao;
    
    private long selectedDate;
    private String selectedPhotoPath;
    
    private boolean isEditMode = false;
    private int editingItemId = -1;
    private Item editingItem;

    /**
     * 打开相册，选择图片
     */
    private final ActivityResultLauncher<String[]> photoPickerLauncher = registerForActivityResult(
            new ActivityResultContracts.OpenDocument(),
            uri -> {
                if (uri != null) {
                    try {
                        // 申请长期读取权限
                        getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        selectedPhotoPath = uri.toString();
                        // 清除着色以显示原图
                        ivPhoto.setImageTintList(null);
                        ivPhoto.setImageURI(uri);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "无法获取图片权限", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        etName = findViewById(R.id.et_name);
        etPrice = findViewById(R.id.et_price);
        etDateDisplay = findViewById(R.id.et_date_display);
        ivPhoto = findViewById(R.id.iv_item_photo);
        cardPhoto = findViewById(R.id.card_photo);
        btnSave = findViewById(R.id.btn_save_item);
        
        itemDao = new ItemDao(this);
        
        // 检查是否是编辑模式
        editingItemId = getIntent().getIntExtra("item_id", -1);
        if (editingItemId != -1) {
            isEditMode = true;
            btnSave.setText("更新物品");
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("编辑物品");
            }
            try {
                loadItemData(editingItemId);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "加载数据失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            selectedDate = System.currentTimeMillis();
            updateDateDisplay(); // 初始化为今天
        }

        // 设置点击事件：选择日期
        etDateDisplay.setOnClickListener(v -> showDatePicker());
        
        // 设置点击事件：选择照片 (点击卡片或图片都可以)
        cardPhoto.setOnClickListener(v -> openGallery());
        ivPhoto.setOnClickListener(v -> openGallery());
        
        // 设置点击事件：保存物品
        btnSave.setOnClickListener(v -> saveItem());
    }

    /**
     * 加载即有物品数据以进行编辑
     * @param id 物品ID
     */
    private void loadItemData(int id) {
        editingItem = itemDao.getItem(id);
        if (editingItem != null) {
            // 安全设置文本
            String name = editingItem.getName();
            etName.setText(name != null ? name : "");
            
            etPrice.setText(String.valueOf(editingItem.getPrice()));
            selectedDate = editingItem.getPurchaseDate();
            selectedPhotoPath = editingItem.getPhotoPath();
            
            updateDateDisplay();
            
            if (selectedPhotoPath != null) {
                try {
                    // 清除着色以显示原图
                    ivPhoto.setImageTintList(null);
                    ivPhoto.setImageURI(Uri.parse(selectedPhotoPath));
                } catch (Exception e) {
                     ivPhoto.setImageResource(android.R.drawable.ic_menu_camera); 
                }
            }
        }
    }

    /**
     * 显示日期选择对话框
     */
    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(selectedDate);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
            calendar.set(year1, month1, dayOfMonth);
            selectedDate = calendar.getTimeInMillis();
            updateDateDisplay();
        }, year, month, day).show();
    }

    /**
     * 更新日期显示文本
     */
    private void updateDateDisplay() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        etDateDisplay.setText(dateFormat.format(new Date(selectedDate)));
    }

    /**
     * 打开系统相册选择图片
     */
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        // 使用 registerForActivityResult 的启动器
        photoPickerLauncher.launch(new String[]{"image/*"});
    }

    /**
     * 保存物品信息到数据库
     */
    private void saveItem() {
        String name = etName.getText().toString();
        String priceStr = etPrice.getText().toString();

        if (name.isEmpty()) {
            Toast.makeText(this, "请输入物品名称", Toast.LENGTH_SHORT).show();
            return;
        }

        double price = 0;
        if (!priceStr.isEmpty()) {
            price = Double.parseDouble(priceStr);
        }

        if (isEditMode && editingItem != null) {
            // 更新逻辑
            editingItem.setName(name);
            editingItem.setPrice(price);
            editingItem.setPurchaseDate(selectedDate);
            editingItem.setPhotoPath(selectedPhotoPath);
            
            itemDao.updateItem(editingItem);
            // 提示框
            Toast.makeText(this, "更新成功", Toast.LENGTH_SHORT).show();
        } else {
            // 创建物品对象
            Item item = new Item(name, Item.STATUS_IN_USE, null, selectedDate, price, selectedPhotoPath);
            long id = itemDao.addItem(item);
    
            if (id > 0) {
                Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "保存失败", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        finish();
    }
}
