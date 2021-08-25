package com.envyful.reforged.gts.api.gui;


import java.util.Comparator;

public enum SortType {

    ALPHABETICAL("Alphabetical", (o1, o2) -> ((String) o1).compareTo(((String) o2))),
    REVERSE_ALPHABETICAL("Reverse Alphabetical", (o1, o2) -> ((String) o2).compareTo((String) o1)),
    MOST_RECENT("Most Recent", Comparator.comparingLong(Long.class::cast)),
    EXPIRING_SOON("Expiring Soon", Comparator.comparingLong(Long.class::cast)),

    ;

    private final String displayName;
    private final Comparator<?> comparator;

    <T> SortType(String displayName, Comparator<T> comparator) {
        this.displayName = displayName;
        this.comparator = comparator;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public Comparator<?> getComparator() {
        return this.comparator;
    }
}
