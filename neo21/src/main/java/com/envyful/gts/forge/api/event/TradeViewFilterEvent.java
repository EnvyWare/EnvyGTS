package com.envyful.gts.forge.api.event;

import com.envyful.api.neoforge.player.ForgeEnvyPlayer;
import com.envyful.gts.forge.api.gui.FilterType;
import com.envyful.gts.forge.api.trade.Trade;
import net.neoforged.bus.api.Event;

import java.util.List;

public class TradeViewFilterEvent extends Event {

    private final ForgeEnvyPlayer player;
    private List<Trade> trades;
    private FilterType filterType;

    public TradeViewFilterEvent(ForgeEnvyPlayer player, List<Trade> trades, FilterType filterType) {
        this.player = player;
        this.trades = trades;
        this.filterType = filterType;
    }

    public ForgeEnvyPlayer getPlayer() {
        return this.player;
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
