package com.envyful.gts.forge.ui;

import com.envyful.api.forge.config.UtilConfigItem;
import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.gts.api.Trade;
import com.envyful.gts.forge.EnvyGTSForge;
import com.envyful.gts.forge.player.GTSAttribute;

import java.util.stream.Collectors;

public class ReturnsUI {

    public static void openUI(ForgeEnvyPlayer player) {
        var config = EnvyGTSForge.getGui().getReturnsGui();
        var attribute = player.getAttributeNow(GTSAttribute.class);
        var pane = config.getGuiSettings().toPane();
        var returnPositions = config.getReturnPositions();
        var collect = attribute.getOwnedTrades().stream().filter(trade -> trade.hasExpired() || trade.wasRemoved() || trade.wasPurchased()).collect(Collectors.toList());

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

        pane.open(player, config.getGuiSettings());
    }
}
