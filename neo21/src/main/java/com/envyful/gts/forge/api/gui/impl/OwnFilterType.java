package com.envyful.gts.forge.api.gui.impl;

import com.envyful.api.config.type.ExtendedConfigItem;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.gts.forge.EnvyGTSForge;
import com.envyful.gts.forge.api.gui.FilterType;
import com.envyful.gts.forge.api.gui.FilterTypeFactory;
import com.envyful.gts.forge.api.trade.Trade;

public class OwnFilterType implements FilterType {

    @Override
    public String getDisplayName() {
        return "Own";
    }

    @Override
    public boolean isAllowed(EnvyPlayer<?> filterer, Trade trade) {
        return trade.isSeller(filterer);
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
