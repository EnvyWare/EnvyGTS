package com.envyful.gts.forge.api.item;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class TradeItemTypeFactory {

    private static final List<TradeItemType> ITEM_TYPES = new ArrayList<>();

    public static void init() {

    }

    public static void register(TradeItemType itemType) {
        ITEM_TYPES.add(itemType);
    }

    public static List<TradeItemType> getAll() {
        return List.copyOf(ITEM_TYPES);
    }

    public static Optional<TradeItemType> byId(String id) {
        for (var itemType : ITEM_TYPES) {
            if (itemType.id().equalsIgnoreCase(id)) {
                return Optional.of(itemType);
            }
        }

        return Optional.empty();
    }

    public static Optional<TradeItemType> parse(String input) {
        var normalised = input.toLowerCase(Locale.ROOT);

        for (var itemType : ITEM_TYPES) {
            if (itemType.getAliases().contains(normalised)) {
                return Optional.of(itemType);
            }
        }

        return Optional.empty();
    }

    @Nullable
    public static TradeItemType getNext(@Nullable TradeItemType itemType) {
        if (itemType == null) {
            return ITEM_TYPES.isEmpty() ? null : ITEM_TYPES.get(0);
        }

        int next = ITEM_TYPES.indexOf(itemType) + 1;

        return next >= ITEM_TYPES.size() ? null : ITEM_TYPES.get(next);
    }
}
