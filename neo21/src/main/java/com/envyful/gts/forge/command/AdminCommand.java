package com.envyful.gts.forge.command;

import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.api.command.annotate.permission.Permissible;
import com.envyful.api.neoforge.player.ForgeEnvyPlayer;
import com.envyful.api.time.UtilTime;
import com.envyful.gts.forge.EnvyGTSForge;
import com.envyful.gts.forge.api.item.TradeItemType;
import com.envyful.gts.forge.api.item.TradeItemTypeFactory;

import java.time.Duration;
import java.util.List;
import java.util.Locale;

@Command(
        value = {
                "admin",
                "a"
        }
)
@Permissible("com.envyful.gts.command.admin")
public class AdminCommand {

    private static final Duration DEFAULT_PRICE_WINDOW = Duration.ofHours(24);

    private static final List<String> EVERY_TYPE_ALIASES = List.of("all", "any");

    @CommandProcessor(executeAsync = false)
    public void onCommand(@Sender ForgeEnvyPlayer player, String[] args) {
        if (args.length == 0) {
            EnvyGTSForge.getGui().getAdminTradesUI().openMenu(player);
            return;
        }

        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "history", "hist" -> this.openHistory(player, args);
            case "prices", "price", "highest" -> this.openHighestPrices(player, args);
            default -> this.sendUsage(player);
        }
    }

    private void openHistory(ForgeEnvyPlayer player, String[] args) {
        if (args.length == 1) {
            EnvyGTSForge.getGui().getAdminTradeHistoryUI().openGlobalHistory(player);
            return;
        }

        EnvyGTSForge.getGui().getAdminTradeHistoryUI().openPlayerHistory(player, args[1]);
    }

    private void openHighestPrices(ForgeEnvyPlayer player, String[] args) {
        var window = DEFAULT_PRICE_WINDOW;
        TradeItemType type = null;

        for (int i = 1; i < args.length; i++) {
            if (EVERY_TYPE_ALIASES.contains(args[i].toLowerCase(Locale.ROOT))) {
                type = null;
                continue;
            }

            var parsedType = TradeItemTypeFactory.parse(args[i]);

            if (parsedType.isPresent()) {
                type = parsedType.get();
                continue;
            }

            var parsedDuration = UtilTime.attemptParseTime(args[i]);

            if (parsedDuration.isPresent() && parsedDuration.get() > 0) {
                window = Duration.ofMillis(parsedDuration.get());
                continue;
            }

            player.message("&cInvalid price history argument: " + args[i]);
            this.sendUsage(player);
            return;
        }

        EnvyGTSForge.getGui().getAdminHighestPricesUI().openHighestPrices(player, window, type);
    }

    private void sendUsage(ForgeEnvyPlayer player) {
        player.message("&e/gts admin &7- Open the admin menu");
        player.message("&e/gts admin history &7- View global trade history");
        player.message("&e/gts admin history <player|uuid> &7- View player trade history");
        player.message("&e/gts admin prices <duration> [all|item|pokemon] &7- View highest sold prices");
    }
}
