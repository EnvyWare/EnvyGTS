package com.envyful.reforged.gts.api;

import com.envyful.api.player.EnvyPlayer;

import java.util.UUID;

/**
 *
 * This interface represents an item that exists on GTS.
 *
 */
public interface Trade {

    /**
     *
     * Method for checking if the given player is the owner (the person selling) of the item on GTS
     *
     * @param player The player to check
     * @return True if the player is the person selling the item
     */
    default boolean isOwner(EnvyPlayer<?> player) {
        return this.isOwner(player.getUuid());
    }

    /**
     *
     * Method for checking if the UUID is the owner (the person selling) of the item on GTS
     *
     * @param uuid The uuid of the player
     * @return True if they are selling the item
     */
    boolean isOwner(UUID uuid);

    /**
     *
     * Checks if the item has gone past the expiry date (and hence moved to the collection area)
     *
     * @return
     */
    boolean hasExpired();

    /**
     *
     * Method for when a player attempts to purchase an item from GTS.
     * Will return false if something fails
     * Returns true if the purchase is successful
     *
     * @param player The player attempting to purchase the item
     * @return True if successfully purchased
     */
    boolean attemptPurchase(EnvyPlayer<?> player);

    /**
     *
     * Method for collecting the item from the GUI
     *
     * @param player The player collecting the item
     */
    void collect(EnvyPlayer<?> player);

    /**
     *
     * Method for an admin removing the item from the GUI
     *
     * @param admin The admin who removed the item
     */
    void adminRemove(EnvyPlayer<?> admin);

    /**
     *
     * Method for deleting this {@link Trade} from all storage
     *
     */
    void delete();

}
