package com.envyful.reforged.gts.forge.ui;

import com.envyful.api.config.type.ConfigItem;
import com.envyful.api.config.type.PositionableConfigItem;
import com.envyful.api.forge.config.UtilConfigItem;
import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.gui.pane.Pane;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.reforged.pixelmon.sprite.UtilSprite;
import com.envyful.api.reforged.pixelmon.storage.UtilPixelmonPlayer;
import com.envyful.reforged.gts.forge.ReforgedGTSForge;
import com.envyful.reforged.gts.forge.config.ReforgedGTSConfig;
import com.envyful.reforged.gts.forge.player.GTSAttribute;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

public class SelectPartyPokemonUI {

    public static void openUI(EnvyPlayer<EntityPlayerMP> player) {
        Pane pane = GuiFactory.paneBuilder()
                .topLeftX(0).topLeftY(0)
                .width(9)
                .height(ReforgedGTSForge.getInstance().getConfig().getGuiSettings().getHeight())
                .build();

        for (ConfigItem fillerItem : ReforgedGTSForge.getInstance().getConfig().getGuiSettings().getFillerItems()) {
            pane.add(GuiFactory.displayableBuilder(ItemStack.class)
                             .itemStack(UtilConfigItem.fromConfigItem(fillerItem))
                             .build());
        }

        setPokemon(player, pane);

        PositionableConfigItem confirmItem = ReforgedGTSForge.getInstance().getConfig().getConfirmItem();

        if (confirmItem.isEnabled()) {
            pane.set(confirmItem.getXPos(), confirmItem.getYPos(), GuiFactory.displayableBuilder(ItemStack.class)
                    .itemStack(UtilConfigItem.fromConfigItem(confirmItem))
                    .clickHandler((envyPlayer, clickType) -> {
                        GTSAttribute attribute = envyPlayer.getAttribute(ReforgedGTSForge.class);

                        if (attribute.getSelectedSlot() == -1) {
                            return;
                        }

                        //TODO: open next UI
                    })
                    .build());
        }

        GuiFactory.guiBuilder()
                .setPlayerManager(ReforgedGTSForge.getInstance().getPlayerManager())
                .addPane(pane)
                .setCloseConsumer(envyPlayer -> {})
                .height(ReforgedGTSForge.getInstance().getConfig().getGuiSettings().getHeight())
                .title(ReforgedGTSForge.getInstance().getConfig().getGuiSettings().getTitle())
                .build().open(player);
    }

    private static void setPokemon(EnvyPlayer<EntityPlayerMP> player, Pane pane) {
        PlayerPartyStorage party = UtilPixelmonPlayer.getParty(player.getParent());
        Pokemon[] all = party.getAll();
        ReforgedGTSConfig config = ReforgedGTSForge.getInstance().getConfig();

        for (int i = 0; i < 6; i++) {
            int pos = config.getPartySelectionPositions().get(i);

            if (i >= all.length || all[i] == null) {
                pane.set(pos % 9, pos / 9, GuiFactory.displayableBuilder(ItemStack.class)
                        .itemStack(UtilConfigItem.fromConfigItem(config.getNoPokemonItem())).build());
            } else {
                final int slot = i;
                pane.set(pos % 9, pos / 9, GuiFactory.displayableBuilder(ItemStack.class)
                        .itemStack(UtilSprite.getPokemonElement(all[i]))
                        .clickHandler((envyPlayer, clickType) -> {
                            GTSAttribute attribute = envyPlayer.getAttribute(ReforgedGTSForge.class);
                            attribute.setSelectedSlot(slot);
                            pane.set(config.getConfirmDisplay() % 9, config.getConfirmDisplay() / 9,
                                     GuiFactory.displayableBuilder(ItemStack.class)
                                             .itemStack(UtilSprite.getPokemonElement(all[slot]))
                                             .build()
                            );
                        }).build());
            }
        }
    }
}
