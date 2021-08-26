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
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class SelectPartyPokemonUI {

    private static final Displayable EMPTY_POKEMON = GuiFactory.displayableBuilder(ItemStack.class)
            .itemStack(new ItemBuilder()
                    .type(Item.getByNameOrId("minecraft:barrier"))
                    .name(" ")
                    .build()).build();

    public static void openUI(EnvyPlayer<EntityPlayerMP> player) {
        Pane pane = GuiFactory.paneBuilder()
                .topLeftX(0).topLeftY(0)
                .width(9)
                .height(4)
                .build();

        pane.fill(MainUI.BACKGROUND_ITEM);

        setPokemon(player, pane);

        GuiFactory.guiBuilder()
                .setPlayerManager(ReforgedGTSForge.getInstance().getPlayerManager())
                .addPane(pane)
                .height(4)
                .title(ReforgedGTSForge.getInstance().getLocale().getGuiName())
                .build().open(player);
    }

    private static void setPokemon(EnvyPlayer<EntityPlayerMP> player, Pane pane) {
        pane.set(1, 1, EMPTY_POKEMON);
        pane.set(1, 2, EMPTY_POKEMON);
        pane.set(1, 3, EMPTY_POKEMON);
        pane.set(1, 5, EMPTY_POKEMON);
        pane.set(1, 6, EMPTY_POKEMON);
        pane.set(1, 7, EMPTY_POKEMON);

        int pos = 0;

        for (Pokemon pokemon : UtilPixelmonPlayer.getParty(player.getParent()).getAll()) {
            ++pos;
            if (pokemon == null) {
                continue;
            }

            pane.set(1, pos, GuiFactory.displayableBuilder(ItemStack.class)
                    .itemStack(UtilSprite.getPixelmonSprite(pokemon))
                    .clickHandler((envyPlayer, clickType) -> {

                    }).build());
        }
    }
}
