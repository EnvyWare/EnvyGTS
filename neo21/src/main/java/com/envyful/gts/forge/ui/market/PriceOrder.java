package com.envyful.gts.forge.ui.market;

import com.envyful.gts.forge.EnvyGTSForge;
import com.envyful.gts.forge.api.trade.TradeHistory;
import com.envyful.gts.forge.api.trade.TradeQuery;

import java.util.function.Function;

/**
 *
 * The end of the market a {@link PriceListUI} is showing the sales from
 *
 */
public enum PriceOrder {

    HIGHEST(query -> EnvyGTSForge.getTradeService().highestPrices(query)),
    LOWEST(query -> EnvyGTSForge.getTradeService().lowestPrices(query)),

    ;

    private final Function<TradeQuery, TradeHistory> lookup;

    PriceOrder(Function<TradeQuery, TradeHistory> lookup) {
        this.lookup = lookup;
    }

    public TradeHistory lookup(TradeQuery query) {
        return this.lookup.apply(query);
    }
}
