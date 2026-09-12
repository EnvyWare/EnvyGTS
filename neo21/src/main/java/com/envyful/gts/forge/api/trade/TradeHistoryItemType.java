package com.envyful.gts.forge.api.trade;

import java.util.List;
import java.util.Optional;

public interface TradeHistoryItemType {

    String getDisplayName();

    Optional<String> getTradeItemId();

    List<String> getAliases();

    default TradeHistoryItemType getNext() {
        return TradeHistoryItemTypeFactory.getNext(this);
    }

}
