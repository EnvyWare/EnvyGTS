package com.envyful.gts.forge.api.trade;

import java.util.Locale;
import java.util.Optional;

public enum TradeHistoryItemType {

    ALL("All", null),
    ITEM("Items", "item"),
    POKEMON("Pokemon", "pokemon");

    private final String displayName;
    private final String tradeItemId;

    TradeHistoryItemType(String displayName, String tradeItemId) {
        this.displayName = displayName;
        this.tradeItemId = tradeItemId;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public String getTradeItemId() {
        return this.tradeItemId;
    }

    public boolean allows(Trade trade) {
        return this.tradeItemId == null || trade.offer().item().id().equalsIgnoreCase(this.tradeItemId);
    }

    public TradeHistoryItemType getNext() {
        return switch (this) {
            case ALL -> POKEMON;
            case POKEMON -> ITEM;
            case ITEM -> ALL;
        };
    }

    public static Optional<TradeHistoryItemType> parse(String input) {
        return switch (input.toLowerCase(Locale.ROOT)) {
            case "all", "any" -> Optional.of(ALL);
            case "item", "items" -> Optional.of(ITEM);
            case "pokemon", "poke", "pokes" -> Optional.of(POKEMON);
            default -> Optional.empty();
        };
    }
}
