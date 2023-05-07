package com.envyful.gts.api;

import com.envyful.api.player.EnvyPlayer;

import java.util.List;
import java.util.Set;

public class TradeManager {

    private static GlobalTradeManager platformTradeManager;

    public static void setPlatformTradeManager(GlobalTradeManager platformTradeManager) {
        TradeManager.platformTradeManager = platformTradeManager;
    }

    public static boolean addTrade(EnvyPlayer<?> player, Trade trade) {
        return platformTradeManager.addTrade(player, trade);
    }

    public static void removeTrade(Trade trade) {
        platformTradeManager.removeTrade(trade);
    }

    public static Set<Trade> getAllTrades() {
        return platformTradeManager.getAllTrades();
    }

    public static Set<Trade> getUserTrades(EnvyPlayer<?> player) {
        return platformTradeManager.getUserTrades(player);
    }


    public static List<Trade> getExpiredTrades(EnvyPlayer<?> player) {
        return platformTradeManager.getExpiredTrades(player);
    }

    public static List<Trade> getPurchasedTrades(EnvyPlayer<?> player) {
        return platformTradeManager.getPurchasedTrades(player);
    }
}
