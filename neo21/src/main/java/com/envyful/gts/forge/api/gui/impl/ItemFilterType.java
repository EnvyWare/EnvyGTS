package com.envyful.gts.forge.api.gui.impl;

import com.envyful.api.config.type.ExtendedConfigItem;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.gts.forge.EnvyGTSForge;
import com.envyful.gts.forge.api.gui.FilterType;
import com.envyful.gts.forge.api.item.type.ItemStackTradeItem;
import com.envyful.gts.forge.api.trade.Trade;

public class ItemFilterType implements FilterType {
    @Override
    public String getDisplayName() {
        return "Item";
    }

    @Override
    public boolean isAllowed(EnvyPlayer<?> filterer, Trade trade) {
        return trade.offer().item() instanceof ItemStackTradeItem;
    }

    @Override
    public ExtendedConfigItem getDisplay() {
        return EnvyGTSForge.getGui().getViewTradesUI().getFilterButton();
    }
}
