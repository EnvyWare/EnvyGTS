package com.envyful.reforged.gts.forge.event;

import com.envyful.reforged.gts.api.Trade;
import net.minecraftforge.fml.common.eventhandler.Event;

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
