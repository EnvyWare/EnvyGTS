package com.envyful.gts.forge.command;

import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.api.command.annotate.permission.Permissible;
import com.envyful.api.neoforge.player.ForgeEnvyPlayer;
import com.envyful.api.text.Placeholder;
import com.envyful.api.time.UtilTime;
import com.envyful.api.time.UtilTimeFormat;
import com.envyful.api.type.UtilParse;
import com.envyful.gts.forge.EnvyGTSForge;
import com.envyful.gts.forge.api.TradeOffer;
import com.envyful.gts.forge.api.item.type.ItemStackTradeItem;
import com.envyful.gts.forge.api.money.InstantPurchaseMoney;
import com.envyful.gts.forge.api.player.GTSAttribute;
import com.envyful.gts.forge.api.trade.ActiveTrade;
import com.envyful.gts.forge.api.event.TradeCreateEvent;
import com.envyful.gts.forge.ui.SelectPartyPokemonUI;
import com.envyful.gts.forge.ui.SellHandOrParty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.NeoForge;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Command(
        value = {
                "sell",
                "s"
        }
)
@Permissible("com.envyful.gts.command.sell")
public class SellCommand {

    @CommandProcessor
    public void onSellCommand(@Sender ForgeEnvyPlayer sender, String[] args) {
        if (sender.getParent().isPassenger()) {
            sender.message(EnvyGTSForge.getLocale().getMessages().getCannotRideAndGts());
            return;
        }

        GTSAttribute attribute = sender.getAttributeNow(GTSAttribute.class);
        var inHand = sender.getParent().getItemInHand(InteractionHand.MAIN_HAND);

        if (args.length == 0) {
            sender.getParent().getPartyNow().retrieveAll("GTS");

            if (Objects.equals(inHand.getItem(), Items.AIR) || EnvyGTSForge.getConfig().isBlackListed(inHand)) {
                SelectPartyPokemonUI.openUI(sender);
            } else {
                SellHandOrParty.open(sender);
            }
            return;
        }

        if (args.length < 2) {
            sender.message(EnvyGTSForge.getLocale().getMessages().getSellInsuffucientArgs());
            return;
        }

        if (Objects.equals(inHand.getItem(), Items.AIR)) {
            sender.message(EnvyGTSForge.getLocale().getMessages().getSellNoItemInHand());
            return;
        }

        if (EnvyGTSForge.getConfig().isBlackListed(inHand)) {
            sender.message(EnvyGTSForge.getLocale().getMessages().getCannotSellBlacklisted());
            return;
        }

        int amount = UtilParse.parseInt(args[0]).orElse(-1);

        if (amount <= 0) {
            sender.message(EnvyGTSForge.getLocale().getMessages().getAmountMustBePositive());
            return;
        }

        double price = UtilParse.parseDouble(args[1]).orElse(-1.0);

        if (price < 1.0) {
            sender.message(EnvyGTSForge.getLocale().getMessages().getPriceMustBeMoreThanOne());
            return;
        }

        if (price > EnvyGTSForge.getConfig().getMaxPrice()) {
            sender.message(EnvyGTSForge.getLocale().getMessages().getCannotGoAboveMaxPrice()
                    .replace("%max_price%",
                            String.format(EnvyGTSForge.getLocale().getMoneyFormat(),
                                    EnvyGTSForge.getConfig().getMaxPrice())));
            return;
        }

        if (amount > inHand.getCount()) {
            sender.message(EnvyGTSForge.getLocale().getMessages().getNotEnoughItems());
            return;
        }

        if (attribute.hasReachedMaximumTrades()) {
            sender.message(EnvyGTSForge.getLocale().getMessages().getMaxTradesAlreadyReached());
            return;
        }

        long duration = TimeUnit.SECONDS.toMillis(EnvyGTSForge.getConfig().getDefaultTradeDurationSeconds());

        if (args.length > 2) {
            duration = UtilTime.attemptParseTime(args[2]).orElse(-1L);

            if (duration < TimeUnit.SECONDS.toMillis(EnvyGTSForge.getConfig().getMinTradeDuration()) || duration < 0) {
                sender.message(
                        EnvyGTSForge.getLocale().getMessages().getCannotGoBelowMinTime()
                                .replace("%min_duration%", String.valueOf(EnvyGTSForge.getConfig().getMinTradeDuration())));
                return;
            }

            if (duration > TimeUnit.SECONDS.toMillis(EnvyGTSForge.getConfig().getMaxTradeDurationSeconds())) {
                sender.message(EnvyGTSForge.getLocale().getMessages().getCannotGoAboveMaxTime()
                                .replace("%max_duration%",
                                        UtilTimeFormat.getFormattedDuration(EnvyGTSForge.getConfig().getMaxTradeDurationSeconds())));
                return;
            }
        }

        var copy = inHand.copy();
        copy.setCount(amount);
        inHand.shrink(amount);

        var offer = TradeOffer.newOffer(sender,
                Instant.now().plus(duration, ChronoUnit.MILLIS),
                new ItemStackTradeItem(copy),
                new InstantPurchaseMoney(price));
        var trade = new ActiveTrade(offer);

        EnvyGTSForge.getTradeService().addListing(trade);
        NeoForge.EVENT_BUS.post(new TradeCreateEvent(sender, trade));
        sender.message(List.of(EnvyGTSForge.getLocale().getMessages().getListedItem()),
                trade,
                Placeholder.simple("%name%", copy.getDisplayName().getString()),
                Placeholder.simple("%price%", String.format(EnvyGTSForge.getLocale().getMoneyFormat(), attribute.getCurrentPrice())));
    }
}
