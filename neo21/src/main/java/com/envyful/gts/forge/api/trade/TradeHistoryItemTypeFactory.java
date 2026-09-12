package com.envyful.gts.forge.api.trade;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class TradeHistoryItemTypeFactory {

    private static final List<TradeHistoryItemType> ITEM_TYPES = new ArrayList<>();

    public static void init() {

    }

    public static void register(TradeHistoryItemType itemType) {
        ITEM_TYPES.add(itemType);
    }

    public static TradeHistoryItemType getDefault() {
        return ITEM_TYPES.get(0);
    }

    public static TradeHistoryItemType getNext(TradeHistoryItemType itemType) {
        int index = ITEM_TYPES.indexOf(itemType);
        return ITEM_TYPES.get((index + 1) % ITEM_TYPES.size());
    }

    public static Optional<TradeHistoryItemType> parse(String input) {
        var normalised = input.toLowerCase(Locale.ROOT);

        for (var itemType : ITEM_TYPES) {
            if (itemType.getAliases().contains(normalised)) {
                return Optional.of(itemType);
            }
        }

        return Optional.empty();
    }
}
