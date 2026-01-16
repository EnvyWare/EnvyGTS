package com.envyful.gts.forge.api;

import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.text.parse.SimplePlaceholder;
import com.envyful.gts.forge.EnvyGTSForge;
import com.envyful.gts.forge.api.item.TradeItem;
import com.envyful.gts.forge.api.money.Money;
import com.envyful.gts.forge.api.player.PlayerInfo;

import java.time.Instant;
import java.util.UUID;

public record TradeOffer(UUID id, PlayerInfo seller, Instant creationTime, Instant expiryTime, TradeItem item, Money price) implements SimplePlaceholder {

    public String displayName() {
        return this.item.displayName();
    }

    public boolean hasExpired() {
        return Instant.now().isAfter(this.expiryTime());
    }

    public static TradeOffer newOffer(EnvyPlayer<?> seller, Instant expiryTime, TradeItem item, Money price) {
        return new TradeOffer(
                UUID.randomUUID(),
                new PlayerInfo(seller),
                Instant.now(),
                expiryTime,
                item,
                price
        );
    }

    @Override
    public String replace(String s) {
        return s.replace("%expires_in%", EnvyGTSForge.getLocale().getExpiryTimeFormat().format(this.expiryTime.toEpochMilli() - System.currentTimeMillis()))
                .replace("%seller%", this.seller().name())
                .replace("%price%", String.format(EnvyGTSForge.getLocale().getMoneyFormat(), this.price().getPrice()));
    }
}
