package com.envyful.reforged.gts.forge.ui;

import com.envyful.api.forge.items.ItemBuilder;
import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.gui.item.Displayable;
import com.envyful.api.gui.pane.Pane;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.reforged.gts.forge.ReforgedGTSForge;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class MainUI {

    public static final Displayable BACKGROUND_ITEM = GuiFactory.displayableBuilder(ItemStack.class)
            .itemStack(new ItemBuilder()
                    .type(Item.getByNameOrId("minecraft:stained_glass_pane"))
                    .damage(15)
                    .build())
            .build();

    public static void open(EnvyPlayer<EntityPlayerMP> player) {
        Pane pane = GuiFactory.paneBuilder()
                .topLeftX(0).topLeftY(0)
                .width(9)
                .height(6)
                .build();

        pane.fill(BACKGROUND_ITEM);

        GuiFactory.guiBuilder()
                .setPlayerManager(ReforgedGTSForge.getInstance().getPlayerManager())
                .addPane(pane)
                .height(5)
                .title("GTS")
                .build().open(player);
    }
}
