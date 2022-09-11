package com.envyful.gts.forge.ui;

import com.envyful.api.config.type.ConfigItem;
import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.config.UtilConfigItem;
import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.gui.pane.Pane;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.reforged.pixelmon.sprite.UtilSprite;
import com.envyful.gts.forge.EnvyGTSForge;
import com.envyful.gts.forge.config.EditDurationConfig;
import com.envyful.gts.forge.player.GTSAttribute;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

public class EditDurationUI {

    public static void openUI(EnvyPlayer<EntityPlayerMP> player, int page, int position) {
        EditDurationConfig config = EnvyGTSForge.getInstance().getGui().getEditDurationUIConfig();
        GTSAttribute attribute = player.getAttribute(EnvyGTSForge.class);

        Pane pane = GuiFactory.paneBuilder()
                .topLeftX(0)
                .topLeftY(0)
                .width(9)
                .height(config.getGuiSettings().getHeight())
                .build();

        for (ConfigItem fillerItem : config.getGuiSettings().getFillerItems()) {
            pane.add(GuiFactory.displayableBuilder(ItemStack.class)
                             .itemStack(UtilConfigItem.fromConfigItem(fillerItem)).build());
        }

//        if (config.getConfirmItem().isEnabled()) {
//            pane.set(config.getConfirmItem().getXPos(), config.getConfirmItem().getYPos(),
//                     GuiFactory.displayableBuilder(ItemStack.class)
//                             .itemStack(UtilConfigItem.fromConfigItem(config.getConfirmItem()))
//                             .clickHandler((envyPlayer, clickType) -> SelectPriceUI.openUI(player, page, position))
//                             .build());
//        }
//
//        if (config.getMinTimeItem().isEnabled()) {
//            pane.set(config.getMinTimeItem().getXPos(), config.getMinTimeItem().getYPos(),
//                     GuiFactory.displayableBuilder(ItemStack.class)
//                             .itemStack(new ItemBuilder(UtilConfigItem.fromConfigItem(config.getMinTimeItem()))
//                                                .name(SelectPriceUI.formatName(attribute,
//                                                                               config.getMinTimeItem().getName()))
//                                                .lore(SelectPriceUI.formatLore(attribute, config.getMinTimeItem().getLore()))
//                                                .build())
//                             .build()
//            );
//        }
//
//        if (config.getCurrentTimeButton().isEnabled()) {
//            pane.set(config.getCurrentTimeButton().getXPos(), config.getCurrentTimeButton().getYPos(),
//                     GuiFactory.displayableBuilder(ItemStack.class)
//                             .itemStack(new ItemBuilder(UtilConfigItem.fromConfigItem(config.getCurrentTimeButton()))
//                                                .name(SelectPriceUI.formatName(attribute,
//                                                                               config.getCurrentTimeButton().getName()))
//                                                .lore(SelectPriceUI.formatLore(attribute, config.getCurrentTimeButton().getLore()))
//                                                .build())
//                             .build()
//            );
//        }
//
//        for (EditDurationConfig.ModifyTimeButton timeButton : config.getTimeButtons()) {
//            pane.set(timeButton.getConfigItem().getXPos(), timeButton.getConfigItem().getYPos(),
//                     GuiFactory.displayableBuilder(ItemStack.class)
//                             .itemStack(UtilConfigItem.fromConfigItem(timeButton.getConfigItem()))
//                             .clickHandler((envyPlayer, clickType) -> {
//                                 long newDuration = attribute.getCurrentDuration() + timeButton.getTimeModifier();
//
//                                 if (newDuration < EnvyGTSForge.getInstance().getConfig().getMinTradeDuration()) {
//                                     newDuration = EnvyGTSForge.getInstance().getConfig().getMinTradeDuration();
//                                 } else if (newDuration > EnvyGTSForge.getInstance().getConfig().getMaxTradeDurationSeconds()) {
//                                     newDuration = EnvyGTSForge.getInstance().getConfig().getMaxTradeDurationSeconds();
//                                 }
//
//                                 attribute.setCurrentDuration(newDuration);
//                                 openUI(player, page, position);
//                             })
//                             .build()
//            );
//        }

        int posX = config.getPokemonPosition() % 9;
        int posY = config.getPokemonPosition() / 9;

        pane.set(posX, posY, GuiFactory.displayableBuilder(ItemStack.class)
                .itemStack(UtilSprite.getPokemonElement(SelectPriceUI.getPokemon(player, page, position),
                                                        config.getSpriteConfig())).build());

        GuiFactory.guiBuilder()
                .setPlayerManager(EnvyGTSForge.getInstance().getPlayerManager())
                .addPane(pane)
                .setCloseConsumer(envyPlayer -> {})
                .height(config.getGuiSettings().getHeight())
                .title(UtilChatColour.translateColourCodes('&', config.getGuiSettings().getTitle()))
                .build().open(player);
    }

}
