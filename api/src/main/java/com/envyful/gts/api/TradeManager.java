package com.envyful.gts.api;

import com.envyful.api.player.EnvyPlayer;

import java.util.List;

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

    public static List<Trade> getAllTrades() {
        return platformTradeManager.getAllTrades();
    }

    public static List<Trade> getUserTrades(EnvyPlayer<?> player) {
        return platformTradeManager.getUserTrades(player);
    }


    public static List<Trade> getExpiredTrades(EnvyPlayer<?> player) {
        return platformTradeManager.getExpiredTrades(player);
    }

    public static List<Trade> getPurchasedTrades(EnvyPlayer<?> player) {
        return platformTradeManager.getPurchasedTrades(player);
    }
}
