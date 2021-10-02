package com.envyful.reforged.gts.forge.ui;

import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.gui.pane.Pane;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.reforged.gts.forge.ReforgedGTSForge;
import net.minecraft.entity.player.EntityPlayerMP;

public class SelectTypeUI {

    public static void openUI(EnvyPlayer<EntityPlayerMP> player) {
        Pane pane = GuiFactory.paneBuilder()
                .topLeftX(0).topLeftY(0)
                .width(9)
                .height(4)
                .build();

/*        pane.set(1, 3, GuiFactory.displayableBuilder(ItemStack.class)
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
                .build());*/

        GuiFactory.guiBuilder()
                .setPlayerManager(ReforgedGTSForge.getInstance().getPlayerManager())
                .addPane(pane)
                .height(3)
                .title(ReforgedGTSForge.getInstance().getLocale().getGuiName())
                .build().open(player);
    }
}
