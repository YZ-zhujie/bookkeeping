package com.zzj.myapplication.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.zzj.myapplication.model.Account;

import java.util.ArrayList;
import java.util.List;

/**
 * 账户数据访问对象 (DAO)
 * 负责 Accounts 表的增删改查操作
 */
public class AccountDao {
    private DatabaseHelper dbHelper;

    public AccountDao(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    /**
     * 添加新账户
     * @param account 账户对象
     * @return 新插入的行ID
     */
    public long addAccount(Account account) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_NAME, account.getName());
        values.put(DatabaseHelper.COLUMN_ACCOUNT_TYPE, account.getType());
        values.put(DatabaseHelper.COLUMN_ACCOUNT_BALANCE, account.getBalance());

        long id = db.insert(DatabaseHelper.TABLE_ACCOUNTS, null, values);
        db.close();
        return id;
    }

    /**
     * 根据 ID 获取账户详情
     * @param id 账户ID
     * @return 账户对象，如果未找到则返回 null
     */
    public Account getAccount(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_ACCOUNTS, null,
                DatabaseHelper.COLUMN_ID + "=?", new String[]{String.valueOf(id)},
                null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        Account account = null;
        if (cursor != null && cursor.getCount() > 0) {
            account = new Account(
                    cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACCOUNT_TYPE)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACCOUNT_BALANCE))
            );
            cursor.close();
        }
        db.close();
        return account;
    }

    /**
     * 获取所有账户列表
     * @return 账户对象列表
     */
    public List<Account> getAllAccounts() {
        List<Account> accountList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + DatabaseHelper.TABLE_ACCOUNTS;

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Account account = new Account(
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACCOUNT_TYPE)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACCOUNT_BALANCE))
                );
                accountList.add(account);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return accountList;
    }

    /**
     * 更新账户信息 (如余额变更)
     * @param account 包含更新信息的账户对象
     * @return 受影响的行数
     */
    public int updateAccount(Account account) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_NAME, account.getName());
        values.put(DatabaseHelper.COLUMN_ACCOUNT_TYPE, account.getType());
        values.put(DatabaseHelper.COLUMN_ACCOUNT_BALANCE, account.getBalance());

        int result = db.update(DatabaseHelper.TABLE_ACCOUNTS, values,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(account.getId())});
        db.close();
        return result;
    }

    /**
     * 删除账户
     * @param account 要删除的账户对象
     */
    public void deleteAccount(Account account) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_ACCOUNTS, DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(account.getId())});
        db.close();
    }
}
