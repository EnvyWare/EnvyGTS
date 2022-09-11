package com.envyful.gts.forge.ui;

import com.envyful.api.config.type.ConfigItem;
import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.config.UtilConfigItem;
import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.gui.pane.Pane;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.reforged.pixelmon.sprite.UtilSprite;
import com.envyful.gts.forge.EnvyGTSForge;
import com.envyful.gts.forge.config.EditPriceConfig;
import com.envyful.gts.forge.player.GTSAttribute;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

public class EditPriceUI {

    public static void openUI(EnvyPlayer<EntityPlayerMP> player, int page, int position) {
        EditPriceConfig config = EnvyGTSForge.getInstance().getGui().getEditPriceUIConfig();
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
//        if (config.getMinPriceItem().isEnabled()) {
//            pane.set(config.getMinPriceItem().getXPos(), config.getMinPriceItem().getYPos(),
//                     GuiFactory.displayableBuilder(ItemStack.class)
//                             .itemStack(new ItemBuilder(UtilConfigItem.fromConfigItem(config.getMinPriceItem()))
//                                                .name(SelectPriceUI.formatName(attribute,
//                                                                               config.getMinPriceItem().getName()))
//                                                .lore(SelectPriceUI.formatLore(attribute, config.getMinPriceItem().getLore()))
//                                                .build())
//                             .clickHandler((envyPlayer, clickType) -> {})
//                             .build()
//            );
//        }
//
//        if (config.getCurrentPriceButton().isEnabled()) {
//            pane.set(config.getCurrentPriceButton().getXPos(), config.getCurrentPriceButton().getYPos(),
//                     GuiFactory.displayableBuilder(ItemStack.class)
//                             .itemStack(new ItemBuilder(UtilConfigItem.fromConfigItem(config.getCurrentPriceButton()))
//                                                .name(SelectPriceUI.formatName(attribute,
//                                                                               config.getCurrentPriceButton().getName()))
//                                                .lore(SelectPriceUI.formatLore(attribute, config.getCurrentPriceButton().getLore()))
//                                                .build())
//                             .clickHandler((envyPlayer, clickType) -> {})
//                             .build()
//            );
//        }
//
//        for (EditPriceConfig.ModifyPriceButton priceButton : config.getPriceButtons()) {
//            pane.set(priceButton.getConfigItem().getXPos(), priceButton.getConfigItem().getYPos(),
//                     GuiFactory.displayableBuilder(ItemStack.class)
//                             .itemStack(UtilConfigItem.fromConfigItem(priceButton.getConfigItem()))
//                             .clickHandler((envyPlayer, clickType) -> {
//                                 attribute.setCurrentPrice(Math.min(EnvyGTSForge.getInstance().getConfig().getMaxPrice(),
//                                                                    Math.max(
//                                         attribute.getCurrentMinPrice(),
//                                         attribute.getCurrentPrice() + priceButton.getPriceModifier()
//                                 )));
//                                 openUI(player, page, position);
//                             })
//                             .build()
//            );
//        }

        int posX = config.getPokemonPosition() % 9;
        int posY = config.getPokemonPosition() / 9;

        pane.set(posX, posY, GuiFactory.displayableBuilder(ItemStack.class)
                .itemStack(UtilSprite.getPokemonElement(SelectPriceUI.getPokemon(player, page, position), config.getSpriteConfig())).build());

        GuiFactory.guiBuilder()
                .setPlayerManager(EnvyGTSForge.getInstance().getPlayerManager())
                .addPane(pane)
                .setCloseConsumer(envyPlayer -> {})
                .height(config.getGuiSettings().getHeight())
                .title(UtilChatColour.translateColourCodes('&', config.getGuiSettings().getTitle()))
                .build().open(player);
    }

}
