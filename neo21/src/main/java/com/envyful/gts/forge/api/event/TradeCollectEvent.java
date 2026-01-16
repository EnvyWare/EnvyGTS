package com.envyful.gts.forge.api.event;

import com.envyful.api.neoforge.player.ForgeEnvyPlayer;
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
    private final Trade trade;

    public TradeCollectEvent(ForgeEnvyPlayer collector, Trade trade) {
        this.collector = collector;
        this.trade = trade;
    }

    public ForgeEnvyPlayer getCollector() {
        return this.collector;
    }

    public Trade getTrade() {
        return this.trade;
    }
}
