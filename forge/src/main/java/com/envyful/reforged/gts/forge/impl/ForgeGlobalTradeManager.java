package com.envyful.reforged.gts.forge.impl;

import com.envyful.api.player.EnvyPlayer;
import com.envyful.reforged.gts.api.GlobalTradeManager;
import com.envyful.reforged.gts.api.Trade;

import java.util.List;

public class ForgeGlobalTradeManager implements GlobalTradeManager {

    //TODO: implement methods

    @Override
    public void addTrade(EnvyPlayer<?> player, Trade trade) {

    }

    @Override
    public List<Trade> getAllTrades() {
        return null;
    }

    @Override
    public List<Trade> getUserTrades(EnvyPlayer<?> player) {
        return null;
    }

    @Override
    public List<Trade> getExpiredTrades(EnvyPlayer<?> player) {
        return null;
    }

    @Override
    public List<Trade> getPurchasedTrades(EnvyPlayer<?> player) {
        return null;
    }
}
