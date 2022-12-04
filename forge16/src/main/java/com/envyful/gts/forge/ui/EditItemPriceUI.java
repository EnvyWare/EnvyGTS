package com.envyful.gts.forge.ui;

import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.concurrency.UtilForgeConcurrency;
import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.api.forge.player.util.UtilPlayer;
import com.envyful.api.reforged.dialogue.DialogueInputRegistry;
import com.envyful.api.type.UtilParse;
import com.envyful.gts.forge.EnvyGTSForge;
import com.envyful.gts.forge.player.GTSAttribute;
import net.minecraft.item.ItemStack;

public class EditItemPriceUI {

    public static void openUI(ForgeEnvyPlayer player, ItemStack itemStack, boolean error) {
        player.getParent().closeContainer();
        GTSAttribute attribute = player.getAttribute(EnvyGTSForge.class);

        UtilForgeConcurrency.runLater(() -> {
            DialogueInputRegistry.builder()
                    .title(UtilChatColour.colour(EnvyGTSForge.getInstance().getLocale().getSellPriceInputDialogueTitle()))
                    .text(UtilChatColour.colour((!error ?
                            EnvyGTSForge.getInstance().getLocale().getSellPriceInputDialogueText() :
                            EnvyGTSForge.getInstance().getLocale().getSellPriceInputDialogueErrorText())
                            .replace("%min_price%", String.format(EnvyGTSForge.getInstance().getLocale().getMoneyFormat(), attribute.getCurrentPrice()))
                            .replace("%pokemon%", itemStack.getHoverName().getString())))
                    .defaultInputValue(String.valueOf(0))
                    .closeOnEscape()
                    .closeHandler(closedScreen -> SellHandOrParty.open(player))
                    .submitHandler(submitted ->
                        UtilPlayer.runCommand(player.getParent(), "gts sell " + itemStack.getCount() + " " +
                                UtilParse.parseDouble(submitted.getInput()).orElse(-1.0)))
                    .open(player.getParent());
        }, 5);
    }

}
