package com.envyful.gts.forge.ui;

import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.config.UtilConfigInterface;
import com.envyful.api.forge.config.UtilConfigItem;
import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.gui.pane.Pane;
import com.envyful.gts.forge.EnvyGTSForge;
import com.envyful.gts.forge.config.GuiConfig;

public class MainUI {

    public static void open(ForgeEnvyPlayer player) {
        GuiConfig.MainUIConfig config = EnvyGTSForge.getInstance().getGui().getMainUIConfig();

        Pane pane = GuiFactory.paneBuilder()
                .topLeftX(0)
                .topLeftY(0)
                .width(9)
                .height(config.getGuiSettings().getHeight())
                .build();

        UtilConfigInterface.fillBackground(pane, config.getGuiSettings());

        UtilConfigItem.builder()
                .clickHandler((envyPlayer, clickType) -> SellHandOrParty.open(player))
                .extendedConfigItem(player, pane, config.getSellItemButton());

        UtilConfigItem.builder()
                .clickHandler((envyPlayer, clickType) -> ClaimTradesUI.openUI(player))
                .extendedConfigItem(player, pane, config.getViewClaimsButton());

        UtilConfigItem.builder()
                .clickHandler((envyPlayer, clickType) -> TimedOutTradesUI.openUI(player))
                .extendedConfigItem(player, pane, config.getViewTimeoutButton());

        UtilConfigItem.builder()
                .clickHandler((envyPlayer, clickType) -> ViewTradesUI.openUI(player))
                .extendedConfigItem(player, pane, config.getViewTradesButton());

        GuiFactory.guiBuilder()
                .setPlayerManager(EnvyGTSForge.getInstance().getPlayerManager())
                .addPane(pane)
                .height(config.getGuiSettings().getHeight())
                .title(UtilChatColour.colour(config.getGuiSettings().getTitle()))
                .build().open(player);
    }
}
