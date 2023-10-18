package com.envyful.gts.forge.ui;

import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.concurrency.UtilForgeConcurrency;
import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.api.reforged.dialogue.DialogueInputRegistry;
import com.envyful.api.type.UtilParse;
import com.envyful.gts.forge.EnvyGTSForge;
import com.envyful.gts.forge.player.GTSAttribute;
import net.minecraft.world.item.ItemStack;

public class EditItemPriceUI {

    public static void openUI(ForgeEnvyPlayer player, ItemStack itemStack, boolean error) {
        player.getParent().closeContainer();
        GTSAttribute attribute = player.getAttribute(GTSAttribute.class);

        UtilForgeConcurrency.runLater(() -> {
            DialogueInputRegistry.builder()
                    .title(UtilChatColour.colour(EnvyGTSForge.getLocale().getSellPriceInputDialogueTitle()))
                    .text(UtilChatColour.colour((!error ?
                            EnvyGTSForge.getLocale().getSellPriceInputDialogueText() :
                            EnvyGTSForge.getLocale().getSellPriceInputDialogueErrorText())
                            .replace("%min_price%", String.format(EnvyGTSForge.getLocale().getMoneyFormat(), attribute.getCurrentPrice()))
                            .replace("%pokemon%", itemStack.getHoverName().getString())))
                    .defaultInputValue(String.valueOf(0))
                    .closeOnEscape()
                    .closeHandler(closedScreen -> SellHandOrParty.open(player))
                    .submitHandler(submitted -> EditItemDurationUI.openUI(player, UtilParse.parseDouble(submitted.getInput()).orElse(-1.0), false))
                    .open(player.getParent());
        }, 5);
    }
}
