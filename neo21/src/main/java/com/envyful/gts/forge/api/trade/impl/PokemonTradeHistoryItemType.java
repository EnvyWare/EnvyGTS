package com.envyful.gts.forge.api.trade.impl;

import com.envyful.gts.forge.api.trade.TradeHistoryItemType;

import java.util.List;
import java.util.Optional;

public class PokemonTradeHistoryItemType implements TradeHistoryItemType {

    @Override
    public String getDisplayName() {
        return "Pokemon";
    }

    @Override
    public Optional<String> getTradeItemId() {
        return Optional.of("pokemon");
    }

    @Override
    public List<String> getAliases() {
        return List.of("pokemon", "poke", "pokes");
    }
}
