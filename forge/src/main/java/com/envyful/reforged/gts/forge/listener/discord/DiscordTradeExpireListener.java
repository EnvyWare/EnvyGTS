package com.envyful.reforged.gts.forge.listener.discord;

import com.envyful.api.concurrency.UtilConcurrency;
import com.envyful.api.forge.listener.LazyListener;
import com.envyful.reforged.gts.api.discord.DiscordEvent;
import com.envyful.reforged.gts.api.discord.DiscordEventManager;
import com.envyful.reforged.gts.forge.event.TradeExpireEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.IOException;

public class DiscordTradeExpireListener extends LazyListener {

    public DiscordTradeExpireListener() {
        super();
    }

    @SubscribeEvent
    public void onTradeCreate(TradeExpireEvent event) {
        DiscordEvent expireHandler = DiscordEventManager.getExpireHandler();

        if (!expireHandler.isEnabled()) {
            return;
        }

        UtilConcurrency.runAsync(() -> {
            try {
                event.getTrade().getWebHook(expireHandler).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
