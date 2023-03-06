package com.envyful.gts.api.gui.impl;

import com.envyful.api.player.EnvyPlayer;
import com.envyful.gts.api.Trade;
import com.envyful.gts.api.gui.FilterType;
import com.envyful.gts.api.gui.FilterTypeFactory;

public abstract class InstantBuyFilterType implements FilterType {
    @Override
    public String getDisplayName() {
        return "Instant Buy";
    }

    @Override
    public boolean isAllowed(EnvyPlayer<?> filterer, Trade trade) {
        return true;
    }

    @Override
    public FilterType getNext() {
        return FilterTypeFactory.getNext(this);
    }
}
