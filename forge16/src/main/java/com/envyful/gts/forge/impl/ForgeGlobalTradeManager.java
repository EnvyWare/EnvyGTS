package com.envyful.gts.forge.impl;

import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.gts.api.GlobalTradeManager;
import com.envyful.gts.api.Trade;
import com.envyful.gts.forge.EnvyGTSForge;
import com.envyful.gts.forge.event.TradeCreateEvent;
import com.envyful.gts.forge.player.GTSAttribute;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.minecraftforge.common.MinecraftForge;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public abstract class ForgeGlobalTradeManager implements GlobalTradeManager {

    protected final Set<Trade> activeTrades = Sets.newHashSet();

    public ForgeGlobalTradeManager() {}

    @Override
    public boolean addTrade(EnvyPlayer<?> player, Trade trade) {
        GTSAttribute attribute = player.getAttribute(EnvyGTSForge.class);

        if (attribute == null) {
            return false;
        }

        TradeCreateEvent event = new TradeCreateEvent((ForgeEnvyPlayer)player, trade);
        MinecraftForge.EVENT_BUS.post(event);

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
    public Set<Trade> getAllTrades() {
        return Sets.newHashSet(this.activeTrades);
    }

    @Override
    public Set<Trade> getUserTrades(EnvyPlayer<?> player) {
        GTSAttribute attribute = player.getAttribute(EnvyGTSForge.class);

        if (attribute == null) {
            return Collections.emptySet();
        }

        return attribute.getOwnedTrades();
    }

    @Override
    public List<Trade> getExpiredTrades(EnvyPlayer<?> player) {
        GTSAttribute attribute = player.getAttribute(EnvyGTSForge.class);

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
        GTSAttribute attribute = player.getAttribute(EnvyGTSForge.class);

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
