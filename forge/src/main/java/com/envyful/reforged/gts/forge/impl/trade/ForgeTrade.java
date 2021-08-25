package com.envyful.reforged.gts.forge.impl.trade;

import com.envyful.reforged.gts.api.Trade;
import com.envyful.reforged.gts.api.gui.FilterType;

import java.util.Objects;
import java.util.UUID;

/**
 *
 * Abstract implementation of the {@link Trade} interface covering the basics
 *
 */
public abstract class ForgeTrade implements Trade {

    private final UUID owner;
    private final double cost;
    private final long expiry;
    private final FilterType type;

    public ForgeTrade(UUID owner, double cost, long expiry, FilterType type) {
        this.owner = owner;
        this.cost = cost;
        this.expiry = expiry;
        this.type = type;
    }

    @Override
    public boolean isOwner(UUID uuid) {
        return Objects.equals(this.owner, uuid);
    }

    @Override
    public boolean hasExpired() {
        return System.currentTimeMillis() >= this.expiry;
    }

    @Override
    public boolean filter(FilterType filterType) {
        return filterType.isAllowed(this.type);
    }
}
