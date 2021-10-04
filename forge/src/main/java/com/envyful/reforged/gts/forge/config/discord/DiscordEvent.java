package com.envyful.reforged.gts.forge.config.discord;

import com.envyful.api.discord.DiscordWebHook;

public class DiscordEvent {

    private final boolean enabled;
    private final DiscordWebHook webHook;

    public DiscordEvent(DiscordWebHook webHook) {
        this.enabled = true;
        this.webHook = webHook;
    }

    public DiscordEvent(boolean enabled) {
        this.enabled = enabled;
        this.webHook = null;
    }

    public DiscordWebHook getWebHook() {
        return this.webHook;
    }

    public boolean isEnabled() {
        return this.enabled;
    }
}
