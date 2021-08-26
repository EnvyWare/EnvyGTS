package com.envyful.reforged.gts.forge.event;

import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.reforged.gts.api.Trade;
import com.envyful.reforged.gts.api.sql.ReforgedGTSQueries;
import com.envyful.reforged.gts.forge.ReforgedGTSForge;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.Optional;
import java.util.UUID;

/**
 *
 * Represents when a player adds a trade to the GTS
 *
 */
@Cancelable
public class TradePurchaseEvent extends Event {

    private final UUID owner;
    private final EnvyPlayer<EntityPlayerMP> purchasee;
    private final Trade trade;

    public TradePurchaseEvent(UUID owner, EnvyPlayer<EntityPlayerMP> purchasee, Trade trade) {
        this.owner = owner;
        this.purchasee = purchasee;
        this.trade = trade;
    }

    public UUID getOwner() {
        return this.owner;
    }

    public Optional<EnvyPlayer<EntityPlayerMP>> getOwnerPlayer() {
        ForgeEnvyPlayer player = ReforgedGTSForge.getInstance().getPlayerManager().getPlayer(this.owner);
        return Optional.ofNullable(player);
    }

    public EnvyPlayer<EntityPlayerMP> getPurchasee() {
        return this.purchasee;
    }

    public Trade getTrade() {
        return this.trade;
    }
}
