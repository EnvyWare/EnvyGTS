package com.envyful.gts.forge.ui;

import com.envyful.api.neoforge.chat.UtilChatColour;
import com.envyful.api.neoforge.concurrency.UtilForgeConcurrency;
import com.envyful.api.neoforge.player.ForgeEnvyPlayer;
import com.envyful.api.neoforge.player.util.UtilPlayer;
import com.envyful.api.time.UtilTime;
import com.envyful.api.time.UtilTimeFormat;
import com.envyful.gts.forge.EnvyGTSForge;
import com.envyful.gts.forge.api.player.GTSAttribute;
import com.pixelmonmod.pixelmon.api.dialogue.DialogueButton;
import com.pixelmonmod.pixelmon.api.dialogue.DialogueFactory;
import net.minecraft.world.InteractionHand;

import java.util.concurrent.TimeUnit;

public class EditItemDurationUI {

    public static void openUI(ForgeEnvyPlayer player, double time, boolean error) {
        player.getParent().closeContainer();
        var attribute = player.getAttributeNow(GTSAttribute.class);
        var itemInHand = player.getParent().getItemInHand(InteractionHand.MAIN_HAND);

        UtilForgeConcurrency.runLater(() -> DialogueFactory.builder()
                .title(UtilChatColour.colour(EnvyGTSForge.getLocale().getDurationInputDialogueTitle()))
                .description(UtilChatColour.colour((!error ?
                        EnvyGTSForge.getLocale().getDurationInputDialogueText() :
                        EnvyGTSForge.getLocale().getDurationInputDialogueErrorText())
                        .replace("%min_price%", String.format(EnvyGTSForge.getLocale().getMoneyFormat(), attribute.getCurrentPrice()))
                        .replace("%min_duration%", UtilTimeFormat.getFormattedDuration(
                                TimeUnit.SECONDS.toMillis(EnvyGTSForge.getConfig().getMinTradeDuration())
                        ))
                        .replace("%max_duration%", UtilTimeFormat.getFormattedDuration(
                                TimeUnit.SECONDS.toMillis(EnvyGTSForge.getConfig().getMinTradeDuration())
                        ))
                        .replace("%pokemon%", itemInHand.getHoverName().getString())))
                .showInput()
                .defaultText(TimeUnit.SECONDS.toMinutes(EnvyGTSForge.getConfig().getMinTradeDuration()) + "m")
                .closeOnEscape()
                .onClose(closedScreen -> SellHandOrParty.open(player))
                .buttons(DialogueButton.builder()
                        .text("Submit")
                        .onClick(submitted -> {
                            long inputtedValue = UtilTime.attemptParseTime(submitted.getInput()).orElse(-1L);

                            if (inputtedValue < TimeUnit.SECONDS.toMillis(EnvyGTSForge.getConfig().getMinTradeDuration()) || inputtedValue < 0) {
                                openUI(player, time, true);
                                return;
                            }

                            if (inputtedValue > TimeUnit.SECONDS.toMillis(EnvyGTSForge.getConfig().getMaxTradeDurationSeconds())) {
                                openUI(player, time, true);
                                return;
                            }

                            UtilPlayer.runCommand(player.getParent(), "gts sell " + itemInHand.getCount() + " " + time + " " + submitted.getInput());
                        })
                        .build())
                .sendTo(player.getParent()), 5);
    }

}
