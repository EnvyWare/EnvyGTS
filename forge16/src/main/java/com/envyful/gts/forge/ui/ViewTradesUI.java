package com.envyful.gts.forge.ui;

import com.envyful.api.forge.concurrency.UtilForgeConcurrency;
import com.envyful.api.forge.config.UtilConfigInterface;
import com.envyful.api.forge.config.UtilConfigItem;
import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.api.text.Placeholder;
import com.envyful.api.text.parse.SimplePlaceholder;
import com.envyful.gts.api.Trade;
import com.envyful.gts.api.gui.FilterType;
import com.envyful.gts.api.gui.FilterTypeFactory;
import com.envyful.gts.api.gui.SortType;
import com.envyful.gts.forge.EnvyGTSForge;
import com.envyful.gts.forge.config.GuiConfig;
import com.envyful.gts.forge.event.TradeViewFilterEvent;
import net.minecraftforge.common.MinecraftForge;

import java.util.List;

public class ViewTradesUI {

    public static void openUI(ForgeEnvyPlayer player) {
        openUI(player, 1, FilterTypeFactory.getDefault(), SortType.MOST_RECENT);
    }

    public static void openUI(ForgeEnvyPlayer player, int page, FilterType filter, SortType sort) {
        GuiConfig.SearchTradesConfig config = EnvyGTSForge.getGui().getSearchUIConfig();
        List<Trade> allTrades = getAllVisibleTrades(player, filter, sort);

        UtilConfigInterface.paginatedBuilder(allTrades)
                .itemConversion(Trade::display)
                .playerManager(EnvyGTSForge.getPlayerManager())
                .configSettings(config.getGuiSettings())
                .extraItems((pane, currentPage) -> {
                    UtilConfigItem.builder()
                            .clickHandler((envyPlayer, clickType) -> SellHandOrParty.open(player))
                            .extendedConfigItem(player, pane, config.getSellButton());

                    UtilConfigItem.builder()
                            .clickHandler((envyPlayer, clickType) -> ReturnsUI.openUI(player))
                            .extendedConfigItem(player, pane, config.getReturnsButton());

                    UtilConfigItem.builder()
                            .clickHandler((envyPlayer, clickType) -> openUI(player, page, filter, sort.getNext()))
                            .extendedConfigItem(player, pane, config.getOrderButton(),
                                    (SimplePlaceholder) name -> name
                                            .replace("%filter%", filter.getDisplayName())
                                            .replace("%order%", sort.getDisplayName()));

                    UtilConfigItem.builder()
                            .clickHandler((envyPlayer, clickType) -> openUI(player, page, filter.getNext(), sort))
                            .extendedConfigItem(player, pane, filter.getDisplay(),
                                    (SimplePlaceholder) name -> name
                                            .replace("%filter%", filter.getDisplayName())
                                            .replace("%order%", sort.getDisplayName()));
                })
                .open(player, page);
    }

    private static List<Trade> getAllVisibleTrades(ForgeEnvyPlayer player, FilterType filter, SortType sortType) {
        List<Trade> allTrades = EnvyGTSForge.getTradeManager().getAllTrades();

        allTrades.removeIf(trade -> {
            if (trade.wasPurchased()) {
                return true;
            }

            if (trade.hasExpired() || trade.wasRemoved()) {
                return true;
            }

            return !trade.filter(player, filter);
        });

        TradeViewFilterEvent filterEvent = new TradeViewFilterEvent(allTrades, filter);

        MinecraftForge.EVENT_BUS.post(filterEvent);

        allTrades = filterEvent.getTrades();
        allTrades.sort((o1, o2) -> o1.compare(o2, sortType));

        return allTrades;
    }
}
