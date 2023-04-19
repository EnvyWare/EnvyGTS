package com.envyful.gts.forge.command;

import com.envyful.api.command.annotate.Child;
import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.Permissible;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
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
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Command(
        value = "sell",
        description = "For selling items to the GTS",
        aliases = {
                "s"
        }
)
@Permissible("com.envyful.gts.command.sell")
@Child
public class SellCommand {

    @CommandProcessor
    public void onSellCommand(@Sender ServerPlayerEntity player, String[] args) {
        ForgeEnvyPlayer sender = EnvyGTSForge.getPlayerManager().getPlayer(player);
        GTSAttribute attribute = sender.getAttribute(EnvyGTSForge.class);
        ItemStack inHand = player.getItemInHand(Hand.MAIN_HAND);

        if (args.length == 0) {
            StorageProxy.getParty(player).retrieveAll();

            if (Objects.equals(inHand.getItem(), Items.AIR) || EnvyGTSForge.getConfig().isBlackListed(inHand)) {
                SelectPartyPokemonUI.openUI(sender);
            } else {
                SellHandOrParty.open(sender);
            }
            return;
        }

        if (args.length < 2) {
            sender.message(UtilChatColour.colour(
                    EnvyGTSForge.getLocale().getMessages().getSellInsuffucientArgs()
            ));
            return;
        }

        if (Objects.equals(inHand.getItem(), Items.AIR)) {
            sender.message(UtilChatColour.colour(
                    EnvyGTSForge.getLocale().getMessages().getSellNoItemInHand()
            ));
            return;
        }

        if (EnvyGTSForge.getConfig().isBlackListed(inHand)) {
            sender.message(UtilChatColour.colour(
                    EnvyGTSForge.getLocale().getMessages().getCannotSellBlacklisted())
            );
            return;
        }

        int amount = UtilParse.parseInteger(args[0]).orElse(-1);

        if (amount <= 0) {
            sender.message(UtilChatColour.colour(
                    EnvyGTSForge.getLocale().getMessages().getAmountMustBePositive()
            ));
            return;
        }

        double price = UtilParse.parseDouble(args[1]).orElse(-1.0);

        if (price < 1.0) {
            sender.message(UtilChatColour.colour(
                    EnvyGTSForge.getLocale().getMessages().getPriceMustBeMoreThanOne()
            ));
            return;
        }

        if (price > EnvyGTSForge.getConfig().getMaxPrice()) {
            sender.message(UtilChatColour.colour(
                    EnvyGTSForge.getLocale().getMessages().getCannotGoAboveMaxPrice()
                            .replace("%max_price%",
                                     String.format(EnvyGTSForge.getLocale().getMoneyFormat(),
                                             EnvyGTSForge.getConfig().getMaxPrice()))
            ));
            return;
        }

        if (amount > inHand.getCount()) {
            player.sendMessage(UtilChatColour.colour(
                    EnvyGTSForge.getLocale().getMessages().getNotEnoughItems()
            ), Util.NIL_UUID);
            return;
        }

        List<Trade> trades = Lists.newArrayList(attribute.getOwnedTrades());

        trades.removeIf(trade -> trade.hasExpired() || trade.wasPurchased() || trade.wasRemoved());

        if (trades.size() >= EnvyGTSForge.getConfig().getMaxListingsPerUser()) {
            sender.message(UtilChatColour.colour(
                    EnvyGTSForge.getLocale().getMessages().getMaxTradesAlreadyReached()
            ));
            return;
        }

        long duration = TimeUnit.SECONDS.toMillis(EnvyGTSForge.getConfig().getDefaultTradeDurationSeconds());

        if (args.length > 2) {
            duration = UtilTime.attemptParseTime(args[2]).orElse(-1L);

            if (duration < TimeUnit.SECONDS.toMillis(EnvyGTSForge.getConfig().getMinTradeDuration()) || duration < 0) {
                sender.message(UtilChatColour.colour(
                        EnvyGTSForge.getLocale().getMessages().getCannotGoBelowMinTime()
                                .replace("%min_duration%", String.valueOf(EnvyGTSForge.getConfig().getMinTradeDuration()))
                ));
                return;
            }

            if (duration > TimeUnit.SECONDS.toMillis(EnvyGTSForge.getConfig().getMaxTradeDurationSeconds())) {
                sender.message(UtilChatColour.colour(
                        EnvyGTSForge.getLocale().getMessages().getCannotGoAboveMaxTime()
                                .replace("%max_duration%",
                                        UtilTimeFormat.getFormattedDuration(EnvyGTSForge.getConfig().getMaxTradeDurationSeconds()))
                ));
                return;
            }
        }

        ItemTrade.Builder builder = (ItemTrade.Builder) ForgeTrade.builder()
                .owner(sender)
                .originalOwnerName(sender.getName())
                .cost(price)
                .expiry(System.currentTimeMillis() + duration)
                .content("i");

        ItemStack copy = inHand.copy();
        copy.setCount(amount);
        builder.contents(copy);
        inHand.shrink(amount);

        player.sendMessage(UtilChatColour.colour(
                EnvyGTSForge.getLocale().getMessages().getAddedItemToGts()
        ), Util.NIL_UUID);
        EnvyGTSForge.getTradeManager().addTrade(sender, builder.build());
    }
}
