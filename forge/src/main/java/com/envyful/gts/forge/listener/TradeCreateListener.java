package com.envyful.gts.forge.listener;

import com.envyful.api.concurrency.UtilConcurrency;
import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.listener.LazyListener;
import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.gts.forge.EnvyGTSForge;
import com.envyful.gts.forge.event.TradeCreateEvent;
import com.envyful.gts.forge.player.GTSAttribute;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TradeCreateListener extends LazyListener {

    private final EnvyGTSForge mod;

    public TradeCreateListener(EnvyGTSForge mod) {
        super();

        this.mod = mod;
    }

    @SubscribeEvent
    public void onTradeCreate(TradeCreateEvent event) {
        if (!this.mod.getConfig().isEnableNewListingBroadcasts()) {
            return;
        }

        UtilConcurrency.runAsync(() -> {
            for (ForgeEnvyPlayer onlinePlayer : this.mod.getPlayerManager().getOnlinePlayers()) {
                GTSAttribute attribute = onlinePlayer.getAttribute(EnvyGTSForge.class);

                if (attribute == null || !attribute.getSettings().isToggledBroadcasts()) {
                    continue;
                }

                for (String s : this.mod.getLocale().getMessages().getCreateTradeBroadcast()) {
                    onlinePlayer.message(UtilChatColour.translateColourCodes(
                            '&',
                            s.replace("%player%", event.getPlayer().getName())
                            .replace("%name%", event.getTrade().getDisplayName())
                            .replace("%cost%",
                                     String.format(EnvyGTSForge.getInstance().getLocale().getMoneyFormat(),
                                                   event.getTrade().getCost()))
                    ));
                }
            }
        });
    }
}
