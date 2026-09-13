package com.envyful.gts.forge.api.trade;

/**
 *
 * The aggregated sale prices of the trades matched by a price lookup.
 *
 * @param sales The number of sales the statistics were calculated from
 * @param lowest The lowest price any of the sales went for
 * @param highest The highest price any of the sales went for
 * @param mean The mean price of all of the sales
 *
 */
public record PriceStatistics(long sales, double lowest, double highest, double mean) {

    public static final PriceStatistics EMPTY = new PriceStatistics(0L, 0.0, 0.0, 0.0);

    public boolean hasSales() {
        return this.sales > 0;
    }
}
