package com.envyful.reforged.gts.forge.event;

import com.envyful.api.player.EnvyPlayer;
import com.envyful.reforged.gts.api.Trade;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 *
 * Represents when a player purchases a trade from the GTS
 *
 */
public class PostTradePurchaseEvent extends Event {

    private final EnvyPlayer<EntityPlayerMP> purchasee;
    private final Trade trade;

    public PostTradePurchaseEvent(EnvyPlayer<EntityPlayerMP> purchasee, Trade trade) {
        this.purchasee = purchasee;
        this.trade = trade;
    }

    public EnvyPlayer<EntityPlayerMP> getPurchasee() {
        return this.purchasee;
    }

    public Trade getTrade() {
        return this.trade;
    }
}
