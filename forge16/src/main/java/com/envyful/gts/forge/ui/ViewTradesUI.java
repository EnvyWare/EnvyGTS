package com.envyful.gts.forge.ui;

import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.config.UtilConfigInterface;
import com.envyful.api.forge.config.UtilConfigItem;
import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.gui.pane.Pane;
import com.envyful.gts.api.Trade;
import com.envyful.gts.api.gui.FilterType;
import com.envyful.gts.api.gui.FilterTypeFactory;
import com.envyful.gts.api.gui.SortType;
import com.envyful.gts.forge.EnvyGTSForge;
import com.envyful.gts.forge.config.GuiConfig;

import java.util.List;

public class ViewTradesUI {

    public static void openUI(ForgeEnvyPlayer player) {
        openUI(player, 0, FilterTypeFactory.getDefault(), SortType.MOST_RECENT);
    }

    public static void openUI(ForgeEnvyPlayer player, int page, FilterType filter, SortType sort) {
        GuiConfig.SearchTradesConfig config = EnvyGTSForge.getInstance().getGui().getSearchUIConfig();

        Pane pane = GuiFactory.paneBuilder()
                .topLeftX(0)
                .topLeftY(0)
                .width(9)
                .height(config.getGuiSettings().getHeight())
                .build();

        UtilConfigInterface.fillBackground(pane, config.getGuiSettings());

        List<Trade> allTrades = EnvyGTSForge.getInstance().getTradeManager().getAllTrades();

        allTrades.removeIf(trade -> {
            if (trade.wasPurchased()) {
                return true;
            }

            if (trade.hasExpired() || trade.wasRemoved()) {
                return true;
            }

            return !trade.filter(player, filter);
        });

        allTrades.sort((o1, o2) -> o1.compare(o2, sort));

        UtilConfigItem.builder()
                .clickHandler((envyPlayer, clickType) -> MainUI.open(player))
                .extendedConfigItem(player, pane, config.getBackButton());

        UtilConfigItem.builder()
                .clickHandler((envyPlayer, clickType) -> {
                    if ((page + 1) > (allTrades.size() / 45)) {
                        openUI(player, 0, filter, sort);
                    } else {
                        openUI(player, page + 1, filter, sort);
                    }
                })
                .extendedConfigItem(player, pane, config.getNextPageItem());

        UtilConfigItem.builder()
                .clickHandler((envyPlayer, clickType) -> {
                    if (page == 0) {
                        openUI(player, (allTrades.size() / 45), filter, sort);
                    } else {
                        openUI(player, page - 1, filter, sort);
                    }
                })
                .extendedConfigItem(player, pane, config.getPreviousPageItem());

        UtilConfigItem.builder()
                .clickHandler((envyPlayer, clickType) -> openUI(player, page, filter, sort.getNext()))
                .extendedConfigItem(player, pane, config.getOrderButton(),
                        name -> name
                                .replace("%filter%", filter.getDisplayName())
                                .replace("%order%", sort.getDisplayName()));

        UtilConfigItem.builder()
                .clickHandler((envyPlayer, clickType) -> openUI(player, page, filter.getNext(), sort))
                .extendedConfigItem(player, pane, config.getFilterButton(),
                        name -> name
                                .replace("%filter%", filter.getDisplayName())
                                .replace("%order%", sort.getDisplayName()));

        for (int i = (page * 45); i < ((page + 1) * 45); i++) {
            if (i >= allTrades.size()) {
                continue;
            }

            Trade trade = allTrades.get(i);
            trade.display(i % 45, pane);
        }

        GuiFactory.guiBuilder()
                .setPlayerManager(EnvyGTSForge.getInstance().getPlayerManager())
                .addPane(pane)
                .height(config.getGuiSettings().getHeight())
                .title(UtilChatColour.colour(config.getGuiSettings().getTitle()))
                .build().open(player);
    }
}
