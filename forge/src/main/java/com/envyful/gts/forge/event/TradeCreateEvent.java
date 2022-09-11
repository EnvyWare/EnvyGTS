package com.envyful.gts.forge.event;

import com.envyful.api.player.EnvyPlayer;
import com.envyful.gts.api.Trade;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 *
 * Represents when a player adds a trade to the GTS
 *
 */
@Cancelable
public class TradeCreateEvent extends Event {

    private final EnvyPlayer<EntityPlayerMP> player;
    private final Trade trade;

    public TradeCreateEvent(EnvyPlayer<EntityPlayerMP> player, Trade trade) {
        this.player = player;
        this.trade = trade;
    }

    public EnvyPlayer<EntityPlayerMP> getPlayer() {
        return this.player;
    }

    public Trade getTrade() {
        return this.trade;
    }
}
