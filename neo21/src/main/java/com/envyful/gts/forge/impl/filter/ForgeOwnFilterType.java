package com.envyful.gts.forge.impl.filter;

import com.envyful.api.config.type.ExtendedConfigItem;
import com.envyful.gts.api.gui.impl.OwnFilterType;
import com.envyful.gts.forge.EnvyGTSForge;

public class ForgeOwnFilterType extends OwnFilterType {

    @Override
    public ExtendedConfigItem getDisplay() {
        return EnvyGTSForge.getGui().getSearchUIConfig().getFilterButton();
    }
}
