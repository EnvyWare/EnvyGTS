package com.envyful.reforged.gts.forge.ui;

import com.envyful.api.config.type.ConfigItem;
import com.envyful.api.forge.items.ItemBuilder;
import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.gui.pane.Pane;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.reforged.gts.forge.ReforgedGTSForge;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class MainUI {

    public static void open(EnvyPlayer<EntityPlayerMP> player) {
        Pane pane = GuiFactory.paneBuilder()
                .topLeftX(0).topLeftY(0)
                .width(9)
                .height(ReforgedGTSForge.getInstance().getConfig().getGuiSettings().getHeight())
                .build();

        for (ConfigItem fillerItem : ReforgedGTSForge.getInstance().getConfig().getGuiSettings().getFillerItems()) {
            pane.add(GuiFactory.displayableBuilder(ItemStack.class).itemStack(new ItemBuilder()
                             .type(Item.getByNameOrId(fillerItem.getType()))
                             .amount(fillerItem.getAmount())
                             .damage(fillerItem.getDamage())
                             .name(fillerItem.getName())
                             .lore(fillerItem.getLore())
                             .build()).build());
        }



        GuiFactory.guiBuilder()
                .setPlayerManager(ReforgedGTSForge.getInstance().getPlayerManager())
                .addPane(pane)
                .height(ReforgedGTSForge.getInstance().getConfig().getGuiSettings().getHeight())
                .title(ReforgedGTSForge.getInstance().getLocale().getGuiName())
                .build().open(player);
    }
}
