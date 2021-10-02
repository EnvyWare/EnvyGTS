package com.envyful.reforged.gts.forge.command;

import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.Permissible;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.api.type.UtilParse;
import com.envyful.reforged.gts.api.gui.FilterType;
import com.envyful.reforged.gts.forge.ReforgedGTSForge;
import com.envyful.reforged.gts.forge.impl.trade.ForgeTrade;
import com.envyful.reforged.gts.forge.impl.trade.type.ItemTrade;
import com.envyful.reforged.gts.forge.ui.SelectPartyPokemonUI;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

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
            sender.message(ReforgedGTSForge.getInstance().getLocale().getMessages().getSellInsuffucientArgs());
            return;
        }

        ItemStack inHand = player.getHeldItemMainhand();

        if (Objects.equals(inHand.getItem(), Items.AIR)) {
            sender.message(ReforgedGTSForge.getInstance().getLocale().getMessages().getSellNoItemInHand());
            return;
        }

        int amount = UtilParse.parseInteger(args[0]).orElse(-1);

        if (amount <= 0) {
            sender.message(ReforgedGTSForge.getInstance().getLocale().getMessages().getAmountMustBePositive());
            return;
        }

        double price = UtilParse.parseDouble(args[1]).orElse(-1.0);

        if (price < 1.0) {
            sender.message(ReforgedGTSForge.getInstance().getLocale().getMessages().getPriceMustBeMoreThanOne());
            return;
        }

        ItemTrade.Builder builder = (ItemTrade.Builder) ForgeTrade.builder()
                .owner(sender)
                .type(FilterType.INSTANT_BUY)
                .cost(price)
                .expiry(System.currentTimeMillis()
                        + TimeUnit.SECONDS.toMillis(ReforgedGTSForge.getInstance().getConfig().getTradeDurationSeconds()))
                .content("i");

        builder.contents(inHand);

        ReforgedGTSForge.getInstance().getTradeManager().addTrade(sender, builder.build());
    }
}
