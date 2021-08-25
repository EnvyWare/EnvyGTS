package com.envyful.reforged.gts.forge.ui;

import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.gui.pane.Pane;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.reforged.gts.forge.ReforgedGTSForge;

public class SelectPartyPokemonUI {

    public static void openUI(EnvyPlayer<?> player) {
        Pane pane = GuiFactory.paneBuilder()
                .topLeftX(0).topLeftY(0)
                .width(9)
                .height(4)
                .build();

        pane.fill(MainUI.BACKGROUND_ITEM);

        GuiFactory.guiBuilder()
                .setPlayerManager(ReforgedGTSForge.getInstance().getPlayerManager())
                .addPane(pane)
                .height(4)
                .title("GTS")
                .build().open(player);
    }
}
