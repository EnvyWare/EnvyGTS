package com.envyful.gts.forge.api.trade;

import com.envyful.gts.forge.api.item.TradeItemType;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;

/**
 *
 * The filters applied to a historical trade lookup
 *
 * @param since Only trades that finished at, or after, this time are included, or null for every trade ever
 * @param itemType The type of item the trades were for, or null for every type
 * @param search The name, species, or item id the trades must match, or null for everything
 * @param player The name or UUID of a player that must have sold or bought the trade, or null for everyone
 *
 */
public record TradeQuery(@Nullable Instant since, @Nullable TradeItemType itemType, @Nullable String search,
                         @Nullable String player) {

    public static TradeQuery all() {
        return new TradeQuery(null, null, null, null);
    }

    public static TradeQuery since(Instant since) {
        return new TradeQuery(since, null, null, null);
    }

    public boolean hasSearch() {
        return this.search != null && !this.search.isBlank();
    }

    public boolean hasPlayer() {
        return this.player != null && !this.player.isBlank();
    }
}
