package com.envyful.gts.forge.command;

import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.api.command.annotate.permission.Permissible;
import com.envyful.api.neoforge.player.ForgeEnvyPlayer;
import com.envyful.gts.forge.EnvyGTSForge;

import java.util.Arrays;
import java.util.Locale;

@Command(
        value = {
                "market",
                "m"
        }
)
@Permissible("com.envyful.gts.command.market")
public class MarketCommand {

    @CommandProcessor(executeAsync = false)
    public void onCommand(@Sender ForgeEnvyPlayer player, String[] args) {
        if (args.length == 0) {
            EnvyGTSForge.getGui().getMarketUI().openMenu(player);
            return;
        }

        var query = args.length > 1 ? String.join(" ", Arrays.copyOfRange(args, 1, args.length)) : null;

        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "recent", "history", "trades" -> this.openRecentTrades(player, query);
            case "prices", "price" -> this.openPrices(player, query);
            case "player", "seller", "buyer" -> this.openPlayerTrades(player, query);
            default -> this.sendUsage(player);
        }
    }

    private void openRecentTrades(ForgeEnvyPlayer player, String search) {
        if (search == null) {
            EnvyGTSForge.getGui().getRecentTradesUI().openUI(player);
            return;
        }

        EnvyGTSForge.getGui().getRecentTradesUI().openSearch(player, search);
    }

    private void openPrices(ForgeEnvyPlayer player, String search) {
        if (search == null) {
            EnvyGTSForge.getGui().getPriceStatisticsUI().openUI(player);
            return;
        }

        EnvyGTSForge.getGui().getPriceStatisticsUI().openSearch(player, search);
    }

    private void openPlayerTrades(ForgeEnvyPlayer player, String searchedPlayer) {
        if (searchedPlayer == null) {
            EnvyGTSForge.getGui().getRecentTradesUI().openOwnTrades(player);
            return;
        }

        EnvyGTSForge.getGui().getRecentTradesUI().openPlayer(player, searchedPlayer);
    }

    private void sendUsage(ForgeEnvyPlayer player) {
        player.message(EnvyGTSForge.getLocale().getMessages().getMarketCommandUsage());
    }
}
