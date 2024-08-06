package com.envyful.gts.forge.listener;

import com.envyful.api.concurrency.UtilConcurrency;
import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.listener.LazyListener;
import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.api.text.Placeholder;
import com.envyful.gts.api.Trade;
import com.envyful.gts.forge.EnvyGTSForge;
import com.envyful.gts.forge.event.TradeCreateEvent;
import com.envyful.gts.forge.impl.trade.type.PokemonTrade;
import com.envyful.gts.forge.player.GTSAttribute;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import net.minecraftforge.eventbus.api.SubscribeEvent;

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

        UtilConcurrency.runAsync(() -> {
            for (ForgeEnvyPlayer onlinePlayer : EnvyGTSForge.getPlayerManager().getOnlinePlayers()) {
                GTSAttribute attribute = onlinePlayer.getAttributeNow(GTSAttribute.class);

                if (attribute == null || !attribute.getSettings().isToggledBroadcasts()) {
                    continue;
                }

                for (String s : EnvyGTSForge.getLocale().getMessages().getCreateTradeBroadcast(this.getPokemon(event.getTrade()))) {
                    s = event.getTrade().replace(s);
                    s = s.replace("%player%", event.getPlayer().getName())
                            .replace("%name%", event.getTrade().getDisplayName())
                            .replace("%cost%",
                                    String.format(EnvyGTSForge.getLocale().getMoneyFormat(),
                                            event.getTrade().getCost()));

                    onlinePlayer.message(UtilChatColour.colour(s));
                }
            }
        });
    }

    private Pokemon getPokemon(Trade trade) {
        if (trade instanceof PokemonTrade) {
            return ((PokemonTrade) trade).getPokemon();
        }

        return null;
    }
}
