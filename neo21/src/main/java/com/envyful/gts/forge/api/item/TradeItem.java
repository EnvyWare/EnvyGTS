package com.envyful.gts.forge.api.item;

import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.text.Placeholder;
import com.envyful.gts.forge.api.item.type.ItemStackTradeItem;
import com.envyful.gts.forge.api.item.type.PokemonTradeItem;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.world.item.ItemStack;

public interface TradeItem {

    String id();

    String displayName();

    ItemStack display(Placeholder... placeholders);

    boolean collect(EnvyPlayer<?> player);

    String serialize();

    static TradeItem deserialize(String id, String data) throws CommandSyntaxException {
        return switch (id) {
            case "pokemon" -> new PokemonTradeItem(data);
            case "item" -> new ItemStackTradeItem(data);
            default -> throw new IllegalArgumentException("Unknown TradeItem id: " + id);
        };
    }

}
