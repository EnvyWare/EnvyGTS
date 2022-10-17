package com.envyful.gts.forge.impl.filter;

import com.envyful.api.player.EnvyPlayer;
import com.envyful.gts.api.Trade;
import com.envyful.gts.api.gui.FilterType;
import com.envyful.gts.api.gui.FilterTypeFactory;
import com.envyful.gts.forge.impl.trade.type.PokemonTrade;

public class PokemonFilterType implements FilterType {
    @Override
    public String getDisplayName() {
        return "Pokemon";
    }

    @Override
    public boolean isAllowed(EnvyPlayer<?> filterer, Trade trade) {
        return trade instanceof PokemonTrade;
    }

    @Override
    public FilterType getNext() {
        return FilterTypeFactory.getNext(this);
    }
}
