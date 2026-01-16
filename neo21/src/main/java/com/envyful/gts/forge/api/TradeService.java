package com.envyful.gts.forge.api;

import com.envyful.api.neoforge.player.ForgeEnvyPlayer;
import com.envyful.gts.forge.api.trade.Trade;

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

    List<Trade> historicalListings();

    List<Trade> historicalListings(ForgeEnvyPlayer player);

}
