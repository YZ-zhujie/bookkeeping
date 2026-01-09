package com.zzj.myapplication.model;

/**
 * 物品实体类
 * 表示用户的个人物品，包含购买信息、状态和照片等
 */
public class Item {
    private int id;
    private String name;
    private int status; // 0: 使用中, 1: 闲置, 2: 丢失, 3: 已售
    private Integer recordId; // 可为空, 关联到 Record 表的外键
    private long purchaseDate;
    private double price;
    private String photoPath;

    public static final int STATUS_IN_USE = 0;
    public static final int STATUS_IDLE = 1;
    public static final int STATUS_LOST = 2;
    public static final int STATUS_SOLD = 3;

    public Item() {}

    public Item(int id, String name, int status, Integer recordId, long purchaseDate, double price, String photoPath) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.recordId = recordId;
        this.purchaseDate = purchaseDate;
        this.price = price;
        this.photoPath = photoPath;
    }

    public Item(String name, int status, Integer recordId, long purchaseDate, double price, String photoPath) {
        this.name = name;
        this.status = status;
        this.recordId = recordId;
        this.purchaseDate = purchaseDate;
        this.price = price;
        this.photoPath = photoPath;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public Integer getRecordId() { return recordId; }
    public void setRecordId(Integer recordId) { this.recordId = recordId; }

    public long getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(long purchaseDate) { this.purchaseDate = purchaseDate; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    /**
     * 获取物品图片路径
     */
    public String getPhotoPath() { return photoPath; }
    public void setPhotoPath(String photoPath) { this.photoPath = photoPath; }
}
