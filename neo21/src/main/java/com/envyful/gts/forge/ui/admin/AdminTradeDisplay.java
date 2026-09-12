package com.envyful.gts.forge.ui.admin;

import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.gui.item.Displayable;
import com.envyful.api.neoforge.items.ItemBuilder;
import com.envyful.api.neoforge.player.ForgeEnvyPlayer;
import com.envyful.api.platform.PlatformProxy;
import com.envyful.api.text.parse.SimplePlaceholder;
import com.envyful.gts.forge.EnvyGTSForge;
import com.envyful.gts.forge.api.trade.ExpiredTrade;
import com.envyful.gts.forge.api.trade.RemovedTrade;
import com.envyful.gts.forge.api.trade.SoldTrade;
import com.envyful.gts.forge.api.trade.Trade;
import com.envyful.gts.forge.api.trade.TradeHistory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

final class AdminTradeDisplay {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");

    private static final DateTimeFormatter SHORT_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private static final String NONE = "-";

    private AdminTradeDisplay() {
    }

    static void reportFailures(ForgeEnvyPlayer player, TradeHistory history) {
        if (!history.hasFailures()) {
            return;
        }

        player.message("&c" + history.failed() + " trade(s) could not be read from the database and are " +
                "missing from this view. Check the server console for details.");
    }

    static Displayable.Builder<ItemStack> build(Trade trade, boolean priceHistory) {
        var item = new ItemBuilder(trade.offer().item().display());

        for (var lore : buildLore(trade, priceHistory)) {
            item.addLore(PlatformProxy.<Component>flatParse(lore));
        }

        return GuiFactory.displayableBuilder(item.build());
    }

    private static List<String> buildLore(Trade trade, boolean priceHistory) {
        var lore = new ArrayList<String>();

        lore.add(" ");

        if (priceHistory) {
            lore.add("&6Sold for &a$" + formatMoney(finalPrice(trade)));
        } else {
            lore.add("&bOutcome: &f" + outcomeDisplayName(trade));
        }

        lore.add("&bSeller: &f" + trade.offer().seller().name());

        if (trade instanceof SoldTrade soldTrade) {
            lore.add("&bBuyer: &f" + soldTrade.sale().buyer().name());
        }

        lore.add("&bDate: &f" + SHORT_DATE_FORMATTER.format(outcomeTime(trade).atZone(ZoneId.systemDefault())));
        lore.add(" ");
        lore.add("&eClick for full details");

        return lore;
    }

    static SimplePlaceholder detailPlaceholder(Trade trade) {
        return line -> line
                .replace("%seller%", trade.offer().seller().name())
                .replace("%seller_uuid%", trade.offer().seller().uniqueId().toString())
                .replace("%buyer%", buyerName(trade))
                .replace("%buyer_uuid%", buyerUniqueId(trade))
                .replace("%listed_price%", formatMoney(trade.offer().price().getPrice()))
                .replace("%sold_price%", soldPrice(trade))
                .replace("%listed_time%", formatDate(trade.offer().creationTime()))
                .replace("%expiry_time%", formatDate(trade.offer().expiryTime()))
                .replace("%outcome_time%", formatDate(outcomeTime(trade)))
                .replace("%outcome%", outcomeDisplayName(trade))
                .replace("%type%", formatItemType(trade))
                .replace("%offer_id%", trade.offer().id().toString())
                .replace("%item%", trade.offer().item().displayName());
    }

    private static String buyerName(Trade trade) {
        return trade instanceof SoldTrade soldTrade ? soldTrade.sale().buyer().name() : NONE;
    }

    private static String buyerUniqueId(Trade trade) {
        return trade instanceof SoldTrade soldTrade ? soldTrade.sale().buyer().uniqueId().toString() : NONE;
    }

    private static String soldPrice(Trade trade) {
        return trade instanceof SoldTrade soldTrade ? formatMoney(soldTrade.sale().purchasePrice()) : NONE;
    }

    private static String formatItemType(Trade trade) {
        return switch (trade.offer().item().id()) {
            case "pokemon" -> "Pokemon";
            case "item" -> "Item";
            default -> trade.offer().item().id();
        };
    }

    private static double finalPrice(Trade trade) {
        if (trade instanceof SoldTrade soldTrade) {
            return soldTrade.sale().purchasePrice();
        }

        return trade.offer().price().getPrice();
    }

    private static String outcomeDisplayName(Trade trade) {
        if (trade instanceof SoldTrade) {
            return "Sold";
        }

        if (trade instanceof ExpiredTrade) {
            return "Expired";
        }

        if (trade instanceof RemovedTrade removedTrade) {
            return switch (removedTrade.removalInfo().reason().toUpperCase(Locale.ROOT)) {
                case "ADMIN_REMOVED" -> "Admin Removed";
                case "OWNER_REMOVED" -> "Owner Removed";
                default -> removedTrade.removalInfo().reason();
            };
        }

        return "Active";
    }

    private static Instant outcomeTime(Trade trade) {
        if (trade instanceof SoldTrade soldTrade) {
            return soldTrade.sale().time();
        }

        if (trade instanceof RemovedTrade removedTrade) {
            return removedTrade.removalInfo().time();
        }

        if (trade instanceof ExpiredTrade expiredTrade) {
            return expiredTrade.outcomeTime();
        }

        return trade.offer().creationTime();
    }

    private static String formatMoney(double price) {
        return String.format(EnvyGTSForge.getLocale().getMoneyFormat(), price);
    }

    private static String formatDate(Instant instant) {
        return DATE_FORMATTER.format(instant.atZone(ZoneId.systemDefault()));
    }
}
