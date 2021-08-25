package com.envyful.reforged.gts.forge.impl;

import com.envyful.api.player.EnvyPlayer;
import com.envyful.reforged.gts.api.GlobalTradeManager;
import com.envyful.reforged.gts.api.Trade;
import com.envyful.reforged.gts.forge.ReforgedGTSForge;
import com.envyful.reforged.gts.forge.player.GTSAttribute;
import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;

public abstract class ForgeGlobalTradeManager implements GlobalTradeManager {

    private final List<Trade> activeTrades = Lists.newArrayList();

    public ForgeGlobalTradeManager() {}

    @Override
    public void addTrade(EnvyPlayer<?> player, Trade trade) {
        GTSAttribute attribute = player.getAttribute(ReforgedGTSForge.class);

        if (attribute == null) {
            return;
        }

        this.activeTrades.add(trade);
        attribute.getOwnedTrades().add(trade);
    }

    @Override
    public List<Trade> getAllTrades() {
        return this.activeTrades;
    }

    @Override
    public List<Trade> getUserTrades(EnvyPlayer<?> player) {
        List<Trade> trades = Lists.newArrayList();

        for (Trade activeTrade : this.activeTrades) {
            if (activeTrade.isOwner(player)) {
                trades.add(activeTrade);
            }
        }

        return trades;
    }

    @Override
    public List<Trade> getExpiredTrades(EnvyPlayer<?> player) {
        GTSAttribute attribute = player.getAttribute(ReforgedGTSForge.class);

        if (attribute == null) {
            return Collections.emptyList();
        }

        List<Trade> expired = Lists.newArrayList();

        for (Trade ownedTrade : attribute.getOwnedTrades()) {
            if (ownedTrade.hasExpired()) {
                expired.add(ownedTrade);
            }
        }

        return expired;
    }

    @Override
    public List<Trade> getPurchasedTrades(EnvyPlayer<?> player) {
        return null; //TODO:
    }
}
