package com.envyful.gts.forge.ui;

import com.envyful.api.neoforge.player.ForgeEnvyPlayer;
import com.envyful.api.text.parse.SimplePlaceholder;
import com.envyful.gts.forge.EnvyGTSForge;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.function.Consumer;

/**
 *
 * The filters a trade history UI offers, shared by the player and the admin menus. Every one of these UIs
 * displays them with the &#37;window&#37;, &#37;type&#37;, &#37;search&#37;, and &#37;player&#37; placeholders
 *
 */
@ConfigSerializable
public class TradeFilterConfig {

    private TradeWindowConfig windowConfig = new TradeWindowConfig();

    private TradeSearchConfig itemSearch = new TradeSearchConfig();

    private TradeSearchConfig playerSearch = new TradeSearchConfig(
            "&bSearch By Player",
            "&7Enter a player name, or UUID, to see the trades they sold or bought.",
            "&cThat is not a valid player name or UUID.",
            "Show Everyone",
            "[A-Za-z0-9_\\-]{1,36}",
            36
    );

    private String everyTypeDisplayName = "All";

    private String noSearchDisplayName = "Anything";

    private String noPlayerDisplayName = "Anyone";

    private String allTimeDisplayName = "All time";

    public TradeFilterConfig() {
    }

    public TradeFilterConfig(TradeWindowConfig windowConfig) {
        this.windowConfig = windowConfig;
    }

    public TradeWindowConfig getWindowConfig() {
        return this.windowConfig;
    }

    /**
     *
     * The filter a UI with a time window opens on
     *
     */
    public TradeFilter defaultFilter() {
        return TradeFilter.of(this.windowConfig.getDefaultWindow());
    }

    public void openItemSearch(ForgeEnvyPlayer player, TradeFilter filter, Consumer<TradeFilter> reopen) {
        this.openItemSearch(player, filter, reopen, () -> reopen.accept(filter));
    }

    public void openItemSearch(ForgeEnvyPlayer player, TradeFilter filter, Consumer<TradeFilter> reopen,
                               Runnable cancelled) {
        this.itemSearch.openInput(player, filter, filter.search(), TradeFilter::withSearch, reopen, cancelled);
    }

    public void openPlayerSearch(ForgeEnvyPlayer player, TradeFilter filter, Consumer<TradeFilter> reopen) {
        this.openPlayerSearch(player, filter, reopen, () -> reopen.accept(filter));
    }

    public void openPlayerSearch(ForgeEnvyPlayer player, TradeFilter filter, Consumer<TradeFilter> reopen,
                                 Runnable cancelled) {
        this.playerSearch.openInput(player, filter, filter.player(), TradeFilter::withPlayer, reopen, cancelled);
    }

    public SimplePlaceholder placeholder(TradeFilter filter) {
        return line -> line
                .replace("%window%", this.windowDisplayName(filter))
                .replace("%type%", TradeHistoryDisplay.itemTypeName(filter.itemType(), this.everyTypeDisplayName))
                .replace("%search%", filter.hasSearch() ? filter.search() : this.noSearchDisplayName)
                .replace("%player%", filter.hasPlayer() ? filter.playerDisplayName() : this.noPlayerDisplayName);
    }

    public void reportNoTrades(ForgeEnvyPlayer player, TradeFilter filter) {
        player.message(this.placeholder(filter).replace(EnvyGTSForge.getLocale().getMessages().getNoTradesFound()));
    }

    public void reportNoSales(ForgeEnvyPlayer player, TradeFilter filter) {
        player.message(this.placeholder(filter).replace(EnvyGTSForge.getLocale().getMessages().getNoSalesFound()));
    }

    private String windowDisplayName(TradeFilter filter) {
        return filter.window() == null ? this.allTimeDisplayName : TradeWindowConfig.format(filter.window());
    }
}
