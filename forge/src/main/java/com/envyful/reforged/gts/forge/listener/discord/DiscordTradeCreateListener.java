package com.envyful.reforged.gts.forge.listener.discord;

import com.envyful.api.concurrency.UtilConcurrency;
import com.envyful.api.discord.DiscordWebHook;
import com.envyful.api.forge.listener.LazyListener;
import com.envyful.reforged.gts.api.discord.DiscordEvent;
import com.envyful.reforged.gts.api.discord.DiscordEventManager;
import com.envyful.reforged.gts.forge.event.TradeCreateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.IOException;

public class DiscordTradeCreateListener extends LazyListener {

    public DiscordTradeCreateListener() {
        super();
    }

    @SubscribeEvent
    public void onTradeCreate(TradeCreateEvent event) {
        DiscordEvent publishHandler = DiscordEventManager.getPublishHandler();

        if (!publishHandler.isEnabled()) {
            return;
        }

        UtilConcurrency.runAsync(() -> {
            try {
                DiscordWebHook webHook = event.getTrade().getWebHook(publishHandler);

                if (webHook != null) {
                    webHook.execute();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
