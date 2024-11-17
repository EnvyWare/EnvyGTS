package com.envyful.gts.forge.config;

import com.envyful.api.discord.yaml.DiscordWebHookConfig;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class GTSWebHook {

    private String event;
    private DiscordWebHookConfig webHook;

    public GTSWebHook() {
    }

    public GTSWebHook(String event, DiscordWebHookConfig webHook) {
        this.event = event;
        this.webHook = webHook;
    }

    public String getEvent() {
        return this.event;
    }

    public DiscordWebHookConfig getWebHook() {
        return this.webHook;
    }
}
