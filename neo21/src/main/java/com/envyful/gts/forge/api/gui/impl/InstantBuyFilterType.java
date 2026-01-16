package com.envyful.gts.forge.api.gui.impl;

import com.envyful.api.config.type.ExtendedConfigItem;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.gts.forge.EnvyGTSForge;
import com.envyful.gts.forge.api.gui.FilterType;
import com.envyful.gts.forge.api.gui.FilterTypeFactory;
import com.envyful.gts.forge.api.trade.Trade;

public class InstantBuyFilterType implements FilterType {
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

    @Override
    public ExtendedConfigItem getDisplay() {
        return EnvyGTSForge.getGui().getSearchUIConfig().getFilterButton();
    }
}
