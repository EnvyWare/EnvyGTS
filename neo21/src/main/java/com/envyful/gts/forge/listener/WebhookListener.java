package com.envyful.gts.forge.listener;

import com.envyful.api.concurrency.UtilConcurrency;
import com.envyful.api.text.Placeholder;
import com.envyful.gts.forge.EnvyGTSForge;
import com.envyful.gts.forge.api.event.PostTradePurchaseEvent;
import com.envyful.gts.forge.api.event.TradeCreateEvent;
import com.envyful.gts.forge.api.event.TradeRemoveEvent;
import net.neoforged.bus.api.SubscribeEvent;

import java.io.IOException;

public class WebhookListener {

    public WebhookListener() {

    }

    @SubscribeEvent
    public void onTradeCreate(TradeCreateEvent event) {
        this.onWebhookEvent("trade_create", event.getTrade(), this.getFilterPlaceholders());
    }

    @SubscribeEvent
    public void onTradeCreate(PostTradePurchaseEvent event) {
        this.onWebhookEvent("trade_purchase", event.getTrade(), this.getFilterPlaceholders());
    }

    @SubscribeEvent
    public void onTradeRemove(TradeRemoveEvent event) {
        this.onWebhookEvent("trade_remove", event.getTrade(), this.getFilterPlaceholders());
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

    private Placeholder getFilterPlaceholders() {
        return Placeholder.simple(s -> {
            for (var replacement : EnvyGTSForge.getConfig().getReplacements()) {
                s = replacement.replace(s);
            }

            return s;
        });
    }
}
