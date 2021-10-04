package com.envyful.reforged.gts.api;

public class TradeData {

    private final String name;
    private final long expiry;

    public TradeData(String name, long expiry) {
        this.name = name;
        this.expiry = expiry;
    }

    public String getName() {
        return this.name;
    }

    public long getExpiry() {
        return this.expiry;
    }
}
