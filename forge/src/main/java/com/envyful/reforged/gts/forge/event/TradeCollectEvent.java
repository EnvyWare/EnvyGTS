package com.envyful.reforged.gts.forge.event;

import com.envyful.api.player.EnvyPlayer;
import com.envyful.reforged.gts.api.Trade;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 *
 * Represents when a player collects a purchased or expired trade from the GTS
 *
 */
public class TradeCollectEvent extends Event {

    private final EnvyPlayer<EntityPlayerMP> collector;
    private final Trade trade;

    public TradeCollectEvent(EnvyPlayer<EntityPlayerMP> collector, Trade trade) {
        this.collector = collector;
        this.trade = trade;
    }

    public EnvyPlayer<EntityPlayerMP> getCollector() {
        return this.collector;
    }

    public Trade getTrade() {
        return this.trade;
    }
}
