package com.envyful.gts.forge.event;

import com.envyful.api.gui.pane.Pane;
import com.envyful.gts.api.gui.FilterType;
import com.envyful.gts.api.gui.SortType;
import net.minecraftforge.eventbus.api.Event;

public class TradesGUISetupEvent extends Event {

    protected final Pane pane;
    protected final int page;
    protected final FilterType filterType;
    protected final SortType sortType;

    public TradesGUISetupEvent(Pane pane, int page, FilterType filterType, SortType sortType) {
        this.pane = pane;
        this.page = page;
        this.filterType = filterType;
        this.sortType = sortType;
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
