package com.envyful.gts.forge.api.trade;

import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.text.ParseResult;
import com.envyful.api.text.Placeholder;
import com.envyful.gts.forge.api.TradeOffer;
import com.envyful.gts.forge.api.event.PlaceholderCollectEvent;
import net.minecraft.server.level.ServerPlayer;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.UUID;

public sealed interface Trade extends Placeholder
        permits ActiveTrade, RemovedTrade, SoldTrade, ExpiredTrade {

    TradeOffer offer();

    default UUID tradeId() {
        return this.offer().id();
    }

    default boolean isSeller(EnvyPlayer<?> player) {
        return this.isSeller(player.getUniqueId());
    }

    default boolean isSeller(ServerPlayer player) {
        return this.isSeller(player.getUUID());
    }

    default boolean isSeller(UUID uuid) {
        return this.offer().seller().uniqueId().equals(uuid);
    }

    @Override
    default @NonNull ParseResult replace(@NonNull ParseResult parseResult) {
        var event = new PlaceholderCollectEvent(this, this.offer());

        for (var placeholder : event.getPlaceholders()) {
            parseResult = placeholder.replace(parseResult);
        }

        return parseResult;
    }
}
