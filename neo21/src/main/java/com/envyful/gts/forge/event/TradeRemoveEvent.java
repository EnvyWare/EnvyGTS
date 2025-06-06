package com.envyful.gts.forge.event;

import com.envyful.gts.api.Trade;
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
