package com.envyful.gts.forge.api.player;

import com.envyful.api.neoforge.player.ForgeEnvyPlayer;
import com.envyful.api.platform.PlatformProxy;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.text.Placeholder;

import java.util.List;
import java.util.UUID;

public record PlayerInfo(UUID uniqueId, String name) {

    public PlayerInfo(EnvyPlayer<?> player) {
        this(player.getUniqueId(), player.getName());
    }

    public ForgeEnvyPlayer getPlayer() {
        return (ForgeEnvyPlayer) PlatformProxy.getPlayerManager().getPlayer(this.uniqueId);
    }

    public void sendMessage(List<String> messages, Placeholder... placeholders) {
        var player = this.getPlayer();

        if (player != null) {
            player.message(messages, placeholders);
        }
    }

}
