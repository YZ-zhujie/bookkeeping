package com.zzj.myapplication.model;

/**
 * 收支分类实体类
 * 表示记账的分类，如餐饮、交通、工资等
 */
public class Category {
    private int id;
    private String name;
    private int type; // 0: 支出, 1: 收入
    private String iconResName; // 存储资源名称字符串以保持灵活性

    public static final int TYPE_EXPENSE = 0;
    public static final int TYPE_INCOME = 1;

    public Category() {}

    public Category(int id, String name, int type, String iconResName) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.iconResName = iconResName;
    }

    public Category(String name, int type, String iconResName) {
        this.name = name;
        this.type = type;
        this.iconResName = iconResName;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    /**
     * 获取分类类型 (0: 支出, 1: 收入)
     */
    public int getType() { return type; }
    public void setType(int type) { this.type = type; }

    public String getIconResName() { return iconResName; }
    public void setIconResName(String iconResName) { this.iconResName = iconResName; }
}
