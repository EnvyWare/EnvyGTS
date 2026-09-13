package com.envyful.gts.forge.ui;

import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.gui.item.Displayable;
import com.envyful.api.neoforge.items.ItemBuilder;
import com.envyful.api.neoforge.player.ForgeEnvyPlayer;
import com.envyful.api.platform.PlatformProxy;
import com.envyful.api.text.parse.SimplePlaceholder;
import com.envyful.gts.forge.EnvyGTSForge;
import com.envyful.gts.forge.api.item.TradeItemType;
import com.envyful.gts.forge.api.item.TradeItemTypeFactory;
import com.envyful.gts.forge.api.trade.ExpiredTrade;
import com.envyful.gts.forge.api.trade.RemovedTrade;
import com.envyful.gts.forge.api.trade.SoldTrade;
import com.envyful.gts.forge.api.trade.Trade;
import com.envyful.gts.forge.api.trade.TradeHistory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/**
 *
 * Shared formatting for the UIs that display historical {@link Trade}s
 *
 */
public final class TradeHistoryDisplay {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");

    private static final DateTimeFormatter SHORT_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private static final String NONE = "-";

    private TradeHistoryDisplay() {
    }

    /**
     *
     * Warns the player about any trades that the database lookup could not read, as they are missing from
     * whatever is about to be displayed to them
     *
     * @param player The player viewing the history
     * @param history The history that was read from the database
     *
     */
    public static void reportFailures(ForgeEnvyPlayer player, TradeHistory history) {
        if (!history.hasFailures()) {
            return;
        }

        player.message(EnvyGTSForge.getLocale().getMessages().getTradeHistoryReadFailure()
                .replace("%failed%", String.valueOf(history.failed())));
    }

    /**
     *
     * Builds the display for a single historical trade, appending the given lore to the item's own display
     *
     * @param trade The trade being displayed
     * @param lore The lore lines, which may contain any of the placeholders from {@link #placeholder(Trade)}
     * @return The builder for the trade's display
     *
     */
    public static Displayable.Builder<ItemStack> build(Trade trade, List<String> lore) {
        var item = new ItemBuilder(trade.offer().item().display());
        var placeholder = placeholder(trade);

        for (var line : lore) {
            item.addLore(PlatformProxy.<Component>flatParse(placeholder.replace(line)));
        }

        return GuiFactory.displayableBuilder(item.build());
    }

    /**
     *
     * The placeholders describing a historical trade
     *
     * @param trade The trade being displayed
     * @return The placeholder
     *
     */
    public static SimplePlaceholder placeholder(Trade trade) {
        return line -> line
                .replace("%seller%", trade.offer().seller().name())
                .replace("%seller_uuid%", trade.offer().seller().uniqueId().toString())
                .replace("%buyer%", buyerName(trade))
                .replace("%buyer_uuid%", buyerUniqueId(trade))
                .replace("%listed_price%", formatMoney(trade.offer().price().getPrice()))
                .replace("%sold_price%", soldPrice(trade))
                .replace("%final_price%", formatMoney(finalPrice(trade)))
                .replace("%listed_time%", formatDate(trade.offer().creationTime()))
                .replace("%expiry_time%", formatDate(trade.offer().expiryTime()))
                .replace("%outcome_time%", formatDate(outcomeTime(trade)))
                .replace("%outcome_date%", formatShortDate(outcomeTime(trade)))
                .replace("%outcome%", outcomeDisplayName(trade))
                .replace("%type%", formatItemType(trade))
                .replace("%offer_id%", trade.offer().id().toString())
                .replace("%item%", trade.offer().item().displayName());
    }

    /**
     *
     * The display name for the type of trade a lookup was filtered to
     *
     * @param itemType The type the lookup was filtered to, or null if it was not filtered
     * @param everyTypeDisplayName The name to use when the lookup was not filtered
     * @return The display name
     *
     */
    public static String itemTypeName(@Nullable TradeItemType itemType, String everyTypeDisplayName) {
        return itemType == null ? everyTypeDisplayName : itemType.getDisplayName();
    }

    public static String formatMoney(double price) {
        return String.format(EnvyGTSForge.getLocale().getMoneyFormat(), price);
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
        return TradeItemTypeFactory.byId(trade.offer().item().id())
                .map(TradeItemType::getDisplayName)
                .orElseGet(() -> trade.offer().item().id());
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

    private static String formatDate(Instant instant) {
        return DATE_FORMATTER.format(instant.atZone(ZoneId.systemDefault()));
    }

    private static String formatShortDate(Instant instant) {
        return SHORT_DATE_FORMATTER.format(instant.atZone(ZoneId.systemDefault()));
    }
}
