package com.zzj.myapplication.model;

/**
 * 记账记录实体类
 * 表示每一笔收支记录
 */
public class Record {
    private int id;
    private double amount;
    private int type; // 0: 支出, 1: 收入
    private int categoryId;
    private int accountId;
    private long date; // 时间戳
    private String note;

    public static final int TYPE_EXPENSE = 0;
    public static final int TYPE_INCOME = 1;

    public Record() {}

    public Record(int id, double amount, int type, int categoryId, int accountId, long date, String note) {
        this.id = id;
        this.amount = amount;
        this.type = type;
        this.categoryId = categoryId;
        this.accountId = accountId;
        this.date = date;
        this.note = note;
    }

    public Record(double amount, int type, int categoryId, int accountId, long date, String note) {
        this.amount = amount;
        this.type = type;
        this.categoryId = categoryId;
        this.accountId = accountId;
        this.date = date;
        this.note = note;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    /**
     * 获取记录类型 (0: 支出, 1: 收入)
     */
    public int getType() { return type; }
    public void setType(int type) { this.type = type; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public int getAccountId() { return accountId; }
    public void setAccountId(int accountId) { this.accountId = accountId; }

    public long getDate() { return date; }
    public void setDate(long date) { this.date = date; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
