package com.envyful.gts.forge.ui;

import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.config.UtilConfigItem;
import com.envyful.api.forge.items.ItemBuilder;
import com.envyful.api.forge.items.ItemFlag;
import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.gui.pane.Pane;
import com.envyful.api.reforged.pixelmon.sprite.UtilSprite;
import com.envyful.api.text.Placeholder;
import com.envyful.gts.api.Trade;
import com.envyful.gts.forge.EnvyGTSForge;
import com.envyful.gts.forge.config.GuiConfig;
import com.envyful.gts.forge.player.GTSAttribute;
import com.envyful.gts.forge.util.UtilPokemonPrice;
import com.google.common.collect.Lists;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.storage.PlayerPartyStorage;
import com.pixelmonmod.pixelmon.api.storage.StorageProxy;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;

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
                    GTSAttribute attribute = player.getAttributeNow(GTSAttribute.class);

                    List<Trade> trades = Lists.newArrayList(attribute.getOwnedTrades());

                    trades.removeIf(trade -> trade.hasExpired() || trade.wasPurchased() || trade.wasRemoved());

                    if (trades.size() >= EnvyGTSForge.getConfig().getMaxListingsPerUser()) {
                        player.message(UtilChatColour.colour(
                                EnvyGTSForge.getLocale().getMessages().getMaxTradesAlreadyReached()
                        ));
                        return;
                    }

                    if (attribute.getSelectedSlot() == -1) {
                        return;
                    }

                    PlayerPartyStorage party = StorageProxy.getParty(player.getParent());

                    if (party.countAblePokemon() <= 1 || party.getTeam().size() <= attribute.getSelectedSlot()) {
                        return;
                    }

                    double price = UtilPokemonPrice.getMinPrice(party.get(attribute.getSelectedSlot()));

                    attribute.setCurrentPrice(price);
                    attribute.setCurrentMinPrice(price);
                    attribute.setCurrentDuration(EnvyGTSForge.getConfig().getDefaultTradeDurationSeconds());
                    SelectPriceUI.openUI(player, attribute.getSelectedSlot());
                })
                .extendedConfigItem(player, pane, config.getConfirmItem());

        pane.open(player, config.getGuiSettings());
    }

    private static void setPokemon(ForgeEnvyPlayer player, Pane pane) {
        PlayerPartyStorage party = StorageProxy.getParty(player.getParent());
        Pokemon[] all = party.getAll();
        GuiConfig.PartyPokemonConfig config = EnvyGTSForge.getGui().getPartyPokemonUIConfig();

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
                ItemBuilder builder = new ItemBuilder(UtilSprite.getPokemonElement(
                        all[i],
                        EnvyGTSForge.getGui().getSpriteConfig(),
                        Placeholder.empty("%below_lore_data%")
                ));

                GTSAttribute gtsAttribute = player.getAttributeNow(GTSAttribute.class);

                if (gtsAttribute.getSelectedSlot() == slot) {
                    builder
                            .enchant(Enchantments.UNBREAKING, 1)
                            .itemFlag(ItemFlag.HIDE_ENCHANTS);
                }

                pane.set(pos % 9, pos / 9, GuiFactory.displayableBuilder(ItemStack.class)
                        .itemStack(builder.build())
                        .clickHandler((envyPlayer, clickType) -> {
                            gtsAttribute.setSelectedSlot(slot);
                            openUI(player);
                        }).build());
            }
        }
    }
}
