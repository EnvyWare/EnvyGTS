package com.envyful.reforged.gts.api;

import com.envyful.api.gui.pane.Pane;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.reforged.gts.api.gui.FilterType;
import com.envyful.reforged.gts.api.gui.SortType;

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
     * Used for sorting the GTS GUI
     *
     * @param other The other trade to compare to
     * @param type The type of sorting happening
     * @return positive if should be placed first; 0 - if equal; negative if should be placed after
     */
    int compare(Trade other, SortType type);

    /**
     *
     * Used for filtering the GTS GUI for specific types
     * Returns true if it matches the filter type
     *
     * @param filterer The person filtering
     * @param filterType The type of filter being checked
     * @return true if it matches the filter
     */
    boolean filter(EnvyPlayer<?> filterer, FilterType filterType);

    /**
     *
     * Displays the Trade in the GUI
     *
     * @param pos The position in the pane
     * @param pane The pane to display in
     */
    void display(int pos, Pane pane);

    /**
     *
     * Method for deleting this {@link Trade} from all storage
     *
     */
    void delete();

    /**
     *
     * Method for saving this {@link Trade} to the storage. Do not call outside of creation methods
     *
     */
    void save();

}
