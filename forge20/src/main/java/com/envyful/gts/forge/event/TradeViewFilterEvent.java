package com.envyful.gts.forge.event;

import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.gts.api.Trade;
import com.envyful.gts.api.gui.FilterType;
import net.minecraftforge.eventbus.api.Event;

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
