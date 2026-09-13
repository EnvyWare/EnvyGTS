package com.envyful.gts.forge.ui.market;

import com.envyful.api.concurrency.UtilConcurrency;
import com.envyful.api.config.type.ConfigInterface;
import com.envyful.api.config.type.ExtendedConfigItem;
import com.envyful.api.config.type.PaginatedConfigInterface;
import com.envyful.api.neoforge.config.UtilConfigInterface;
import com.envyful.api.neoforge.config.UtilConfigItem;
import com.envyful.api.neoforge.player.ForgeEnvyPlayer;
import com.envyful.api.type.Pair;
import com.envyful.gts.forge.EnvyGTSForge;
import com.envyful.gts.forge.api.item.TradeItemTypeFactory;
import com.envyful.gts.forge.api.trade.SoldTrade;
import com.envyful.gts.forge.api.trade.Trade;
import com.envyful.gts.forge.ui.TradeFilter;
import com.envyful.gts.forge.ui.TradeFilterConfig;
import com.envyful.gts.forge.ui.TradeHistoryDisplay;
import com.envyful.gts.forge.ui.TradeWindowConfig;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * The UI showing players every GTS listing that finished within the window they're viewing
 *
 */
@ConfigSerializable
public class RecentTradesUI {

    private PaginatedConfigInterface guiSettings = PaginatedConfigInterface.paginatedBuilder()
            .title("EnvyGTS Recent Trades")
            .height(6)
            .fillType(ConfigInterface.FillType.BLOCK)
            .nextPageButton(ExtendedConfigItem.builder()
                    .type("pixelmon:trade_holder_right")
                    .amount(1)
                    .name("&aNext Page")
                    .positions(Pair.of(8, 5))
                    .build())
            .previousPageButton(ExtendedConfigItem.builder()
                    .type("pixelmon:trade_holder_left")
                    .amount(1)
                    .name("&aPrevious Page")
                    .positions(Pair.of(0, 5))
                    .build())
            .build();

    private TradeFilterConfig filterConfig = new TradeFilterConfig(
            new TradeWindowConfig("1d", List.of("1d", "3d", "7d")));

    private ExtendedConfigItem backButton = ExtendedConfigItem.builder()
            .type("pixelmon:eject_button")
            .amount(1)
            .name("&cBack")
            .positions(Pair.of(4, 5))
            .build();

    private ExtendedConfigItem searchButton = ExtendedConfigItem.builder()
            .type("minecraft:oak_sign")
            .amount(1)
            .name("&bSearch: &f%search%")
            .lore("&7Click to look up a Pokemon", "&7species or an item by name")
            .positions(Pair.of(2, 5))
            .build();

    private ExtendedConfigItem playerButton = ExtendedConfigItem.builder()
            .type("minecraft:player_head")
            .amount(1)
            .name("&bPlayer: &f%player%")
            .lore("&7Click to only show the trades", "&7a player sold or bought")
            .positions(Pair.of(6, 5))
            .build();

    private ExtendedConfigItem typeButton = ExtendedConfigItem.builder()
            .type("pixelmon:poke_ball")
            .amount(1)
            .name("&bShowing: &f%type%")
            .lore("&7Click to switch between all,", "&7pokemon, and items")
            .positions(Pair.of(3, 5))
            .build();

    private ExtendedConfigItem windowButton = ExtendedConfigItem.builder()
            .type("minecraft:clock")
            .amount(1)
            .name("&eLast &f%window%")
            .lore("&7Click to change how far back", "&7the trades shown go")
            .positions(Pair.of(5, 5))
            .build();

    private List<String> soldListingLore = List.of(
            " ",
            "&bOutcome: &f%outcome%",
            "&bSold for: &a$%sold_price%",
            "&bSeller: &f%seller%",
            "&bBuyer: &f%buyer%",
            "&bDate: &f%outcome_date%",
            " ",
            "&eClick for more details"
    );

    private List<String> unsoldListingLore = List.of(
            " ",
            "&bOutcome: &f%outcome%",
            "&bListed for: &a$%listed_price%",
            "&bSeller: &f%seller%",
            "&bDate: &f%outcome_date%",
            " ",
            "&eClick for more details"
    );

    public void openUI(ForgeEnvyPlayer player) {
        this.openUI(player, this.filterConfig.defaultFilter(), 1);
    }

    public void openSearch(ForgeEnvyPlayer player, String search) {
        this.openUI(player, this.filterConfig.defaultFilter().withSearch(search), 1);
    }

    public void openPlayer(ForgeEnvyPlayer player, String searchedPlayer) {
        this.openUI(player, this.filterConfig.defaultFilter().withPlayer(searchedPlayer), 1);
    }

    /**
     *
     * Opens the trades the viewing player sold or bought, looked up by their UUID so that a name change does
     * not hide them
     *
     */
    public void openOwnTrades(ForgeEnvyPlayer player) {
        this.openUI(player, this.filterConfig.defaultFilter()
                .withPlayer(player.getUniqueId().toString(), player.getName()), 1);
    }

    public void openUI(ForgeEnvyPlayer player, TradeFilter filter, int page) {
        UtilConcurrency.runAsync(() -> {
            var history = EnvyGTSForge.getTradeService().completedListings(filter.toQuery());

            TradeHistoryDisplay.reportFailures(player, history);

            if (history.trades().isEmpty()) {
                this.filterConfig.reportNoTrades(player, filter);
            }

            this.openPane(player, history.trades(), filter, page);
        });
    }

    @SuppressWarnings("unchecked")
    private void openPane(ForgeEnvyPlayer player, List<Trade> trades, TradeFilter filter, int page) {
        var openPage = new AtomicInteger(page);
        var placeholder = this.filterConfig.placeholder(filter);

        UtilConfigInterface.paginatedBuilder(trades)
                .itemConversion(trade -> TradeHistoryDisplay.build(trade, this.listingLore(trade))
                        .singleClick()
                        .asyncClick()
                        .clickHandler((envyPlayer, clickType) -> EnvyGTSForge.getGui().getMarketTradeDetailUI()
                                .openDetails(player, trade, () -> this.openPane(player, trades, filter, openPage.get())))
                        .build())
                .configSettings(this.guiSettings)
                .extraItems((pane, currentPage) -> {
                    openPage.set(currentPage);

                    UtilConfigItem.builder()
                            .asyncClick(false)
                            .clickHandler((envyPlayer, clickType) -> EnvyGTSForge.getGui().getMarketUI().openMenu(player))
                            .extendedConfigItem(player, pane, this.backButton);

                    UtilConfigItem.builder()
                            .asyncClick(false)
                            .clickHandler((envyPlayer, clickType) -> this.filterConfig.openItemSearch(player, filter,
                                    searched -> this.openUI(player, searched, 1)))
                            .extendedConfigItem(player, pane, this.searchButton, placeholder);

                    UtilConfigItem.builder()
                            .asyncClick(false)
                            .clickHandler((envyPlayer, clickType) -> this.filterConfig.openPlayerSearch(player, filter,
                                    searched -> this.openUI(player, searched, 1)))
                            .extendedConfigItem(player, pane, this.playerButton, placeholder);

                    UtilConfigItem.builder()
                            .asyncClick(false)
                            .clickHandler((envyPlayer, clickType) -> this.openUI(player,
                                    filter.withItemType(TradeItemTypeFactory.getNext(filter.itemType())), 1))
                            .extendedConfigItem(player, pane, this.typeButton, placeholder);

                    UtilConfigItem.builder()
                            .asyncClick(false)
                            .clickHandler((envyPlayer, clickType) -> this.openUI(player,
                                    filter.withWindow(this.filterConfig.getWindowConfig().getNext(filter.window())), 1))
                            .extendedConfigItem(player, pane, this.windowButton, placeholder);
                })
                .open(player, page);
    }

    private List<String> listingLore(Trade trade) {
        return trade instanceof SoldTrade ? this.soldListingLore : this.unsoldListingLore;
    }
}
