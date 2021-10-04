package com.envyful.reforged.gts.api.gui;


import com.envyful.reforged.gts.api.TradeData;

import java.util.Comparator;

public enum SortType {

    ALPHABETICAL("Alphabetical", (o1, o2) -> o1.getName().compareTo(o2.getName())),
    REVERSE_ALPHABETICAL("Reverse Alphabetical", (o1, o2) -> o2.getName().compareTo(o1.getName())),
    MOST_RECENT("Most Recent", Comparator.comparingLong(TradeData::getExpiry).reversed()),
    EXPIRING_SOON("Expiring Soon", Comparator.comparingLong(TradeData::getExpiry)),

    ;

    private final String displayName;
    private final Comparator<TradeData> comparator;

    SortType(String displayName, Comparator<TradeData> comparator) {
        this.displayName = displayName;
        this.comparator = comparator;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public Comparator<TradeData> getComparator() {
        return this.comparator;
    }

    public SortType getNext() {
        SortType[] values = values();

        if (ordinal() >= (values.length - 1)) {
            return values[0];
        }

        return values[ordinal() + 1];
    }
}
