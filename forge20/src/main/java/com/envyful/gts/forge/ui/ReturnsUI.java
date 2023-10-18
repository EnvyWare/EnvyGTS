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
import com.envyful.gts.forge.player.GTSAttribute;

import java.util.List;
import java.util.stream.Collectors;

public class ReturnsUI {

    public static void openUI(ForgeEnvyPlayer player) {
        GuiConfig.Returns config = EnvyGTSForge.getGui().getReturnsGui();
        GTSAttribute attribute = player.getAttribute(GTSAttribute.class);

        Pane pane = GuiFactory.paneBuilder()
                .topLeftX(0)
                .topLeftY(0)
                .width(9)
                .height(config.getGuiSettings().getHeight())
                .build();

        UtilConfigInterface.fillBackground(pane, config.getGuiSettings());

        List<Integer> returnPositions = config.getReturnPositions();

        List<Trade> collect = attribute.getOwnedTrades().stream().filter(trade -> trade.hasExpired() || trade.wasRemoved() || trade.wasPurchased()).collect(Collectors.toList());

        for (int i = 0; i < returnPositions.size(); i++) {
            if (i >= collect.size()) {
                break;
            }

            Trade trade = collect.get(i);
            trade.displayClaimable(returnPositions.get(i), envyPlayer -> openUI(player), pane);
        }

        UtilConfigItem.builder()
                .clickHandler((envyPlayer, clickType) -> ViewTradesUI.openUI(player))
                .extendedConfigItem(player, pane, config.getBackButton());

        GuiFactory.guiBuilder()
                .setPlayerManager(EnvyGTSForge.getPlayerManager())
                .addPane(pane)
                .height(config.getGuiSettings().getHeight())
                .title(UtilChatColour.colour(config.getGuiSettings().getTitle()))
                .build().open(player);
    }
}
