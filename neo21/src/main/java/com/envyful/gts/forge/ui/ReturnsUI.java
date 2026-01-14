package com.envyful.gts.forge.ui;

import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.neoforge.config.UtilConfigInterface;
import com.envyful.api.neoforge.config.UtilConfigItem;
import com.envyful.api.neoforge.player.ForgeEnvyPlayer;
import com.envyful.gts.forge.EnvyGTSForge;
import com.envyful.gts.forge.player.GTSAttribute;

public class ReturnsUI {

    public static void openUI(ForgeEnvyPlayer player) {
        openUI(player, 1);
    }

    @SuppressWarnings("unchecked")
    public static void openUI(ForgeEnvyPlayer player, int page) {
        var config = EnvyGTSForge.getGui().getReturnsGui();
        var attribute = player.getAttributeNow(GTSAttribute.class);
        var collections = attribute.getCollections();

        UtilConfigInterface.paginatedBuilder(collections)
                .itemConversion(collection -> GuiFactory.displayableBuilder(collection.offer().item().display())
                        .clickHandler((envyPlayer, clickType) -> {
                            var updatedCollection = attribute.getCollectionItem(collection.getId());

                            if (updatedCollection == null) {
                                openUI(player);
                                //TODO: message the player
                                return;
                            }

                            attribute.removeCollectionItem(collection);
                            collection.offer().item().collect(player);
                            openUI(player);
                            //TODO: message the player
                        })
                        .build())
                .configSettings(config.getGuiSettings())
                .extraItems((pane, currentPage) -> {
                    UtilConfigItem.builder()
                            .clickHandler((envyPlayer, clickType) -> ViewTradesUI.openUI(player))
                            .extendedConfigItem(player, pane, config.getBackButton());
                })
                .open(player, page);
    }
}
