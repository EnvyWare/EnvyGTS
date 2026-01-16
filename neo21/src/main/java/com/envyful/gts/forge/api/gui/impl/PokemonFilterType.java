package com.envyful.gts.forge.api.gui.impl;

import com.envyful.api.config.type.ExtendedConfigItem;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.gts.forge.EnvyGTSForge;
import com.envyful.gts.forge.api.gui.FilterType;
import com.envyful.gts.forge.api.item.type.PokemonTradeItem;
import com.envyful.gts.forge.api.trade.Trade;

public class PokemonFilterType implements FilterType {
    @Override
    public String getDisplayName() {
        return "Pokemon";
    }

    @Override
    public boolean isAllowed(EnvyPlayer<?> filterer, Trade trade) {
        return trade.offer().item() instanceof PokemonTradeItem;
    }

    @Override
    public ExtendedConfigItem getDisplay() {
        return EnvyGTSForge.getGui().getSearchUIConfig().getFilterButton();
    }
}
