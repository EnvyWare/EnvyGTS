package com.envyful.gts.api.gui;

import com.envyful.api.config.type.ConfigItem;
import com.envyful.api.config.type.ExtendedConfigItem;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.gts.api.Trade;

/**
 *
 * Enum used for filtering for specific {@link Trade} types in the GUI
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
