package com.envyful.reforged.gts.forge.listener.discord;

import com.envyful.api.concurrency.UtilConcurrency;
import com.envyful.api.forge.listener.LazyListener;
import com.envyful.reforged.gts.api.discord.DiscordEvent;
import com.envyful.reforged.gts.api.discord.DiscordEventManager;
import com.envyful.reforged.gts.forge.event.TradeRemoveEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.IOException;

public class DiscordTradeRemoveListener extends LazyListener {

    public DiscordTradeRemoveListener() {
        super();
    }

    @SubscribeEvent
    public void onTradeCreate(TradeRemoveEvent event) {
        DiscordEvent removeHandler = DiscordEventManager.getExpireHandler();

        if (!removeHandler.isEnabled()) {
            return;
        }

        UtilConcurrency.runAsync(() -> {
            try {
                event.getTrade().getWebHook(removeHandler).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
