package com.envyful.gts.forge.ui;

import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.concurrency.UtilForgeConcurrency;
import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.api.type.UtilParse;
import com.envyful.gts.forge.EnvyGTSForge;
import com.envyful.gts.forge.player.GTSAttribute;
import com.pixelmonmod.pixelmon.api.dialogue.DialogueButton;
import com.pixelmonmod.pixelmon.api.dialogue.DialogueFactory;
import com.pixelmonmod.pixelmon.api.dialogue.InputPattern;
import net.minecraft.world.item.ItemStack;

import java.awt.*;

public class EditItemPriceUI {

    public static void openUI(ForgeEnvyPlayer player, ItemStack itemStack, boolean error) {
        player.getParent().closeContainer();
        GTSAttribute attribute = player.getAttributeNow(GTSAttribute.class);

        UtilForgeConcurrency.runLater(() -> {
            DialogueFactory.builder()
                    .title(UtilChatColour.colour(EnvyGTSForge.getLocale().getSellPriceInputDialogueTitle()))
                    .description(UtilChatColour.colour((!error ?
                            EnvyGTSForge.getLocale().getSellPriceInputDialogueText() :
                            EnvyGTSForge.getLocale().getSellPriceInputDialogueErrorText())
                            .replace("%min_price%", String.format(EnvyGTSForge.getLocale().getMoneyFormat(), attribute.getCurrentPrice()))
                            .replace("%pokemon%", itemStack.getHoverName().getString())))
                    .closeOnEscape()
                    .onClose(closedScreen -> SellHandOrParty.open(player))
                    .buttons(DialogueButton.builder()
                            .acceptedInputs(InputPattern.of("[0-9]+", UtilChatColour.colour(EnvyGTSForge.getLocale().getSellPriceInputDialogueErrorText())))
                            .text("0")
                            .backgroundColor(Color.GRAY)
                            .onClick(submitted -> EditItemDurationUI.openUI(player, UtilParse.parseDouble(submitted.getInput()).orElse(0.0), false))
                            .build())
                    .sendTo(player.getParent());
        }, 5);
    }
}
