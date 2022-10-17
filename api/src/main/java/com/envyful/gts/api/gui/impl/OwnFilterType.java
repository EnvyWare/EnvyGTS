package com.envyful.gts.api.gui.impl;

import com.envyful.api.player.EnvyPlayer;
import com.envyful.gts.api.Trade;
import com.envyful.gts.api.gui.FilterType;
import com.envyful.gts.api.gui.FilterTypeFactory;

public class OwnFilterType implements FilterType {
    @Override
    public String getDisplayName() {
        return "Own";
    }

    @Override
    public boolean isAllowed(EnvyPlayer<?> filterer, Trade trade) {
        return trade.isOwner(filterer);
    }

    @Override
    public FilterType getNext() {
        return FilterTypeFactory.getNext(this);
    }
}
