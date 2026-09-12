package com.envyful.gts.forge.api.trade.impl;

import com.envyful.gts.forge.api.trade.TradeHistoryItemType;

import java.util.List;
import java.util.Optional;

public class AllTradeHistoryItemType implements TradeHistoryItemType {

    @Override
    public String getDisplayName() {
        return "All";
    }

    @Override
    public Optional<String> getTradeItemId() {
        return Optional.empty();
    }

    @Override
    public List<String> getAliases() {
        return List.of("all", "any");
    }
}
