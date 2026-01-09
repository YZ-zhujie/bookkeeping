package com.zzj.myapplication.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.zzj.myapplication.model.Category;

import java.util.ArrayList;
import java.util.List;

/**
 * 分类数据访问对象 (DAO)
 * 负责 Categories 表的数据库操作
 */
public class CategoryDao {
    private DatabaseHelper dbHelper;

    public CategoryDao(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    /**
     * 添加新分类
     * @param category 分类对象
     * @return 新插入的行ID
     */
    public long addCategory(Category category) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_NAME, category.getName());
        values.put(DatabaseHelper.COLUMN_CATEGORY_TYPE, category.getType());
        values.put(DatabaseHelper.COLUMN_CATEGORY_ICON, category.getIconResName());

        long id = db.insert(DatabaseHelper.TABLE_CATEGORIES, null, values);
        db.close();
        return id;
    }

    /**
     * 获取所有分类列表
     * @return 分类对象列表
     */
    public List<Category> getAllCategories() {
        List<Category> categoryList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + DatabaseHelper.TABLE_CATEGORIES;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Category category = new Category(
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY_TYPE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY_ICON))
                );
                categoryList.add(category);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return categoryList;
    }
    
    /**
     * 根据类型 (收入/支出) 获取分类列表
     * @param type 收支类型 (0: 支出, 1: 收入)
     * @return 分类对象列表
     */
    public List<Category> getCategoriesByType(int type) {
        List<Category> categoryList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_CATEGORIES, null,
                DatabaseHelper.COLUMN_CATEGORY_TYPE + "=?", new String[]{String.valueOf(type)},
                null, null, null);

        if (cursor.moveToFirst()) {
            do {
                Category category = new Category(
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY_TYPE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY_ICON))
                );
                categoryList.add(category);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return categoryList;
    }
}
