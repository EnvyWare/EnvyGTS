package com.envyful.reforged.gts.forge.listener;

import com.envyful.api.concurrency.UtilConcurrency;
import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.listener.LazyListener;
import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.reforged.gts.forge.ReforgedGTSForge;
import com.envyful.reforged.gts.forge.event.TradeCreateEvent;
import com.envyful.reforged.gts.forge.player.GTSAttribute;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TradeCreateListener extends LazyListener {

    private final ReforgedGTSForge mod;

    public TradeCreateListener(ReforgedGTSForge mod) {
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
                GTSAttribute attribute = onlinePlayer.getAttribute(ReforgedGTSForge.class);

                if (attribute == null || !attribute.getSettings().isToggledBroadcasts()) {
                    continue;
                }

                for (String s : this.mod.getLocale().getMessages().getCreateTradeBroadcast()) {
                    onlinePlayer.message(UtilChatColour.translateColourCodes(
                            '&',
                            s.replace("%player%", event.getPlayer().getName())
                            .replace("%name%", event.getTrade().getDisplayName())
                    ));
                }
            }
        });
    }
}
