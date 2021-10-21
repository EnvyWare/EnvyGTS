package com.envyful.reforged.gts.forge.command;

import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.Permissible;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.api.type.UtilParse;
import com.envyful.reforged.gts.forge.ReforgedGTSForge;
import com.envyful.reforged.gts.forge.impl.trade.ForgeTrade;
import com.envyful.reforged.gts.forge.impl.trade.type.ItemTrade;
import com.envyful.reforged.gts.forge.ui.SelectPartyPokemonUI;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Command(
        value = "sell",
        description = "For selling items to the GTS",
        aliases = {
                "s"
        }
)
@Permissible("reforged.gts.command.sell")
public class SellCommand {

    @CommandProcessor
    public void onSellCommand(@Sender EntityPlayerMP player, String[] args) {
        ForgeEnvyPlayer sender = ReforgedGTSForge.getInstance().getPlayerManager().getPlayer(player);

        if (args.length == 0) {
            SelectPartyPokemonUI.openUI(sender);
            return;
        }

        if (args.length != 2) {
            sender.message(UtilChatColour.translateColourCodes(
                    '&',
                    ReforgedGTSForge.getInstance().getLocale().getMessages().getSellInsuffucientArgs()
            ));
            return;
        }

        ItemStack inHand = player.getHeldItemMainhand();

        if (Objects.equals(inHand.getItem(), Items.AIR)) {
            sender.message(UtilChatColour.translateColourCodes(
                    '&',
                    ReforgedGTSForge.getInstance().getLocale().getMessages().getSellNoItemInHand()
            ));
            return;
        }

        if (ReforgedGTSForge.getInstance().getConfig().isBlackListed(inHand)) {
            sender.message(UtilChatColour.translateColourCodes(
                    '&',
                    ReforgedGTSForge.getInstance().getLocale().getMessages().getCannotSellBlacklisted())
            );
            return;
        }

        int amount = UtilParse.parseInteger(args[0]).orElse(-1);

        if (amount <= 0) {
            sender.message(UtilChatColour.translateColourCodes(
                    '&',
                    ReforgedGTSForge.getInstance().getLocale().getMessages().getAmountMustBePositive()
            ));
            return;
        }

        double price = UtilParse.parseDouble(args[1]).orElse(-1.0);

        if (price < 1.0) {
            sender.message(UtilChatColour.translateColourCodes(
                    '&',
                    ReforgedGTSForge.getInstance().getLocale().getMessages().getPriceMustBeMoreThanOne()
            ));
            return;
        }

        if (amount > inHand.getCount()) {
            player.sendMessage(new TextComponentString(UtilChatColour.translateColourCodes(
                    '&',
                    ReforgedGTSForge.getInstance().getLocale().getMessages().getNotEnoughItems()
            )));
            return;
        }

        ItemTrade.Builder builder = (ItemTrade.Builder) ForgeTrade.builder()
                .owner(sender)
                .originalOwnerName(sender.getName())
                .cost(price)
                .expiry(System.currentTimeMillis()
                                + TimeUnit.SECONDS.toMillis(ReforgedGTSForge.getInstance().getConfig().getDefaultTradeDurationSeconds()))
                .content("i");

        ItemStack copy = inHand.copy();
        copy.setCount(amount);
        builder.contents(copy);
        inHand.shrink(amount);

        player.sendMessage(new TextComponentString(UtilChatColour.translateColourCodes(
                '&',
                ReforgedGTSForge.getInstance().getLocale().getMessages().getAddedItemToGts()
        )));
        ReforgedGTSForge.getInstance().getTradeManager().addTrade(sender, builder.build());
    }
}
