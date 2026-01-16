package com.envyful.gts.forge.ui;

import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.gui.pane.Pane;
import com.envyful.api.neoforge.chat.UtilChatColour;
import com.envyful.api.neoforge.config.UtilConfigItem;
import com.envyful.api.neoforge.items.ItemBuilder;
import com.envyful.api.neoforge.items.ItemFlag;
import com.envyful.api.neoforge.player.ForgeEnvyPlayer;
import com.envyful.gts.forge.EnvyGTSForge;
import com.envyful.gts.forge.config.GuiConfig;
import com.envyful.gts.forge.api.player.GTSAttribute;
import com.envyful.gts.forge.util.UtilPokemonPrice;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.storage.PCBox;
import com.pixelmonmod.pixelmon.api.storage.PCStorage;
import com.pixelmonmod.pixelmon.api.storage.StorageProxy;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public class SelectPCPokemonUI {

    public static void openUI(ForgeEnvyPlayer player) {
        openUI(player, 0);
    }

    public static void openUI(ForgeEnvyPlayer player, int page) {
        var config = EnvyGTSForge.getGui().getPcConfig();
        var pane = config.getGuiSettings().toPane();
        var pc = StorageProxy.getPCForPlayerNow(player.getParent());

        setPokemon(player, page, pane);

        UtilConfigItem.builder()
                .clickHandler((envyPlayer, clickType) -> SelectPartyPokemonUI.openUI(player))
                .extendedConfigItem(player, pane, config.getBackButton());

        UtilConfigItem.builder()
                .clickHandler((envyPlayer, clickType) -> {
                    if (page >= (pc.getBoxCount() - 1)) {
                        openUI(player, 0);
                    } else {
                        openUI(player, page + 1);
                    }
                })
                .extendedConfigItem(player, pane, config.getNextPageButton());

        UtilConfigItem.builder()
                .clickHandler((envyPlayer, clickType) -> {
                    if (page <= 0) {
                        openUI(player, pc.getBoxCount() - 1);
                    } else {
                        openUI(player, page - 1);
                    }
                })
                .extendedConfigItem(player, pane, config.getPreviousPageButton());

        pane.open(player, config.getGuiSettings());
    }

    private static void setPokemon(ForgeEnvyPlayer player, int page, Pane pane) {
        var pc = StorageProxy.getPCForPlayerNow(player.getParent());
        var box = pc.getBox(page);
        var config = EnvyGTSForge.getGui().getPcConfig();

        for (int i = 0; i < config.getPerPage(); i++) {
            int posX = i % 5;
            int posY = i / 5;
            var pokemon = box.get(i);

            if (pokemon == null) {
                pane.set(2 + posX, posY, config.getNoPokemonItem().toDisplayable());
            } else if (isBlockedPokemon(pokemon)) {
                pane.set(2 + posX, posY, config.getUntradeablePokemonItem().toDisplayable());
            } else {
                final int slot = i;

                pane.set(2 + posX, posY, GuiFactory.displayableBuilder(ItemStack.class)
                        .itemStack(EnvyGTSForge.getGui().getSpriteConfig().fromPokemon(pokemon))
                        .asyncClick(false)
                        .clickHandler((envyPlayer, clickType) -> handleClickingPokemon(player, page, slot))
                        .build());
            }
        }
    }

    private static boolean isBlockedPokemon(Pokemon pokemon) {
        if (pokemon.isUntradeable()) {
            return true;
        }

        if (!EnvyGTSForge.getConfig().isAllowEggs() && pokemon.isEgg()) {
            return true;
        }

        return EnvyGTSForge.getConfig().isBlackListed(pokemon);
    }

    private static void handleClickingPokemon(ForgeEnvyPlayer player, int page, int slot) {
        var attribute = player.getAttributeNow(GTSAttribute.class);

        if (attribute.hasReachedMaximumTrades()) {
            player.message(EnvyGTSForge.getLocale().getMessages().getMaxTradesAlreadyReached());
            return;
        }

        var pc = StorageProxy.getPCForPlayerNow(player.getParent());
        var box = pc.getBox(page);
        var pokemon = box.get(slot);

        if (pokemon == null) {
            player.message(EnvyGTSForge.getLocale().getMessages().getNoPokemonInSlot());
            return;
        }

        if (isBlockedPokemon(pokemon)) {
            player.message(EnvyGTSForge.getLocale().getMessages().getBlockedPokemon());
            return;
        }

        var price = UtilPokemonPrice.getMinPrice(pokemon);
        attribute.setCurrentPrice(price);
        attribute.setCurrentMinPrice(price);
        SelectPriceUI.openUI(player, page, slot, false);
    }
}
