package com.envyful.reforged.gts.forge.impl;

import com.envyful.api.player.EnvyPlayer;
import com.envyful.reforged.gts.api.GlobalTradeManager;
import com.envyful.reforged.gts.api.Trade;
import com.envyful.reforged.gts.forge.ReforgedGTSForge;
import com.envyful.reforged.gts.forge.event.TradeCreateEvent;
import com.envyful.reforged.gts.forge.player.GTSAttribute;
import com.google.common.collect.Lists;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;

import java.util.Collections;
import java.util.List;

public abstract class ForgeGlobalTradeManager implements GlobalTradeManager {

    protected final List<Trade> activeTrades = Lists.newArrayList();

    public ForgeGlobalTradeManager() {}

    @Override
    public boolean addTrade(EnvyPlayer<?> player, Trade trade) {
        GTSAttribute attribute = player.getAttribute(ReforgedGTSForge.class);

        if (attribute == null) {
            return false;
        }

        TradeCreateEvent event = new TradeCreateEvent((EnvyPlayer<EntityPlayerMP>) player, trade);
        MinecraftForge.EVENT_BUS.post(event);

        if (event.isCanceled()) {
            return false;
        }

        this.activeTrades.add(trade);
        attribute.getOwnedTrades().add(trade);
        return true;
    }

    @Override
    public List<Trade> getAllTrades() {
        return Lists.newArrayList(this.activeTrades);
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
        return Collections.emptyList(); //TODO:
    }
}
