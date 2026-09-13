package com.envyful.gts.forge.api.item;

import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.text.Placeholder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.world.item.ItemStack;

public interface TradeItem {

    String id();

    String displayName();

    /**
     *
     * The lower case terms this item can be found by when a player searches the trade history, separated by
     * spaces. For a Pokemon that is its species, and for an item it is the name it is called in game followed
     * by its item id, so that a renamed custom item is still found by the name players know it as
     *
     * @return The search terms
     *
     */
    String searchKey();

    ItemStack display(Placeholder... placeholders);

    boolean collect(EnvyPlayer<?> player);

    String serialize();

    static TradeItem deserialize(String id, String data) throws CommandSyntaxException {
        var itemType = TradeItemTypeFactory.byId(id);

        if (itemType.isEmpty()) {
            throw new IllegalArgumentException("Unknown TradeItem id: " + id);
        }

        return itemType.get().deserialize(data);
    }

}
