package com.envyful.gts.forge.api;

import com.envyful.api.neoforge.player.ForgeEnvyPlayer;
import com.envyful.gts.forge.api.trade.Trade;
import com.envyful.gts.forge.api.item.TradeItemType;
import com.envyful.gts.forge.api.trade.TradeHistory;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
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

    TradeHistory historicalListings();

    TradeHistory historicalListings(ForgeEnvyPlayer player);

    TradeHistory historicalListings(String playerQuery);

    TradeHistory highestPrices(Instant since, @Nullable TradeItemType itemType);

}
