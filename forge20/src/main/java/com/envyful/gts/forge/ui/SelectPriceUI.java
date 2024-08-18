package com.envyful.gts.forge.ui;

import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.api.type.UtilParse;
import com.envyful.gts.forge.EnvyGTSForge;
import com.envyful.gts.forge.player.GTSAttribute;
import com.pixelmonmod.pixelmon.api.dialogue.DialogueButton;
import com.pixelmonmod.pixelmon.api.dialogue.DialogueFactory;
import com.pixelmonmod.pixelmon.api.dialogue.InputPattern;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.storage.StorageProxy;

import java.awt.*;
import java.util.regex.Pattern;

public class SelectPriceUI {

    public static void openUI(ForgeEnvyPlayer player, int slot) {
        openUI(player, -1, slot, false);
    }

    public static void openUI(ForgeEnvyPlayer player, int page, int slot, boolean error) {
        player.getParent().closeContainer();
        GTSAttribute attribute = player.getAttributeNow(GTSAttribute.class);
        Pokemon pokemon = getPokemon(player, page, slot);

        if (pokemon == null) {
            return;
        }

        DialogueFactory.builder()
                .title(UtilChatColour.colour(EnvyGTSForge.getLocale().getSellPriceInputDialogueTitle()))
                .description(UtilChatColour.colour(EnvyGTSForge.getLocale().getSellPriceInputDialogueText()))
                .closeOnEscape()
                .onClose(closedScreen -> {
                    if (page == -1) {
                        SelectPartyPokemonUI.openUI(player);
                    } else {
                        SelectPCPokemonUI.openUI(player, page);
                    }
                })
                .buttons(DialogueButton.builder()
                        .text("Submit")
                        .backgroundColor(Color.GRAY)
                        .acceptedInputs(InputPattern.of(Pattern.compile("[0-9]+"), UtilChatColour.colour(EnvyGTSForge.getLocale().getSellPriceInputDialogueErrorText())))
                        .onClick(submitted -> {
                            var inputtedValue = UtilParse.parseDouble(submitted.getInput()).orElse(-1.0);

                            if (inputtedValue < attribute.getCurrentMinPrice() || inputtedValue < 0) {
                                openUI(player, page, slot, true);
                                return;
                            }

                            if (inputtedValue > EnvyGTSForge.getConfig().getMaxPrice()) {
                                openUI(player, page, slot, true);
                                return;
                            }

                            attribute.setCurrentPrice(inputtedValue);
                            EditDurationUI.openUI(player, page, slot, false);
                        })
                        .build()).sendTo(player.getParent());
    }

    public static Pokemon getPokemon(ForgeEnvyPlayer player, int page, int slot) {
        if (page == -1) {
            return StorageProxy.getPartyNow(player.getParent()).get(slot);
        } else {
            return StorageProxy.getPCForPlayerNow(player.getParent()).getBox(page).get(slot);
        }
    }
}
