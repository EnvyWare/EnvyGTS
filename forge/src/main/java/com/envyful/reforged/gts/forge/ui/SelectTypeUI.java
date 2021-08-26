package com.envyful.reforged.gts.forge.ui;

import com.envyful.api.forge.items.ItemBuilder;
import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.gui.item.Displayable;
import com.envyful.api.gui.pane.Pane;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.reforged.pixelmon.sprite.UtilSprite;
import com.envyful.api.reforged.pixelmon.storage.UtilPixelmonPlayer;
import com.envyful.reforged.gts.forge.ReforgedGTSForge;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.config.PixelmonItemsPokeballs;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class SelectTypeUI {

    public static void openUI(EnvyPlayer<EntityPlayerMP> player) {
        Pane pane = GuiFactory.paneBuilder()
                .topLeftX(0).topLeftY(0)
                .width(9)
                .height(4)
                .build();

        pane.fill(MainUI.BACKGROUND_ITEM);

        pane.set(1, 3, GuiFactory.displayableBuilder(ItemStack.class)
                        .itemStack(new ItemBuilder()
                                .type(Items.DIAMOND)
                                .name(ReforgedGTSForge.getInstance().getLocale().getItemItem().getName())
                                .lore(ReforgedGTSForge.getInstance().getLocale().getItemItem().getLore())
                                .build())
                        .clickHandler((envyPlayer, clickType) -> {})
                .build());
        pane.set(1, 6, GuiFactory.displayableBuilder(ItemStack.class)
                        .itemStack(new ItemBuilder()
                                .type(PixelmonItemsPokeballs.pokeBall)
                                .name(ReforgedGTSForge.getInstance().getLocale().getPokemonItem().getName())
                                .lore(ReforgedGTSForge.getInstance().getLocale().getPokemonItem().getLore())
                                .build())
                        .clickHandler((envyPlayer, clickType) -> SelectPartyPokemonUI.openUI((EnvyPlayer<EntityPlayerMP>) envyPlayer))
                .build());

        GuiFactory.guiBuilder()
                .setPlayerManager(ReforgedGTSForge.getInstance().getPlayerManager())
                .addPane(pane)
                .height(3)
                .title(ReforgedGTSForge.getInstance().getLocale().getGuiName())
                .build().open(player);
    }
}
