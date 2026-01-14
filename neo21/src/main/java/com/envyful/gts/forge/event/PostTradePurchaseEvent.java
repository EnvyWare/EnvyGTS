package com.envyful.gts.forge.event;

import com.envyful.api.neoforge.player.ForgeEnvyPlayer;
import com.envyful.gts.forge.api.TradeOffer;
import com.envyful.gts.forge.api.trade.Trade;
import net.neoforged.bus.api.Event;

/**
 *
 * Represents when a player purchases a trade from the GTS
 *
 */
public class PostTradePurchaseEvent extends Event {

    private final ForgeEnvyPlayer purchasee;
    private final Trade trade;

    public PostTradePurchaseEvent(ForgeEnvyPlayer purchasee, Trade trade) {
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
