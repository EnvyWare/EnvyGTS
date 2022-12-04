package com.envyful.gts.forge.ui;

import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.concurrency.UtilForgeConcurrency;
import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.api.reforged.dialogue.DialogueInputRegistry;
import com.envyful.api.time.UtilTimeFormat;
import com.envyful.api.type.UtilParse;
import com.envyful.gts.forge.EnvyGTSForge;
import com.envyful.gts.forge.impl.trade.type.PokemonTrade;
import com.envyful.gts.forge.player.GTSAttribute;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.storage.PCBox;
import com.pixelmonmod.pixelmon.api.storage.PlayerPartyStorage;
import com.pixelmonmod.pixelmon.api.storage.StorageProxy;

import java.util.concurrent.TimeUnit;

public class EditDurationUI {

    public static void openUI(ForgeEnvyPlayer player, int page, int position, boolean error) {
        player.getParent().closeContainer();
        GTSAttribute attribute = player.getAttribute(EnvyGTSForge.class);
        Pokemon pokemon = SelectPriceUI.getPokemon(player, page, position);

        UtilForgeConcurrency.runLater(() -> {
            DialogueInputRegistry.builder()
                    .title(UtilChatColour.colour(EnvyGTSForge.getInstance().getLocale().getDurationInputDialogueTitle()))
                    .text(UtilChatColour.colour((!error ?
                            EnvyGTSForge.getInstance().getLocale().getDurationInputDialogueText() :
                            EnvyGTSForge.getInstance().getLocale().getDurationInputDialogueErrorText())
                            .replace("%min_price%", String.format(EnvyGTSForge.getInstance().getLocale().getMoneyFormat(), attribute.getCurrentPrice()))
                            .replace("%min_duration%", UtilTimeFormat.getFormattedDuration(
                                    TimeUnit.SECONDS.toMillis(EnvyGTSForge.getInstance().getConfig().getMinTradeDuration())
                            ))
                            .replace("%pokemon%", pokemon.getDisplayName())))
                    .defaultInputValue(String.valueOf(TimeUnit.SECONDS.toMinutes(EnvyGTSForge.getInstance().getConfig().getMinTradeDuration())))
                    .closeOnEscape()
                    .closeHandler(closedScreen -> {
                        if (page == -1) {
                            SelectPartyPokemonUI.openUI(player);
                        } else {
                            SelectPCPokemonUI.openUI(player, page);
                        }
                    })
                    .submitHandler(submitted -> {
                        int inputtedValue = UtilParse.parseInteger(submitted.getInput()).orElse(-1);

                        if (inputtedValue < TimeUnit.SECONDS.toMinutes(EnvyGTSForge.getInstance().getConfig().getMinTradeDuration()) || inputtedValue < 0) {
                            openUI(player, page, position, true);
                            return;
                        }

                        Pokemon pixelmon;

                        if (page == -1) {
                            PlayerPartyStorage party = StorageProxy.getParty(player.getParent());
                            pixelmon = party.get(position);
                            party.set(position, null);
                        } else {
                            PCBox box = StorageProxy.getPCForPlayer(player.getParent()).getBox(page);
                            pixelmon = box.get(position);
                            box.set(position, null);
                        }

                        EnvyGTSForge.getInstance().getTradeManager()
                                .addTrade(player, ((PokemonTrade.Builder) PokemonTrade.builder()
                                        .cost(attribute.getCurrentPrice())
                                        .expiry(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(attribute.getCurrentDuration()))
                                        .owner(player)
                                        .originalOwnerName(player.getName())
                                        .content("p"))
                                        .contents(pixelmon)
                                        .build());
                        attribute.setCurrentDuration(0);
                        attribute.setCurrentMinPrice(0);
                        attribute.setCurrentPrice(0);
                        attribute.setSelectedSlot(-1);
                    })
                    .open(player.getParent());
        }, 5);
    }

}
