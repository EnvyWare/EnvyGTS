package com.envyful.gts.forge.ui;

import com.envyful.api.concurrency.UtilConcurrency;
import com.envyful.api.neoforge.chat.UtilChatColour;
import com.envyful.api.neoforge.player.ForgeEnvyPlayer;
import com.envyful.api.platform.PlatformProxy;
import com.envyful.api.text.Placeholder;
import com.envyful.api.time.UtilTime;
import com.envyful.api.time.UtilTimeFormat;
import com.envyful.gts.forge.EnvyGTSForge;
import com.envyful.gts.forge.api.TradeOffer;
import com.envyful.gts.forge.api.item.type.PokemonTradeItem;
import com.envyful.gts.forge.api.money.InstantPurchaseMoney;
import com.envyful.gts.forge.api.player.GTSAttribute;
import com.envyful.gts.forge.api.trade.ActiveTrade;
import com.envyful.gts.forge.api.event.TradeCreateEvent;
import com.pixelmonmod.pixelmon.api.dialogue.DialogueButton;
import com.pixelmonmod.pixelmon.api.dialogue.DialogueFactory;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.storage.PCBox;
import com.pixelmonmod.pixelmon.api.storage.PlayerPartyStorage;
import com.pixelmonmod.pixelmon.api.storage.StorageProxy;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.NeoForge;

import java.awt.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class EditDurationUI {

    public static void openUI(ForgeEnvyPlayer player, int page, int position, boolean error) {
        player.getParent().closeContainer();
        var attribute = player.getAttributeNow(GTSAttribute.class);
        var pokemon = SelectPriceUI.getPokemon(player, page, position);

        PlatformProxy.runLater(() -> {
            DialogueFactory.builder()
                    .title(PlatformProxy.<Component>parse(EnvyGTSForge.getLocale().getDurationInputDialogueTitle()))
                    .description(UtilChatColour.colour((!error ?
                            EnvyGTSForge.getLocale().getDurationInputDialogueText() :
                            EnvyGTSForge.getLocale().getDurationInputDialogueErrorText()).replace("%min_price%", String.format(EnvyGTSForge.getLocale().getMoneyFormat(), attribute.getCurrentPrice()))
                            .replace("%min_duration%", UtilTimeFormat.getFormattedDuration(
                                    TimeUnit.SECONDS.toMillis(EnvyGTSForge.getConfig().getMinTradeDuration())
                            ))
                            .replace("%max_duration%", UtilTimeFormat.getFormattedDuration(
                                    TimeUnit.SECONDS.toMillis(EnvyGTSForge.getConfig().getMinTradeDuration())
                            ))
                            .replace("%pokemon%", pokemon.getDisplayName().getString())))
                    .defaultText(TimeUnit.SECONDS.toMinutes(EnvyGTSForge.getConfig().getMinTradeDuration()) + "m")
                    .maxInputLength(10)
                    .closeOnEscape()
                    .hideUI()
                    .onClose(closedScreen -> {
                        if (page == -1) {
                            SelectPartyPokemonUI.openUI(player);
                        } else {
                            SelectPCPokemonUI.openUI(player, page);
                        }
                    })
                    .buttons(
                            DialogueButton.builder()
                                    .text(Component.literal("Submit"))
                                    .backgroundColor(Color.BLACK)
                                    .backgroundHoverColor(Color.GRAY)
                                    .onClick(submitted -> {
                                        UtilConcurrency.runAsync(() -> {
                                            long inputtedValue = UtilTime.attemptParseTime(submitted.getInput()).orElse(-1L);

                                            if (inputtedValue < TimeUnit.SECONDS.toMillis(EnvyGTSForge.getConfig().getMinTradeDuration()) || inputtedValue < 0) {
                                                openUI(player, page, position, true);
                                                return;
                                            }

                                            if (inputtedValue > TimeUnit.SECONDS.toMillis(EnvyGTSForge.getConfig().getMaxTradeDurationSeconds())) {
                                                openUI(player, page, position, true);
                                                return;
                                            }

                                            Pokemon pixelmon;

                                            if (page == -1) {
                                                PlayerPartyStorage party = player.getParent().getPartyNow();
                                                pixelmon = party.get(position);
                                                party.set(position, null);
                                            } else {
                                                PCBox box = StorageProxy.getPCForPlayerNow(player.getParent()).getBox(page);
                                                pixelmon = box.get(position);
                                                box.set(position, null);
                                            }

                                            var offer = TradeOffer.newOffer(player,
                                                    Instant.now().plus(inputtedValue, ChronoUnit.MILLIS),
                                                    new PokemonTradeItem(pixelmon),
                                                    new InstantPurchaseMoney(attribute.getCurrentPrice()));

                                            var trade = new ActiveTrade(offer);
                                            EnvyGTSForge.getTradeService().addListing(trade);
                                            NeoForge.EVENT_BUS.post(new TradeCreateEvent(player, trade));
                                            attribute.setCurrentMinPrice(0);
                                            attribute.setCurrentPrice(0);
                                            player.message(List.of(EnvyGTSForge.getLocale().getMessages().getListedItem()),
                                                    trade,
                                                    Placeholder.simple("%name%", pixelmon.getDisplayName().getString()),
                                                    Placeholder.simple("%price%", String.format(EnvyGTSForge.getLocale().getMoneyFormat(), attribute.getCurrentPrice())));
                                        });
                                    })
                                    .build()
                    ).sendTo(player.getParent());
        }, 5);
    }

}
