package com.envyful.gts.forge.api.trade;

import java.util.List;

/**
 *
 * The result of a historical trade lookup.
 *
 * @param trades The trades that were successfully read from the database
 * @param failed The number of rows that could not be read, and so are missing from {@link #trades()}
 *
 */
public record TradeHistory(List<Trade> trades, int failed) {

    public static final TradeHistory EMPTY = new TradeHistory(List.of(), 0);

    public boolean hasFailures() {
        return this.failed > 0;
    }
}
