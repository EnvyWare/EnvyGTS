package com.envyful.gts.forge.impl.filter;

import com.envyful.api.player.EnvyPlayer;
import com.envyful.gts.api.Trade;
import com.envyful.gts.api.gui.FilterType;
import com.envyful.gts.api.gui.FilterTypeFactory;
import com.envyful.gts.forge.impl.trade.type.ItemTrade;

public class ItemFilterType implements FilterType {
    @Override
    public String getDisplayName() {
        return "Item";
    }

    @Override
    public boolean isAllowed(EnvyPlayer<?> filterer, Trade trade) {
        return trade instanceof ItemTrade;
    }

    @Override
    public FilterType getNext() {
        return FilterTypeFactory.getNext(this);
    }
}
