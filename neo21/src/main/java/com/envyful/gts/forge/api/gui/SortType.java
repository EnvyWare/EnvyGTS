package com.envyful.gts.forge.api.gui;


import com.envyful.gts.forge.api.trade.Trade;

import java.time.Instant;
import java.util.Comparator;

public enum SortType implements Comparator<Trade> {

    ALPHABETICAL("Alphabetical", Comparator.comparing(trade -> trade.offer().displayName())),
    REVERSE_ALPHABETICAL("Reverse Alphabetical", (o1, o2) -> o2.offer().displayName().compareTo(o1.offer().displayName())),
    MOST_RECENT("Most Recent", Comparator.<Trade, Instant>comparing(o -> o.offer().expiryTime()).reversed()),
    EXPIRING_SOON("Expiring Soon", Comparator.comparing(o -> o.offer().expiryTime())),

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

    @Override
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
