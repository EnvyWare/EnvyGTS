package com.envyful.gts.api.gui;


import com.envyful.gts.api.Trade;

import java.util.Comparator;

public enum SortType {

    ALPHABETICAL("Alphabetical", Comparator.comparing(Trade::getDisplayName)),
    REVERSE_ALPHABETICAL("Reverse Alphabetical", (o1, o2) -> o2.getDisplayName().compareTo(o1.getDisplayName())),
    MOST_RECENT("Most Recent", Comparator.comparingLong(Trade::getExpiry).reversed()),
    EXPIRING_SOON("Expiring Soon", Comparator.comparingLong(Trade::getExpiry)),

    ;

    private final String displayName;
    private final Comparator<Trade> comparator;

    SortType(String displayName, Comparator<Trade> comparator) {
        this.displayName = displayName;
        this.comparator = comparator;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public int compare(Trade o1, Trade o2) {
        return this.comparator.compare(o1, o2);
    }

    public SortType getNext() {
        SortType[] values = values();

        if (ordinal() >= (values.length - 1)) {
            return values[0];
        }

        return values[ordinal() + 1];
    }
}
