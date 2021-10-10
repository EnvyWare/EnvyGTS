package com.envyful.reforged.gts.forge.ui;

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
import com.envyful.reforged.gts.api.Trade;
import com.envyful.reforged.gts.forge.ReforgedGTSForge;
import com.envyful.reforged.gts.forge.config.GuiConfig;
import com.envyful.reforged.gts.forge.player.GTSAttribute;
import com.envyful.reforged.gts.forge.util.UtilPokemonPrice;
import com.google.common.collect.Lists;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.storage.PCBox;
import com.pixelmonmod.pixelmon.api.storage.PCStorage;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;

import java.util.List;

public class SelectPCPokemonUI {

    public static void openUI(EnvyPlayer<EntityPlayerMP> player) {
        openUI(player, 0);
    }

    public static void openUI(EnvyPlayer<EntityPlayerMP> player, int page) {
        GuiConfig.SelectFromPCConfig config = ReforgedGTSForge.getInstance().getGui().getPcConfig();

        ((GTSAttribute) player.getAttribute(ReforgedGTSForge.class)).setSelectedSlot(-1);

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

        PCStorage pc = UtilPixelmonPlayer.getPC(player.getParent());

        setPokemon(player, page, pane);

        if (config.getConfirmButton().isEnabled()) {
            pane.set(config.getConfirmButton().getXPos(), config.getConfirmButton().getYPos(),
                     GuiFactory.displayableBuilder(ItemStack.class)
                             .itemStack(UtilConfigItem.fromConfigItem(config.getConfirmButton()))
                             .clickHandler((envyPlayer, clickType) -> {})
                             .build()
            );
        }

        if (config.getBackButton().isEnabled()) {
            pane.set(config.getBackButton().getXPos(), config.getBackButton().getYPos(),
                     GuiFactory.displayableBuilder(ItemStack.class)
                             .itemStack(UtilConfigItem.fromConfigItem(config.getBackButton()))
                             .clickHandler((envyPlayer, clickType) -> SelectPartyPokemonUI.openUI(player))
                             .build()
            );
        }

        if (config.getNextPageButton().isEnabled()) {
            pane.set(config.getNextPageButton().getXPos(), config.getNextPageButton().getYPos(),
                     GuiFactory.displayableBuilder(ItemStack.class)
                             .itemStack(UtilConfigItem.fromConfigItem(config.getNextPageButton()))
                             .clickHandler((envyPlayer, clickType) -> {
                                 if (page >= pc.getLastBox()) {
                                     openUI(player, 0);
                                 } else {
                                     openUI(player, page + 1);
                                 }
                             })
                             .build()
            );
        }

        if (config.getPreviousPageButton().isEnabled()) {
            pane.set(config.getPreviousPageButton().getXPos(), config.getPreviousPageButton().getYPos(),
                     GuiFactory.displayableBuilder(ItemStack.class)
                             .itemStack(UtilConfigItem.fromConfigItem(config.getPreviousPageButton()))
                             .clickHandler((envyPlayer, clickType) -> {
                                 if (page <= 0) {
                                     openUI(player, pc.getLastBox());
                                 } else {
                                     openUI(player, page - 1);
                                 }
                             })
                             .build()
            );
        }

        if (config.getConfirmButton().isEnabled()) {
            pane.set(config.getConfirmButton().getXPos(), config.getConfirmButton().getYPos(),
                     GuiFactory.displayableBuilder(ItemStack.class)
                    .itemStack(UtilConfigItem.fromConfigItem(config.getConfirmButton()))
                    .clickHandler((envyPlayer, clickType) -> {
                        GTSAttribute attribute = envyPlayer.getAttribute(ReforgedGTSForge.class);

                        if (attribute.getSelectedSlot() == -1) {
                            return;
                        }

                        List<Trade> trades = Lists.newArrayList(attribute.getOwnedTrades());

                        trades.removeIf(trade -> trade.hasExpired() || trade.wasPurchased() || trade.wasRemoved());

                        if (trades.size() >= ReforgedGTSForge.getInstance().getConfig().getMaxListingsPerUser()) {
                            player.message(UtilChatColour.translateColourCodes(
                                    '&',
                                    ReforgedGTSForge.getInstance().getLocale().getMessages().getMaxTradesAlreadyReached()
                            ));
                            return;
                        }

                        double price = UtilPokemonPrice.getMinPrice(pc.getBox(page).get(attribute.getSelectedSlot()));

                        attribute.setCurrentPrice(price);
                        attribute.setCurrentMinPrice(price);
                        attribute.setCurrentDuration(ReforgedGTSForge.getInstance().getConfig().getDefaultTradeDurationSeconds());
                        SelectPriceUI.openUI(player, page, attribute.getSelectedSlot());
                    })
                    .build());
        }

        GuiFactory.guiBuilder()
                .setPlayerManager(ReforgedGTSForge.getInstance().getPlayerManager())
                .addPane(pane)
                .setCloseConsumer(envyPlayer -> {})
                .height(config.getGuiSettings().getHeight())
                .title(UtilChatColour.translateColourCodes('&', config.getGuiSettings().getTitle()))
                .build().open(player);
    }

    private static void setPokemon(EnvyPlayer<EntityPlayerMP> player, int page, Pane pane) {
        PCStorage pc = UtilPixelmonPlayer.getPC(player.getParent());
        PCBox box = pc.getBox(page);
        GuiConfig.SelectFromPCConfig config = ReforgedGTSForge.getInstance().getGui().getPcConfig();

        for (int i = 0; i < config.getPerPage(); i++) {
            int posX = i % 5;
            int posY = i / 5;
            Pokemon pokemon = box.get(i);

            if (pokemon == null) {
                pane.set(2 + posX, posY, GuiFactory.displayableBuilder(ItemStack.class)
                        .itemStack(UtilConfigItem.fromConfigItem(config.getNoPokemonItem())).build());
            } else if (pokemon.hasSpecFlag("untradeable") || pokemon.isEgg() || ReforgedGTSForge.getInstance().getConfig().getBlacklist().contains(pokemon.getSpecies())) {
                pane.set(2 + posX, posY, GuiFactory.displayableBuilder(ItemStack.class)
                        .itemStack(UtilConfigItem.fromConfigItem(config.getUntradeablePokemonItem())).build());
            } else {
                final int slot = i;
                pane.set(2 + posX, posY, GuiFactory.displayableBuilder(ItemStack.class)
                        .itemStack(UtilSprite.getPokemonElement(
                                pokemon,
                                ReforgedGTSForge.getInstance().getGui().getPcConfig().getSpriteConfig()
                        ))
                        .clickHandler((envyPlayer, clickType) -> {
                            GTSAttribute attribute = envyPlayer.getAttribute(ReforgedGTSForge.class);
                            attribute.setSelectedSlot(slot);
                            pane.set(config.getConfirmSlot() % 9, config.getConfirmSlot() / 9,
                                     GuiFactory.displayableBuilder(ItemStack.class)
                                             .itemStack(new ItemBuilder(UtilSprite.getPokemonElement(
                                                     box.get(slot),
                                                     ReforgedGTSForge.getInstance().getGui().getPartyPokemonUIConfig().getSpriteConfig()
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
