package com.envyful.reforged.gts.forge.event;

import com.envyful.reforged.gts.api.Trade;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 *
 * Represents when a Trade expires from the GTS
 *
 */
@Cancelable
public class TradeExpireEvent extends Event {

    private final Trade trade;

    public TradeExpireEvent(Trade trade) {
        this.trade = trade;
    }

    public Trade getTrade() {
        return this.trade;
    }
}
