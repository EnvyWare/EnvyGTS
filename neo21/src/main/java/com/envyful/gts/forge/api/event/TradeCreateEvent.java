package com.envyful.gts.forge.api.event;

import com.envyful.api.neoforge.player.ForgeEnvyPlayer;
import com.envyful.gts.forge.api.trade.Trade;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

/**
 *
 * Represents when a player adds a trade to the GTS
 *
 */
public class TradeCreateEvent extends Event implements ICancellableEvent {

    private final ForgeEnvyPlayer player;
    private final Trade trade;

    public TradeCreateEvent(ForgeEnvyPlayer player, Trade trade) {
        this.player = player;
        this.trade = trade;
    }

    public ForgeEnvyPlayer getPlayer() {
        return this.player;
    }

    public Trade getTrade() {
        return this.trade;
    }
}
