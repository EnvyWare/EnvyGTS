package com.envyful.gts.forge.ui;

import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.config.UtilConfigInterface;
import com.envyful.api.forge.config.UtilConfigItem;
import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.gui.pane.Pane;
import com.envyful.gts.forge.EnvyGTSForge;
import com.envyful.gts.forge.config.GuiConfig;
import net.minecraft.util.Hand;

public class SellHandOrParty {

    public static void open(ForgeEnvyPlayer player) {
        GuiConfig.SellHandOrParty config = EnvyGTSForge.getInstance().getGui().getSellHandOrParty();

        Pane pane = GuiFactory.paneBuilder()
                .topLeftX(0)
                .topLeftY(0)
                .width(9)
                .height(config.getGuiSettings().getHeight())
                .build();

        UtilConfigInterface.fillBackground(pane, config.getGuiSettings());

        UtilConfigItem.builder()
                .clickHandler((envyPlayer, clickType) -> SelectPartyPokemonUI.openUI(player))
                .extendedConfigItem(player, pane, config.getSelectParty());

        UtilConfigItem.builder()
                .clickHandler((envyPlayer, clickType) -> EditItemPriceUI.openUI(player, player.getParent().getItemInHand(Hand.MAIN_HAND), false))
                .extendedConfigItem(player, pane, config.getSellHand());

        GuiFactory.guiBuilder()
                .setPlayerManager(EnvyGTSForge.getInstance().getPlayerManager())
                .addPane(pane)
                .height(config.getGuiSettings().getHeight())
                .title(UtilChatColour.colour(config.getGuiSettings().getTitle()))
                .build().open(player);
    }
}
