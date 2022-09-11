package com.envyful.gts.forge.listener.discord;

import com.envyful.api.concurrency.UtilConcurrency;
import com.envyful.api.discord.DiscordWebHook;
import com.envyful.api.forge.listener.LazyListener;
import com.envyful.gts.api.discord.DiscordEvent;
import com.envyful.gts.api.discord.DiscordEventManager;
import com.envyful.gts.forge.event.PostTradePurchaseEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.IOException;

public class DiscordTradePurchaseListener extends LazyListener {

    public DiscordTradePurchaseListener() {
        super();
    }

    @SubscribeEvent
    public void onTradeCreate(PostTradePurchaseEvent event) {
        DiscordEvent purchaseHandler = DiscordEventManager.getPurchaseHandler();

        if (purchaseHandler == null || !purchaseHandler.isEnabled()) {
            return;
        }

        UtilConcurrency.runAsync(() -> {
            try {
                DiscordWebHook webHook = event.getTrade().getWebHook(purchaseHandler);

                if (webHook != null) {
                    webHook.execute();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
