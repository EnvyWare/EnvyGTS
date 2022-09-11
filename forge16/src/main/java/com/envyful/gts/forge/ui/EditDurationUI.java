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
import com.envyful.gts.forge.config.EditDurationConfig;
import com.envyful.gts.forge.player.GTSAttribute;

import java.util.concurrent.TimeUnit;

public class EditDurationUI {

    public static void openUI(ForgeEnvyPlayer player, int page, int position) {
        EditDurationConfig config = EnvyGTSForge.getInstance().getGui().getEditDurationUIConfig();
        GTSAttribute attribute = player.getAttribute(EnvyGTSForge.class);

        Pane pane = GuiFactory.paneBuilder()
                .topLeftX(0)
                .topLeftY(0)
                .width(9)
                .height(config.getGuiSettings().getHeight())
                .build();

        UtilConfigInterface.fillBackground(pane, config.getGuiSettings());

        UtilConfigItem.builder()
                .clickHandler((envyPlayer, clickType) -> SelectPriceUI.openUI(player, page, position))
                .extendedConfigItem(player, pane, config.getConfirmItem());

        UtilConfigItem.builder()
                .extendedConfigItem(player, pane, config.getMinTimeItem(), name -> name
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
                .extendedConfigItem(player, pane, config.getCurrentTimeButton(), name -> name
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

        for (EditDurationConfig.ModifyTimeButton timeButton : config.getTimeButtons()) {
            UtilConfigItem.builder()
                    .clickHandler((envyPlayer, clickType) -> {
                        long newDuration = attribute.getCurrentDuration() + timeButton.getTimeModifier();

                        if (newDuration < EnvyGTSForge.getInstance().getConfig().getMinTradeDuration()) {
                            newDuration = EnvyGTSForge.getInstance().getConfig().getMinTradeDuration();
                        } else if (newDuration > EnvyGTSForge.getInstance().getConfig().getMaxTradeDurationSeconds()) {
                            newDuration = EnvyGTSForge.getInstance().getConfig().getMaxTradeDurationSeconds();
                        }

                        attribute.setCurrentDuration(newDuration);
                        openUI(player, page, position);
                    })
                    .extendedConfigItem(player, pane, timeButton.getConfigItem());
        }

        int posX = config.getPokemonPosition() % 9;
        int posY = config.getPokemonPosition() / 9;

        pane.set(posX, posY, GuiFactory.displayable(UtilSprite.getPokemonElement(SelectPriceUI.getPokemon(player, page, position), config.getSpriteConfig())));

        GuiFactory.guiBuilder()
                .setPlayerManager(EnvyGTSForge.getInstance().getPlayerManager())
                .addPane(pane)
                .height(config.getGuiSettings().getHeight())
                .title(UtilChatColour.translateColourCodes('&', config.getGuiSettings().getTitle()))
                .build().open(player);
    }

}
