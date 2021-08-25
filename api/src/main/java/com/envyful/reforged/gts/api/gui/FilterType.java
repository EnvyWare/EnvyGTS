package com.envyful.reforged.gts.api.gui;

/**
 *
 * Enum used for filtering for specific {@link com.envyful.reforged.gts.api.Trade} types in the GUI
 *
 */
public enum FilterType {

    INSTANT_BUY("Instant Buy"),
    AUCTION("Auction"),
    ALL("All")

    ;

    private String displayName;

    FilterType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return this.displayName;
    }
}
