package com.envyful.reforged.gts.forge.ui;

import com.envyful.api.config.type.ConfigItem;
import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.config.UtilConfigItem;
import com.envyful.api.forge.items.ItemBuilder;
import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.gui.pane.Pane;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.reforged.pixelmon.sprite.UtilSprite;
import com.envyful.api.reforged.pixelmon.storage.UtilPixelmonPlayer;
import com.envyful.api.time.UtilTimeFormat;
import com.envyful.reforged.gts.forge.ReforgedGTSForge;
import com.envyful.reforged.gts.forge.config.GuiConfig;
import com.envyful.reforged.gts.forge.impl.trade.type.PokemonTrade;
import com.envyful.reforged.gts.forge.player.GTSAttribute;
import com.google.common.collect.Lists;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.storage.PCBox;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class SelectPriceUI {

    public static void openUI(EnvyPlayer<EntityPlayerMP> player, int slot) {
        openUI(player, -1, slot);
    }

    public static void openUI(EnvyPlayer<EntityPlayerMP> player, int page, int slot) {
        GuiConfig.PokemonPriceConfig config = ReforgedGTSForge.getInstance().getGui().getPriceConfig();

        Pane pane = GuiFactory.paneBuilder()
                .topLeftX(0)
                .topLeftY(0)
                .width(9)
                .height(config.getGuiSettings().getHeight())
                .build();

        Pokemon pokemon = getPokemon(player, page, slot);
        GTSAttribute attribute = player.getAttribute(ReforgedGTSForge.class);

        for (ConfigItem fillerItem : config.getGuiSettings().getFillerItems()) {
            pane.add(GuiFactory.displayableBuilder(ItemStack.class)
                             .itemStack(UtilConfigItem.fromConfigItem(fillerItem)).build());
        }

        if (config.getConfirmItem().isEnabled()) {
            pane.set(config.getConfirmItem().getXPos(), config.getConfirmItem().getYPos(),
                     GuiFactory.displayableBuilder(ItemStack.class)
                             .itemStack(UtilConfigItem.fromConfigItem(config.getConfirmItem()))
                             .clickHandler((envyPlayer, clickType) -> {
                                 Pokemon pixelmon = null;

                                 if (page == -1) {
                                     PlayerPartyStorage party = UtilPixelmonPlayer.getParty(player.getParent());
                                     pixelmon = party.get(slot);
                                     party.set(slot, null);
                                 } else {
                                     PCBox box = UtilPixelmonPlayer.getPC(player.getParent()).getBox(page);
                                     pixelmon = box.get(slot);
                                     box.set(slot, null);
                                 }

                                 ReforgedGTSForge.getInstance().getTradeManager()
                                         .addTrade(player, ((PokemonTrade.Builder) PokemonTrade.builder()
                                                 .cost(attribute.getCurrentPrice())
                                                 .expiry(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(attribute.getCurrentDuration()))
                                                 .owner(player)
                                                 .content("p"))
                                                 .contents(pixelmon)
                                         .build());
                                 ((EntityPlayerMP) envyPlayer.getParent()).closeScreen();
                             })
                             .build()
            );
        }

        if (config.getBackButton().isEnabled()) {
            pane.set(config.getBackButton().getXPos(), config.getBackButton().getYPos(),
                     GuiFactory.displayableBuilder(ItemStack.class)
                             .itemStack(UtilConfigItem.fromConfigItem(config.getBackButton()))
                             .clickHandler((envyPlayer, clickType) -> {
                                 if (page == -1) {
                                     SelectPartyPokemonUI.openUI(player);
                                 } else {
                                     SelectPCPokemonUI.openUI(player, page);
                                 }
                             })
                             .build()
            );
        }

        if (config.getMinPriceItem().isEnabled()) {
            pane.set(config.getMinPriceItem().getXPos(), config.getMinPriceItem().getYPos(),
                     GuiFactory.displayableBuilder(ItemStack.class)
                             .itemStack(new ItemBuilder(UtilConfigItem.fromConfigItem(config.getMinPriceItem()))
                                     .name(formatName(attribute, config.getMinPriceItem().getName()))
                                                .lore(formatLore(attribute, config.getMinPriceItem().getLore()))
                                                .build())
                             .clickHandler((envyPlayer, clickType) -> {})
                             .build()
            );
        }

        if (config.getModifyPriceButton().isEnabled()) {
            pane.set(config.getModifyPriceButton().getXPos(), config.getModifyPriceButton().getYPos(),
                     GuiFactory.displayableBuilder(ItemStack.class)
                             .itemStack(new ItemBuilder(UtilConfigItem.fromConfigItem(config.getModifyPriceButton()))
                                                .name(formatName(attribute, config.getModifyPriceButton().getName()))
                                                .lore(formatLore(attribute, config.getModifyPriceButton().getLore()))
                                                .build())
                             .clickHandler((envyPlayer, clickType) -> EditPriceUI.openUI(player, page, slot))
                             .build()
            );
        }

        if (config.getModifyDurationButton().isEnabled()) {
            pane.set(config.getModifyDurationButton().getXPos(), config.getModifyDurationButton().getYPos(),
                     GuiFactory.displayableBuilder(ItemStack.class)
                             .itemStack(new ItemBuilder(UtilConfigItem.fromConfigItem(config.getModifyDurationButton()))
                                                .name(formatName(attribute, config.getModifyDurationButton().getName()))
                                                .lore(formatLore(attribute, config.getModifyDurationButton().getLore()))
                                                .build())
                             .clickHandler((envyPlayer, clickType) -> EditDurationUI.openUI(player, page, slot))
                             .build()
            );
        }

        int posX = config.getPokemonPosition() % 9;
        int posY = config.getPokemonPosition() / 9;

        pane.set(posX, posY, GuiFactory.displayableBuilder(ItemStack.class)
                .itemStack(UtilSprite.getPokemonElement(pokemon, config.getSpriteConfig())).build());

        GuiFactory.guiBuilder()
                .setPlayerManager(ReforgedGTSForge.getInstance().getPlayerManager())
                .addPane(pane)
                .setCloseConsumer(envyPlayer -> {})
                .height(config.getGuiSettings().getHeight())
                .title(config.getGuiSettings().getTitle())
                .build().open(player);
    }

    public static Pokemon getPokemon(EnvyPlayer<EntityPlayerMP> player, int page, int slot) {
        if (page == -1) {
            return UtilPixelmonPlayer.getParty(player.getParent()).get(slot);
        } else {
            return UtilPixelmonPlayer.getPC(player.getParent()).getBox(page).get(slot);
        }
    }

    public static List<String> formatLore(GTSAttribute attribute, List<String> lore) {
        List<String> newLore = Lists.newArrayList();

        for (String s : lore) {
            newLore.add(formatName(attribute, s));
        }

        return newLore;
    }

    public static String formatName(GTSAttribute attribute, String name) {
        return UtilChatColour.translateColourCodes('&', name
                .replace("%min_price%", attribute.getCurrentMinPrice() + "")
                .replace(
                        "%time%",
                        UtilTimeFormat.getFormattedDuration(TimeUnit.SECONDS.toMillis(attribute.getCurrentDuration())) + ""
                )
                .replace("%price%", attribute.getCurrentPrice() + "")
                .replace("%min_time%", UtilTimeFormat.getFormattedDuration(
                        TimeUnit.SECONDS.toMillis(ReforgedGTSForge.getInstance().getConfig().getMinTradeDuration())
                ))
        );
    }
}
