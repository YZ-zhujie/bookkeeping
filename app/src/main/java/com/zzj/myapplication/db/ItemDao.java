package com.zzj.myapplication.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.zzj.myapplication.model.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * 物品数据访问对象 (DAO)
 * 负责 Items 表的增删改查
 */
public class ItemDao {
    private DatabaseHelper dbHelper;

    public ItemDao(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    /**
     * 添加新物品
     * @param item 物品对象
     * @return 新插入的行ID
     */
    public long addItem(Item item) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_NAME, item.getName());
        values.put(DatabaseHelper.COLUMN_ITEM_STATUS, item.getStatus());
        if (item.getRecordId() != null) {
            values.put(DatabaseHelper.COLUMN_ITEM_RECORD_ID, item.getRecordId());
        }
        values.put(DatabaseHelper.COLUMN_ITEM_PURCHASE_DATE, item.getPurchaseDate());
        values.put(DatabaseHelper.COLUMN_ITEM_PRICE, item.getPrice());
        values.put(DatabaseHelper.COLUMN_ITEM_PHOTO_PATH, item.getPhotoPath());

        long id = db.insert(DatabaseHelper.TABLE_ITEMS, null, values);
        db.close();
        return id;
    }

    /**
     * 获取所有物品列表 (按ID降序)
     * @return 物品对象列表
     */
    public List<Item> getAllItems() {
        List<Item> itemList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + DatabaseHelper.TABLE_ITEMS + " ORDER BY " + DatabaseHelper.COLUMN_ID + " DESC";
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Item item = new Item(
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ITEM_STATUS)),
                        cursor.isNull(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ITEM_RECORD_ID)) ? null : cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ITEM_RECORD_ID)),
                        cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ITEM_PURCHASE_DATE)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ITEM_PRICE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ITEM_PHOTO_PATH))
                );
                itemList.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return itemList;
    }

    /**
     * 根据ID获取物品
     * @param id 物品ID
     * @return 物品对象，若未找到返回 null
     */
    public Item getItem(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_ITEMS, null,
                DatabaseHelper.COLUMN_ID + "=?", new String[]{String.valueOf(id)},
                null, null, null);

        Item item = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                item = new Item(
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ITEM_STATUS)),
                        cursor.isNull(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ITEM_RECORD_ID)) ? null : cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ITEM_RECORD_ID)),
                        cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ITEM_PURCHASE_DATE)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ITEM_PRICE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ITEM_PHOTO_PATH))
                );
            }
            cursor.close();
        }
        db.close();
        return item;
    }

    /**
     * 更新物品信息 (全量更新)
     * @param item 包含更新信息的物品对象
     * @return 受影响的行数
     */
    public int updateItem(Item item) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_NAME, item.getName());
        values.put(DatabaseHelper.COLUMN_ITEM_STATUS, item.getStatus());
        values.put(DatabaseHelper.COLUMN_ITEM_PURCHASE_DATE, item.getPurchaseDate());
        values.put(DatabaseHelper.COLUMN_ITEM_PRICE, item.getPrice());
        values.put(DatabaseHelper.COLUMN_ITEM_PHOTO_PATH, item.getPhotoPath());
        
        int rows = db.update(DatabaseHelper.TABLE_ITEMS, values,
                DatabaseHelper.COLUMN_ID + "=?", new String[]{String.valueOf(item.getId())});
        db.close();
        return rows;
    }

    /**
     *
     * 更新物品状态
     * @param itemId 物品ID
     * @param newStatus 新状态
     * @return 受影响的行数
     */
    public int updateItemStatus(int itemId, int newStatus) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_ITEM_STATUS, newStatus);
        
        int rows = db.update(DatabaseHelper.TABLE_ITEMS, values, 
                DatabaseHelper.COLUMN_ID + "=?", new String[]{String.valueOf(itemId)});
        db.close();
        return rows;
    }

    /**
     * 删除物品
     * @param item 要删除的物品对象
     */
    public void deleteItem(Item item) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_ITEMS, DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(item.getId())});
        db.close();
    }
}
