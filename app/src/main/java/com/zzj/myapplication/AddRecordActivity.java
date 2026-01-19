package com.zzj.myapplication;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;
import android.widget.EditText;
import android.widget.RadioGroup;
import com.zzj.myapplication.db.AccountDao;
import com.zzj.myapplication.db.CategoryDao;
import com.zzj.myapplication.db.RecordDao;
import com.zzj.myapplication.model.Account;
import com.zzj.myapplication.model.Category;
import com.zzj.myapplication.model.Record;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 记一笔 (记账) 页面
 * 允许用户输入金额、选择收支类型、分类、账户，并添加备注保存记录
 */
public class AddRecordActivity extends AppCompatActivity {

    private EditText etAmount, etNote, etDateDisplay;
    private AutoCompleteTextView actvCategory, actvAccount;
    private RadioGroup toggleType;
    private Button btnSave;
    
    private long selectedDate;
    
    private AccountDao accountDao;
    private CategoryDao categoryDao;
    private RecordDao recordDao;

    private List<Account> accountList;
    private List<Category> categoryList;
    private int currentType = Record.TYPE_EXPENSE; // 默认为支出
    
    private int selectedCategoryIndex = -1;
    private int selectedAccountIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_record);
        
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }
        // 返回上一页
        toolbar.setNavigationOnClickListener(v -> finish());

        // 初始化 UI 控件
        etAmount = findViewById(R.id.et_amount);
        etNote = findViewById(R.id.et_note);
        etDateDisplay = findViewById(R.id.et_date_display);
        
        actvCategory = findViewById(R.id.actv_category);
        actvAccount = findViewById(R.id.actv_account);
        
        toggleType = findViewById(R.id.toggle_type);
        btnSave = findViewById(R.id.btn_save);

        // 初始化数据库 DAO
        accountDao = new AccountDao(this);
        categoryDao = new CategoryDao(this);
        recordDao = new RecordDao(this);

        // 默认选中支出
        toggleType.check(R.id.btn_expense);

        // 监听收支类型切换
        toggleType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.btn_income) {
                currentType = Record.TYPE_INCOME;
            } else {
                currentType = Record.TYPE_EXPENSE;
            }
            loadCategories(); // 切换类型时重新加载对应分类
            // 清空之前的选择
            actvCategory.setText("");
            selectedCategoryIndex = -1;
        });

        // 加载数据
        loadAccounts();
        loadCategories();

        // 初始化为今天
        selectedDate = System.currentTimeMillis();
        updateDateDisplay();

        etDateDisplay.setOnClickListener(v -> showDatePicker());

        // 保存按钮点击事件
        btnSave.setOnClickListener(v -> saveRecord());
        
        actvAccount.setOnItemClickListener((parent, view, position, id) -> {
            selectedAccountIndex = position;
        });
        
        actvCategory.setOnItemClickListener((parent, view, position, id) -> {
            selectedCategoryIndex = position;
        });

        actvAccount.setOnClickListener(v -> actvAccount.showDropDown());
        actvCategory.setOnClickListener(v -> actvCategory.showDropDown());
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

        // 点击确认的时候， 更新Calendar 到用户新选择的日期，更新时间戳，已经ui
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
     * 加载账户列表并填充账户下拉框
     */
    private void loadAccounts() {
        accountList = accountDao.getAllAccounts();
        List<String> accountNames = new ArrayList<>();
        for (Account account : accountList) {
            accountNames.add(account.getName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, accountNames);
        actvAccount.setAdapter(adapter);
    }

    /**
     * 根据当前选中的收支类型加载分类列表
     */
    private void loadCategories() {
        categoryList = categoryDao.getCategoriesByType(currentType);
        List<String> categoryNames = new ArrayList<>();
        for (Category category : categoryList) {
            categoryNames.add(category.getName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, categoryNames);
        actvCategory.setAdapter(adapter);
    }

    /**
     * 保存记账记录到数据库
     * 同时更新关联账户的余额
     */
    private void saveRecord() {
        String amountStr = etAmount.getText().toString();
        String note = etNote.getText().toString();

        if (amountStr.isEmpty()) {
            Toast.makeText(this, "请输入金额", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);
        
        // 验证分类和账户是否已选择
        // 注意：由于用户可能不通过点击 item，而是直接输入（虽然 inputType设为了none），
        // 但最好还是依赖 index 或者 text 匹配。这里为了简单，依赖 index，
        // 如果用户选完又清空了，Index可能不准，所以最好检查 text 是否为空
        String categoryText = actvCategory.getText().toString();
        String accountText = actvAccount.getText().toString();
        
        if (categoryText.isEmpty() || accountText.isEmpty()) {
             Toast.makeText(this, "请选择分类和账户", Toast.LENGTH_SHORT).show();
             return;
        }
        
        if (selectedCategoryIndex == -1) {
             // 尝试通过名字找回 index，防止配置变化丢失索引
             for(int i=0; i<categoryList.size(); i++) {
                 if(categoryList.get(i).getName().equals(categoryText)) {
                     selectedCategoryIndex = i;
                     break;
                 }
             }
        }
        
        if (selectedAccountIndex == -1) {
             for(int i=0; i<accountList.size(); i++) {
                 if(accountList.get(i).getName().equals(accountText)) {
                     selectedAccountIndex = i;
                     break;
                 }
             }
        }
        
        if (selectedCategoryIndex == -1 || selectedAccountIndex == -1) {
             Toast.makeText(this, "无效的分类或账户", Toast.LENGTH_SHORT).show();
             return;
        }
        
        Category selectedCategory = categoryList.get(selectedCategoryIndex);
        Account selectedAccount = accountList.get(selectedAccountIndex);

        // 创建记录对象
        Record record = new Record();
        record.setAmount(amount);
        record.setType(currentType);
        record.setCategoryId(selectedCategory.getId());
        record.setAccountId(selectedAccount.getId());
        record.setDate(selectedDate);
        record.setNote(note);

        // 1. 保存记录
        long recordId = recordDao.addRecord(record);

        if (recordId > 0) {
            // 2. 更新关联账户的余额
            if (currentType == Record.TYPE_INCOME) {
                selectedAccount.setBalance(selectedAccount.getBalance() + amount);
            } else {
                selectedAccount.setBalance(selectedAccount.getBalance() - amount);
            }
            accountDao.updateAccount(selectedAccount);

            Toast.makeText(this, "记录保存成功", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "保存失败", Toast.LENGTH_SHORT).show();
        }
    }
}
