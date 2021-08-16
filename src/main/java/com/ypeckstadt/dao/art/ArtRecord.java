package com.ypeckstadt.dao.art;

import com.ypeckstadt.util.DateUtils;

public class ArtRecord {

    private final String id;
    private final String accountId;
    private final int price;
    private final long createdAt;

    public ArtRecord(String artId, String accountId, int price, long createdAt) {
        this.id = artId;
        this.accountId = accountId;
        this.price = price;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public String getAccountId() {
        return accountId;
    }

    public int getPrice() {
        return price;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return "Art : id " + id + ", owner " + accountId + ", price " + price + ", createdAt " + DateUtils.millisToDateStr(createdAt, DateUtils.FORMAT_YYYYMMDD);
    }
}
