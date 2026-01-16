package com.envyful.gts.forge.api.event;

import com.envyful.api.neoforge.player.ForgeEnvyPlayer;
import com.envyful.gts.forge.api.CollectionItem;
import com.envyful.gts.forge.api.trade.Trade;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

/**
 *
 * Represents when a player collects a purchased or expired trade from the GTS
 *
 */
public class TradeCollectEvent extends Event implements ICancellableEvent {

    private final ForgeEnvyPlayer collector;
    private final CollectionItem trade;

    public TradeCollectEvent(ForgeEnvyPlayer collector, CollectionItem trade) {
        this.collector = collector;
        this.trade = trade;
    }

    public ForgeEnvyPlayer getCollector() {
        return this.collector;
    }

    public CollectionItem getTrade() {
        return this.trade;
    }
}
