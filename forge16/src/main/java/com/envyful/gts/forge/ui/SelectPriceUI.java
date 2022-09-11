package com.envyful.gts.forge.ui;

import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.config.UtilConfigInterface;
import com.envyful.api.forge.config.UtilConfigItem;
import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.gui.pane.Pane;
import com.envyful.api.reforged.pixelmon.sprite.UtilSprite;
import com.envyful.api.time.UtilTimeFormat;
import com.envyful.gts.forge.EnvyGTSForge;
import com.envyful.gts.forge.config.GuiConfig;
import com.envyful.gts.forge.impl.trade.type.PokemonTrade;
import com.envyful.gts.forge.player.GTSAttribute;
import com.google.common.collect.Lists;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.storage.PCBox;
import com.pixelmonmod.pixelmon.api.storage.PlayerPartyStorage;
import com.pixelmonmod.pixelmon.api.storage.StorageProxy;
import net.minecraft.util.text.ITextComponent;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class SelectPriceUI {

    public static void openUI(ForgeEnvyPlayer player, int slot) {
        openUI(player, -1, slot);
    }

    public static void openUI(ForgeEnvyPlayer player, int page, int slot) {
        GuiConfig.PokemonPriceConfig config = EnvyGTSForge.getInstance().getGui().getPriceConfig();

        Pane pane = GuiFactory.paneBuilder()
                .topLeftX(0)
                .topLeftY(0)
                .width(9)
                .height(config.getGuiSettings().getHeight())
                .build();

        Pokemon pokemon = getPokemon(player, page, slot);
        GTSAttribute attribute = player.getAttribute(EnvyGTSForge.class);

        UtilConfigInterface.fillBackground(pane, config.getGuiSettings());

        UtilConfigItem.builder()
                .clickHandler((envyPlayer, clickType) -> {
                    Pokemon pixelmon = null;

                    if (page == -1) {
                        PlayerPartyStorage party = StorageProxy.getParty(player.getParent());
                        pixelmon = party.get(slot);
                        party.set(slot, null);
                    } else {
                        PCBox box = StorageProxy.getPCForPlayer(player.getParent()).getBox(page);
                        pixelmon = box.get(slot);
                        box.set(slot, null);
                    }

                    EnvyGTSForge.getInstance().getTradeManager()
                            .addTrade(player, ((PokemonTrade.Builder) PokemonTrade.builder()
                                    .cost(attribute.getCurrentPrice())
                                    .expiry(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(attribute.getCurrentDuration()))
                                    .owner(player)
                                    .originalOwnerName(player.getName())
                                    .content("p"))
                                    .contents(pixelmon)
                                    .build());

                    player.getParent().closeContainer();
                })
                .extendedConfigItem(player, pane, config.getConfirmItem());

        UtilConfigItem.builder()
                .clickHandler((envyPlayer, clickType) -> {
                    if (page == -1) {
                        SelectPartyPokemonUI.openUI(player);
                    } else {
                        SelectPCPokemonUI.openUI(player, page);
                    }
                })
                .extendedConfigItem(player, pane, config.getBackButton());

        UtilConfigItem.builder()
                .extendedConfigItem(player, pane, config.getMinPriceItem(), name -> name
                        .replace("%min_price%", String.format(EnvyGTSForge.getInstance().getLocale().getMoneyFormat(),
                                attribute.getCurrentMinPrice()))
                        .replace(
                                "%time%",
                                UtilTimeFormat.getFormattedDuration(TimeUnit.SECONDS.toMillis(attribute.getCurrentDuration())) + ""
                        )
                        .replace("%price%", String.format(EnvyGTSForge.getInstance().getLocale().getMoneyFormat(), attribute.getCurrentPrice()))
                        .replace("%min_time%", UtilTimeFormat.getFormattedDuration(
                                TimeUnit.SECONDS.toMillis(EnvyGTSForge.getInstance().getConfig().getMinTradeDuration())
                        )));

        UtilConfigItem.builder()
                .clickHandler((envyPlayer, clickType) -> EditPriceUI.openUI(player, page, slot))
                .extendedConfigItem(player, pane, config.getModifyPriceButton(), name -> name
                        .replace("%min_price%", String.format(EnvyGTSForge.getInstance().getLocale().getMoneyFormat(),
                                attribute.getCurrentMinPrice()))
                        .replace(
                                "%time%",
                                UtilTimeFormat.getFormattedDuration(TimeUnit.SECONDS.toMillis(attribute.getCurrentDuration())) + ""
                        )
                        .replace("%price%", String.format(EnvyGTSForge.getInstance().getLocale().getMoneyFormat(), attribute.getCurrentPrice()))
                        .replace("%min_time%", UtilTimeFormat.getFormattedDuration(
                                TimeUnit.SECONDS.toMillis(EnvyGTSForge.getInstance().getConfig().getMinTradeDuration())
                        )));

        UtilConfigItem.builder()
                .clickHandler((envyPlayer, clickType) -> EditDurationUI.openUI(player, page, slot))
                .extendedConfigItem(player, pane, config.getModifyDurationButton(), name -> name
                        .replace("%min_price%", String.format(EnvyGTSForge.getInstance().getLocale().getMoneyFormat(),
                                attribute.getCurrentMinPrice()))
                        .replace(
                                "%time%",
                                UtilTimeFormat.getFormattedDuration(TimeUnit.SECONDS.toMillis(attribute.getCurrentDuration())) + ""
                        )
                        .replace("%price%", String.format(EnvyGTSForge.getInstance().getLocale().getMoneyFormat(), attribute.getCurrentPrice()))
                        .replace("%min_time%", UtilTimeFormat.getFormattedDuration(
                                TimeUnit.SECONDS.toMillis(EnvyGTSForge.getInstance().getConfig().getMinTradeDuration())
                        )));

        int posX = config.getPokemonPosition() % 9;
        int posY = config.getPokemonPosition() / 9;

        pane.set(posX, posY, GuiFactory.displayable(UtilSprite.getPokemonElement(pokemon, config.getSpriteConfig())));

        GuiFactory.guiBuilder()
                .setPlayerManager(EnvyGTSForge.getInstance().getPlayerManager())
                .addPane(pane)
                .height(config.getGuiSettings().getHeight())
                .title(UtilChatColour.colour(config.getGuiSettings().getTitle()))
                .build().open(player);
    }

    public static Pokemon getPokemon(ForgeEnvyPlayer player, int page, int slot) {
        if (page == -1) {
            return StorageProxy.getParty(player.getParent()).get(slot);
        } else {
            return StorageProxy.getPCForPlayer(player.getParent()).getBox(page).get(slot);
        }
    }

    public static List<ITextComponent> formatLore(GTSAttribute attribute, List<String> lore) {
        List<ITextComponent> newLore = Lists.newArrayList();

        for (String s : lore) {
            newLore.add(formatName(attribute, s));
        }

        return newLore;
    }

    public static ITextComponent formatName(GTSAttribute attribute, String name) {
        return UtilChatColour.colour(name
                .replace("%min_price%", String.format(EnvyGTSForge.getInstance().getLocale().getMoneyFormat(),
                                                      attribute.getCurrentMinPrice()))
                .replace(
                        "%time%",
                        UtilTimeFormat.getFormattedDuration(TimeUnit.SECONDS.toMillis(attribute.getCurrentDuration())) + ""
                )
                .replace("%price%", String.format(EnvyGTSForge.getInstance().getLocale().getMoneyFormat(), attribute.getCurrentPrice()))
                .replace("%min_time%", UtilTimeFormat.getFormattedDuration(
                        TimeUnit.SECONDS.toMillis(EnvyGTSForge.getInstance().getConfig().getMinTradeDuration())
                ))
        );
    }
}
