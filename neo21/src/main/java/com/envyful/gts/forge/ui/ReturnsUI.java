package com.envyful.gts.forge.ui;

import com.envyful.api.neoforge.config.UtilConfigItem;
import com.envyful.api.neoforge.player.ForgeEnvyPlayer;
import com.envyful.gts.forge.api.TradeOffer;
import com.envyful.gts.forge.EnvyGTSForge;
import com.envyful.gts.forge.player.GTSAttribute;

import java.util.stream.Collectors;

public class ReturnsUI {

    public static void openUI(ForgeEnvyPlayer player) {
        var config = EnvyGTSForge.getGui().getReturnsGui();
        var attribute = player.getAttributeNow(GTSAttribute.class);
        var pane = config.getGuiSettings().toPane();
        var returnPositions = config.getReturnPositions();
        var collect = attribute.getCollections();

        for (int i = 0; i < returnPositions.size(); i++) {
            if (i >= collect.size()) {
                break;
            }

            //TODO:
        }

        UtilConfigItem.builder()
                .clickHandler((envyPlayer, clickType) -> ViewTradesUI.openUI(player))
                .extendedConfigItem(player, pane, config.getBackButton());

        pane.open(player, config.getGuiSettings());
    }
}
