package com.envyful.gts.forge.ui;

import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.config.UtilConfigInterface;
import com.envyful.api.forge.config.UtilConfigItem;
import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.gui.pane.Pane;
import com.envyful.gts.api.Trade;
import com.envyful.gts.forge.EnvyGTSForge;
import com.envyful.gts.forge.config.GuiConfig;

import java.util.List;

public class TimedOutTradesUI {

    public static void openUI(ForgeEnvyPlayer player) {
        openUI(player, 0);
    }

    public static void openUI(ForgeEnvyPlayer player, int page) {
        GuiConfig.TimedOutTradesConfig config = EnvyGTSForge.getInstance().getGui().getTimedOutUIConfig();

        Pane pane = GuiFactory.paneBuilder()
                .topLeftX(0)
                .topLeftY(0)
                .width(9)
                .height(config.getGuiSettings().getHeight())
                .build();

        UtilConfigInterface.fillBackground(pane, config.getGuiSettings());

        List<Trade> allTrades = EnvyGTSForge.getInstance().getTradeManager().getExpiredTrades(player);

        UtilConfigItem.builder()
                .clickHandler((envyPlayer, clickType) -> MainUI.open(player))
                .extendedConfigItem(player, pane, config.getBackButton());

        UtilConfigItem.builder()
                .clickHandler((envyPlayer, clickType) -> {
                    if ((page + 1) >= (allTrades.size() / 45)) {
                        openUI(player, 0);
                    } else {
                        openUI(player, page - 1);
                    }
                })
                .extendedConfigItem(player, pane, config.getNextPageItem());

        UtilConfigItem.builder()
                .clickHandler((envyPlayer, clickType) -> {
                    if (page == 0) {
                        openUI(player, (allTrades.size() / 45));
                    } else {
                        openUI(player, page - 1);
                    }
                })
                .extendedConfigItem(player, pane, config.getPreviousPageItem());

        for (int i = (page * 45); i < ((page + 1) * 45); i++) {
            if (i >= allTrades.size()) {
                continue;
            }

            Trade trade = allTrades.get(i);
            trade.displayClaimable(i % 45, clicker -> TimedOutTradesUI.openUI(player, page), pane);
        }

        GuiFactory.guiBuilder()
                .setPlayerManager(EnvyGTSForge.getInstance().getPlayerManager())
                .addPane(pane)
                .height(config.getGuiSettings().getHeight())
                .title(UtilChatColour.colour(config.getGuiSettings().getTitle()))
                .build().open(player);
    }
}
