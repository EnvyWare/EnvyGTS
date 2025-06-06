package com.envyful.gts.forge.ui;

import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.gui.pane.Pane;
import com.envyful.api.neoforge.chat.UtilChatColour;
import com.envyful.api.neoforge.config.UtilConfigItem;
import com.envyful.api.neoforge.items.ItemBuilder;
import com.envyful.api.neoforge.items.ItemFlag;
import com.envyful.api.neoforge.player.ForgeEnvyPlayer;
import com.envyful.api.reforged.pixelmon.sprite.UtilSprite;
import com.envyful.api.text.Placeholder;
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
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.List;

public class SelectPCPokemonUI {

    public static void openUI(ForgeEnvyPlayer player) {
        openUI(player, 0);
    }

    public static void openUI(ForgeEnvyPlayer player, int page) {
        GuiConfig.SelectFromPCConfig config = EnvyGTSForge.getGui().getPcConfig();

        (player.getAttributeNow(GTSAttribute.class)).setSelectedSlot(-1);

        var pane = config.getGuiSettings().toPane();

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
                    GTSAttribute attribute = player.getAttributeNow(GTSAttribute.class);

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

        pane.open(player, config.getGuiSettings());
    }

    private static void setPokemon(ForgeEnvyPlayer player, int page, Pane pane) {
        PCStorage pc = StorageProxy.getPCForPlayerNow(player.getParent());
        PCBox box = pc.getBox(page);
        GuiConfig.SelectFromPCConfig config = EnvyGTSForge.getGui().getPcConfig();
        var enchants = ServerLifecycleHooks.getCurrentServer().registryAccess().registryOrThrow(Registries.ENCHANTMENT);

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
                                EnvyGTSForge.getGui().getSpriteConfig(),
                                Placeholder.empty("%below_lore_data%")
                        ))
                        .clickHandler((envyPlayer, clickType) -> {
                            GTSAttribute attribute = player.getAttributeNow(GTSAttribute.class);
                            attribute.setSelectedSlot(slot);
                            pane.set(config.getConfirmSlot() % 9, config.getConfirmSlot() / 9,
                                    GuiFactory.displayableBuilder(ItemStack.class)
                                            .itemStack(new ItemBuilder(UtilSprite.getPokemonElement(
                                                    box.get(slot),
                                                    EnvyGTSForge.getGui().getSpriteConfig(),
                                                    Placeholder.empty("%below_lore_data%")
                                            ))
                                                    .enchant(enchants.getHolder(Enchantments.UNBREAKING).orElseThrow(), 1)
                                                    .itemFlag(ItemFlag.HIDE_ENCHANTS)
                                                    .build())
                                            .build()
                            );
                        }).build());
            }
        }
    }
}
