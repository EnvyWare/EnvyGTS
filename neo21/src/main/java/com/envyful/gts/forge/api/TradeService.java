package com.envyful.gts.forge.api;

import com.envyful.api.neoforge.player.ForgeEnvyPlayer;
import com.envyful.gts.forge.api.trade.PriceStatistics;
import com.envyful.gts.forge.api.trade.Trade;
import com.envyful.gts.forge.api.trade.TradeHistory;
import com.envyful.gts.forge.api.trade.TradeQuery;

import java.util.List;
import java.util.UUID;

/**
 *
 * The central service for managing trades in the GTS
 *
 */
public interface TradeService {

    List<Trade> activeListings();

    Trade activeListing(UUID offerId);

    List<Trade> userListings(ForgeEnvyPlayer player);

    void addListing(Trade trade);

    void adminRemoveListing(Trade trade);

    void ownerRemoveListing(Trade trade);

    void addSale(Sale sale);

    TradeHistory completedListings(TradeQuery query);

    TradeHistory highestPrices(TradeQuery query);

    TradeHistory lowestPrices(TradeQuery query);

    PriceStatistics priceStatistics(TradeQuery query);

    /**
     *
     * Called once the server has started, for any work that has to wait until then
     *
     */
    default void onServerStarted() {
    }

}
