package com.zzj.myapplication.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.zzj.myapplication.model.Record;

import java.util.ArrayList;
import java.util.List;

/**
 * 记账记录数据访问对象 (DAO)
 * 负责 Records 表的增删改查
 */
public class RecordDao {
    private DatabaseHelper dbHelper;

    public RecordDao(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    /**
     * 添加新的记账记录
     * @param record 记录对象
     * @return 新插入的行ID
     */
    public long addRecord(Record record) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_RECORD_AMOUNT, record.getAmount());
        values.put(DatabaseHelper.COLUMN_RECORD_TYPE, record.getType());
        values.put(DatabaseHelper.COLUMN_RECORD_CATEGORY_ID, record.getCategoryId());
        values.put(DatabaseHelper.COLUMN_RECORD_ACCOUNT_ID, record.getAccountId());
        values.put(DatabaseHelper.COLUMN_RECORD_DATE, record.getDate());
        values.put(DatabaseHelper.COLUMN_RECORD_NOTE, record.getNote());

        long id = db.insert(DatabaseHelper.TABLE_RECORDS, null, values);
        db.close();
        return id;
    }

    /**
     * 获取所有记账记录 (按日期降序排列)
     * @return 记录对象列表
     */
    public List<Record> getAllRecords() {
        List<Record> recordList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + DatabaseHelper.TABLE_RECORDS + " ORDER BY " + DatabaseHelper.COLUMN_RECORD_DATE + " DESC";
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Record record = new Record(
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RECORD_AMOUNT)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RECORD_TYPE)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RECORD_CATEGORY_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RECORD_ACCOUNT_ID)),
                        cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RECORD_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RECORD_NOTE))
                );
                recordList.add(record);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return recordList;
    }

    /**
     * 删除单条记录
     * @param record 要删除的记录对象
     */
    public void deleteRecord(Record record) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_RECORDS, DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(record.getId())});
        db.close();
    }

    /**
     * 根据时间范围查询记录 (闭区间 [start, end])
     * @param startTime 开始时间戳
     * @param endTime 结束时间戳
     * @return 符合条件的记录列表
     */
    public List<Record> getRecordsByDateRange(long startTime, long endTime) {
        List<Record> recordList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + DatabaseHelper.TABLE_RECORDS + 
                             " WHERE " + DatabaseHelper.COLUMN_RECORD_DATE + " >= ? AND " + 
                             DatabaseHelper.COLUMN_RECORD_DATE + " <= ? " +
                             " ORDER BY " + DatabaseHelper.COLUMN_RECORD_DATE + " DESC";
        
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(startTime), String.valueOf(endTime)});

        if (cursor.moveToFirst()) {
            do {
                Record record = new Record(
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RECORD_AMOUNT)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RECORD_TYPE)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RECORD_CATEGORY_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RECORD_ACCOUNT_ID)),
                        cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RECORD_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RECORD_NOTE))
                );
                recordList.add(record);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return recordList;
    }
}
