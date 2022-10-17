package com.envyful.gts.forge.ui;

import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.config.UtilConfigInterface;
import com.envyful.api.forge.config.UtilConfigItem;
import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.api.forge.player.util.UtilPlayer;
import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.gui.pane.Pane;
import com.envyful.api.time.UtilTimeFormat;
import com.envyful.gts.forge.EnvyGTSForge;
import com.envyful.gts.forge.config.EditItemPriceConfig;
import com.envyful.gts.forge.player.GTSAttribute;
import net.minecraft.item.ItemStack;

import java.util.concurrent.TimeUnit;

public class EditItemPriceUI {

    public static void openUI(ForgeEnvyPlayer player, ItemStack itemStack) {
        EditItemPriceConfig config = EnvyGTSForge.getInstance().getGui().getEditItemPriceUIConfig();
        GTSAttribute attribute = player.getAttribute(EnvyGTSForge.class);

        Pane pane = GuiFactory.paneBuilder()
                .topLeftX(0)
                .topLeftY(0)
                .width(9)
                .height(config.getGuiSettings().getHeight())
                .build();

        UtilConfigInterface.fillBackground(pane, config.getGuiSettings());

        UtilConfigItem.builder()
                .asyncClick(false)
                .clickHandler((envyPlayer, clickType) -> {
                    player.getParent().closeContainer();
                    UtilPlayer.runCommand(player.getParent(), "gts sell " + itemStack.getCount() + " " + attribute.getCurrentPrice());
                })
                .extendedConfigItem(player, pane, config.getConfirmItem());

        UtilConfigItem.builder()
                .extendedConfigItem(player, pane, config.getMinPriceItem(), name ->
                        name.replace("%min_price%", String.format(EnvyGTSForge.getInstance().getLocale().getMoneyFormat(),
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
                .extendedConfigItem(player, pane, config.getCurrentPriceButton(), name ->
                        name.replace("%min_price%", String.format(EnvyGTSForge.getInstance().getLocale().getMoneyFormat(),
                                        attribute.getCurrentMinPrice()))
                                .replace(
                                        "%time%",
                                        UtilTimeFormat.getFormattedDuration(TimeUnit.SECONDS.toMillis(attribute.getCurrentDuration())) + ""
                                )
                                .replace("%price%", String.format(EnvyGTSForge.getInstance().getLocale().getMoneyFormat(), attribute.getCurrentPrice()))
                                .replace("%min_time%", UtilTimeFormat.getFormattedDuration(
                                        TimeUnit.SECONDS.toMillis(EnvyGTSForge.getInstance().getConfig().getMinTradeDuration())
                                )));

        for (EditItemPriceConfig.ModifyPriceButton priceButton : config.getPriceButtons()) {
            UtilConfigItem.builder()
                    .clickHandler((envyPlayer, clickType) -> {
                        attribute.setCurrentPrice(Math.min(EnvyGTSForge.getInstance().getConfig().getMaxPrice(),
                                Math.max(
                                        attribute.getCurrentMinPrice(),
                                        attribute.getCurrentPrice() + priceButton.getPriceModifier()
                                )));
                        openUI(player, itemStack);
                    })
                    .extendedConfigItem(player, pane, priceButton.getConfigItem());
        }

        int posX = config.getItemPosition() % 9;
        int posY = config.getItemPosition() / 9;

        pane.set(posX, posY, GuiFactory.displayable(itemStack));

        GuiFactory.guiBuilder()
                .setPlayerManager(EnvyGTSForge.getInstance().getPlayerManager())
                .addPane(pane)
                .height(config.getGuiSettings().getHeight())
                .title(UtilChatColour.colour(config.getGuiSettings().getTitle()))
                .build().open(player);
    }

}
