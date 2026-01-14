package com.envyful.gts.forge.ui;

import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.gui.pane.Pane;
import com.envyful.api.neoforge.chat.UtilChatColour;
import com.envyful.api.neoforge.config.UtilConfigItem;
import com.envyful.api.neoforge.items.ItemBuilder;
import com.envyful.api.neoforge.player.ForgeEnvyPlayer;
import com.envyful.api.reforged.pixelmon.sprite.UtilSprite;
import com.envyful.api.text.Placeholder;
import com.envyful.gts.forge.api.TradeOffer;
import com.envyful.gts.forge.EnvyGTSForge;
import com.envyful.gts.forge.config.GuiConfig;
import com.envyful.gts.forge.player.GTSAttribute;
import com.envyful.gts.forge.util.UtilPokemonPrice;
import com.google.common.collect.Lists;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.storage.PlayerPartyStorage;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.List;

public class SelectPartyPokemonUI {

    public static void openUI(ForgeEnvyPlayer player) {
        GuiConfig.PartyPokemonConfig config = EnvyGTSForge.getGui().getPartyPokemonUIConfig();
        GTSAttribute playerAttribute = player.getAttributeNow(GTSAttribute.class);

        if (playerAttribute.getSelectedSlot() > 6) {
            playerAttribute.setSelectedSlot(-1);
        }

        var pane = config.getGuiSettings().toPane();

        setPokemon(player, pane);

        UtilConfigItem.builder()
                .clickHandler((envyPlayer, clickType) -> SelectPCPokemonUI.openUI(player))
                .extendedConfigItem(player, pane, config.getViewPCButton());

        UtilConfigItem.builder()
                .clickHandler((envyPlayer, clickType) -> SellHandOrParty.open(player))
                .extendedConfigItem(player, pane, config.getBackButton());

        UtilConfigItem.builder()
                .asyncClick(false)
                .clickHandler((envyPlayer, clickType) -> {
                    var attribute = player.getAttributeNow(GTSAttribute.class);

                    if (attribute.hasReachedMaximumTrades()) {
                        player.message(EnvyGTSForge.getLocale().getMessages().getMaxTradesAlreadyReached());
                        return;
                    }

                    if (attribute.getSelectedSlot() == -1) {
                        return;
                    }

                    var party = player.getParent().getPartyNow();

                    if (party.countAblePokemon() <= 1 || party.getTeam().size() <= attribute.getSelectedSlot()) {
                        return;
                    }

                    var price = UtilPokemonPrice.getMinPrice(party.get(attribute.getSelectedSlot()));

                    attribute.setCurrentPrice(price);
                    attribute.setCurrentMinPrice(price);
                    attribute.setCurrentDuration(EnvyGTSForge.getConfig().getDefaultTradeDurationSeconds());
                    SelectPriceUI.openUI(player, attribute.getSelectedSlot());
                })
                .extendedConfigItem(player, pane, config.getConfirmItem());

        pane.open(player, config.getGuiSettings());
    }

    private static void setPokemon(ForgeEnvyPlayer player, Pane pane) {
        var party = player.getParent().getPartyNow();
        var all = party.getAll();
        var config = EnvyGTSForge.getGui().getPartyPokemonUIConfig();
        var enchants = ServerLifecycleHooks.getCurrentServer().registryAccess().registryOrThrow(Registries.ENCHANTMENT);

        for (int i = 0; i < 6; i++) {
            int pos = config.getPartySelectionPositions().get(i);

            if (i >= all.length || all[i] == null) {
                pane.set(pos % 9, pos / 9, GuiFactory.displayable(UtilConfigItem.fromConfigItem(config.getNoPokemonItem())));
            } else if (all[i].isUntradeable() ||
                    (!EnvyGTSForge.getConfig().isAllowEggs() && all[i].isEgg()) ||
                    EnvyGTSForge.getConfig().isBlackListed(all[i])) {
                pane.set(pos % 9, pos / 9, GuiFactory.displayable(UtilConfigItem.fromConfigItem(config.getUntradeablePokemonItem())));
            } else {
                final int slot = i;
                var gtsAttribute = player.getAttributeNow(GTSAttribute.class);
                var item = EnvyGTSForge.getGui().getSpriteConfig().fromPokemon(all[i],Placeholder.empty("%below_lore_data%"));

                if (gtsAttribute.getSelectedSlot() == slot) {
                    item.enchant(enchants.getHolder(Enchantments.MENDING).orElseThrow(), 1);
                }

                pane.set(pos % 9, pos / 9, GuiFactory.displayableBuilder(ItemStack.class)
                        .itemStack(item)
                        .clickHandler((envyPlayer, clickType) -> {
                            gtsAttribute.setSelectedSlot(slot);
                            openUI(player);
                        }).build());
            }
        }
    }
}
