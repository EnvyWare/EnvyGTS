package com.envyful.gts.forge.listener;

import com.envyful.api.concurrency.UtilConcurrency;
import com.envyful.api.text.Placeholder;
import com.envyful.gts.forge.EnvyGTSForge;
import com.envyful.gts.forge.event.PostTradePurchaseEvent;
import com.envyful.gts.forge.event.TradeCreateEvent;
import com.envyful.gts.forge.event.TradeRemoveEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.io.IOException;

public class WebhookListener {

    public WebhookListener() {

    }

    @SubscribeEvent
    public void onTradeCreate(TradeCreateEvent event) {
        this.onWebhookEvent("trade_create");
    }

    @SubscribeEvent
    public void onTradeCreate(PostTradePurchaseEvent event) {
        this.onWebhookEvent("trade_purchase");
    }

    @SubscribeEvent
    public void onTradeRemove(TradeRemoveEvent event) {
        this.onWebhookEvent("trade_remove");
    }

    private void onWebhookEvent(String type, Placeholder... placeholders) {
        UtilConcurrency.runAsync(() -> {
            for (var webhook : EnvyGTSForge.getConfig().getWebhooks(type)) {
                try {
                    webhook.execute(placeholders);
                } catch (IOException e) {
                    EnvyGTSForge.getLogger().error("Failed to execute webhook: ", e);
                }
            }
        });
    }
}
