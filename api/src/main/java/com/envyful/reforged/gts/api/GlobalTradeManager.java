package com.envyful.reforged.gts.api;

import com.envyful.api.player.EnvyPlayer;

import java.util.List;

/**
 *
 * Interface representing all management operations for the server's GTS
 *
 */
public interface GlobalTradeManager {

    /**
     *
     * Represents a player adding a trade to the GTS
     *
     * @param player The player adding the trade
     * @param trade The trade being added
     */
    boolean addTrade(EnvyPlayer<?> player, Trade trade);

    /**
     *
     * Gets all trades on the GTS
     *
     * @return The list of all active trades
     */
    List<Trade> getAllTrades();

    /**
     *
     * Gets all active trades for the player
     *
     * @param player The player
     * @return The player's active trades
     */
    List<Trade> getUserTrades(EnvyPlayer<?> player);


    /**
     *
     * Gets all the expired trades for the player
     *
     * @param player The player
     * @return The player's expired trades
     */
    List<Trade> getExpiredTrades(EnvyPlayer<?> player);

    /**
     *
     * Gets all the trades the player has purchased but not yet collected
     *
     * @param player The player
     * @return All the player's uncollected trades
     */
    List<Trade> getPurchasedTrades(EnvyPlayer<?> player);

}
