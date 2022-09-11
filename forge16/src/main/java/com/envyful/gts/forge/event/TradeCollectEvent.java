package com.envyful.gts.forge.event;

import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.gts.api.Trade;
import net.minecraftforge.eventbus.api.Event;

/**
 *
 * Represents when a player collects a purchased or expired trade from the GTS
 *
 */
public class TradeCollectEvent extends Event {

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
