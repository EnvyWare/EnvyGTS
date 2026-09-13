package com.envyful.gts.forge.ui;

import com.envyful.gts.forge.api.item.TradeItemType;
import com.envyful.gts.forge.api.trade.TradeQuery;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.Instant;

/**
 *
 * What a player is currently looking at in one of the trade history UIs
 *
 * @param window How far back the trades shown go, or null for every trade ever
 * @param itemType The type of item being shown, or null for every type
 * @param search The species or item name being searched for, or null when not searching
 * @param player The name or UUID of the player whose trades are being shown, or null for everyone's
 * @param playerName The name to show for that player, when they were filtered by UUID
 *
 */
public record TradeFilter(@Nullable Duration window, @Nullable TradeItemType itemType, @Nullable String search,
                          @Nullable String player, @Nullable String playerName) {

    public static final TradeFilter ALL_TIME = new TradeFilter(null, null, null, null, null);

    public static TradeFilter of(@Nullable Duration window) {
        return new TradeFilter(window, null, null, null, null);
    }

    public TradeQuery toQuery() {
        return new TradeQuery(this.window == null ? null : Instant.now().minus(this.window),
                this.itemType, this.search, this.player);
    }

    public TradeFilter withWindow(@Nullable Duration window) {
        return new TradeFilter(window, this.itemType, this.search, this.player, this.playerName);
    }

    public TradeFilter withItemType(@Nullable TradeItemType itemType) {
        return new TradeFilter(this.window, itemType, this.search, this.player, this.playerName);
    }

    public TradeFilter withSearch(@Nullable String search) {
        return new TradeFilter(this.window, this.itemType, search, this.player, this.playerName);
    }

    public TradeFilter withPlayer(@Nullable String player) {
        return this.withPlayer(player, null);
    }

    public TradeFilter withPlayer(@Nullable String player, @Nullable String playerName) {
        return new TradeFilter(this.window, this.itemType, this.search, player, playerName);
    }

    public boolean hasSearch() {
        return this.search != null && !this.search.isBlank();
    }

    public boolean hasPlayer() {
        return this.player != null && !this.player.isBlank();
    }

    /**
     *
     * How the filtered player should be shown, which is their name when they were looked up by UUID
     *
     */
    public String playerDisplayName() {
        return this.playerName == null ? this.player : this.playerName;
    }
}
