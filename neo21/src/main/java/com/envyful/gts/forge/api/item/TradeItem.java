package com.envyful.gts.forge.api.item;

import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.text.Placeholder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.world.item.ItemStack;

public interface TradeItem {

    String id();

    String displayName();

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
