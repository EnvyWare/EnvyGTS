package com.envyful.gts.forge.ui;

import com.envyful.api.neoforge.chat.UtilChatColour;
import com.envyful.api.neoforge.player.ForgeEnvyPlayer;
import com.envyful.api.platform.PlatformProxy;
import com.envyful.api.type.UtilParse;
import com.envyful.gts.forge.EnvyGTSForge;
import com.envyful.gts.forge.player.GTSAttribute;
import com.pixelmonmod.pixelmon.api.dialogue.DialogueButton;
import com.pixelmonmod.pixelmon.api.dialogue.DialogueFactory;
import com.pixelmonmod.pixelmon.api.dialogue.InputPattern;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.storage.StorageProxy;
import net.minecraft.network.chat.Component;

import java.awt.*;
import java.util.regex.Pattern;

public class SelectPriceUI {

    public static void openUI(ForgeEnvyPlayer player, int slot) {
        openUI(player, -1, slot, false);
    }

    public static void openUI(ForgeEnvyPlayer player, int page, int slot, boolean error) {
        player.getParent().closeContainer();
        var pokemon = getPokemon(player, page, slot);

        if (pokemon == null) {
            return;
        }

        dialogueBuilder(player, page, slot, error).sendTo(player.getParent());
    }

    public static Pokemon getPokemon(ForgeEnvyPlayer player, int page, int slot) {
        if (page == -1) {
            return StorageProxy.getPartyNow(player.getParent()).get(slot);
        } else {
            return StorageProxy.getPCForPlayerNow(player.getParent()).getBox(page).get(slot);
        }
    }

    public static DialogueFactory.Builder dialogueBuilder(ForgeEnvyPlayer player, int page, int slot, boolean error) {
        var attribute = player.getAttributeNow(GTSAttribute.class);
        var pokemon = getPokemon(player, page, slot);

        if (pokemon == null) {
            return null;
        }

        return DialogueFactory.builder()
                .title(PlatformProxy.<Component>flatParse(EnvyGTSForge.getLocale().getSellPriceInputDialogueTitle()))
                .description(UtilChatColour.colour((error ? EnvyGTSForge.getLocale().getSellPriceInputDialogueErrorText() : EnvyGTSForge.getLocale().getSellPriceInputDialogueText())
                        .replace("%pokemon%", pokemon.getDisplayName().getString())
                        .replace("%min_price%", String.format(EnvyGTSForge.getLocale().getMoneyFormat(), attribute.getCurrentMinPrice()))
                        .replace("%max_price%", String.format(EnvyGTSForge.getLocale().getMoneyFormat(), EnvyGTSForge.getConfig().getMaxPrice()))))
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
                        .acceptedInputs(InputPattern.of(Pattern.compile("[0-9]+"), UtilChatColour.colour(EnvyGTSForge.getLocale().getSellPriceInputDialogueErrorText()
                                .replace("%min_price%", String.format(EnvyGTSForge.getLocale().getMoneyFormat(), attribute.getCurrentMinPrice()))
                                .replace("%max_price%", String.format(EnvyGTSForge.getLocale().getMoneyFormat(), EnvyGTSForge.getConfig().getMaxPrice()))
                                .replace("%pokemon%", pokemon.getDisplayName().getString()))))
                        .onClick(submitted -> {
                            var inputtedValue = UtilParse.parseDouble(submitted.getInput()).orElse(-1.0);

                            if (inputtedValue < attribute.getCurrentMinPrice() || inputtedValue < 0) {
                                submitted.setSettings(dialogueBuilder(player, page, slot, true).createSettings());
                                submitted.setCloseUI(false);
                                return;
                            }

                            if (inputtedValue > EnvyGTSForge.getConfig().getMaxPrice()) {
                                submitted.setSettings(dialogueBuilder(player, page, slot, true).createSettings());
                                submitted.setCloseUI(false);
                                return;
                            }

                            attribute.setCurrentPrice(inputtedValue);
                            EditDurationUI.openUI(player, page, slot, false);
                        })
                        .build());
    }
}
