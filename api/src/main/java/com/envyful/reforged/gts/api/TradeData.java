package com.envyful.reforged.gts.api;

import java.util.UUID;

public class TradeData {

    private final UUID owner;
    private final String name;
    private final long expiry;

    public TradeData(UUID owner, String name, long expiry) {
        this.owner = owner;
        this.name = name;
        this.expiry = expiry;
    }

    public UUID getOwner() {
        return this.owner;
    }

    public String getName() {
        return this.name;
    }

    public long getExpiry() {
        return this.expiry;
    }
}
