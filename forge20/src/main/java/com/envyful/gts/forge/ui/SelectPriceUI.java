package com.envyful.gts.forge.ui;

import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.concurrency.UtilForgeConcurrency;
import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.api.reforged.dialogue.DialogueInputRegistry;
import com.envyful.api.type.UtilParse;
import com.envyful.gts.forge.EnvyGTSForge;
import com.envyful.gts.forge.player.GTSAttribute;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.storage.StorageProxy;

public class SelectPriceUI {

    public static void openUI(ForgeEnvyPlayer player, int slot) {
        openUI(player, -1, slot, false);
    }

    public static void openUI(ForgeEnvyPlayer player, int page, int slot, boolean error) {
        player.getParent().closeContainer();
        GTSAttribute attribute = player.getAttribute(GTSAttribute.class);
        Pokemon pokemon = getPokemon(player, page, slot);

        if (pokemon == null) {
            return;
        }

        UtilForgeConcurrency.runLater(() -> DialogueInputRegistry.builder()
                .title(UtilChatColour.colour(EnvyGTSForge.getLocale().getSellPriceInputDialogueTitle()))
                .text(UtilChatColour.colour((!error ?
                        EnvyGTSForge.getLocale().getSellPriceInputDialogueText() :
                        EnvyGTSForge.getLocale().getSellPriceInputDialogueErrorText())
                        .replace("%min_price%", String.format(EnvyGTSForge.getLocale().getMoneyFormat(), attribute.getCurrentPrice()))
                        .replace("%max_price%", String.format(EnvyGTSForge.getLocale().getMoneyFormat(), EnvyGTSForge.getConfig().getMaxPrice()))
                        .replace("%pokemon%", pokemon.getDisplayName())))
                .defaultInputValue(String.valueOf(attribute.getCurrentPrice()))
                .closeOnEscape()
                .closeHandler(closedScreen -> {
                    if (page == -1) {
                        SelectPartyPokemonUI.openUI(player);
                    } else {
                        SelectPCPokemonUI.openUI(player, page);
                    }
                })
                .submitHandler(submitted -> {
                    double inputtedValue = UtilParse.parseDouble(submitted.getInput()).orElse(-1.0);

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
                .open(player.getParent()), 5);
    }

    public static Pokemon getPokemon(ForgeEnvyPlayer player, int page, int slot) {
        if (page == -1) {
            return StorageProxy.getPartyNow(player.getParent()).get(slot);
        } else {
            return StorageProxy.getPCForPlayerNow(player.getParent()).getBox(page).get(slot);
        }
    }
}
