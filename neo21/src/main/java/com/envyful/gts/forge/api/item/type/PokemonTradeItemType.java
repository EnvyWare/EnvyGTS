package com.envyful.gts.forge.api.item.type;

import com.envyful.gts.forge.api.item.TradeItem;
import com.envyful.gts.forge.api.item.TradeItemType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import java.util.List;

public class PokemonTradeItemType implements TradeItemType {

    @Override
    public String id() {
        return "pokemon";
    }

    @Override
    public String getDisplayName() {
        return "Pokemon";
    }

    @Override
    public TradeItem deserialize(String data) throws CommandSyntaxException {
        return new PokemonTradeItem(data);
    }

    @Override
    public List<String> getAliases() {
        return List.of("pokemon", "poke", "pokes");
    }
}
