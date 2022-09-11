package com.envyful.gts.forge.ui;

import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.config.UtilConfigInterface;
import com.envyful.api.forge.config.UtilConfigItem;
import com.envyful.api.forge.items.ItemBuilder;
import com.envyful.api.forge.items.ItemFlag;
import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.gui.pane.Pane;
import com.envyful.api.reforged.pixelmon.sprite.UtilSprite;
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
        GuiConfig.PartyPokemonConfig config = EnvyGTSForge.getInstance().getGui().getPartyPokemonUIConfig();

        ((GTSAttribute) player.getAttribute(EnvyGTSForge.class)).setSelectedSlot(-1);

        Pane pane = GuiFactory.paneBuilder()
                .topLeftX(0).topLeftY(0)
                .width(9)
                .height(config.getGuiSettings().getHeight())
                .build();

        UtilConfigInterface.fillBackground(pane, config.getGuiSettings());

        setPokemon(player, pane);

        UtilConfigItem.builder()
                .clickHandler((envyPlayer, clickType) -> SelectPCPokemonUI.openUI(player))
                .extendedConfigItem(player, pane, config.getViewPCButton());

        UtilConfigItem.builder()
                .clickHandler((envyPlayer, clickType) -> MainUI.open(player))
                .extendedConfigItem(player, pane, config.getBackButton());

        UtilConfigItem.builder()
                .clickHandler((envyPlayer, clickType) -> {
                    GTSAttribute attribute = envyPlayer.getAttribute(EnvyGTSForge.class);

                    List<Trade> trades = Lists.newArrayList(attribute.getOwnedTrades());

                    trades.removeIf(trade -> trade.hasExpired() || trade.wasPurchased() || trade.wasRemoved());

                    if (trades.size() >= EnvyGTSForge.getInstance().getConfig().getMaxListingsPerUser()) {
                        player.message(UtilChatColour.translateColourCodes(
                                '&',
                                EnvyGTSForge.getInstance().getLocale().getMessages().getMaxTradesAlreadyReached()
                        ));
                        return;
                    }

                    if (attribute.getSelectedSlot() == -1) {
                        return;
                    }

                    PlayerPartyStorage party = StorageProxy.getParty(player.getParent());

                    if (party.countAblePokemon() <= 1) {
                        return;
                    }

                    double price = UtilPokemonPrice.getMinPrice(party.get(attribute.getSelectedSlot()));

                    attribute.setCurrentPrice(price);
                    attribute.setCurrentMinPrice(price);
                    attribute.setCurrentDuration(EnvyGTSForge.getInstance().getConfig().getDefaultTradeDurationSeconds());
                    SelectPriceUI.openUI(player, attribute.getSelectedSlot());
                })
                .extendedConfigItem(player, pane, config.getConfirmItem());

        GuiFactory.guiBuilder()
                .setPlayerManager(EnvyGTSForge.getInstance().getPlayerManager())
                .addPane(pane)
                .height(config.getGuiSettings().getHeight())
                .title(UtilChatColour.colour(config.getGuiSettings().getTitle()))
                .build().open(player);
    }

    private static void setPokemon(ForgeEnvyPlayer player, Pane pane) {
        PlayerPartyStorage party = StorageProxy.getParty(player.getParent());
        Pokemon[] all = party.getAll();
        GuiConfig.PartyPokemonConfig config = EnvyGTSForge.getInstance().getGui().getPartyPokemonUIConfig();

        for (int i = 0; i < 6; i++) {
            int pos = config.getPartySelectionPositions().get(i);

            if (i >= all.length || all[i] == null) {
                pane.set(pos % 9, pos / 9, GuiFactory.displayableBuilder(ItemStack.class)
                        .itemStack(UtilConfigItem.fromConfigItem(config.getNoPokemonItem())).build());
            } else if (all[i].isUntradeable() ||
                    (!EnvyGTSForge.getInstance().getConfig().isAllowEggs() && all[i].isEgg()) ||
                    EnvyGTSForge.getInstance().getConfig().isBlackListed(all[i])) {
                pane.set(pos % 9, pos / 9, GuiFactory.displayableBuilder(ItemStack.class)
                        .itemStack(UtilConfigItem.fromConfigItem(config.getUntradeablePokemonItem())).build());
            } else {
                final int slot = i;
                pane.set(pos % 9, pos / 9, GuiFactory.displayableBuilder(ItemStack.class)
                        .itemStack(UtilSprite.getPokemonElement(
                                all[i],
                                EnvyGTSForge.getInstance().getGui().getPartyPokemonUIConfig().getSpriteConfig()
                        ))
                        .clickHandler((envyPlayer, clickType) -> {
                            GTSAttribute attribute = envyPlayer.getAttribute(EnvyGTSForge.class);
                            attribute.setSelectedSlot(slot);
                            pane.set(config.getConfirmDisplay() % 9, config.getConfirmDisplay() / 9,
                                     GuiFactory.displayableBuilder(ItemStack.class)
                                             .itemStack(new ItemBuilder(UtilSprite.getPokemonElement(
                                                     all[slot],
                                                     EnvyGTSForge.getInstance().getGui().getPartyPokemonUIConfig().getSpriteConfig()
                                             ))
                                                                .enchant(Enchantments.UNBREAKING, 1)
                                                                .itemFlag(ItemFlag.HIDE_ENCHANTS)
                                                                .build())
                                             .build()
                            );
                        }).build());
            }
        }
    }
}
