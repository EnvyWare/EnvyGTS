package com.envyful.reforged.gts.api.gui;

import java.util.function.Predicate;

/**
 *
 * Enum used for filtering for specific {@link com.envyful.reforged.gts.api.Trade} types in the GUI
 *
 */
public enum FilterType {

    INSTANT_BUY("Instant Buy", type -> type.getDisplayName().equalsIgnoreCase("Instant Buy")),
    AUCTION("Auction", type -> type.getDisplayName().equalsIgnoreCase("Auction")),
    ALL("All", type -> true)

    ;

    private final String displayName;
    private final Predicate<FilterType> predicate;

    FilterType(String displayName, Predicate<FilterType> predicate) {
        this.displayName = displayName;
        this.predicate = predicate;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public boolean isAllowed(FilterType type) {
        return this.predicate.test(type);
    }

    public FilterType getNext() {
        FilterType[] values = values();

        if (ordinal() >= (values.length - 1)) {
            return values[0];
        }

        return values[ordinal() + 1];
    }
}
