package com.envyful.gts.forge.impl.filter;

import com.envyful.api.config.type.ExtendedConfigItem;
import com.envyful.gts.api.gui.impl.AllFilterType;
import com.envyful.gts.forge.EnvyGTSForge;

public class ForgeAllFilterType extends AllFilterType {

    @Override
    public ExtendedConfigItem getDisplay() {
        return EnvyGTSForge.getGui().getSearchUIConfig().getFilterButton();
    }
}
