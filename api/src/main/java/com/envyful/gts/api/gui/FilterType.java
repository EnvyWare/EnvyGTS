package com.envyful.gts.api.gui;

import com.envyful.api.player.EnvyPlayer;
import com.envyful.gts.api.Trade;

import java.util.function.BiPredicate;

/**
 *
 * Enum used for filtering for specific {@link Trade} types in the GUI
 *
 */
public enum FilterType {

    INSTANT_BUY("Instant Buy", (envyPlayer, trade) -> true),
    OWN("Your Trades", (envyPlayer, trade) -> trade.isOwner(envyPlayer)),
    ALL("All", (envyPlayer, trade) -> true)

    ;

    private final String displayName;
    private final BiPredicate<EnvyPlayer<?>, Trade> predicate;

    FilterType(String displayName, BiPredicate<EnvyPlayer<?>, Trade> predicate) {
        this.displayName = displayName;
        this.predicate = predicate;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public boolean isAllowed(EnvyPlayer<?> filterer, Trade trade) {
        return this.predicate.test(filterer, trade);
    }

    public FilterType getNext() {
        FilterType[] values = values();

        if (ordinal() >= (values.length - 1)) {
            return values[0];
        }

        return values[ordinal() + 1];
    }
}
