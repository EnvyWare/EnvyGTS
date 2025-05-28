package com.envyful.gts.forge.ui;

import com.envyful.api.forge.config.UtilConfigItem;
import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.gts.forge.EnvyGTSForge;
import com.envyful.gts.forge.config.GuiConfig;
import net.minecraft.util.Hand;

public class SellHandOrParty {

    public static void open(ForgeEnvyPlayer player) {
        GuiConfig.SellHandOrParty config = EnvyGTSForge.getGui().getSellHandOrParty();
        var pane = config.getGuiSettings().toPane();

        UtilConfigItem.builder()
                .asyncClick(false)
                .clickHandler((envyPlayer, clickType) -> SelectPartyPokemonUI.openUI(player))
                .extendedConfigItem(player, pane, config.getSelectParty());

        UtilConfigItem.builder()
                .asyncClick(false)
                .clickHandler((envyPlayer, clickType) -> EditItemPriceUI.openUI(player, player.getParent().getItemInHand(Hand.MAIN_HAND), false))
                .extendedConfigItem(player, pane, config.getSellHand());

        UtilConfigItem.builder()
                .asyncClick(false)
                .clickHandler((envyPlayer, clickType) -> ViewTradesUI.openUI(player))
                .extendedConfigItem(player, pane, config.getBackButton());

        pane.open(player, config.getGuiSettings());
    }
}
