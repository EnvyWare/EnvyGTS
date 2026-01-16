package com.envyful.gts.forge.api.event;

import com.envyful.api.neoforge.player.ForgeEnvyPlayer;
import com.envyful.gts.forge.api.trade.Trade;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

/**
 *
 * Represents when a player purchases a trade from the GTS
 *
 */
public abstract class TradePurchaseEvent extends Event {

    private final ForgeEnvyPlayer seller;
    private final ForgeEnvyPlayer purchaser;
    private final Trade trade;

    protected TradePurchaseEvent(ForgeEnvyPlayer seller, ForgeEnvyPlayer purchaser, Trade trade) {
        this.seller = seller;
        this.purchaser = purchaser;
        this.trade = trade;
    }

    public ForgeEnvyPlayer getSeller() {
        return this.seller;
    }

    public ForgeEnvyPlayer getPurchaser() {
        return this.purchaser;
    }

    public Trade getTrade() {
        return this.trade;
    }

    public static class Pre extends TradePurchaseEvent implements ICancellableEvent {

        public Pre(ForgeEnvyPlayer seller, ForgeEnvyPlayer purchaser, Trade trade) {
            super(seller, purchaser, trade);
        }
    }

    public static class Post extends TradePurchaseEvent {

        public Post(ForgeEnvyPlayer seller, ForgeEnvyPlayer purchaser, Trade trade) {
            super(seller, purchaser, trade);
        }
    }
}
