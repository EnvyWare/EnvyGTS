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
import com.envyful.reforged.gts.forge.config.ReforgedGTSConfig;
import com.envyful.reforged.gts.forge.player.GTSAttribute;
import com.google.common.collect.Lists;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class SelectPriceUI {

    public static void openUI(EnvyPlayer<EntityPlayerMP> player, int slot) {
        openUI(player, -1, slot);
    }

    public static void openUI(EnvyPlayer<EntityPlayerMP> player, int page, int slot) {
        ReforgedGTSConfig.PokemonPriceConfig config = ReforgedGTSForge.getInstance().getConfig().getPriceConfig();

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
                             .clickHandler((envyPlayer, clickType) -> {})
                             .build()
            );
        }

        if (config.getMinPriceItem().isEnabled()) {
            pane.set(config.getMinPriceItem().getXPos(), config.getMinPriceItem().getYPos(),
                     GuiFactory.displayableBuilder(ItemStack.class)
                             .itemStack(new ItemBuilder(UtilConfigItem.fromConfigItem(config.getMinPriceItem()))
                                                .lore(formatLore(attribute, pokemon, config.getMinPriceItem().getLore()))
                                                .build())
                             .clickHandler((envyPlayer, clickType) -> {})
                             .build()
            );
        }

        if (config.getModifyPriceButton().isEnabled()) {
            pane.set(config.getModifyPriceButton().getXPos(), config.getModifyPriceButton().getYPos(),
                     GuiFactory.displayableBuilder(ItemStack.class)
                             .itemStack(UtilConfigItem.fromConfigItem(config.getModifyPriceButton()))
                             .clickHandler((envyPlayer, clickType) -> {})
                             .build()
            );
        }

        if (config.getModifyDurationButton().isEnabled()) {
            pane.set(config.getModifyDurationButton().getXPos(), config.getModifyDurationButton().getYPos(),
                     GuiFactory.displayableBuilder(ItemStack.class)
                             .itemStack(new ItemBuilder(UtilConfigItem.fromConfigItem(config.getModifyDurationButton()))
                                                .lore(formatLore(attribute, pokemon,
                                                                 config.getMinPriceItem().getLore()))
                                                .build())
                             .clickHandler((envyPlayer, clickType) -> {})
                             .build()
            );
        }

        int posX = config.getPokemonPosition() % 9;
        int posY = config.getPokemonPosition() / 9;

        pane.set(posX, posY, GuiFactory.displayableBuilder(ItemStack.class)
                .itemStack(UtilSprite.getPokemonElement(pokemon)).build());

        GuiFactory.guiBuilder()
                .setPlayerManager(ReforgedGTSForge.getInstance().getPlayerManager())
                .addPane(pane)
                .setCloseConsumer(envyPlayer -> {})
                .height(config.getGuiSettings().getHeight())
                .title(config.getGuiSettings().getTitle())
                .build().open(player);
    }

    private static Pokemon getPokemon(EnvyPlayer<EntityPlayerMP> player, int page, int slot) {
        if (page == -1) {
            return UtilPixelmonPlayer.getParty(player.getParent()).get(slot);
        } else {
            return UtilPixelmonPlayer.getPC(player.getParent()).getBox(page).get(slot);
        }
    }

    private static List<String> formatLore(GTSAttribute attribute, Pokemon pokemon, List<String> lore) {
        List<String> newLore = Lists.newArrayList();

        for (String s : lore) {
            newLore.add(UtilChatColour.translateColourCodes('&', s
                    .replace("%min_price%", attribute.getCurrentMinPrice() + "")
                    .replace("%duration%",
                             UtilTimeFormat.getFormattedDuration(TimeUnit.SECONDS.toMillis(attribute.getCurrentDuration())) + "")
            ));
        }

        return newLore;
    }
}
