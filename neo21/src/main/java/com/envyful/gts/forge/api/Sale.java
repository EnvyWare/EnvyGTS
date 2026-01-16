package com.envyful.gts.forge.api;

import com.envyful.api.neoforge.player.ForgeEnvyPlayer;
import com.envyful.gts.forge.api.player.PlayerInfo;
import com.envyful.gts.forge.api.trade.Trade;

import java.time.Instant;
import java.util.UUID;

public record Sale(UUID saleId, UUID offerId, PlayerInfo buyer, Instant time, double purchasePrice) {

    public Sale(Trade trade, ForgeEnvyPlayer player) {
        this(UUID.randomUUID(), trade.offer().id(),
                new PlayerInfo(player.getUniqueId(), player.getName()),
                Instant.now(), trade.offer().price().getPrice());
    }


}
