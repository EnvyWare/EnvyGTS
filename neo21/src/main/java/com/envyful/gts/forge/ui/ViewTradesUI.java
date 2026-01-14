package com.envyful.gts.forge.ui;

import com.envyful.api.neoforge.config.UtilConfigInterface;
import com.envyful.api.neoforge.config.UtilConfigItem;
import com.envyful.api.neoforge.player.ForgeEnvyPlayer;
import com.envyful.api.text.parse.SimplePlaceholder;
import com.envyful.gts.forge.EnvyGTSForge;
import com.envyful.gts.forge.api.gui.FilterType;
import com.envyful.gts.forge.api.gui.FilterTypeFactory;
import com.envyful.gts.forge.api.gui.SortType;
import com.envyful.gts.forge.api.trade.Trade;
import com.envyful.gts.forge.event.TradeViewFilterEvent;
import com.envyful.gts.forge.event.TradesGUISetupEvent;
import net.neoforged.neoforge.common.NeoForge;

import java.util.ArrayList;
import java.util.List;

public class ViewTradesUI {

    public static void openUI(ForgeEnvyPlayer player) {
        openUI(player, 1, FilterTypeFactory.getDefault(), SortType.MOST_RECENT);
    }

    public static void openUI(ForgeEnvyPlayer player, int page, FilterType filter, SortType sort) {
        var config = EnvyGTSForge.getGui().getSearchUIConfig();
        var allTrades = getAllVisibleTrades(player, filter, sort);

        UtilConfigInterface.paginatedBuilder(allTrades)
                .itemConversion(trade -> trade.offer().item().display()
                        .clickHandler((envyPlayer, clickType) -> {
                            var updatedTrade = EnvyGTSForge.getTradeService().activeListing(trade.offer().id());

                            if (updatedTrade == null) {
                                //TODO: message the player
                                return;
                            }


                        })
                        .build())
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

                    NeoForge.EVENT_BUS.post(new TradesGUISetupEvent(player, pane, page, filter, sort));
                })
                .open(player, page);
    }

    private static List<Trade> getAllVisibleTrades(ForgeEnvyPlayer player, FilterType filter, SortType sortType) {
        List<Trade> allTrades = new ArrayList<>(EnvyGTSForge.getTradeService().activeListings());

        allTrades.removeIf(trade -> !filter.isAllowed(player, trade));
        var filterEvent = new TradeViewFilterEvent(player, allTrades, filter);

        NeoForge.EVENT_BUS.post(filterEvent);

        allTrades = filterEvent.getTrades();
        allTrades.sort(sortType::compare);

        return allTrades;
    }
}
