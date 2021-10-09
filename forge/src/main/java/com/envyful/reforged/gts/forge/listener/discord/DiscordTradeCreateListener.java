package com.envyful.reforged.gts.forge.listener.discord;

import com.envyful.api.forge.listener.LazyListener;
import com.envyful.reforged.gts.forge.ReforgedGTSForge;
import com.envyful.reforged.gts.forge.config.discord.DiscordEvent;
import com.envyful.reforged.gts.forge.config.discord.DiscordEventManager;
import com.envyful.reforged.gts.forge.event.TradeCreateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class DiscordTradeCreateListener extends LazyListener {

    private final ReforgedGTSForge mod;

    public DiscordTradeCreateListener(ReforgedGTSForge mod) {
        super();

        this.mod = mod;
    }

    @SubscribeEvent
    public void onTradeCreate(TradeCreateEvent event) {
        DiscordEvent publishHandler = DiscordEventManager.getPublishHandler();

        if (!publishHandler.isEnabled()) {
            return;
        }


    }
}
