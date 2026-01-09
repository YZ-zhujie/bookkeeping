package com.zzj.myapplication.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 数据库帮助类
 * 负责创建数据库、升级数据库以及初始化默认数据
 * 管理 Accounts, Categories, Records, Items 四张表
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "accounting_app.db";
    private static final int DATABASE_VERSION = 1;

    // 表名常量
    public static final String TABLE_ACCOUNTS = "accounts";
    public static final String TABLE_CATEGORIES = "categories";
    public static final String TABLE_RECORDS = "records";
    public static final String TABLE_ITEMS = "items";

    // 通用列名
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";

    // 账户表列名
    public static final String COLUMN_ACCOUNT_TYPE = "type";
    public static final String COLUMN_ACCOUNT_BALANCE = "balance";

    // 分类表列名
    public static final String COLUMN_CATEGORY_TYPE = "type"; // 0: 支出, 1: 收入
    public static final String COLUMN_CATEGORY_ICON = "icon_res";

    // 记录表列名
    public static final String COLUMN_RECORD_AMOUNT = "amount";
    public static final String COLUMN_RECORD_TYPE = "type"; // 0: 支出, 1: 收入
    public static final String COLUMN_RECORD_CATEGORY_ID = "category_id";
    public static final String COLUMN_RECORD_ACCOUNT_ID = "account_id";
    public static final String COLUMN_RECORD_DATE = "date";
    public static final String COLUMN_RECORD_NOTE = "note";

    // 物品表列名
    public static final String COLUMN_ITEM_STATUS = "status";
    public static final String COLUMN_ITEM_RECORD_ID = "record_id";
    public static final String COLUMN_ITEM_PURCHASE_DATE = "purchase_date";
    public static final String COLUMN_ITEM_PRICE = "price";
    public static final String COLUMN_ITEM_PHOTO_PATH = "photo_path";


    // 建表语句
    private static final String CREATE_TABLE_ACCOUNTS = "CREATE TABLE " + TABLE_ACCOUNTS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_NAME + " TEXT, "
            + COLUMN_ACCOUNT_TYPE + " TEXT, "
            + COLUMN_ACCOUNT_BALANCE + " REAL"
            + ")";

    private static final String CREATE_TABLE_CATEGORIES = "CREATE TABLE " + TABLE_CATEGORIES + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_NAME + " TEXT, "
            + COLUMN_CATEGORY_TYPE + " INTEGER, "
            + COLUMN_CATEGORY_ICON + " TEXT"
            + ")";

    private static final String CREATE_TABLE_RECORDS = "CREATE TABLE " + TABLE_RECORDS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_RECORD_AMOUNT + " REAL, "
            + COLUMN_RECORD_TYPE + " INTEGER, "
            + COLUMN_RECORD_CATEGORY_ID + " INTEGER, "
            + COLUMN_RECORD_ACCOUNT_ID + " INTEGER, "
            + COLUMN_RECORD_DATE + " INTEGER, "
            + COLUMN_RECORD_NOTE + " TEXT, "
            + "FOREIGN KEY(" + COLUMN_RECORD_CATEGORY_ID + ") REFERENCES " + TABLE_CATEGORIES + "(" + COLUMN_ID + "), "
            + "FOREIGN KEY(" + COLUMN_RECORD_ACCOUNT_ID + ") REFERENCES " + TABLE_ACCOUNTS + "(" + COLUMN_ID + ")"
            + ")";

    private static final String CREATE_TABLE_ITEMS = "CREATE TABLE " + TABLE_ITEMS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_NAME + " TEXT, "
            + COLUMN_ITEM_STATUS + " INTEGER, "
            + COLUMN_ITEM_RECORD_ID + " INTEGER, "
            + COLUMN_ITEM_PURCHASE_DATE + " INTEGER, "
            + COLUMN_ITEM_PRICE + " REAL, "
            + COLUMN_ITEM_PHOTO_PATH + " TEXT, "
            + "FOREIGN KEY(" + COLUMN_ITEM_RECORD_ID + ") REFERENCES " + TABLE_RECORDS + "(" + COLUMN_ID + ")"
            + ")";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * 数据库创建回调
     * 创建 Accounts, Categories, Records, Items 表并插入默认数据
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_ACCOUNTS);
        db.execSQL(CREATE_TABLE_CATEGORIES);
        db.execSQL(CREATE_TABLE_RECORDS);
        db.execSQL(CREATE_TABLE_ITEMS);

        insertDefaultData(db);
    }

    /**
     * 数据库升级回调
     * 删除旧表并重新创建
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 如果存在旧表则删除
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACCOUNTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECORDS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);

        // 重新创建表
        onCreate(db);
    }

    /**
     * 插入默认的账户和分类数据
     */
    private void insertDefaultData(SQLiteDatabase db) {
        // 默认账户数据
        db.execSQL("INSERT INTO " + TABLE_ACCOUNTS + " (" + COLUMN_NAME + ", " + COLUMN_ACCOUNT_TYPE + ", " + COLUMN_ACCOUNT_BALANCE + ") VALUES ('现金', 'Cash', 0)");
        db.execSQL("INSERT INTO " + TABLE_ACCOUNTS + " (" + COLUMN_NAME + ", " + COLUMN_ACCOUNT_TYPE + ", " + COLUMN_ACCOUNT_BALANCE + ") VALUES ('银行卡', 'Card', 0)");
        db.execSQL("INSERT INTO " + TABLE_ACCOUNTS + " (" + COLUMN_NAME + ", " + COLUMN_ACCOUNT_TYPE + ", " + COLUMN_ACCOUNT_BALANCE + ") VALUES ('微信', 'WeChat', 0)");
        db.execSQL("INSERT INTO " + TABLE_ACCOUNTS + " (" + COLUMN_NAME + ", " + COLUMN_ACCOUNT_TYPE + ", " + COLUMN_ACCOUNT_BALANCE + ") VALUES ('支付宝', 'Alipay', 0)");

        // 默认分类数据 - 支出
        insertCategory(db, "餐饮", 0, "ic_food");
        insertCategory(db, "交通", 0, "ic_transport");
        insertCategory(db, "购物", 0, "ic_shopping");
        insertCategory(db, "娱乐", 0, "ic_entertainment");
        insertCategory(db, "居住", 0, "ic_housing");
        insertCategory(db, "医疗", 0, "ic_medical");
        insertCategory(db, "数码", 0, "ic_digital");

        // 默认分类数据 - 收入
        insertCategory(db, "工资", 1, "ic_salary");
        insertCategory(db, "兼职", 1, "ic_part_time");
        insertCategory(db, "理财", 1, "ic_investment");
        insertCategory(db, "其他", 1, "ic_other");
    }

    /**
     * 辅助方法：插入单个分类
     */
    private void insertCategory(SQLiteDatabase db, String name, int type, String icon) {
        String sql = "INSERT INTO " + TABLE_CATEGORIES + " (" + COLUMN_NAME + ", " + COLUMN_CATEGORY_TYPE + ", " + COLUMN_CATEGORY_ICON + ") VALUES ('" + name + "', " + type + ", '" + icon + "')";
        db.execSQL(sql);
    }
}
