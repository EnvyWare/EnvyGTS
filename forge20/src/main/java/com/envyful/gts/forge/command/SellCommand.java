package com.envyful.gts.forge.command;

import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.api.command.annotate.permission.Permissible;
import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.api.time.UtilTime;
import com.envyful.api.time.UtilTimeFormat;
import com.envyful.api.type.UtilParse;
import com.envyful.gts.api.Trade;
import com.envyful.gts.forge.EnvyGTSForge;
import com.envyful.gts.forge.impl.trade.ForgeTrade;
import com.envyful.gts.forge.impl.trade.type.ItemTrade;
import com.envyful.gts.forge.player.GTSAttribute;
import com.envyful.gts.forge.ui.SelectPartyPokemonUI;
import com.envyful.gts.forge.ui.SellHandOrParty;
import com.google.common.collect.Lists;
import com.pixelmonmod.pixelmon.api.storage.StorageProxy;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Items;

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
    public void onSellCommand(@Sender ForgeEnvyPlayer player, String[] args) {
        if (player.getParent().isPassenger()) {
            player.message(EnvyGTSForge.getLocale().getMessages().getCannotRideAndGts());
            return;
        }

        GTSAttribute attribute = player.getAttributeNow(GTSAttribute.class);
        var inHand = player.getParent().getItemInHand(InteractionHand.MAIN_HAND);

        if (args.length == 0) {
            StorageProxy.getPartyNow(player.getParent()).retrieveAll("GTS");

            if (Objects.equals(inHand.getItem(), Items.AIR) || EnvyGTSForge.getConfig().isBlackListed(inHand)) {
                SelectPartyPokemonUI.openUI(player);
            } else {
                SellHandOrParty.open(player);
            }
            return;
        }

        if (args.length < 2) {
            player.message(UtilChatColour.colour(
                    EnvyGTSForge.getLocale().getMessages().getSellInsuffucientArgs()
            ));
            return;
        }

        if (Objects.equals(inHand.getItem(), Items.AIR)) {
            player.message(UtilChatColour.colour(
                    EnvyGTSForge.getLocale().getMessages().getSellNoItemInHand()
            ));
            return;
        }

        if (EnvyGTSForge.getConfig().isBlackListed(inHand)) {
            player.message(UtilChatColour.colour(
                    EnvyGTSForge.getLocale().getMessages().getCannotSellBlacklisted())
            );
            return;
        }

        int amount = UtilParse.parseInt(args[0]).orElse(-1);

        if (amount <= 0) {
            player.message(UtilChatColour.colour(
                    EnvyGTSForge.getLocale().getMessages().getAmountMustBePositive()
            ));
            return;
        }

        double price = UtilParse.parseDouble(args[1]).orElse(-1.0);

        if (price < 1.0) {
            player.message(UtilChatColour.colour(
                    EnvyGTSForge.getLocale().getMessages().getPriceMustBeMoreThanOne()
            ));
            return;
        }

        if (price > EnvyGTSForge.getConfig().getMaxPrice()) {
            player.message(UtilChatColour.colour(
                    EnvyGTSForge.getLocale().getMessages().getCannotGoAboveMaxPrice()
                            .replace("%max_price%",
                                     String.format(EnvyGTSForge.getLocale().getMoneyFormat(),
                                             EnvyGTSForge.getConfig().getMaxPrice()))
            ));
            return;
        }

        if (amount > inHand.getCount()) {
            player.message(UtilChatColour.colour(
                    EnvyGTSForge.getLocale().getMessages().getNotEnoughItems()
            ));
            return;
        }

        List<Trade> trades = Lists.newArrayList(attribute.getOwnedTrades());

        trades.removeIf(trade -> trade.hasExpired() || trade.wasPurchased() || trade.wasRemoved());

        if (trades.size() >= EnvyGTSForge.getConfig().getMaxListingsPerUser()) {
            player.message(UtilChatColour.colour(
                    EnvyGTSForge.getLocale().getMessages().getMaxTradesAlreadyReached()
            ));
            return;
        }

        long duration = TimeUnit.SECONDS.toMillis(EnvyGTSForge.getConfig().getDefaultTradeDurationSeconds());

        if (args.length > 2) {
            duration = UtilTime.attemptParseTime(args[2]).orElse(-1L);

            if (duration < TimeUnit.SECONDS.toMillis(EnvyGTSForge.getConfig().getMinTradeDuration()) || duration < 0) {
                player.message(UtilChatColour.colour(
                        EnvyGTSForge.getLocale().getMessages().getCannotGoBelowMinTime()
                                .replace("%min_duration%", String.valueOf(EnvyGTSForge.getConfig().getMinTradeDuration()))
                ));
                return;
            }

            if (duration > TimeUnit.SECONDS.toMillis(EnvyGTSForge.getConfig().getMaxTradeDurationSeconds())) {
                player.message(UtilChatColour.colour(
                        EnvyGTSForge.getLocale().getMessages().getCannotGoAboveMaxTime()
                                .replace("%max_duration%",
                                        UtilTimeFormat.getFormattedDuration(EnvyGTSForge.getConfig().getMaxTradeDurationSeconds()))
                ));
                return;
            }
        }

        ItemTrade.Builder builder = (ItemTrade.Builder) ForgeTrade.builder()
                .owner(player)
                .originalOwnerName(player.getName())
                .cost(price)
                .expiry(System.currentTimeMillis() + duration)
                .content("i");

        var copy = inHand.copy();
        copy.setCount(amount);
        builder.contents(copy);
        inHand.shrink(amount);

        player.message(UtilChatColour.colour(
                EnvyGTSForge.getLocale().getMessages().getAddedItemToGts()
        ));
        EnvyGTSForge.getTradeManager().addTrade(player, builder.build());
    }
}
