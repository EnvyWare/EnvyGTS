package com.envyful.gts.forge.listener;

import com.envyful.api.neoforge.listener.LazyListener;
import com.envyful.api.platform.PlatformProxy;
import com.envyful.api.text.Placeholder;
import com.envyful.gts.api.Trade;
import com.envyful.gts.forge.EnvyGTSForge;
import com.envyful.gts.forge.event.TradeCreateEvent;
import com.envyful.gts.forge.impl.trade.type.PokemonTrade;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import net.neoforged.bus.api.SubscribeEvent;

public class TradeCreateListener extends LazyListener {

    public TradeCreateListener() {
        super();
    }

    @SubscribeEvent
    public void onTradeCreate(TradeCreateEvent event) {
        var blockedReason = EnvyGTSForge.getConfig().isBlocked(event.getTrade().getDisplayName());

        if (blockedReason != null) {
            event.setCanceled(true);
            event.getPlayer().message(EnvyGTSForge.getLocale().getBlockedItem(), Placeholder.simple("%reason%", blockedReason));
            return;
        }

        if (!EnvyGTSForge.getConfig().isEnableNewListingBroadcasts()) {
            return;
        }

        PlatformProxy.broadcastMessage(EnvyGTSForge.getLocale().getMessages().getCreateTradeBroadcast(this.getPokemon(event.getTrade())), event.getTrade());
    }

    private Pokemon getPokemon(Trade trade) {
        if (trade instanceof PokemonTrade) {
            return ((PokemonTrade) trade).getPokemon();
        }

        return null;
    }
}
