package com.envyful.gts.forge.event;

import com.envyful.gts.api.Trade;
import com.envyful.gts.api.gui.FilterType;
import net.minecraftforge.eventbus.api.Event;

import java.util.List;

public class TradeViewFilterEvent extends Event {

    private List<Trade> trades;
    private FilterType filterType;

    public TradeViewFilterEvent(List<Trade> trades, FilterType filterType) {
        this.trades = trades;
        this.filterType = filterType;
    }

    public List<Trade> getTrades() {
        return this.trades;
    }

    public void setTrades(List<Trade> trades) {
        this.trades = trades;
    }

    public FilterType getFilterType() {
        return this.filterType;
    }
}
