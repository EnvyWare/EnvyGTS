package com.envyful.gts.forge.ui;

import com.envyful.api.config.type.ConfigItem;
import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.config.UtilConfigItem;
import com.envyful.api.forge.items.ItemBuilder;
import com.envyful.api.forge.items.ItemFlag;
import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.gui.pane.Pane;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.reforged.pixelmon.sprite.UtilSprite;
import com.envyful.api.reforged.pixelmon.storage.UtilPixelmonPlayer;
import com.envyful.gts.api.Trade;
import com.envyful.gts.forge.EnvyGTSForge;
import com.envyful.gts.forge.config.GuiConfig;
import com.envyful.gts.forge.player.GTSAttribute;
import com.envyful.gts.forge.util.UtilPokemonPrice;
import com.google.common.collect.Lists;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;

import java.util.List;

public class SelectPartyPokemonUI {

    public static void openUI(EnvyPlayer<EntityPlayerMP> player) {
        GuiConfig.PartyPokemonConfig config = EnvyGTSForge.getInstance().getGui().getPartyPokemonUIConfig();

        ((GTSAttribute) player.getAttribute(EnvyGTSForge.class)).setSelectedSlot(-1);

        Pane pane = GuiFactory.paneBuilder()
                .topLeftX(0).topLeftY(0)
                .width(9)
                .height(config.getGuiSettings().getHeight())
                .build();

        for (ConfigItem fillerItem : config.getGuiSettings().getFillerItems()) {
            pane.add(GuiFactory.displayableBuilder(ItemStack.class)
                             .itemStack(UtilConfigItem.fromConfigItem(fillerItem))
                             .build());
        }

        setPokemon(player, pane);

        if (config.getViewPCButton().isEnabled()) {
            pane.set(config.getViewPCButton().getXPos(), config.getViewPCButton().getYPos(),
                     GuiFactory.displayableBuilder(ItemStack.class)
                             .itemStack(UtilConfigItem.fromConfigItem(config.getViewPCButton()))
                             .clickHandler((envyPlayer, clickType) -> SelectPCPokemonUI.openUI(player))
                             .build()
            );
        }

        if (config.getBackButton().isEnabled()) {
            pane.set(config.getBackButton().getXPos(), config.getBackButton().getYPos(),
                     GuiFactory.displayableBuilder(ItemStack.class)
                             .itemStack(UtilConfigItem.fromConfigItem(config.getBackButton()))
                             .clickHandler((envyPlayer, clickType) -> MainUI.open(player))
                             .build()
            );
        }

        if (config.getConfirmItem().isEnabled()) {
            pane.set(config.getConfirmItem().getXPos(), config.getConfirmItem().getYPos(),
                     GuiFactory.displayableBuilder(ItemStack.class)
                             .itemStack(UtilConfigItem.fromConfigItem(config.getConfirmItem()))
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

                                 PlayerPartyStorage party = UtilPixelmonPlayer.getParty(player.getParent());

                                 if (party.countAblePokemon() <= 1) {
                                     return;
                                 }

                                 double price = UtilPokemonPrice.getMinPrice(party.get(attribute.getSelectedSlot()));

                                 attribute.setCurrentPrice(price);
                                 attribute.setCurrentMinPrice(price);
                                 attribute.setCurrentDuration(EnvyGTSForge.getInstance().getConfig().getDefaultTradeDurationSeconds());
                                 SelectPriceUI.openUI(player, attribute.getSelectedSlot());
                             })
                             .build()
            );
        }

        GuiFactory.guiBuilder()
                .setPlayerManager(EnvyGTSForge.getInstance().getPlayerManager())
                .addPane(pane)
                .setCloseConsumer(envyPlayer -> {})
                .height(config.getGuiSettings().getHeight())
                .title(UtilChatColour.translateColourCodes('&', config.getGuiSettings().getTitle()))
                .build().open(player);
    }

    private static void setPokemon(EnvyPlayer<EntityPlayerMP> player, Pane pane) {
        PlayerPartyStorage party = UtilPixelmonPlayer.getParty(player.getParent());
        Pokemon[] all = party.getAll();
        GuiConfig.PartyPokemonConfig config = EnvyGTSForge.getInstance().getGui().getPartyPokemonUIConfig();

        for (int i = 0; i < 6; i++) {
            int pos = config.getPartySelectionPositions().get(i);

            if (i >= all.length || all[i] == null) {
                pane.set(pos % 9, pos / 9, GuiFactory.displayableBuilder(ItemStack.class)
                        .itemStack(UtilConfigItem.fromConfigItem(config.getNoPokemonItem())).build());
            } else if (all[i].hasSpecFlag("untradeable") ||
                    (!EnvyGTSForge.getInstance().getConfig().isAllowEggs() && all[i].isEgg()) ||
                    EnvyGTSForge.getInstance().getConfig().isBlackListed(all[i]) ||
                    all[i].isInRanch()) {
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
