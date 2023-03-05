package com.envyful.gts.forge.ui;

import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.concurrency.UtilForgeConcurrency;
import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.api.forge.player.util.UtilPlayer;
import com.envyful.api.reforged.dialogue.DialogueInputRegistry;
import com.envyful.api.time.UtilTime;
import com.envyful.api.time.UtilTimeFormat;
import com.envyful.api.type.UtilParse;
import com.envyful.gts.forge.EnvyGTSForge;
import com.envyful.gts.forge.impl.trade.ForgeTrade;
import com.envyful.gts.forge.impl.trade.type.PokemonTrade;
import com.envyful.gts.forge.player.GTSAttribute;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.storage.PCBox;
import com.pixelmonmod.pixelmon.api.storage.PlayerPartyStorage;
import com.pixelmonmod.pixelmon.api.storage.StorageProxy;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

import java.util.concurrent.TimeUnit;

public class EditItemDurationUI {

    public static void openUI(ForgeEnvyPlayer player, double time, boolean error) {
        player.getParent().closeContainer();
        GTSAttribute attribute = player.getAttribute(EnvyGTSForge.class);
        ItemStack itemInHand = player.getParent().getItemInHand(Hand.MAIN_HAND);

        UtilForgeConcurrency.runLater(() -> DialogueInputRegistry.builder()
                .title(UtilChatColour.colour(EnvyGTSForge.getLocale().getDurationInputDialogueTitle()))
                .text(UtilChatColour.colour((!error ?
                        EnvyGTSForge.getLocale().getDurationInputDialogueText() :
                        EnvyGTSForge.getLocale().getDurationInputDialogueErrorText())
                        .replace("%min_price%", String.format(EnvyGTSForge.getLocale().getMoneyFormat(), attribute.getCurrentPrice()))
                        .replace("%min_duration%", UtilTimeFormat.getFormattedDuration(
                                TimeUnit.SECONDS.toMillis(EnvyGTSForge.getConfig().getMinTradeDuration())
                        ))
                        .replace("%max_duration%", UtilTimeFormat.getFormattedDuration(
                                TimeUnit.SECONDS.toMillis(EnvyGTSForge.getConfig().getMinTradeDuration())
                        ))
                        .replace("%item%", itemInHand.getHoverName().getString())))
                .defaultInputValue(TimeUnit.SECONDS.toMinutes(EnvyGTSForge.getConfig().getMinTradeDuration()) + "m")
                .closeOnEscape()
                .closeHandler(closedScreen -> SellHandOrParty.open(player))
                .submitHandler(submitted -> {
                    long inputtedValue = UtilTime.attemptParseTime(submitted.getInput()).orElse(-1L);

                    if (inputtedValue < TimeUnit.SECONDS.toMillis(EnvyGTSForge.getConfig().getMinTradeDuration()) || inputtedValue < 0) {
                        openUI(player, time,true);
                        return;
                    }

                    if (inputtedValue > TimeUnit.SECONDS.toMillis(EnvyGTSForge.getConfig().getMaxTradeDurationSeconds())) {
                        openUI(player, time,true);
                        return;
                    }

                    UtilPlayer.runCommand(player.getParent(), "gts sell " + itemInHand.getCount() + " " +
                            time + " " +  UtilParse.parseDouble(submitted.getInput()).orElse(-1.0));
                })
                .open(player.getParent()), 5);
    }

}
