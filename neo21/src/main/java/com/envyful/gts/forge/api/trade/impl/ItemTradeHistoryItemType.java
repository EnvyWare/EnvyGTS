package com.envyful.gts.forge.api.trade.impl;

import com.envyful.gts.forge.api.trade.TradeHistoryItemType;

import java.util.List;
import java.util.Optional;

public class ItemTradeHistoryItemType implements TradeHistoryItemType {

    @Override
    public String getDisplayName() {
        return "Items";
    }

    @Override
    public Optional<String> getTradeItemId() {
        return Optional.of("item");
    }

    @Override
    public List<String> getAliases() {
        return List.of("item", "items");
    }
}
