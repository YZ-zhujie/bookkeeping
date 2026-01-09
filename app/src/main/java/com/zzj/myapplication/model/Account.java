package com.zzj.myapplication.model;

/**
 * 账户实体类
 * 表示用户的资产账户，如现金、银行卡、微信等
 */
public class Account {
    private int id;
    private String name;
    private String type; // 账户类型 (如: Cash, Card, WeChat, Alipay)
    private double balance;

    public Account() {}

    public Account(int id, String name, String type, double balance) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.balance = balance;
    }

    public Account(String name, String type, double balance) {
        this.name = name;
        this.type = type;
        this.balance = balance;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    /**
     * 获取账户当前余额
     */
    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }
}
