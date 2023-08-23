package com.envyful.gts.forge.event;

import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.gts.api.Trade;
import net.minecraftforge.eventbus.api.Event;

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
