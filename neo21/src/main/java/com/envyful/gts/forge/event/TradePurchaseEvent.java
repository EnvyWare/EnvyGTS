package com.envyful.gts.forge.event;

import com.envyful.api.neoforge.player.ForgeEnvyPlayer;
import com.envyful.gts.forge.api.TradeOffer;
import com.envyful.gts.forge.api.trade.Trade;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

/**
 *
 * Represents when a player purchases a trade from the GTS
 *
 */
public class TradePurchaseEvent extends Event implements ICancellableEvent {

    private final ForgeEnvyPlayer purchasee;
    private final Trade trade;

    public TradePurchaseEvent(ForgeEnvyPlayer purchasee, Trade trade) {
        this.purchasee = purchasee;
        this.trade = trade;
    }

    public ForgeEnvyPlayer getPurchasee() {
        return this.purchasee;
    }

    public Trade getTrade() {
        return this.trade;
    }
}
