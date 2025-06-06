package com.envyful.gts.forge.impl;

import com.envyful.api.neoforge.player.ForgeEnvyPlayer;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.gts.api.GlobalTradeManager;
import com.envyful.gts.api.Trade;
import com.envyful.gts.forge.event.TradeCreateEvent;
import com.envyful.gts.forge.player.GTSAttribute;
import com.google.common.collect.Lists;
import net.neoforged.neoforge.common.NeoForge;

import java.util.Collections;
import java.util.List;

public abstract class ForgeGlobalTradeManager implements GlobalTradeManager {

    protected final List<Trade> activeTrades = Lists.newArrayList();

    public ForgeGlobalTradeManager() {}

    @Override
    public boolean addTrade(EnvyPlayer<?> player, Trade trade) {
        GTSAttribute attribute = ((ForgeEnvyPlayer) player).getAttributeNow(GTSAttribute.class);

        if (attribute == null) {
            return false;
        }

        TradeCreateEvent event = new TradeCreateEvent((ForgeEnvyPlayer)player, trade);
        NeoForge.EVENT_BUS.post(event);

        if (event.isCanceled()) {
            return false;
        }

        this.activeTrades.add(trade);
        attribute.getOwnedTrades().add(trade);
        return true;
    }

    @Override
    public void removeTrade(Trade trade) {
        this.activeTrades.remove(trade);
    }

    @Override
    public List<Trade> getAllTrades() {
        return Lists.newArrayList(this.activeTrades);
    }

    @Override
    public List<Trade> getUserTrades(EnvyPlayer<?> player) {
        GTSAttribute attribute = player.getAttributeNow(GTSAttribute.class);

        if (attribute == null) {
            return Collections.emptyList();
        }

        return attribute.getOwnedTrades();
    }

    @Override
    public List<Trade> getExpiredTrades(EnvyPlayer<?> player) {
        GTSAttribute attribute = player.getAttributeNow(GTSAttribute.class);

        if (attribute == null) {
            return Collections.emptyList();
        }

        List<Trade> expired = Lists.newArrayList();

        for (Trade ownedTrade : attribute.getOwnedTrades()) {
            if ((ownedTrade.hasExpired() || ownedTrade.wasRemoved()) && !ownedTrade.wasPurchased()) {
                expired.add(ownedTrade);
            }
        }

        return expired;
    }

    @Override
    public List<Trade> getPurchasedTrades(EnvyPlayer<?> player) {
        GTSAttribute attribute = player.getAttributeNow(GTSAttribute.class);

        if (attribute == null) {
            return Collections.emptyList();
        }

        List<Trade> purchased = Lists.newArrayList();

        for (Trade trade : attribute.getOwnedTrades()) {
            if (trade.wasPurchased()) {
                purchased.add(trade);
            }
        }

        return purchased;
    }
}
