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
import com.pixelmonmod.pixelmon.api.storage.PCBox;
import com.pixelmonmod.pixelmon.api.storage.PCStorage;
import com.pixelmonmod.pixelmon.api.storage.StorageProxy;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.List;

public class SelectPCPokemonUI {

    public static void openUI(ForgeEnvyPlayer player) {
        openUI(player, 0);
    }

    public static void openUI(ForgeEnvyPlayer player, int page) {
        GuiConfig.SelectFromPCConfig config = EnvyGTSForge.getGui().getPcConfig();

        player.getAttributeNow(GTSAttribute.class).setSelectedSlot(-1);

        Pane pane = GuiFactory.paneBuilder()
                .topLeftX(0).topLeftY(0)
                .width(9)
                .height(config.getGuiSettings().getHeight())
                .build();

        UtilConfigInterface.fillBackground(pane, config.getGuiSettings());

        PCStorage pc = StorageProxy.getPCForPlayerNow(player.getParent());

        setPokemon(player, page, pane);

        UtilConfigItem.builder().extendedConfigItem(player, pane, config.getConfirmButton());
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

        UtilConfigItem.builder()
                .asyncClick(false)
                .clickHandler((envyPlayer, clickType) -> {
                    GTSAttribute attribute = envyPlayer.getAttributeNow(GTSAttribute.class);

                    if (attribute.getSelectedSlot() == -1) {
                        return;
                    }

                    List<Trade> trades = Lists.newArrayList(attribute.getOwnedTrades());

                    trades.removeIf(trade -> trade.hasExpired() || trade.wasPurchased() || trade.wasRemoved());

                    if (trades.size() >= EnvyGTSForge.getConfig().getMaxListingsPerUser()) {
                        player.message(UtilChatColour.colour(
                                EnvyGTSForge.getLocale().getMessages().getMaxTradesAlreadyReached()
                        ));
                        return;
                    }

                    double price = UtilPokemonPrice.getMinPrice(pc.getBox(page).get(attribute.getSelectedSlot()));

                    attribute.setCurrentPrice(price);
                    attribute.setCurrentMinPrice(price);
                    attribute.setCurrentDuration(EnvyGTSForge.getConfig().getDefaultTradeDurationSeconds());
                    SelectPriceUI.openUI(player, page, attribute.getSelectedSlot(), false);
                })
                .extendedConfigItem(player, pane, config.getConfirmButton());

        GuiFactory.guiBuilder()
                .setPlayerManager(EnvyGTSForge.getPlayerManager())
                .addPane(pane)
                .height(config.getGuiSettings().getHeight())
                .title(UtilChatColour.colour(config.getGuiSettings().getTitle()))
                .build().open(player);
    }

    private static void setPokemon(ForgeEnvyPlayer player, int page, Pane pane) {
        PCStorage pc = StorageProxy.getPCForPlayerNow(player.getParent());
        PCBox box = pc.getBox(page);
        GuiConfig.SelectFromPCConfig config = EnvyGTSForge.getGui().getPcConfig();

        for (int i = 0; i < config.getPerPage(); i++) {
            int posX = i % 5;
            int posY = i / 5;
            Pokemon pokemon = box.get(i);

            if (pokemon == null) {
                pane.set(2 + posX, posY, GuiFactory.displayable((UtilConfigItem.fromConfigItem(config.getNoPokemonItem()))));
            } else if (pokemon.isUntradeable() ||
                    (!EnvyGTSForge.getConfig().isAllowEggs() && pokemon.isEgg()) ||
                    EnvyGTSForge.getConfig().isBlackListed(pokemon)) {
                pane.set(2 + posX, posY, GuiFactory.displayable(UtilConfigItem.fromConfigItem(config.getUntradeablePokemonItem())));
            } else {
                final int slot = i;
                pane.set(2 + posX, posY, GuiFactory.displayableBuilder(ItemStack.class)
                        .itemStack(UtilSprite.getPokemonElement(
                                pokemon,
                                EnvyGTSForge.getGui().getPcConfig().getSpriteConfig()
                        ))
                        .clickHandler((envyPlayer, clickType) -> {
                            GTSAttribute attribute = ((ForgeEnvyPlayer) envyPlayer).getAttributeNow(GTSAttribute.class);
                            attribute.setSelectedSlot(slot);
                            pane.set(config.getConfirmSlot() % 9, config.getConfirmSlot() / 9,
                                    GuiFactory.displayableBuilder(ItemStack.class)
                                            .itemStack(new ItemBuilder(UtilSprite.getPokemonElement(
                                                    box.get(slot),
                                                    EnvyGTSForge.getGui().getPartyPokemonUIConfig().getSpriteConfig()
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
