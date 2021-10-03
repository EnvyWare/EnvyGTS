package com.envyful.reforged.gts.forge.ui;

import com.envyful.api.config.type.ConfigItem;
import com.envyful.api.forge.config.UtilConfigItem;
import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.gui.pane.Pane;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.reforged.gts.api.Trade;
import com.envyful.reforged.gts.forge.ReforgedGTSForge;
import com.envyful.reforged.gts.forge.config.ReforgedGTSConfig;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

import java.util.List;

public class TimedOutTradesUI {

    public static void openUI(EnvyPlayer<EntityPlayerMP> player) {
        openUI(player, 0);
    }

    public static void openUI(EnvyPlayer<EntityPlayerMP> player, int page) {
        Pane pane = GuiFactory.paneBuilder()
                .topLeftX(0)
                .topLeftY(0)
                .width(9)
                .height(ReforgedGTSForge.getInstance().getConfig().getSearchGuiSettings().getHeight())
                .build();

        for (ConfigItem fillerItem : ReforgedGTSForge.getInstance().getConfig().getSearchGuiSettings().getFillerItems()) {
            pane.add(GuiFactory.displayableBuilder(ItemStack.class)
                             .itemStack(UtilConfigItem.fromConfigItem(fillerItem))
                             .build());
        }

        List<Trade> allTrades = ReforgedGTSForge.getInstance().getTradeManager().getExpiredTrades(player);

        ReforgedGTSConfig config = ReforgedGTSForge.getInstance().getConfig();

        if (config.getBackButton().isEnabled()) {
            pane.set(config.getBackButton().getXPos(), config.getBackButton().getYPos(),
                     GuiFactory.displayableBuilder(ItemStack.class)
                             .itemStack(UtilConfigItem.fromConfigItem(config.getBackButton()))
                             .clickHandler((envyPlayer, clickType) -> MainUI.open(player))
                             .build()
            );
        }

        if (config.getNextPageItem().isEnabled()) {
            pane.set(config.getNextPageItem().getXPos(), config.getNextPageItem().getYPos(),
                     GuiFactory.displayableBuilder(ItemStack.class)
                             .itemStack(UtilConfigItem.fromConfigItem(config.getNextPageItem()))
                             .clickHandler((envyPlayer, clickType) -> {
                                 if ((page + 1) >= (allTrades.size() / 45)) {
                                     openUI(player, 0);
                                 } else {
                                     openUI(player, page - 1);
                                 }
                             })
                             .build()
            );
        }

        if (config.getPreviousPageItem().isEnabled()) {
            pane.set(config.getPreviousPageItem().getXPos(), config.getPreviousPageItem().getYPos(),
                     GuiFactory.displayableBuilder(ItemStack.class)
                             .itemStack(UtilConfigItem.fromConfigItem(config.getPreviousPageItem()))
                             .clickHandler((envyPlayer, clickType) -> {
                                 if (page == 0) {
                                     openUI(player, (allTrades.size() / 45));
                                 } else {
                                     openUI(player, page - 1);
                                 }
                             })
                             .build()
            );
        }

        for (int i = (page * 45); i < ((page + 1) * 45); i++) {
            if (i >= allTrades.size()) {
                continue;
            }

            Trade trade = allTrades.get(i);
            trade.display(i % 45, pane);
        }

        GuiFactory.guiBuilder()
                .setPlayerManager(ReforgedGTSForge.getInstance().getPlayerManager())
                .addPane(pane)
                .setCloseConsumer(envyPlayer -> {})
                .height(ReforgedGTSForge.getInstance().getConfig().getSearchGuiSettings().getHeight())
                .title(ReforgedGTSForge.getInstance().getConfig().getSearchGuiSettings().getTitle())
                .build().open(player);
    }
}
