package com.ypeckstadt.dao.account;

import com.ypeckstadt.util.DateUtils;

public class AccountRecord {

    private final String id;
    private final int balance;
    private final long createdAt;

    public AccountRecord(String id, int balance, long createdAt) {
        this.id = id;
        this.balance = balance;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public int getBalance() {
        return balance;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return "Account : id " + id + ", balance " + balance + ", createdAt " + DateUtils.millisToDateStr(createdAt, DateUtils.FORMAT_YYYYMMDD);
    }
}
