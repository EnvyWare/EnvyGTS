package com.envyful.gts.forge.api.event;

import com.envyful.gts.forge.api.trade.Trade;
import net.neoforged.bus.api.Event;

/**
 *
 * Represents when a Trade is removed from the GTS
 *
 */
public class TradeRemoveEvent extends Event {

    private final Trade trade;

    public TradeRemoveEvent(Trade trade) {
        this.trade = trade;
    }

    public Trade getTrade() {
        return this.trade;
    }
}
