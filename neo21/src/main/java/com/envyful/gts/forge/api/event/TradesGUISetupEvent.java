package com.envyful.gts.forge.api.event;

import com.envyful.api.gui.pane.Pane;
import com.envyful.api.neoforge.player.ForgeEnvyPlayer;
import com.envyful.gts.forge.api.gui.FilterType;
import com.envyful.gts.forge.api.gui.SortType;
import net.neoforged.bus.api.Event;

public class TradesGUISetupEvent extends Event {

    protected final ForgeEnvyPlayer player;
    protected final Pane pane;
    protected final int page;
    protected final FilterType filterType;
    protected final SortType sortType;

    public TradesGUISetupEvent(ForgeEnvyPlayer player, Pane pane, int page, FilterType filterType, SortType sortType) {
        this.player = player;
        this.pane = pane;
        this.page = page;
        this.filterType = filterType;
        this.sortType = sortType;
    }

    public ForgeEnvyPlayer getPlayer() {
        return this.player;
    }

    public Pane getPane() {
        return this.pane;
    }

    public int getPage() {
        return this.page;
    }

    public FilterType getFilterType() {
        return this.filterType;
    }

    public SortType getSortType() {
        return this.sortType;
    }
}
