package com.envyful.gts.forge.ui;

import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.gui.pane.Pane;
import com.envyful.api.neoforge.config.UtilConfigItem;
import com.envyful.api.neoforge.player.ForgeEnvyPlayer;
import com.envyful.gts.forge.EnvyGTSForge;
import com.envyful.gts.forge.config.GuiConfig;
import com.envyful.gts.forge.api.player.GTSAttribute;
import com.envyful.gts.forge.util.UtilPokemonPrice;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import net.minecraft.world.item.ItemStack;

public class SelectPartyPokemonUI {

    public static void openUI(ForgeEnvyPlayer player) {
        var config = EnvyGTSForge.getGui().getPartyPokemonUIConfig();
        var pane = config.getGuiSettings().toPane();

        setPokemon(player, pane);

        UtilConfigItem.builder()
                .clickHandler((envyPlayer, clickType) -> SelectPCPokemonUI.openUI(player))
                .extendedConfigItem(player, pane, config.getViewPCButton());

        UtilConfigItem.builder()
                .clickHandler((envyPlayer, clickType) -> SellHandOrParty.open(player))
                .extendedConfigItem(player, pane, config.getBackButton());

        pane.open(player, config.getGuiSettings());
    }

    private static void setPokemon(ForgeEnvyPlayer player, Pane pane) {
        var party = player.getParent().getPartyNow();
        var all = party.getAll();
        var config = EnvyGTSForge.getGui().getPartyPokemonUIConfig();

        for (int i = 0; i < 6; i++) {
            int pos = config.getPartySelectionPositions().get(i);
            var posX = pos % 9;
            var posY = pos / 9;

            if (i >= all.length || all[i] == null) {
                pane.set(posX, posY, config.getNoPokemonItem().toDisplayable());
            } else if (isBlockedPokemon(all[i])) {
                pane.set(posX, posY, config.getUntradeablePokemonItem().toDisplayable());
            } else {
                final int slot = i;
                var item = EnvyGTSForge.getGui().getSpriteConfig().fromPokemon(all[i]);

                pane.set(posX, posY, GuiFactory.displayableBuilder(ItemStack.class)
                        .itemStack(item)
                        .asyncClick(false)
                        .clickHandler((envyPlayer, clickType) -> handleClickingPokemon(player, slot))
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

    private static void handleClickingPokemon(ForgeEnvyPlayer player, int slot) {
        var attribute = player.getAttributeNow(GTSAttribute.class);

        if (attribute.hasReachedMaximumTrades()) {
            player.message(EnvyGTSForge.getLocale().getMessages().getMaxTradesAlreadyReached());
            return;
        }

        var party = player.getParent().getPartyNow();

        if (party.countAblePokemon() <= 1) {
            player.message(EnvyGTSForge.getLocale().getMessages().getInsufficientPartyMembers());
            return;
        }

        var pokemon = party.get(slot);

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
        SelectPriceUI.openUI(player, slot);
    }
}
