package com.envyful.gts.forge.listener.discord;

import com.envyful.api.concurrency.UtilConcurrency;
import com.envyful.api.discord.DiscordWebHook;
import com.envyful.api.forge.listener.LazyListener;
import com.envyful.gts.api.discord.DiscordEvent;
import com.envyful.gts.api.discord.DiscordEventManager;
import com.envyful.gts.forge.EnvyGTSForge;
import com.envyful.gts.forge.event.TradeRemoveEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.io.IOException;

public class DiscordTradeRemoveListener extends LazyListener {

    public DiscordTradeRemoveListener() {
        super();
    }

    @SubscribeEvent
    public void onTradeCreate(TradeRemoveEvent event) {
        DiscordEvent removeHandler = DiscordEventManager.getRemoveHandler();

        if (removeHandler == null || !removeHandler.isEnabled()) {
            return;
        }

        UtilConcurrency.runAsync(() -> {
            try {
                DiscordWebHook webHook = event.getTrade().getWebHook(removeHandler);

                if (webHook != null) {
                    webHook.execute();
                } else {
                    EnvyGTSForge.getLogger().warn("Failed to remove webhook for trade: {}", event.getTrade().getDisplayName());
                }
            } catch (IOException e) {
                EnvyGTSForge.getLogger().error("Failed to send trade remove webhook", e);
            }
        });
    }
}
