package com.envyful.gts.forge.event;

import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.gts.api.Trade;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

/**
 *
 * Represents when a player adds a trade to the GTS
 *
 */
@Cancelable
public class TradeCreateEvent extends Event {

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
