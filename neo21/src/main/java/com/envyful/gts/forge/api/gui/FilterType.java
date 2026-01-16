package com.envyful.gts.forge.api.gui;

import com.envyful.api.config.type.ExtendedConfigItem;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.gts.forge.api.TradeOffer;
import com.envyful.gts.forge.api.trade.Trade;

/**
 *
 * Enum used for filtering for specific {@link TradeOffer} types in the GUI
 *
 */
public interface FilterType {

    String getDisplayName();

    boolean isAllowed(EnvyPlayer<?> filterer, Trade trade);

    default FilterType getNext() {
        return FilterTypeFactory.getNext(this);
    }

    ExtendedConfigItem getDisplay();

}
