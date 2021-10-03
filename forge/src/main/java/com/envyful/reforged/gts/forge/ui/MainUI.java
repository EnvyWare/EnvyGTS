package com.envyful.reforged.gts.forge.ui;

import com.envyful.api.config.type.ConfigItem;
import com.envyful.api.forge.config.UtilConfigItem;
import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.gui.pane.Pane;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.reforged.gts.forge.ReforgedGTSForge;
import com.envyful.reforged.gts.forge.config.ReforgedGTSConfig;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

public class MainUI {

    public static void open(EnvyPlayer<EntityPlayerMP> player) {
        ReforgedGTSConfig.MainUIConfig config = ReforgedGTSForge.getInstance().getConfig().getMainUIConfig();

        Pane pane = GuiFactory.paneBuilder()
                .topLeftX(0)
                .topLeftY(0)
                .width(9)
                .height(config.getGuiSettings().getHeight())
                .build();

        for (ConfigItem fillerItem : config.getGuiSettings().getFillerItems()) {
            pane.add(GuiFactory.displayableBuilder(ItemStack.class)
                             .itemStack(UtilConfigItem.fromConfigItem(fillerItem))
                             .build());
        }

        if (config.getSellItemButton().isEnabled()) {
            pane.set(config.getSellItemButton().getXPos(), config.getSellItemButton().getYPos(),
                     GuiFactory.displayableBuilder(ItemStack.class)
                             .itemStack(UtilConfigItem.fromConfigItem(config.getSellItemButton()))
                             .clickHandler((envyPlayer, clickType) -> SelectPartyPokemonUI.openUI(player))
                             .build()
            );
        }

        if (config.getViewClaimsButton().isEnabled()) {
            pane.set(config.getViewClaimsButton().getXPos(), config.getViewClaimsButton().getYPos(),
                     GuiFactory.displayableBuilder(ItemStack.class)
                             .itemStack(UtilConfigItem.fromConfigItem(config.getViewClaimsButton()))
                             .clickHandler((envyPlayer, clickType) -> ClaimTradesUI.openUI(player))
                             .build()
            );
        }

        if (config.getViewTimeoutButton().isEnabled()) {
            pane.set(config.getViewTimeoutButton().getXPos(), config.getViewTimeoutButton().getYPos(),
                     GuiFactory.displayableBuilder(ItemStack.class)
                             .itemStack(UtilConfigItem.fromConfigItem(config.getViewTimeoutButton()))
                             .clickHandler((envyPlayer, clickType) -> TimedOutTradesUI.openUI(player))
                             .build()
            );
        }

        if (config.getViewTradesButton().isEnabled()) {
            pane.set(config.getViewTradesButton().getXPos(), config.getViewTradesButton().getYPos(),
                     GuiFactory.displayableBuilder(ItemStack.class)
                             .itemStack(UtilConfigItem.fromConfigItem(config.getViewTradesButton()))
                             .clickHandler((envyPlayer, clickType) -> ViewTradesUI.openUI(player))
                             .build()
            );
        }

        GuiFactory.guiBuilder()
                .setPlayerManager(ReforgedGTSForge.getInstance().getPlayerManager())
                .addPane(pane)
                .setCloseConsumer(envyPlayer -> {})
                .height(config.getGuiSettings().getHeight())
                .title(config.getGuiSettings().getTitle())
                .build().open(player);
    }
}
